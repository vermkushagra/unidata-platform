package org.unidata.mdm.data.service.segments;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.unidata.mdm.core.type.data.ArrayAttribute;
import org.unidata.mdm.core.type.data.ArrayValue;
import org.unidata.mdm.core.type.data.Attribute;
import org.unidata.mdm.core.type.data.Attribute.AttributeType;
import org.unidata.mdm.core.type.data.AttributeIterator;
import org.unidata.mdm.core.type.data.CodeLinkValue;
import org.unidata.mdm.core.type.data.ComplexAttribute;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.SimpleAttribute;
import org.unidata.mdm.core.type.data.SimpleAttribute.DataType;
import org.unidata.mdm.core.type.data.impl.AbstractSimpleAttribute;
import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.data.type.data.EtalonRecord;
import org.unidata.mdm.data.type.data.EtalonRecordInfoSection;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.data.OriginRecordInfoSection;
import org.unidata.mdm.data.type.keys.RecordOriginKey;
import org.unidata.mdm.meta.service.LookupService;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.type.info.impl.EnumerationInfoHolder;
import org.unidata.mdm.meta.util.ModelUtils;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov
 *
 */
public interface AttributesPostProcessingSupport {
    /**
     * Link field.
     */
    static final Pattern LINK_FIELD_TEMPLATE = Pattern.compile("\\{[a-zA-Z][_\\-a-zA-Z0-9]*\\}");

    MetaModelService metaModelService();

    LookupService lookupService();

    default boolean processRecords(@Nonnull List<EtalonRecord> etalons, @Nonnull List<OriginRecord> origins){

        Map<String, Map<String, AttributeModelElement>> attributeMap = getAttributeMap(origins, etalons);

        filterAttributesNotFoundInModel(etalons, origins, attributeMap);

        Map<String, Map<String, AttributeModelElement>> linksEnumsOrTemplates = filterLinksEnumsOrTemplates(attributeMap);
        etalons.forEach(r -> process(r, linksEnumsOrTemplates.get(r.getInfoSection().getEntityName()),
                r.getInfoSection().getValidFrom(), r.getInfoSection().getValidTo()));
        origins.forEach(r -> process(r, linksEnumsOrTemplates.get(r.getInfoSection().getOriginKey().getEntityName()),
                r.getInfoSection().getValidFrom(), r.getInfoSection().getValidTo()));

        return true;
    }

    /**
     * @param origins -  collection of origins
     * @param etalons -  collection of etalons
     * @return map , where key is attribute name , and value is attribute holder
     */
    default Map<String, Map<String, AttributeModelElement>> getAttributeMap(List<OriginRecord> origins, List<EtalonRecord> etalons) {

        Stream<String> etalonEntities = etalons.stream()
                                               .map(EtalonRecord::getInfoSection)
                                               .filter(Objects::nonNull)
                                               .map(EtalonRecordInfoSection::getEntityName);

        Stream<String> originEntities = origins.stream()
                                               .map(OriginRecord::getInfoSection)
                                               .filter(Objects::nonNull)
                                               .map(OriginRecordInfoSection::getOriginKey)
                                               .filter(Objects::nonNull)
                                               .map(RecordOriginKey::getEntityName);

        Stream<String> entities = Stream.concat(etalonEntities, originEntities);

        return entities.distinct()
                       .filter(Objects::nonNull)
                       .collect(toMap(ent -> ent, ent -> metaModelService().getAttributesInfoMap(ent)));
    }

    /**
     * Filter attributes not found in meta model
     * @param etalons etalons for filter
     * @param origins origins for filter
     * @param attributeMap attribute map
     */
    default void filterAttributesNotFoundInModel(@Nonnull List<EtalonRecord> etalons, @Nonnull List<OriginRecord> origins, Map<String, Map<String, AttributeModelElement>> attributeMap) {
        etalons.forEach(record -> filterAttributesNotFoundInModelRecursive(
                attributeMap.get(record.getInfoSection().getEntityName()), record, EMPTY));
        origins.forEach(record -> filterAttributesNotFoundInModelRecursive(
                attributeMap.get(record.getInfoSection().getOriginKey().getEntityName()), record, EMPTY));
    }

    /**
     * @param all map with attributes
     * @return filtered map
     */
    default Map<String, Map<String, AttributeModelElement>> filterLinksEnumsOrTemplates(
            Map<String, Map<String, AttributeModelElement>> all) {

        Map<String, Map<String, AttributeModelElement>> linksOrCodeAttributes = new HashMap<>(all.size(), 1);
        for (Entry<String, Map<String, AttributeModelElement>> entry : all.entrySet()) {

            Map<String, AttributeModelElement> filtered = entry.getValue()
                  .entrySet()
                  .stream()
                  .filter(ent -> ent.getValue().isLinkTemplate()
                          || ent.getValue().isLookupLink()
                          || ent.getValue().isEnumValue())
                  .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

            if (!filtered.isEmpty()) {
                linksOrCodeAttributes.put(entry.getKey(), filtered);
            }
        }

        return linksOrCodeAttributes;
    }

    /**
     * Process entry point.
     * @param record the record to process
     * @param linksEnumsOrCodeAttributes link templates, enums values, or code attribute references
     */
    default void process(DataRecord record, Map<String, AttributeModelElement> linksEnumsOrCodeAttributes, Date validFrom, Date validTo) {

        if (MapUtils.isEmpty(linksEnumsOrCodeAttributes)) {
            return;
        }

        Map<String, Set<Attribute>> enumValues = new HashMap<>();
        for (Entry<String, AttributeModelElement> attrEntry : linksEnumsOrCodeAttributes.entrySet()) {

            if (attrEntry.getValue().isLinkTemplate()) {

                // Historically, only 1st level is processed
                String transformed = processLinkTemplate(record, attrEntry.getValue().getLinkTemplate());
                SimpleAttribute<?> attrHit = record.getSimpleAttribute(attrEntry.getValue().getName());
                if (attrHit != null && attrHit.getDataType() == DataType.STRING) {
                    attrHit.castValue(transformed);
                } else {
                    record.addAttribute(
                            AbstractSimpleAttribute.of(DataType.STRING,
                                    attrEntry.getValue().getName(),
                                    transformed));
                }

            } else if (attrEntry.getValue().isLookupLink()) {
                Collection<Attribute> attrs = record.getAttributeRecursive(attrEntry.getKey());
                if (CollectionUtils.isNotEmpty(attrs)) {
                    for (Attribute attr : attrs) {
                        if (attr instanceof ArrayAttribute) {
                            ArrayAttribute<?> arrayAttribute = (ArrayAttribute<?>) attr;
                            if (CollectionUtils.isNotEmpty(arrayAttribute.getValue())) {
                                for (ArrayValue<?> arrayValue : arrayAttribute.getValue()) {
                                    Pair<String, String> lookupDisplayName = lookupService().getLookupDisplayNameById(attrEntry.getValue().getLookupLinkName(), arrayValue.getValue(), validFrom, validTo, attrEntry.getValue().getLookupEntityDisplayAttributes(), attrEntry.getValue().showFieldNamesInDisplay());
                                    if (lookupDisplayName != null) {
                                        arrayValue.setDisplayValue(genDisplayValue(lookupDisplayName.getValue()));
                                        ((CodeLinkValue) arrayValue).setLinkEtalonId(lookupDisplayName.getKey());
                                    }
                                }
                            }
                        } else {
                            SimpleAttribute<?> simpleAttribute = (SimpleAttribute<?>) attr;
                            if (simpleAttribute.getValue() != null) {
                                Pair<String, String> lookupDisplayName = lookupService().getLookupDisplayNameById(attrEntry.getValue().getLookupLinkName(), simpleAttribute.getValue(), validFrom, validTo, attrEntry.getValue().getLookupEntityDisplayAttributes(), attrEntry.getValue().showFieldNamesInDisplay());
                                if (lookupDisplayName != null) {
                                    simpleAttribute.setDisplayValue(genDisplayValue(lookupDisplayName.getValue()));
                                    ((CodeLinkValue) simpleAttribute).setLinkEtalonId(lookupDisplayName.getKey());
                                }

                            }
                        }
                    }
                }

            } else if (attrEntry.getValue().isEnumValue()) {

                String enumName = attrEntry.getValue().getEnumName();
                if (enumValues.containsKey(enumName)) {
                    enumValues.get(enumName).addAll(record.getAttributeRecursive(attrEntry.getKey()));
                } else {
                    enumValues.put(enumName, new HashSet<>(record.getAttributeRecursive(attrEntry.getKey())));
                }
            }
        }

        processEnumDisplayValues(enumValues);
    }

    /**
     * Processes enum display values.
     * @param values value map with enum name key and attr values
     */
    @SuppressWarnings("unchecked")
    default void processEnumDisplayValues(Map<String, Set<Attribute>> values) {

        if (MapUtils.isEmpty(values)) {
            return;
        }

        MeasurementPoint.start();
        try {

            for (Entry<String, Set<Attribute>> entry : values.entrySet()) {

                if (isEmpty(entry.getValue())) {
                    continue;
                }

                EnumerationInfoHolder ew = metaModelService().getValueById(entry.getKey(), EnumerationInfoHolder.class);
                entry.getValue().stream()
                    .map(attr -> (SimpleAttribute<String>) attr)
                    .forEach(attr -> attr.setDisplayValue(ew.getEnumerationMap().get(attr.getValue())));
            }


        } finally {
            MeasurementPoint.stop();
        }
    }

    default String genDisplayValue(final String displayValue) {
        return displayValue != null ? displayValue : "<No display value found>";
    }

    /**
     * Actually processes the template and replaces place holders with value.
     * @param record the record
     * @param template the template
     * @return processed template
     */
    default String processLinkTemplate(DataRecord record, String template) {

        StringBuffer result = new StringBuffer();
        Matcher m = LINK_FIELD_TEMPLATE.matcher(template);

        while (m.find()) {
            int left = m.start();
            int right = m.end();

            String field = template.substring(left + 1, right - 1);
            String replacement = extractFieldValue(record, field);
            m.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(result);

        return result.toString();
    }

    /**
     * Extracts field value.
     * @param record the record
     * @param field the field
     * @return value or hint <invalid value for link> for attributes, that cannot be processed
     */
    default String extractFieldValue(DataRecord record, String field) {

        SimpleAttribute<?> attr = record.getSimpleAttribute(field);
        if (attr == null || attr.getValue() == null) {
            return EMPTY;
        }

        switch (attr.getDataType()) {
            case BOOLEAN:
                return Boolean.toString((Boolean) attr.getValue());
            case INTEGER:
                return Long.toString((Long) attr.getValue());
            case NUMBER:
                return Double.toString((Double) attr.getValue());
            case STRING:
                return attr.getValue().toString();
            default:
                return EMPTY;
        }
    }



    /**
     *
     * @param map - attribute map
     * @param record -  record
     * @param path - prefix path
     */
    default void filterAttributesNotFoundInModelRecursive(Map<String, AttributeModelElement> map, DataRecord record, String path) {

        if (map == null) {
            return;
        }

        AttributeIterator it = record.attributeIterator();
        while (it.hasNext()) {

            Attribute attr = it.next();
            String fullPath = ModelUtils.getAttributePath(path, attr.getName());
            if (!map.containsKey(fullPath)) {
                it.remove();
                continue;
            }

            if (attr.getAttributeType() == AttributeType.COMPLEX) {
                ComplexAttribute cattr = (ComplexAttribute) attr;
                for (DataRecord nested : cattr) {
                    filterAttributesNotFoundInModelRecursive(map, nested, fullPath);
                }
            }
        }
    }
}

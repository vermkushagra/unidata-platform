/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.unidata.mdm.backend.service.data.listener.record;

import static com.unidata.mdm.backend.common.context.SearchRequestContext.forEtalonData;
import static com.unidata.mdm.backend.common.search.FormField.range;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.Collections;
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

import com.unidata.mdm.backend.common.search.fields.RecordHeaderField;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.meta.SimpleDataType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchRequestOperator;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.ArrayValue;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.Attribute.AttributeType;
import com.unidata.mdm.backend.common.types.AttributeIterator;
import com.unidata.mdm.backend.common.types.CodeLinkValue;
import com.unidata.mdm.backend.common.types.ComplexAttribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.EtalonRecordInfoSection;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.OriginRecordInfoSection;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.common.types.impl.AbstractSimpleAttribute;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.model.util.wrappers.EnumerationWrapper;
import com.unidata.mdm.meta.LookupEntityDef;

/**
 * @author Mikhail Mikhailov
 *
 */
public abstract class AbstractDataRecordAttributesProcessingExecutor {

    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
    /**
     * Search service.
     */
    @Autowired
    private SearchService searchService;
    /**
     * Link field.
     */
    private static final Pattern LINK_FIELD_TEMPLATE = Pattern.compile("\\{[a-zA-Z][_\\-a-zA-Z0-9]*\\}");

    /**
     * Constructor.
     */
    public AbstractDataRecordAttributesProcessingExecutor() {
        super();
    }

    protected boolean processRecords(@Nonnull List<EtalonRecord> etalons, @Nonnull  List<OriginRecord> origins){
        // todo add options parameter for process only part of type attributes
        Map<String, Map<String, AttributeInfoHolder>> attributeMap = getAttributeMap(origins, etalons);

        filterAttributesNotFoundInModel(etalons, origins, attributeMap);

        Map<String, Map<String, AttributeInfoHolder>> linksEnumsOrTemplates = filterLinksEnumsOrTemplates(attributeMap);
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
    private Map<String, Map<String, AttributeInfoHolder>> getAttributeMap(List<OriginRecord> origins, List<EtalonRecord> etalons) {

        Stream<String> etalonEntities = etalons.stream()
                                               .map(EtalonRecord::getInfoSection)
                                               .filter(Objects::nonNull)
                                               .map(EtalonRecordInfoSection::getEntityName);

        Stream<String> originEntities = origins.stream()
                                               .map(OriginRecord::getInfoSection)
                                               .filter(Objects::nonNull)
                                               .map(OriginRecordInfoSection::getOriginKey)
                                               .filter(Objects::nonNull)
                                               .map(OriginKey::getEntityName);

        Stream<String> entities = Stream.concat(etalonEntities, originEntities);

        return entities.distinct()
                       .filter(Objects::nonNull)
                       .collect(toMap(ent -> ent, metaModelService::getAttributesInfoMap));
    }

    /**
     * Filter attributes not found in meta model
     * @param etalons etalons for filter
     * @param origins origins for filter
     * @param attributeMap attribute map
     */
    private void filterAttributesNotFoundInModel(@Nonnull List<EtalonRecord> etalons, @Nonnull List<OriginRecord> origins, Map<String, Map<String, AttributeInfoHolder>> attributeMap) {
        etalons.forEach(record -> filterAttributesNotFoundInModelRecursive(
                attributeMap.get(record.getInfoSection().getEntityName()), record, EMPTY));
        origins.forEach(record -> filterAttributesNotFoundInModelRecursive(
                attributeMap.get(record.getInfoSection().getOriginKey().getEntityName()), record, EMPTY));
    }

    /**
     * @param all map with attributes
     * @return filtered map
     */
    private Map<String, Map<String, AttributeInfoHolder>> filterLinksEnumsOrTemplates(
            Map<String, Map<String, AttributeInfoHolder>> all) {

        Map<String, Map<String, AttributeInfoHolder>> linksOrCodeAttributes = new HashMap<>(all.size(), 1);
        for (Entry<String, Map<String, AttributeInfoHolder>> entry : all.entrySet()) {

            Map<String, AttributeInfoHolder> filtered = entry.getValue()
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
    private void process(DataRecord record, Map<String, AttributeInfoHolder> linksEnumsOrCodeAttributes, Date validFrom, Date validTo) {

        if (MapUtils.isEmpty(linksEnumsOrCodeAttributes)) {
            return;
        }

        Map<AttributeInfoHolder, Set<Attribute>> lookupLinkValues = new HashMap<>();
        Map<String, Set<Attribute>> enumValues = new HashMap<>();
        for (Entry<String, AttributeInfoHolder> attrEntry : linksEnumsOrCodeAttributes.entrySet()) {

            if (attrEntry.getValue().isLinkTemplate()) {

                // Historically, only 1st level is processed
                String transformed = processLinkTemplate(record, attrEntry.getValue().getLinkTemplate());
                SimpleAttribute<?> attrHit = record.getSimpleAttribute(attrEntry.getValue().getAttribute().getName());
                if (attrHit != null && attrHit.getDataType() == DataType.STRING) {
                    attrHit.castValue(transformed);
                } else {
                    record.addAttribute(
                            AbstractSimpleAttribute.of(DataType.STRING,
                                    attrEntry.getValue().getAttribute().getName(),
                                    transformed));
                }

            } else if (attrEntry.getValue().isLookupLink()) {

                if (lookupLinkValues.containsKey(attrEntry.getValue())) {
                    lookupLinkValues.get(attrEntry.getValue()).addAll(record.getAttributeRecursive(attrEntry.getKey()));
                } else {
                    lookupLinkValues.put(attrEntry.getValue(), new HashSet<>(record.getAttributeRecursive(attrEntry.getKey())));
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

        processLookupLinkDisplayValues(lookupLinkValues, validFrom, validTo);
        processEnumDisplayValues(enumValues);
    }

    /**
     * Processes enum display values.
     * @param values value map with enum name key and attr values
     */
    @SuppressWarnings("unchecked")
    private void processEnumDisplayValues(Map<String, Set<Attribute>> values) {

        if (MapUtils.isEmpty(values)) {
            return;
        }

        MeasurementPoint.start();
        try {

            for (Entry<String, Set<Attribute>> entry : values.entrySet()) {

                if (isEmpty(entry.getValue())) {
                    continue;
                }

                EnumerationWrapper ew = metaModelService.getValueById(entry.getKey(), EnumerationWrapper.class);
                entry.getValue().stream()
                    .map(attr -> (SimpleAttribute<String>) attr)
                    .forEach(attr -> attr.setDisplayValue(ew.getEnumerationMap().get(attr.getValue())));
            }


        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Processes display names for code attribute links.
     * @param values code attributes map holder
     */
    private void processLookupLinkDisplayValues(Map<AttributeInfoHolder, Set<Attribute>> values, Date validFrom, Date validTo) {

        if (MapUtils.isEmpty(values)) {
            return;
        }

        MeasurementPoint.start();
        try {

            Map<SearchRequestContext, Pair<AttributeInfoHolder, Attribute>> request = new HashMap<>();

            for (Entry<AttributeInfoHolder, Set<Attribute>> entry : values.entrySet()) {

                if (isEmpty(entry.getValue())) {
                    continue;
                }
                String lookupName = entry.getKey().getLookupLinkName();
                LookupEntityDef lookupEntity = metaModelService.getLookupEntityById(lookupName);

                List<String> mdAttrs = CollectionUtils.isNotEmpty(entry.getKey().getLookupEntityDisplayAttributes())
                    ? entry.getKey().getLookupEntityDisplayAttributes()
                    : ModelUtils.findMainDisplayableAttrNamesSorted(lookupEntity);

                if (isEmpty(mdAttrs)) {
                    continue;
                }
                mdAttrs.add(lookupEntity.getCodeAttribute().getName());

                for (Attribute value : entry.getValue()) {

                    // String or Long
                    List<Object> searchValues;
                    if (value.getAttributeType() == AttributeType.ARRAY) {
                        searchValues = value.isEmpty()
                            ? Collections.emptyList()
                            : ((ArrayAttribute<?>) value).getValue().stream()
                                .map(ArrayValue::getValue)
                                .collect(Collectors.toList());
                    } else {
                        searchValues = ((SimpleAttribute<?>) value).getValue() == null
                            ? Collections.emptyList()
                            : singletonList(((SimpleAttribute<?>) value).getValue());
                    }

                    if (isEmpty(searchValues)) {
                        continue;
                    }

                    Date asDate = SearchUtils.getDateForDisplayAttributes(new Date(), validFrom, validTo);
                    SearchRequestContext searchCtx = forEtalonData(lookupName)
                            .operator(SearchRequestOperator.OP_OR)
                            .form(FormFieldsGroup
                                    .createAndGroup()
                                    .addFormField(range(SimpleDataType.TIMESTAMP,
                                            RecordHeaderField.FIELD_FROM.getField(), null, asDate
                                    ))
                                    .addFormField(range(SimpleDataType.TIMESTAMP,
                                            RecordHeaderField.FIELD_TO.getField(), asDate, null
                                    ))
                                    .addFormField(FormField.strictValues(
                                            lookupEntity.getCodeAttribute().getSimpleDataType(),
                                            lookupEntity.getCodeAttribute().getName(),
                                            searchValues)))
                            .returnFields(mdAttrs)
                            .onlyQuery(true)
                            .count(searchValues.size())
                            .page(0)
                            .build();

                    request.put(searchCtx, new ImmutablePair<>(entry.getKey(), value));
                }
            }

            if (isEmpty(request)) {
                return;
            }

            ComplexSearchRequestContext context = ComplexSearchRequestContext.multi(request.keySet());
            Map<SearchRequestContext, SearchResultDTO> response = searchService.search(context);

            for (Entry<SearchRequestContext, SearchResultDTO> entry : response.entrySet()) {

                SearchResultDTO result = entry.getValue();
                if (isEmpty(result.getHits())) {
                    continue;
                }

                SearchRequestContext ctx = entry.getKey();
                Pair<AttributeInfoHolder, Attribute> attrPair = request.get(ctx);
                Attribute attrValue = attrPair.getValue();
                AttributeInfoHolder attrHolder = attrPair.getKey();

                if (attrValue.getAttributeType() == AttributeType.ARRAY) {
                    for (ArrayValue<?> a : ((ArrayAttribute<?>) attrValue).getValue()) {
                        SearchResultHitDTO hit = findHit(result, a.getValue(), ctx.getReturnFields().get(ctx.getReturnFields().size()-1));
                        String displayValue = extractSearchResult(hit, ctx.getReturnFields().subList(0, ctx.getReturnFields().size()-1), attrHolder);
                        a.setDisplayValue(genDisplayValue(displayValue));
                        ((CodeLinkValue) a).setLinkEtalonId(hit.getId());

                    }
                } else {
                    SimpleAttribute<?> simpleAttribute = (SimpleAttribute<?>) attrValue;
                    SearchResultHitDTO hit = findHit(result, simpleAttribute.getValue(),  ctx.getReturnFields().get(ctx.getReturnFields().size() - 1));
                    String displayValue = extractSearchResult(hit, ctx.getReturnFields().subList(0, ctx.getReturnFields().size() - 1), attrHolder);
                    simpleAttribute.setDisplayValue(genDisplayValue(displayValue));
                    ((CodeLinkValue) attrValue).setLinkEtalonId(hit.getId());
                }
            }
        } finally {
            MeasurementPoint.stop();
        }
    }

    private String genDisplayValue(final String displayValue) {
        return displayValue != null ? displayValue : MessageUtils.getMessage("app.error.no.display.value");
    }

    /**
     * Actually processes the template and replaces place holders with value.
     * @param record the record
     * @param template the template
     * @return processed template
     */
    private String processLinkTemplate (DataRecord record, String template) {

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
    private String extractFieldValue(DataRecord record, String field) {

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
     * Extracts search result.
     * @param hit the related hit
     * @param fieldNames field name
     * @param attrHolder the link attribute metadata holder
     * @return result or null
     */
    protected String extractSearchResult(SearchResultHitDTO hit, List<String> fieldNames, AttributeInfoHolder attrHolder) {

        if (hit == null || hit.getPreview().isEmpty()) {
            return null;
        }

        Map<String, AttributeInfoHolder> attrMap = Collections.emptyMap();
        if (attrHolder.showFieldNamesInDisplay()) {
            attrMap = metaModelService.getAttributesInfoMap(attrHolder.getLookupLinkName());
        }

        StringBuilder b = new StringBuilder();
        for (String name : fieldNames) {
            SearchResultHitFieldDTO fieldValue = hit.getFieldValue(name);
            if(fieldValue != null && fieldValue.isNonNullField()){

                String converted = String.valueOf(fieldValue.isCollection()
                        ? fieldValue.getFirstValue() + " (" + String.join(", ", fieldValue.getValues().subList(1, fieldValue.getValues().size()).stream()
                                .map(Object::toString).collect(Collectors.toList())) + ")"
                        : fieldValue.getFirstValue());

                if (attrHolder.showFieldNamesInDisplay()) {
                    AttributeInfoHolder targetField = attrMap.get(name);
                    converted = targetField != null
                            ? targetField.getAttribute().getDisplayName() + ": " + converted
                            : converted;
                }

                boolean firstHit = b.length() == 0;
                b.append(firstHit ? converted : " ");
                b.append(firstHit ? "" : converted);
            }
        }

        return b.length() == 0 ? null : b.toString();
    }

    /**
     *
     * @param result - search result
     * @param value - search value
     * @param codeName - name of value in preview
     * @return hit if present
     */
    protected SearchResultHitDTO findHit(SearchResultDTO result, Object value, String codeName) {
        for (SearchResultHitDTO hit : result.getHits()) {
            SearchResultHitFieldDTO field = hit.getFieldValue(codeName);
            if (field == null) {
                continue;
            }
            if (field.getValues().stream().map(String::valueOf).anyMatch(str -> str.equals(value.toString()))) {
                return hit;
            }
        }
        return null;
    }

    /**
     *
     * @param map - attribute map
     * @param record -  record
     * @param path - prefix path
     */
    private void filterAttributesNotFoundInModelRecursive(Map<String, AttributeInfoHolder> map, DataRecord record, String path) {

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

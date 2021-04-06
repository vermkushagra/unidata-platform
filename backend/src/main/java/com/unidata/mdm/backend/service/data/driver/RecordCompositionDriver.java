package com.unidata.mdm.backend.service.data.driver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.Attribute.AttributeType;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.CodeAttribute.CodeDataType;
import com.unidata.mdm.backend.common.types.ComplexAttribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.extended.ExtendedSerializableDataRecordBuilder;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.model.util.wrappers.BVTMapWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;
import com.unidata.mdm.meta.AbstractAttributeDef;
import com.unidata.mdm.meta.ComplexAttributeDef;

/**
 * @author Mikhail Mikhailov
 *         Record etalon composer.
 */
public class RecordCompositionDriver extends EtalonCompositionDriverBase<DataRecord> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordCompositionDriver.class);

    /**
     * Constructor.
     */
    public RecordCompositionDriver() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasActiveBVR(List<CalculableHolder<DataRecord>> calculables) {
        return super.composeDefaultBVR(calculables, false) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasActiveBVT(List<CalculableHolder<DataRecord>> calculables) {
        // Not applicable
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataRecord composeBVR(List<CalculableHolder<DataRecord>> versions, boolean includeInactive, boolean includeWinners) {
        DataRecord selected = super.composeDefaultBVR(versions, includeInactive);
        return SerializableDataRecord.of(selected);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataRecord composeBVT(List<CalculableHolder<DataRecord>> calculables, boolean includeInactive, boolean includeWinners) {

        // 1. Try entity type first
        BVTMapWrapper wrapper = metaModelService.getValueById(calculables.get(0).getTypeName(), EntityWrapper.class);

        // 2. Try lookup secondly.
        if (wrapper == null) {
            wrapper = metaModelService.getValueById(calculables.get(0).getTypeName(), LookupEntityWrapper.class);
        }

        // 3. Check
        if (wrapper == null) {
            final String message = "Meta model type wrapper for id '{}' not found for BVT calculation.";
            LOGGER.warn(message, calculables.get(0).getTypeName());
            throw new DataProcessingException(message,
                    ExceptionId.EX_DATA_NO_TYPE_WRAPPER_FOR_BVT_CALCULATION,
                    calculables.get(0).getTypeName());
        }

        Map<Integer, Map<String, List<CalculableHolder<Attribute>>>> valuesMap
                = createValuesMap(calculables, includeInactive, wrapper);
        if (valuesMap.isEmpty()) {
            return null;
        }


        DataRecord result = new SerializableDataRecord(valuesMap.size());
        setAttributesRecursive(result, wrapper, valuesMap, "", 0, includeWinners);

        return result;
    }

    /**
     * Extract all simple  attributes from bvt wrapper
     *
     * @param wrapper bvt wrapper
     * @return collection of simple attributes
     */
    private Collection<? extends AbstractAttributeDef> getArrayOrSimpleAttributeDefinitions(BVTMapWrapper wrapper) {
        if (wrapper.isLookup()) {
            LookupEntityWrapper entityWrapper = (LookupEntityWrapper) wrapper;
            Collection<AbstractAttributeDef> abstractAttributeDefs = new ArrayList<>(
                    entityWrapper.getEntity().getArrayAttribute().size() +
                    entityWrapper.getEntity().getSimpleAttribute().size() +
                    entityWrapper.getEntity().getAliasCodeAttributes().size() + 1);

            abstractAttributeDefs.addAll(entityWrapper.getEntity().getSimpleAttribute());
            abstractAttributeDefs.addAll(entityWrapper.getEntity().getArrayAttribute());
            abstractAttributeDefs.addAll(entityWrapper.getEntity().getAliasCodeAttributes());
            abstractAttributeDefs.add(entityWrapper.getEntity().getCodeAttribute());

            return abstractAttributeDefs;
        } else {
            EntityWrapper entityWrapper = ((EntityWrapper) wrapper);
            Collection<AbstractAttributeDef> abstractAttributeDefs = new ArrayList<>(
                    entityWrapper.getEntity().getSimpleAttribute().size() +
                    entityWrapper.getEntity().getArrayAttribute().size());

            abstractAttributeDefs.addAll(entityWrapper.getEntity().getSimpleAttribute());
            abstractAttributeDefs.addAll(entityWrapper.getEntity().getArrayAttribute());
            return abstractAttributeDefs;
        }
    }

    /**
     * Extract all complex attributes from bvt wrapper
     *
     * @param wrapper bvt wrapper
     * @return collection of complex attributes
     */
    private Collection<? extends ComplexAttributeDef> getComplexAttributeDefinitions(BVTMapWrapper wrapper) {
        if (wrapper.isLookup()) {
            return Collections.emptyList();
        } else {
            return ((EntityWrapper) wrapper).getEntity().getComplexAttribute();
        }
    }

    /**
     * Sets values recursive.
     *
     * @param current
     * @param valuesMap
     * @param path
     * @param level
     */
    private void setAttributesRecursive(
            DataRecord current, @Nonnull BVTMapWrapper wrapper,
            Map<Integer, Map<String, List<CalculableHolder<Attribute>>>> valuesMap,
            String path,
            int level,
            boolean includeWinners) {

        Map<String, Map<String, Integer>> bvtMap = wrapper.getBvtMap();
        for (AbstractAttributeDef sattr : getArrayOrSimpleAttributeDefinitions(wrapper)) {

            String attrPath = ModelUtils.getAttributePath(level, path, sattr);
            Map<String, Integer> sourceSystems = bvtMap.get(attrPath);

            if (sourceSystems == null) {
                continue;
            }

            // Source systems should be sorted reversed (from the greatest to the least)
            Attribute value = null;
            CalculableHolder<Attribute> valueHolder = null;
            for (Entry<String, Integer> sys : sourceSystems.entrySet()) {
                Map<String, List<CalculableHolder<Attribute>>> values = valuesMap.get(sys.getValue());
                if (values == null) {
                    continue;
                }
                valueHolder = extractLatest(values.get(attrPath));
                value = valueHolder == null ? null : valueHolder.getValue();
                if (value == null) {
                    continue;
                }

                break;
            }

            if (value != null) {
                Attribute result = buildAttribute(valueHolder, includeWinners);

                if (value.getAttributeType() == AttributeType.CODE) {
                    collectCodeAttributeSupplementaryValues(value, result, valuesMap, attrPath);
                }

                if (result != null) {
                    current.addAttribute(result);
                }
            }
        }

        for (ComplexAttributeDef cattr : getComplexAttributeDefinitions(wrapper)) {

            String attrPath = ModelUtils.getAttributePath(level, path, cattr);
            Map<String, Integer> sourceSystems = bvtMap.get(attrPath);

            // Merge settings define weights on the complex attribute level as well.
            // Set complex attribute at whole and continue, if requested.
            if (sourceSystems != null) {

                ComplexAttribute value = null;
                CalculableHolder<Attribute> valueHolder = null;
                for (Entry<String, Integer> sys : sourceSystems.entrySet()) {
                    Map<String, List<CalculableHolder<Attribute>>> values = valuesMap.get(sys.getValue());
                    if (values == null) {
                        continue;
                    }
                    valueHolder = extractLatest(values.get(attrPath));

                    if (valueHolder == null || valueHolder.getValue() == null) {
                        continue;
                    }

                    value = (ComplexAttribute) valueHolder.getValue();

                    break;
                }

                if (value != null) {
                    ComplexAttribute result = (ComplexAttribute) buildAttribute(valueHolder, includeWinners);
                    if (result != null) {
                        current.addAttribute(result);
                    }
                }

                continue;
            }

            // Process nested records one by one otherwise.
            // TODO currently doesn't work. Handle multiple nested records ASAP.
            throw new RuntimeException("Handling multiple nested records not implemented!");

            /*
            NestedEntityDef nested = metaModelService.getNestedEntityByIdNoDeps(cattr.getNestedEntityName());

            NestedRecord record = JaxbUtils.getDataObjectFactory().createNestedRecord();
            current.getComplexAttributes().add(
                    JaxbUtils.getDataObjectFactory().createComplexAttribute()
                        .withName(cattr.getName())
                        .withNestedRecord(record));

            setAttributesRecursive(record, nested, bvtMap, valuesMap, attrPath, level + 1);
            */
        }

    }

    private Attribute buildAttribute(CalculableHolder<Attribute> valueHolder, boolean withExtendedInformation){
        Attribute result = null;
        Attribute value = valueHolder.getValue();
        if(withExtendedInformation){
            if (value.getAttributeType() == AttributeType.SIMPLE) {
                result = ExtendedSerializableDataRecordBuilder.of((SimpleAttribute<?>)value, valueHolder.getSourceSystem(), valueHolder.getExternalId());
            } else if (value.getAttributeType() == AttributeType.ARRAY) {
                result =  ExtendedSerializableDataRecordBuilder.of((ArrayAttribute<?>) value, valueHolder.getSourceSystem(), valueHolder.getExternalId());
            } else if (value.getAttributeType() == AttributeType.CODE) {
                result = ExtendedSerializableDataRecordBuilder.of((CodeAttribute<?>) value, valueHolder.getSourceSystem(), valueHolder.getExternalId());
            } else if(value.getAttributeType() == AttributeType.COMPLEX){
                result = ExtendedSerializableDataRecordBuilder.of((ComplexAttribute) value, valueHolder.getSourceSystem(), valueHolder.getExternalId());
            }
        } else {
            if (value.getAttributeType() == AttributeType.SIMPLE) {
                result = SerializableDataRecord.of((SimpleAttribute<?>)value);
            } else if (value.getAttributeType() == AttributeType.ARRAY) {
                result = SerializableDataRecord.of((ArrayAttribute<?>) value);
            } else if (value.getAttributeType() == AttributeType.CODE) {
                result = SerializableDataRecord.of((CodeAttribute<?>) value);
            } else if(value.getAttributeType() == AttributeType.COMPLEX){
                result = SerializableDataRecord.of((ComplexAttribute) value);
            }
        }
        return result;
    }

    /**
     * Creates values map.
     *
     * @param versions        versions set
     * @param includeInactive include inactive or not.
     * @param w               wrapper
     * @return map
     */
    private Map<Integer, Map<String, List<CalculableHolder<Attribute>>>> createValuesMap(
            List<CalculableHolder<DataRecord>> versions,
            boolean includeInactive, BVTMapWrapper w) {

        Map<String, List<CalculableHolder<DataRecord>>> versionsBySourceSystem
                = super.collectVersionsBySourceSystem(versions);
        if (versionsBySourceSystem.isEmpty()) {
            return Collections.emptyMap();
        }

        return collectAttributeValues(versionsBySourceSystem, w, includeInactive);
    }

    /**
     * Collects attribute values.
     *
     * @param versionsBySourceSystem versions ordered by source system
     * @param w                      entity wrapper
     * @param includeInactive        include inactive versions into calculations or not
     * @return values map
     */
    private Map<Integer, Map<String, List<CalculableHolder<Attribute>>>> collectAttributeValues(
            Map<String, List<CalculableHolder<DataRecord>>> versionsBySourceSystem,
            BVTMapWrapper w, boolean includeInactive) {

        Map<String, Map<String, Integer>> bvtMap = w.getBvtMap();
        Map<Integer, Map<String, List<CalculableHolder<Attribute>>>> valuesMap
                = new HashMap<>(versionsBySourceSystem.size());
        for (Entry<String, List<CalculableHolder<DataRecord>>> e : versionsBySourceSystem.entrySet()) {
            // Expect more then one record for the same source system.
            // This can be particular the case for merged active records,
            // which all continue to contribute
            for (CalculableHolder<DataRecord> h : e.getValue()) {
                if (h.getStatus() == RecordStatus.INACTIVE && !includeInactive) {
                    continue;
                }

                collectValuesRecursive(h, bvtMap, valuesMap, 0, "", h.getValue());
            }
        }

        return valuesMap;
    }

    /**
     * Collects attributes values recursive.
     *
     * @param bvtMap   the BVT map
     * @param attrsMap attributes map
     * @param level    current nesting level
     * @param path     current path
     * @param nr       current nested attribute
     */
    private void collectValuesRecursive(
            CalculableHolder<DataRecord> source,
            Map<String, Map<String, Integer>> bvtMap,
            Map<Integer, Map<String, List<CalculableHolder<Attribute>>>> valuesMap,
            int level, String path, DataRecord nr) {

        String attrPath;
        for (Attribute attr : nr.getAllAttributes()) {

            attrPath = ModelUtils.getAttributePath(level, path, attr.getName());
            collectAttribute(attrPath, attr, source, bvtMap, valuesMap);

            if (attr.getAttributeType() == AttributeType.COMPLEX) {

                ComplexAttribute cAttr = (ComplexAttribute) attr;
                // UN-1632
                if (cAttr.getRecords().isEmpty()) {
                    continue;
                }

                // FIXME support multiple nested records ASAP!!
                collectValuesRecursive(source, bvtMap, valuesMap, level + 1,
                        attrPath, cAttr.getRecords().get(0));
            }
        }
    }

    /**
     * Collects single attribute.
     *
     * @param attrPath  current attribute path
     * @param attr      the attribute
     * @param source    origin source object
     * @param bvtMap    the BVT map
     * @param valuesMap values map
     */
    private void collectAttribute(String attrPath, Attribute attr,
                                  CalculableHolder<DataRecord> source,
                                  Map<String, Map<String, Integer>> bvtMap,
                                  Map<Integer, Map<String, List<CalculableHolder<Attribute>>>> valuesMap) {

        // 1. Get weights map, defined for this attribute
        Map<String, Integer> attrWeights = bvtMap.get(attrPath);
        if (attrWeights == null) {
            return;
        }

        // 2. Select weight for the source system of the source object
        Integer weight = attrWeights.get(source.getSourceSystem());
        if (weight == null) {
            return;
        }

        // 3. Select attributes map for this weight. Create one if necessary.
        Map<String, List<CalculableHolder<Attribute>>> attrsMap
                = valuesMap.get(weight);
        if (attrsMap == null) {
            attrsMap = new HashMap<>();
            valuesMap.put(weight, attrsMap);
        }

        // 4. Get attribute holder list. Create if needed
        List<CalculableHolder<Attribute>> values = attrsMap.get(attrPath);
        if (values == null) {
            values = new ArrayList<>();
            attrsMap.put(attrPath, values);
        }

        // 5. Put attribute
        values.add(new RecordAttributeHolder(attr, attrPath, source.getSourceSystem(), source.getExternalId(), source.getLastUpdate()));
    }

    /**
     * Gets the youngest record available.
     *
     * @param list the list
     * @return record
     */
    private <V extends Attribute> CalculableHolder<V>  extractLatest(List<CalculableHolder<V>> list) {

        CalculableHolder<V> result = null;
        for (int i = 0; list != null && i < list.size(); i++) {

            CalculableHolder<V> c = list.get(i);
            if (result != null) {
                result = c.getLastUpdate().compareTo(result.getLastUpdate()) > 0 ? c : result;
            } else {
                result = c;
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private void collectCodeAttributeSupplementaryValues(
            Attribute selected,
            Attribute result,
            Map<Integer, Map<String, List<CalculableHolder<Attribute>>>> valuesMap, String attrPath) {

        // Collect supplementary. Instances already ordered in source system wight order
        List<Object> supplementary = new ArrayList<>();
        for (Entry<Integer, Map<String, List<CalculableHolder<Attribute>>>> entry : valuesMap.entrySet()) {
            List<CalculableHolder<Attribute>> others = entry.getValue().get(attrPath);
            if (others != null && !others.isEmpty()) {

                supplementary.addAll(others.stream()
                        .map(CalculableHolder::getValue)
                        .filter(v -> v != selected)
                        .map(attr -> ((CodeAttribute<?>) attr).getValue())
                        .collect(Collectors.toList()));
            }
        }

        if (!supplementary.isEmpty()) {
            if (((CodeAttribute<?>) result).getDataType() == CodeDataType.INTEGER) {
                ((CodeAttribute<Long>) result).setSupplementary(supplementary.stream().map(v -> (Long) v).collect(Collectors.toList()));
            } else if (((CodeAttribute<?>) result).getDataType() == CodeDataType.STRING) {
                ((CodeAttribute<String>) result).setSupplementary(supplementary.stream().map(v -> (String) v).collect(Collectors.toList()));
            }
        }
    }
}

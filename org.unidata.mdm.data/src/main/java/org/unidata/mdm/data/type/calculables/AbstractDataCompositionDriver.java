package org.unidata.mdm.data.type.calculables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.unidata.mdm.core.type.calculables.BvrCalculationInfo;
import org.unidata.mdm.core.type.calculables.BvtCalculationInfo;
import org.unidata.mdm.core.type.calculables.Calculable;
import org.unidata.mdm.core.type.calculables.CalculableHolder;
import org.unidata.mdm.core.type.calculables.CompositionDriver;
import org.unidata.mdm.core.type.calculables.impl.attribute.RecordAttributeHolder;
import org.unidata.mdm.core.type.data.ArrayAttribute;
import org.unidata.mdm.core.type.data.Attribute;
import org.unidata.mdm.core.type.data.Attribute.AttributeType;
import org.unidata.mdm.core.type.data.CodeAttribute;
import org.unidata.mdm.core.type.data.CodeAttribute.CodeDataType;
import org.unidata.mdm.core.type.data.ComplexAttribute;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.core.type.data.SimpleAttribute;
import org.unidata.mdm.core.type.data.extended.ExtendedSerializableDataRecordBuilder;
import org.unidata.mdm.core.type.data.impl.SerializableDataRecord;
import org.unidata.mdm.core.type.model.AttributeModelElement;

/**
 * @author Mikhail Mikhailov
 * Base ECD class.
 */
public abstract class AbstractDataCompositionDriver<T extends Calculable, A extends BvrCalculationInfo<T>, B extends BvtCalculationInfo<T>>
    implements CompositionDriver<T, A, B> {
    /**
     * Constructor.
     */
    public AbstractDataCompositionDriver() {
        super();
    }
    /**
     * Gets path for an attribute.
     * @param level current level
     * @param path current path
     * @param attrName attribute name
     * @return joined path
     */
    protected String getAttributePath(int level, String path, String attrName) {
        return level == 0 ? attrName : String.join(".", path, attrName);
    }
    /**
     * Collects versions by their source systems.
     * @param versions the versions
     * @return map
     */
    protected Map<String, List<CalculableHolder<T>>>
        collectVersionsBySourceSystem(List<CalculableHolder<T>> versions) {

        Map<String, List<CalculableHolder<T>>> versionsBySourceSystem = new HashMap<>();
        for (int i = 0; i < versions.size(); i++) {

            CalculableHolder<T> current = versions.get(i);
            if (!versionsBySourceSystem.containsKey(current.getSourceSystem())) {
                versionsBySourceSystem.put(current.getSourceSystem(), new ArrayList<>());
            }

            versionsBySourceSystem.get(current.getSourceSystem()).add(current);
        }

        return versionsBySourceSystem;
    }
    /**
     * Filters versions by status, last update date and source system, returning versions map.
     * @param versions the versions to filter
     * @return filtered versions map
     */
    protected Map<String, CalculableHolder<T>>
        filterVersionsBySourceSystem(List<CalculableHolder<T>> versions) {

        Map<String, CalculableHolder<T>> versionsBySourceSystem = new HashMap<>();

        // 1. Collect origins by source system, filtering out old and inactive versions
        for (int i = 0; i < versions.size(); i++) {

            CalculableHolder<T> current = versions.get(i);
            String sourceSystem = current.getSourceSystem();

            CalculableHolder<T> other = versionsBySourceSystem.get(sourceSystem);
            // 1.1 Check if the source system contains a version already
            if (other != null) {

                // 1.1.1 Replace, if the current LUD is after the previous one
                // Ugly stuff, but helps to overcome wrong Date <-> Timestamp method covariant selection UN-10553
                boolean replace = current.getLastUpdate().getTime() > other.getLastUpdate().getTime();
                if (replace) {
                    versionsBySourceSystem.put(sourceSystem, current);
                }
            } else {
                versionsBySourceSystem.put(sourceSystem, current);
            }
        }

        return versionsBySourceSystem;
    }
    /**
     *
     * @param mergeSet
     * @param includeInactive
     * @return
     */
    protected T selectVersionFromMergeSet(Map<Integer, List<CalculableHolder<T>>> mergeSet, boolean includeInactive) {

        T result = null;
        for (Entry<Integer, List<CalculableHolder<T>>> entry : mergeSet.entrySet()) {

            CalculableHolder<T> c = getLatest(entry.getValue());
            if (c.getStatus() == RecordStatus.INACTIVE) {
                return includeInactive ? c.getValue() : null;
            }
            result = c.getValue();
            break;
        }

        return result;
    }

    /**
     * Gets the youngest record available.
     * @param list the list
     * @return record
     */
    protected CalculableHolder<T> getLatest(List<CalculableHolder<T>> list) {

        CalculableHolder<T> result = null;
        for (CalculableHolder<T> c : list) {
            if (result != null) {
                // Ugly stuff, but helps to overcome wrong Date <-> Timestamp method covariant selection
                result = c.getLastUpdate().getTime() > result.getLastUpdate().getTime() ? c : result;
            } else {
                result = c;
            }
        }

        return result;
    }

    /**
     * Compose default BVR.
     * @param versions the versions
     * @param includeInactive include incative or not
     * @return selected version
     */
    protected T composeDefaultBVR(DataBvrCaclulationInfo<T> info) {

        // 1. Get versions by source systems
        Map<String, CalculableHolder<T>> versionsMap = filterVersionsBySourceSystem(info.getVersions());

        // 2. Add support for different source systems of the same weight. The younger one will be selected.
        Map<Integer, List<CalculableHolder<T>>> mergeSet = new LinkedHashMap<>(info.getVersions().size());
        if (!versionsMap.isEmpty()) {

            Map<String, Integer> sortedSourceSystems = info.getBvrMap();
            for (Entry<String, Integer> entry : sortedSourceSystems.entrySet()) {

                // 2.1 Skip merged
                CalculableHolder<T> c = versionsMap.get(entry.getKey());
                if (c == null || c.getStatus() == RecordStatus.MERGED) {
                    continue;
                }

                // 2.2 Put to merge set
                if (!mergeSet.containsKey(entry.getValue())) {
                    mergeSet.put(entry.getValue(), new ArrayList<>());
                }

                mergeSet.get(entry.getValue()).add(c);
            }
        }

        // 3. Get result
        return selectVersionFromMergeSet(mergeSet, info.includeInactive());
    }

    protected DataRecord composeDefaultBVT(DataBvtCaclulationInfo<T> info) {

        Map<Integer, Map<String, List<CalculableHolder<Attribute>>>> valuesMap
                = createValuesMap(info);

        if (valuesMap.isEmpty()) {
            return null;
        }

        DataRecord result = new SerializableDataRecord(valuesMap.size());
        setAttributes(result, valuesMap, info);

        return result;
    }

    /**
     * Sets values recursive.
     *
     * @param current
     * @param valuesMap
     */
    private void setAttributes(
            DataRecord current,
            Map<Integer, Map<String, List<CalculableHolder<Attribute>>>> valuesMap,
            DataBvtCaclulationInfo<T> info) {

        Map<String, Map<String, Integer>> bvtMap = info.getBvtMap();
        for (Entry<String, AttributeModelElement> entry : info.getAttrsMap().entrySet()) {

            // 0. First level only
            if (entry.getValue().getLevel() > 0) {
                continue;
            }

            String attrPath = entry.getKey();

            // 1. Simple or array.
            if (!entry.getValue().isComplex()) {

                Map<String, Integer> sourceSystems = bvtMap.get(attrPath);

                if (sourceSystems == null) {
                    continue;
                }

                // Source systems should be sorted in reversed order (from the greatest to the least)
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

                    Attribute result = buildAttribute(valueHolder, info.includeWinners());
                    if (value.getAttributeType() == AttributeType.CODE) {
                        collectCodeAttributeSupplementaryValues(value, result, valuesMap, attrPath);
                    }

                    if (result != null) {
                        current.addAttribute(result);
                    }
                }
            // 2. Complex
            } else {

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

                        ComplexAttribute result = (ComplexAttribute) buildAttribute(valueHolder, info.includeWinners());
                        if (result != null) {
                            current.addAttribute(result);
                        }
                    }

                    continue;
                }
            }
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
    private Map<Integer, Map<String, List<CalculableHolder<Attribute>>>>
        createValuesMap(DataBvtCaclulationInfo<T> info) {

        Map<String, List<CalculableHolder<T>>> versionsBySourceSystem
                = collectVersionsBySourceSystem(info.getVersions());

        if (versionsBySourceSystem.isEmpty()) {
            return Collections.emptyMap();
        }

        return collectAttributeValues(versionsBySourceSystem, info);
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
            Map<String, List<CalculableHolder<T>>> versionsBySourceSystem,
            DataBvtCaclulationInfo<T> info) {

        Map<Integer, Map<String, List<CalculableHolder<Attribute>>>> valuesMap
                = new HashMap<>(versionsBySourceSystem.size());

        for (Entry<String, List<CalculableHolder<T>>> e : versionsBySourceSystem.entrySet()) {
            // Expect more then one record for the same source system.
            // This can be particular the case for merged active records,
            // which all continue to contribute
            for (CalculableHolder<T> h : e.getValue()) {

                if (h.getStatus() == RecordStatus.INACTIVE && !info.includeInactive()) {
                    continue;
                }

                collectValuesRecursive(h, info, valuesMap, 0, "", (DataRecord) h.getValue());
            }
        }

        return valuesMap;
    }

    /**
     * Collects attributes values recursive.
     *
     * @param bvtMap   the BVT map
     * @param valuesMap values map
     * @param level    current nesting level
     * @param path     current path
     * @param nr       current nested attribute
     */
    private void collectValuesRecursive(
            CalculableHolder<T> source,
            DataBvtCaclulationInfo<T> info,
            Map<Integer, Map<String, List<CalculableHolder<Attribute>>>> valuesMap,
            int level, String path, DataRecord nr) {

        Map<String, Map<String, Integer>> bvtMap = info.getBvtMap();
        String attrPath;
        for (Attribute attr : nr.getAllAttributes()) {

            attrPath = getAttributePath(level, path, attr.getName());
            collectAttribute(attrPath, attr, source, bvtMap, valuesMap);

            if (attr.getAttributeType() == AttributeType.COMPLEX) {

                ComplexAttribute cAttr = (ComplexAttribute) attr;
                // UN-1632
                if (cAttr.isEmpty()) {
                    continue;
                }

                // FIXME support multiple nested records ASAP!!
                collectValuesRecursive(source, info, valuesMap, level + 1, attrPath, cAttr.get(0));
            }
        }
    }

    /**
     * Collects single attribute.
     *
     * @param path  current attribute path
     * @param attr      the attribute
     * @param source    origin source object
     * @param bvtMap    the BVT map
     * @param valuesMap values map
     */
    private void collectAttribute(String path, Attribute attr,
                                  CalculableHolder<T> source,
                                  Map<String, Map<String, Integer>> bvtMap,
                                  Map<Integer, Map<String, List<CalculableHolder<Attribute>>>> valuesMap) {

        // 1. Get weights map, defined for this attribute
        Map<String, Integer> attrWeights = bvtMap.get(path);
        if (attrWeights == null) {
            return;
        }

        // 2. Select weight for the source system of the source object
        Integer weight = attrWeights.get(source.getSourceSystem());
        if (weight == null) {
            return;
        }

        // 3. Select attributes map for this weight.
        // Create one if necessary.
        // Create attribute holder list if needed
        // Put attribute
        valuesMap
            .computeIfAbsent(weight, key -> new HashMap<>())
            .computeIfAbsent(path, key -> new ArrayList<>())
            .add(new RecordAttributeHolder(attr, path, source));
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

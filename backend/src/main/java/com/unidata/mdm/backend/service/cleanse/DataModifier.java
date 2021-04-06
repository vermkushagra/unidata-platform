package com.unidata.mdm.backend.service.cleanse;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.common.context.DQContext;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.ComplexAttribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.service.model.util.ModelUtils;

/**
 * Interface contains method for working with data. Generally any service that
 * needs data extraction can implement this interface.
 * @author ilya.bykov
 */
public interface DataModifier {

    /** The Constant EMPTY. */
    public static final String EMPTY = "";
    /** The Constant DELIMETER. */
    static final String DELIMITER = ".";

    /**
     * Find.
     *
     * @param elements
     *            the elements
     * @param record
     *            the record
     * @param entityName
     *            the entity name
     * @return the map
     */
    default Map<String, Attribute> find(Set<String> elements, DataRecord record, String entityName) {
        Map<String, Attribute> result = new HashMap<>();
        find(result, elements, record, entityName);
        return result;
    }

    /**
     * Removes the.
     *
     * @param elements
     *            the elements
     * @param record
     *            the record
     */
    default void remove(Set<String> elements, DataRecord record) {
        remove(elements, record, EMPTY);
    }

    /**
     * Removes the.
     *
     * @param elements
     *            the elements
     * @param record
     *            the record
     * @param path
     *            the path
     */
    default void remove(Set<String> elements, DataRecord record, String path) {

        record.getSimpleAttributes().removeIf(sa -> elements
                .contains(StringUtils.isEmpty(path) ? sa.getName() : String.join(DELIMITER, path, sa.getName())));
        /*
         * record.getComplexAttributes().forEach(ca -> { String elementPath =
         * StringUtils.isEmpty(path) ? ca.getName() : String.join(DELIMITER,
         * path, ca.getName()); ca.getNestedRecord().forEach(nr -> {
         * remove(elements, nr, elementPath); }); });
         */
    }

    /**
     * Replace.
     *
     * @param elements
     *            the elements
     * @param record
     *            the record
     */
    default void replace(Map<String, Attribute> elements, DQContext<DataRecord> ctx) {
        replace(elements, ctx, EMPTY);
    }

    /**
     * Replace.
     *
     * @param elements
     *            the elements
     * @param record
     *            the record
     * @param path
     *            the path
     */
    default void replace(Map<String, Attribute> elements, DQContext<DataRecord> ctx, String path) {

        for (Entry<String, Attribute> entry : elements.entrySet()) {

            boolean isOutput = entry.getValue() instanceof DQAttributeWrapper && ((DQAttributeWrapper) entry.getValue()).isOutputPort();
            if (!isOutput) {
                continue;
            }

            Attribute attribute = ((DQAttributeWrapper) entry.getValue()).getPure();
            
            Collection<Attribute> hits = ctx.getRecord().getAttributeRecursive(entry.getKey());

            // Set. All hits are set right now in case of compund path. TODO: rewrite to DQFrame!
            for (Attribute existing : hits) {
                ctx.setModified(true);
                set(attribute, existing);
            }

            boolean create = hits.isEmpty();
            if (create) {
                List<DataRecord> targets = Collections.singletonList(ctx.getRecord());
                if (ModelUtils.isCompoundPath(entry.getKey())) {
                    Collection<ComplexAttribute> holders
                        = ctx.getRecord().getComplexAttributeRecursive(
                            ModelUtils.stripAttributePath(ModelUtils.getAttributeLevel(entry.getKey()) - 1, entry.getKey()));
                    targets = holders.stream().flatMap(ca -> ca.getRecords().stream()).collect(Collectors.toList());
                }

                for (DataRecord target : targets) {
                    ctx.setModified(true);
                    target.addAttribute(attribute);
                }
            }
        }
    }

    default void set(Attribute from, Attribute to) {
        switch (from.getAttributeType()) {
        case ARRAY:
            ((ArrayAttribute<?>) to).castValue(((ArrayAttribute<?>) from).toArray());
            break;
        case SIMPLE:
            ((SimpleAttribute<?>) to).castValue(((SimpleAttribute<?>) from).getValue());
            break;
        default:
            break;
        }
    }

    /**
     * Find.
     *
     * @param result
     *            the result
     * @param elements
     *            the elements
     * @param record
     *            the record
     * @param path
     *            the path
     */
    default void find(Map<String, Attribute> result, Set<String> elements, DataRecord record, String path) {

        for (String attrPath : elements) {
            Collection<Attribute> attrs = record.getAttributeRecursive(attrPath);
            if (!CollectionUtils.isEmpty(attrs)) {
                result.put(attrPath, attrs.iterator().next());
            }
        }
    }

    /**
     * Copy nested.
     *
     * @param toCopy
     *            the to copy
     * @return the nested record
     */
    default DataRecord copyRecord(DataRecord toCopy) {
        return SerializableDataRecord.of(toCopy);
    }
}

package org.unidata.mdm.search.type.indexing;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

/**
 * @author Mikhail Mikhailov on Oct 10, 2019
 * The record (fields container) interface.
 */
public interface IndexingRecord {
    /**
     * @return the fields
     */
    List<IndexingField> getFields();
    /**
     * Checks this record for being empty.
     * @return true, if empty, false otherwise
     */
    default boolean isEmpty() {
        return CollectionUtils.isEmpty(getFields());
    }
}

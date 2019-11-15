package org.unidata.mdm.data.service;

import org.unidata.mdm.data.type.apply.RecordDeleteChangeSet;
import org.unidata.mdm.data.type.apply.RecordMergeChangeSet;
import org.unidata.mdm.data.type.apply.RecordUpsertChangeSet;

public interface RecordChangeSetProcessor {
    /**
     * Applies upsert change set to DB and index.
     * @param set the set to apply
     */
    void apply(RecordUpsertChangeSet set);
    /**
     * Applies delete change set to DB and index.
     * @param set the set to apply
     */
    void apply(RecordDeleteChangeSet set);
    /**
     * Applies merge change set to DB and index.
     * @param set the set to apply
     */
    void apply(RecordMergeChangeSet set);
}
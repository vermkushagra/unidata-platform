package org.unidata.mdm.data.service;

import org.unidata.mdm.data.type.apply.RelationDeleteChangeSet;
import org.unidata.mdm.data.type.apply.RelationMergeChangeSet;
import org.unidata.mdm.data.type.apply.RelationUpsertChangeSet;
/**
 * @author Mikhail Mikhailov
 * Basic relation change set processor.
 */
public interface RelationChangeSetProcessor {
    /**
     * Applies upsert change set to DB and index.
     * @param set the set to apply
     */
    void apply(RelationUpsertChangeSet set);
    /**
     * Applies delete change set to DB and index.
     * @param set the set to apply
     */
    void apply(RelationDeleteChangeSet set);
    /**
     * Applies merge change set to DB and index.
     * @param set the set to apply
     */
    void apply(RelationMergeChangeSet set);
}
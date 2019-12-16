package org.unidata.mdm.data.type.apply.batch.impl;

import org.unidata.mdm.data.context.MergeRequestContext;
import org.unidata.mdm.data.type.apply.RelationMergeChangeSet;
import org.unidata.mdm.system.type.pipeline.VoidPipelineOutput;

/**
 * @author Mikhail Mikhailov
 * Merge set.
 */
public class RelationMergeBatchSet extends RelationMergeChangeSet {
    /**
     * Constructor.
     * @param accumulator
     */
    public RelationMergeBatchSet(AbstractRelationBatchSetAccumulator<MergeRequestContext, VoidPipelineOutput> accumulator) {
        super();
    }
}

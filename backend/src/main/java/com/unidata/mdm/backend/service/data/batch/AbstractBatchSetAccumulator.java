package com.unidata.mdm.backend.service.data.batch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.context.CommonRequestContext;

/**
 * @author Mikhail Mikhailov
 * Basic accumulator.
 */
public abstract class AbstractBatchSetAccumulator<T extends CommonRequestContext>
    implements BatchSetAccumulator<T> {
    /**
     * Batch operations targets (table names).
     */
    protected final Map<BatchTarget, String> targets;
    /**
     * The working copy.
     */
    protected final List<T> workingCopy;
    /**
     * Size of the whole set, being imported.
     */
    protected BatchSetSize batchSetSize;
    /**
     * Supported iteration types.
     */
    protected Collection<BatchSetIterationType> supportedTypes;
    /**
     * Constructor.
     * @param commitSize commit size
     * @param targets target tables
     */
    protected AbstractBatchSetAccumulator(int commitSize, Map<BatchTarget, String> targets) {
        super();
        this.targets = targets;
        this.workingCopy = new ArrayList<>(commitSize);
        this.supportedTypes = Collections.emptyList();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void charge(Collection<T> charge) {
        workingCopy.addAll(charge);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void discharge() {
        workingCopy.clear();
        supportedTypes = Collections.emptyList();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> workingCopy() {
        return workingCopy;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public BatchSetSize getBatchSetSize() {
        return batchSetSize;
    }

    /**
     * @param batchSetSize the batchSetSize to set
     */
    public void setBatchSetSize(BatchSetSize batchSetSize) {
        this.batchSetSize = batchSetSize;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<BatchSetIterationType> getSupportedIterationTypes() {
        return supportedTypes;
    }
    /**
     * Sets iteration types
     * @param types supported types
     */
    public void setSupportedIterationTypes(Collection<BatchSetIterationType> types) {
        this.supportedTypes = types;
    }
    /**
     * @return the targets
     */
    @Override
    public Map<BatchTarget, String> getTargets() {
        return targets;
    }
}

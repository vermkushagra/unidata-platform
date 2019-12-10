package org.unidata.mdm.data.type.apply.batch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.unidata.mdm.system.context.CommonRequestContext;
import org.unidata.mdm.system.type.pipeline.Pipeline;

/**
 * @author Mikhail Mikhailov
 * Basic accumulator.
 */
public abstract class AbstractBatchSetAccumulator<T extends CommonRequestContext>
    implements BatchSetAccumulator<T> {
    /**
     * The working copy.
     */
    protected final List<T> workingCopy;
    /**
     * Size of the whole set, being imported.
     */
    protected BatchSetSize batchSetSize;
    /**
     * Aborting on failure execution policy.
     */
    protected boolean abortOnFailure = false;
    /**
     * Initial size of the commit interval.
     */
    protected int commitSize;
    /**
     * Pipeline.
     */
    protected Pipeline pipeline;
    /**
     * Constructor.
     * @param commitSize commit size
     */
    protected AbstractBatchSetAccumulator(int commitSize) {
        super();
        this.workingCopy = new ArrayList<>(commitSize);
        this.commitSize = commitSize;
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

    @Override
    public boolean isAbortOnFailure() {
        return abortOnFailure;
    }

    public void setAbortOnFailure(boolean abortOnFailure) {
        this.abortOnFailure = abortOnFailure;
    }
    /**
     * Sets the pipeline, which may be used for set execution.
     * @param p the pipeline
     */
    public void setPipeline(Pipeline p) {
        this.pipeline = p;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Pipeline pipeline() {
        return pipeline;
    }
}

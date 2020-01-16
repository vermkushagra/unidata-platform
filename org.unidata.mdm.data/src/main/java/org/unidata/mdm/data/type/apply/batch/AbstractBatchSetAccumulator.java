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

package org.unidata.mdm.data.type.apply.batch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.unidata.mdm.system.context.CommonRequestContext;
import org.unidata.mdm.system.type.batch.BatchSetAccumulator;
import org.unidata.mdm.system.type.batch.BatchSetSize;
import org.unidata.mdm.system.type.pipeline.Pipeline;
import org.unidata.mdm.system.type.pipeline.PipelineOutput;

/**
 * @author Mikhail Mikhailov
 * Basic accumulator.
 */
public abstract class AbstractBatchSetAccumulator<T extends CommonRequestContext, O extends PipelineOutput>
    implements BatchSetAccumulator<T, O> {
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

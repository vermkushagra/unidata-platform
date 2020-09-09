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

    private boolean abortOnFailure = false;

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

    @Override
    public boolean isAbortOnFailure() {
        return abortOnFailure;
    }

    public void setAbortOnFailure(boolean abortOnFailure) {
        this.abortOnFailure = abortOnFailure;
    }
}

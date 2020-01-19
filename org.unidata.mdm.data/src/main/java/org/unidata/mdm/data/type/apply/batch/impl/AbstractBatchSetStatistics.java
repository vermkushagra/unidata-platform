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

package org.unidata.mdm.data.type.apply.batch.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.unidata.mdm.system.dto.ExecutionResult;
import org.unidata.mdm.system.type.batch.BatchSetStatistics;

/**
 * @author Mikhail Mikhailov on Dec 13, 2019
 */
public abstract class AbstractBatchSetStatistics<T extends ExecutionResult> implements BatchSetStatistics<T> {
    /**
     * Updated count. Delete period writes to this variable.
     */
    protected long updated = 0L;
    /**
     * General operation failure count.
     */
    protected long failed = 0L;
    /**
     * Skipped due to NO_ACTION or the like.
     */
    protected long skipped = 0L;
    /**
     * Collect full blown output results.
     */
    protected boolean collectResults;
    /**
     * The result.
     */
    protected List<T> results;
    /**
     * Constructor.
     */
    protected AbstractBatchSetStatistics() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        this.updated = 0L;
        this.failed = 0L;
        this.skipped = 0L;
        if (Objects.nonNull(this.results)) {
            this.results.clear();
        }
    }
    /**
     * @param failed the failed to add
     */
    public void incrementFailed(long failed) {
        this.failed += failed;
    }
    /**
     * @param skipped the skipped to add
     */
    public void incrementSkipped(long skipped) {
        this.skipped += skipped;
    }
    /**
     * @param updated the updated to add
     */
    public void incrementUpdated(long updated) {
        this.updated += updated;
    }
    /**
     * @param failed the failed to add
     */
    public void incrementFailed() {
        incrementFailed(1L);
    }
    /**
     * @param skipped the skipped to add
     */
    public void incrementSkipped() {
        incrementSkipped(1L);
    }
    /**
     * @param updated the updated to add
     */
    public void incrementUpdated() {
        incrementUpdated(1L);
    }
    /**
     * @return the failed
     */
    public long getFailed() {
        return failed;
    }
    /**
     * @return the skipped
     */
    public long getSkipped() {
        return skipped;
    }
    /**
     * @return the updated
     */
    public long getUpdated() {
        return updated;
    }
    /**
     * @return the collectOutput
     */
    @Override
    public boolean collectResults() {
        return collectResults;
    }
    /**
     * @param collectOutput the collectOutput to set
     */
    @Override
    public void collectResults(boolean collectOutput) {
        this.collectResults = collectOutput;
    }
    /**
     * Adds result to this stat object
     * @param dto
     */
    public void addResult(T dto) {

        if (!collectResults()) {
            return;
        }

        if (Objects.isNull(this.results)) {
            this.results = new ArrayList<>();
        }

        this.results.add(dto);
    }
    /**
     * @return the results
     */
    @Override
    public List<T> getResults() {
        return Objects.isNull(results) ? Collections.emptyList() : results;
    }
}

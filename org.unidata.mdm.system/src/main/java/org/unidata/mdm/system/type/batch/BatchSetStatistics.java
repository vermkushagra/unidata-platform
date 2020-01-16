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

package org.unidata.mdm.system.type.batch;

import java.util.List;

import org.unidata.mdm.system.type.pipeline.PipelineOutput;

/**
 * Batch statistics base.
 * @author Mikhail Mikhailov on Dec 13, 2019
 */
public interface BatchSetStatistics<T extends PipelineOutput> {
    /**
     * Clear state.
     */
    void reset();
    /**
     * Returns the result collecting state.
     * @return true, if currently set to collect results
     */
    boolean collectResults();
    /**
     * Sets this statistic collector to collect (or not) execution results.
     * @param collectOutput the state to set
     */
    void collectResults(boolean collectOutput);
    /**
     * Gets the execution results, if they are set to be collected.
     * @return results or empty list
     */
    List<T> getResults();
}

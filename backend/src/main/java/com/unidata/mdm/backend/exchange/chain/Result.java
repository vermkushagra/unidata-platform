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

package com.unidata.mdm.backend.exchange.chain;

/**
 * Class responsible for collecting processing results.
 */
public class Result {

    private String entityName;
    private int total;
    private int processed;
    private int failed;
    private int reject;

    public Result() {
    }

    /**
     * @return total input items
     */
    public int getTotal() {
        return total;
    }

    public Result setTotal(int total) {
        this.total = total;
        return this;
    }

    /**
     * @return total processed items
     */
    public int getProcessed() {
        return processed;
    }

    public Result setProcessed(int processed) {
        this.processed = processed;
        return this;
    }

    /**
     * @return total failed items
     */
    public int getFailed() {
        return failed;
    }

    public Result setFailed(int failed) {
        this.failed = failed;
        return this;
    }

    /**
     * @return total reject items
     */
    public int getReject() {
        return reject;
    }

    public Result setReject(int reject) {
        this.reject = reject;
        return this;
    }

    /**
     * @return entity name of items
     */
    public String getEntityName() {
        return entityName;
    }

    public Result setEntityName(String entityName) {
        this.entityName = entityName;
        return this;
    }

    public void addTotal(int addition) {
        total += addition;
    }

    public void addRejected(int addition) {
        reject += addition;
    }

    public void addFailed(int addition) {
        failed += addition;
    }

    public void addProcessed(int addition) {
        processed += addition;
    }

    @Override
    public String toString() {
        return "Result{" +
                "total=" + total +
                ", processed=" + processed +
                ", failed=" + failed +
                ", reject=" + reject +
                '}';
    }
}

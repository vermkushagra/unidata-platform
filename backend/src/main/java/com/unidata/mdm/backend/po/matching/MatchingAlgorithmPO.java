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

package com.unidata.mdm.backend.po.matching;
/**
 * Matching algorithm table.
 */
public class MatchingAlgorithmPO {
    /**
     * Id field.
     */
    public static final String FIELD_ID = "id";
    /**
     * Algorithm id field.
     */
    public static final String FIELD_ALGORITHM_ID = "algorithm_id";
    /**
     * Rule id field.
     */
    public static final String FIELD_RULE_ID = "rule_id";
    /**
     * Data field.
     */
    public static final String FIELD_DATA = "data";
    /**
     * Rule id.
     */
    private int ruleId;
    /**
     * Algorithm id.
     */
    private int algorithmId;
    /**
     * Data.
     */
    private String data;

    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }

    public int getAlgorithmId() {
        return algorithmId;
    }

    public void setAlgorithmId(int algorithmId) {
        this.algorithmId = algorithmId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

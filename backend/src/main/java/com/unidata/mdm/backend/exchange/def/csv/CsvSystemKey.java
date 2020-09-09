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

package com.unidata.mdm.backend.exchange.def.csv;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unidata.mdm.backend.exchange.def.SystemKey;

/**
 * @author Mikhail Mikhailov
 * UD etalon id field.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CsvSystemKey extends SystemKey {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 6816590112282793103L;
    /**
     * Indices for the external ID (normally single index).
     */
    private List<Integer> indices;
    /**
     * Optional join with element.
     */
    private String joinWith;
    /**
     * Constructor.
     */
    public CsvSystemKey() {
        super();
    }

    /**
     * @return the indices
     */
    public List<Integer> getIndices() {
        return indices;
    }


    /**
     * @param indices the indices to set
     */
    public void setIndices(List<Integer> indices) {
        this.indices = indices;
    }


    /**
     * @return the joinWith
     */
    public String getJoinWith() {
        return joinWith;
    }


    /**
     * @param joinWith the joinWith to set
     */
    public void setJoinWith(String joinWith) {
        this.joinWith = joinWith;
    }

}

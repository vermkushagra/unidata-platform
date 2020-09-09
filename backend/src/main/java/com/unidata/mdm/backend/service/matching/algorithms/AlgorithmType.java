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

package com.unidata.mdm.backend.service.matching.algorithms;

/**
 * @author Mikhail Mikhailov
 * Type of the algorithm.
 * The type prescribes, which field is used for indexing and also for other things.
 */
public enum AlgorithmType {
    /**
     * Exact with no variations.
     */
    EXACT_STRICT_MATCH(1),
    /**
     * Exact with null, matching everything.
     */
    EXACT_NULL_MATCH_EVERYTHING(2),
    /**
     * Exact with null, matching nothing.
     */
    EXACT_NULL_MATCH_NOTHING(3),
    /**
     * Inexact (normalized, with fixed length).
     */
    INEXACT_NORMALIZED_LENGTH(4),
    /**
     * Excluded value match nothing.
     */
    EXACT_EXCLUDED_VALUE_MATCH_NOTHING(5),
    /**
     * Excluded value match nothing.
     */
    FUZZY(6);
    /**
     * Constructor.
     * @param id algorithm id.
     */
    private AlgorithmType(int id) {
        this.id = id;
    }
    /**
     * Algorithm id.
     */
    private final int id;
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
}

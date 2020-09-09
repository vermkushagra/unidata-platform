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

package com.unidata.mdm.backend.service.data.util;

import java.util.Collection;
import java.util.Objects;

import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.ComplexAttribute;
import com.unidata.mdm.backend.common.types.DataRecord;

/**
 * @author Mikhail Mikhailov
 * Calculates sub-tree hashes.
 */
public final class RecursiveHashCalculator {

    /**
     * Constructor.
     */
    private RecursiveHashCalculator() {
        super();
    }

    /**
     * Traverses a nested record and collectes hash codes.
     * @param record the record
     * @return sum of hash codes
     */
    public static long traverse(DataRecord record) {

        long result = 0L;
        if (Objects.nonNull(record)) {
            Collection<Attribute> attrs = record.getAllAttributes();
            for (Attribute attr : attrs) {
                switch (attr.getAttributeType()) {
                    case SIMPLE:
                    case ARRAY:
                        result += attr.hashCode();
                        break;
                    case COMPLEX:
                        result += traverse((ComplexAttribute) attr);
                        break;
                    case CODE:
                        // TODO add code
                        break;
                }
            }
        }

        return result;
    }

    /**
     * Traverses hierarchy of a complex attribute and collects hash codes.
     * @param attribute the attribute
     * @return sum of hash codes
     */
    public static long traverse(ComplexAttribute attribute) {

        long result = 0L;
        for (int i = 0;
             attribute != null && !attribute.isEmpty() && i < attribute.size();
             i++) {
            DataRecord record = attribute.get(i);
            result += traverse(record);
        }

        return result;
    }
}

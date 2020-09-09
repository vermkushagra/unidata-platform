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

package com.unidata.mdm.backend.service.classifier.po;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

/**
 * The Class ClsfNodeArrayAttrPO.
 */
public class ClsfNodeArrayAttrPO extends ClsfNodeAttrPO {

    /** The values. */
    private  final List<String> values = new ArrayList<>();

    /**
     * The Enum FieldColumns.
     */
    public enum FieldColumns {

        /** The default value. */
        VALUES
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(final Collection<String> values) {
        this.values.clear();
        if (CollectionUtils.isNotEmpty(values)) {
            this.values.addAll(values);
        }
    }

    public void addValue(final String value) {
        values.add(value);
    }
}

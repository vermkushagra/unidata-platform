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

package org.unidata.mdm.data.type.keys;

import java.io.Serializable;

import org.unidata.mdm.core.type.keys.EtalonKey;

/**
 * Etalon record id.
 */
public class RecordEtalonKey extends EtalonKey implements Serializable {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -6495921977514956243L;
    /**
     * Constructor.
     * @param b builder.
     */
    private RecordEtalonKey(RecordEtalonKeyBuilder b) {
        super(b);
    }
    /**
     * Builder.
     * @return
     */
    public static RecordEtalonKeyBuilder builder() {
        return new RecordEtalonKeyBuilder();
    }
    /**
     * Copy.
     * @return
     */
    public static RecordEtalonKeyBuilder builder(RecordEtalonKey other) {
        return new RecordEtalonKeyBuilder(other);
    }
    /**
     * @author Mikhail Mikhailov
     * Builder class.
     */
    public static class RecordEtalonKeyBuilder extends EtalonKeyBuilder<RecordEtalonKeyBuilder> {
        /**
         * Constructor.
         */
        private RecordEtalonKeyBuilder() {
            super();
        }
        /**
         * Copy constructor.
         * @param other
         */
        private RecordEtalonKeyBuilder(RecordEtalonKey other) {
            super(other);
        }
        /**
         * Build.
         * @return key
         */
        @Override
        public RecordEtalonKey build() {
            return new RecordEtalonKey(this);
        }
    }
}

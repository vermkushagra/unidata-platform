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
 * @author Mikhail Mikhailov
 * Relation etalon key.
 */
public class RelationEtalonKey extends EtalonKey implements Serializable {
    /**
     * GSVUID.
     */
    private static final long serialVersionUID = -634317613499579406L;
    /**
     * From etalon key.
     */
    private final RecordEtalonKey from;
    /**
     * To etalon key.
     */
    private final RecordEtalonKey to;
    /**
     * Constructor.
     */
    private RelationEtalonKey(RelationEtalonKeyBuilder b) {
        super(b);
        this.from = b.from;
        this.to = b.to;
    }
    /**
     * @return the from
     */
    public RecordEtalonKey getFrom() {
        return from;
    }
    /**
     * @return the to
     */
    public RecordEtalonKey getTo() {
        return to;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPending() {
        return super.isPending() || (from != null && from.isPending());
    }
    /**
     * Builder.
     * @return
     */
    public static RelationEtalonKeyBuilder builder() {
        return new RelationEtalonKeyBuilder();
    }
    /**
     * Copy.
     * @return
     */
    public static RelationEtalonKeyBuilder builder(RelationEtalonKey other) {
        return new RelationEtalonKeyBuilder(other);
    }
    /**
     * The builder class
     * @author Mikhail Mikhailov
     */
    public static class RelationEtalonKeyBuilder extends EtalonKeyBuilder<RelationEtalonKeyBuilder> {
        /**
         * From etalon key.
         */
        private RecordEtalonKey from;
        /**
         * To etalon key.
         */
        private RecordEtalonKey to;
        /**
         * Constructor.
         */
        private RelationEtalonKeyBuilder() {
            super();
        }
        /**
         * Copy constructor.
         * @param other
         */
        private RelationEtalonKeyBuilder(RelationEtalonKey other) {
            super(other);
            this.from = other.from;
            this.to = other.to;
        }
        /**
         * @param from the from to set
         */
        public RelationEtalonKeyBuilder from(RecordEtalonKey from) {
            this.from = from;
            return self();
        }
        /**
         * @param to the to to set
         */
        public RelationEtalonKeyBuilder to(RecordEtalonKey to) {
            this.to = to;
            return self();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public RelationEtalonKey build() {
            return new RelationEtalonKey(this);
        }
    }
}

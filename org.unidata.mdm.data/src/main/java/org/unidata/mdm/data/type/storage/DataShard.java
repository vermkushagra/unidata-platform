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

package org.unidata.mdm.data.type.storage;

/**
 * @author Mikhail Mikhailov
 * Partition / Shard.
 */
public final class DataShard {
    /**
     * Shard number.
     */
    private final int number;
    /**
     * Primary node.
     */
    private final DataNode primary;
    /**
     * Constructor.
     */
    private DataShard(DataShardBuilder b) {
        super();
        this.number = b.number;
        this.primary = b.primary;
    }
    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }
    /**
     * @return the primary
     */
    public DataNode getPrimary() {
        return primary;
    }
    /**
     * Builder instance
     * @return builder
     */
    public static DataShardBuilder builder() {
        return new DataShardBuilder();
    }
    public static class DataShardBuilder {
        /**
         * Shard number.
         */
        private int number;
        /**
         * Primary node.
         */
        private DataNode primary;
        /**
         * Constructor.
         */
        private DataShardBuilder() {
            super();
        }
        /**
         * @param number the number to set
         */
        public DataShardBuilder number(int number) {
            this.number = number;
            return this;
        }
        /**
         * @param primary the primary to set
         */
        public DataShardBuilder primary(DataNode primary) {
            this.primary = primary;
            return this;
        }
        /**
         * Builder func.
         * @return instance
         */
        public DataShard build() {
            return new DataShard(this);
        }
    }
}

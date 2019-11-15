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

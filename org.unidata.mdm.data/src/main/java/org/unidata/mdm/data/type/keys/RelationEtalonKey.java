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

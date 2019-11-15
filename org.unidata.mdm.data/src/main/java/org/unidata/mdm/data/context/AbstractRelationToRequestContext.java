package org.unidata.mdm.data.context;

import org.unidata.mdm.core.type.keys.LSN;

/**
 * @author Mikhail Mikhailov
 * REL to base context.
 */
public abstract class AbstractRelationToRequestContext
    extends AbstractRecordIdentityContext implements RelationIdentityContext {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 7823433615112393227L;
    /**
     * Ealon key.
     */
    protected final String relationEtalonKey;
    /**
     * Origin key.
     */
    protected final String relationOriginKey;
    /**
     * Local (to partition) sequence number.
     */
    protected final LSN relationLsn;
    /**
     * Constructor.
     * @param parentContext the parent context. May be null.
     */
    public AbstractRelationToRequestContext(AbstractRelationToRequestContextBuilder<?> b) {
        super(b);
        this.relationEtalonKey = b.relationEtalonKey;
        this.relationOriginKey = b.relationOriginKey;
        this.relationLsn = b.relationLsn != null && b.relationShard != null
                ? LSN.of(b.relationShard, b.relationLsn)
                : null;
    }
    /**
     * @return the goldenKey
     */
    @Override
    public String getRelationEtalonKey() {
        return relationEtalonKey;
    }
    /**
     * @return the relationOriginKey
     */
    @Override
    public String getRelationOriginKey() {
        return relationOriginKey;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Long getRelationLsn() {
        return relationLsn != null ? relationLsn.getLsn() : null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getRelationShard() {
        return relationLsn != null ? relationLsn.getShard() : null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public LSN getRelationLsnAsObject() {
        return relationLsn;
    }
    /**
     * Builder.
     * @author Mikhail Mikhailov
     *
     * @param <X>
     */
    public abstract static class AbstractRelationToRequestContextBuilder<X extends AbstractRelationToRequestContextBuilder<X>>
        extends AbstractRecordIdentityContextBuilder<X> {
        /**
         * Shard number.
         */
        protected Integer relationShard;
        /**
         * Global sequence number.
         */
        protected Long relationLsn;
        /**
         * Golden key.
         */
        protected String relationEtalonKey;
        /**
         * Origin key.
         */
        protected String relationOriginKey;
        /**
         * Constructor.
         */
        protected AbstractRelationToRequestContextBuilder() {
            super();
        }
        /**
         * Copy constructor.
         * @param other
         */
        protected AbstractRelationToRequestContextBuilder(AbstractRelationToRequestContext other) {
            super(other);
            this.relationEtalonKey = other.relationEtalonKey;
            this.relationOriginKey = other.relationOriginKey;
            this.relationLsn = other.relationLsn != null ? other.relationLsn.getLsn() : null;
            this.relationShard = other.relationLsn != null ? other.relationLsn.getShard() : null;

        }
        /**
         * @param relationEtalonKey the etalon key to set
         */
        public X relationEtalonKey(String relationEtalonKey) {
            this.relationEtalonKey = relationEtalonKey;
            return self();
        }
        /**
         * @param gsn the gsn to set
         */
        public X relationLsn(LSN lsn) {
            this.relationLsn = lsn != null ? lsn.getLsn() : null;
            this.relationShard = lsn != null ? lsn.getShard() : null;
            return self();
        }
        /**
         * @param lsn the lsn to set
         */
        public X relationLsn(Long lsn) {
            this.relationLsn = lsn;
            return self();
        }
        /**
         * @param shard the shard to set
         */
        public X relationShard(Integer shard) {
            this.relationShard = shard;
            return self();
        }
        /**
         * @param relationOriginKey the origin key to set
         */
        public X relationOriginKey(String relationOriginKey) {
            this.relationOriginKey = relationOriginKey;
            return self();
        }
    }
}

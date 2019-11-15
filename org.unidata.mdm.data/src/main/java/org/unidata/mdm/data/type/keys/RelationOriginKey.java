package org.unidata.mdm.data.type.keys;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.unidata.mdm.core.type.keys.OriginKey;

/**
 * @author Mikhail Mikhailov
 * The relation origin key.
 */
public class RelationOriginKey extends OriginKey implements Serializable {
    /**
     * GSVUID.
     */
    private static final long serialVersionUID = 1100366431373688112L;
    /**
     * The from side origin key.
     */
    private final RecordOriginKey from;
    /**
     * The to side origin key.
     */
    private final RecordOriginKey to;
    /**
     * Saved box key.
     */
    private final String boxKey;
    /**
     * Constructor.
     * @param b the key builder
     */
    public RelationOriginKey(RelationOriginKeyBuilder b) {
        super(b);
        this.from = b.from;
        this.to = b.to;
        this.boxKey = StringUtils.join(getFrom() != null ? getFrom().getExternalId() : "", "|", getSourceSystem(), "|", getTo().getExternalId());
    }
    /**
     * @return the from
     */
    public RecordOriginKey getFrom() {
        return from;
    }
    /**
     * @return the to
     */
    public RecordOriginKey getTo() {
        return to;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String toBoxKey() {
        return boxKey;
    }
    /**
     * Creates new builder object.
     * @param other object to copy
     * @return builder
     */
    public static RelationOriginKeyBuilder builder(RelationOriginKey other) {
        return new RelationOriginKeyBuilder(other);
    }
    /**
     * Creates new builder object.
     * @return builder
     */
    public static RelationOriginKeyBuilder builder() {
        return new RelationOriginKeyBuilder();
    }
    /**
     * The rel origin key builder.
     * @author Mikhail Mikhailov
     */
    public static class RelationOriginKeyBuilder extends OriginKeyBuilder<RelationOriginKeyBuilder> {
        /**
         * The from side origin key.
         */
        private RecordOriginKey from;
        /**
         * The to side origin key.
         */
        private RecordOriginKey to;
        /**
         * Constructor.
         */
        private RelationOriginKeyBuilder() {
            super();
        }
        /**
         * Constructor.
         * @param other the key to copy.
         */
        private RelationOriginKeyBuilder(RelationOriginKey other) {
            super(other);
            this.from = other.from;
            this.to = other.to;
        }
        /**
         * @param from the from to set
         */
        public RelationOriginKeyBuilder from(RecordOriginKey from) {
            this.from = from;
            return self();
        }
        /**
         * @param to the to to set
         */
        public RelationOriginKeyBuilder to(RecordOriginKey to) {
            this.to = to;
            return self();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public RelationOriginKey build() {
            return new RelationOriginKey(this);
        }
    }
}

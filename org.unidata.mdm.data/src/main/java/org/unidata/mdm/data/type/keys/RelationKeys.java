/**
 *
 */
package org.unidata.mdm.data.type.keys;

import java.io.Serializable;

import org.unidata.mdm.core.type.keys.Keys;
import org.unidata.mdm.data.type.data.RelationType;

/**
 * @author Mikhail Mikhailov
 * Relation keys.
 */
public class RelationKeys extends Keys<RelationEtalonKey, RelationOriginKey> implements Serializable {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -2872129636945155801L;
    /**
     * Relation name.
     */
    private final String relationName;
    /**
     * Rel type.
     */
    private final RelationType relationType;
    /**
     * From entity name.
     */
    private final String fromEntityName;
    /**
     * To side entity name.
     */
    private final String toEntityName;
    /**
     * Constructor.
     * @param b the builder
     */
    private RelationKeys(RelationKeysBuilder b) {
        super(b);
        this.fromEntityName = b.fromEntityName;
        this.toEntityName = b.toEntityName;
        this.relationName = b.relationName;
        this.relationType = b.relationType;
    }
    /**
     * @return the fromEntityName
     */
    public String getFromEntityName() {
        return fromEntityName;
    }
    /**
     * Returns 'from' etalon + origin as {@link RecordKeys} for the callers, that require it.
     * @return referenced record keys as {@link RecordKeys}
     */
    public RecordKeys getFromAsRecordKeys() {
        return RecordKeys.builder()
                .entityName(fromEntityName)
                .etalonKey(etalonKey.getFrom())
                .originKey(originKey.getFrom())
                .build();
    }
    /**
     * Returns 'to' etalon + origin as {@link RecordKeys} for the callers, that require it.
     * @return referenced record keys as {@link RecordKeys}
     */
    public RecordKeys getToAsRecordKeys() {
        return RecordKeys.builder()
                .entityName(toEntityName)
                .etalonKey(etalonKey.getTo())
                .originKey(originKey.getTo())
                .build();
    }
    /**
     * @return the toEntityName
     */
    public String getToEntityName() {
        return toEntityName;
    }
    /**
     * @return the realtionName
     */
    public String getRelationName() {
        return relationName;
    }

    /**
     * @return the relationType
     */
    public RelationType getRelationType() {
        return relationType;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public KeysType getType() {
        return KeysType.RELATION_KEYS;
    }
    /**
     * Creates a new builder instance.
     * @return new builder instance
     */
    public static RelationKeysBuilder builder() {
        return new RelationKeysBuilder();
    }
    /**
     * Creates a new builder instance.
     * @return new builder instance
     */
    public static RelationKeysBuilder builder(RelationKeys keys) {
        return new RelationKeysBuilder(keys);
    }
    /**
     * @author Mikhail Mikhailov
     * Relations keys builder class.
     */
    public static class RelationKeysBuilder extends KeysBuilder<RelationKeysBuilder, RelationEtalonKey, RelationOriginKey> {
        /**
         * Relation name.
         */
        private String relationName;
        /**
         * Rel type.
         */
        private RelationType relationType;
        /**
         * From entity name.
         */
        private String fromEntityName;
        /**
         * To side entity name.
         */
        private String toEntityName;
        /**
         * Constructor.
         */
        private RelationKeysBuilder() {
            super();
        }
        /**
         * Constructor.
         * @param keys the keys to copy
         */
        private RelationKeysBuilder(RelationKeys keys) {
            super(keys);
            this.relationName = keys.relationName;
            this.relationType = keys.relationType;
            this.fromEntityName = keys.fromEntityName;
            this.toEntityName = keys.toEntityName;
        }
        /**
         * @param relationName the relationName to set
         */
        public RelationKeysBuilder relationName(String relationName) {
            this.relationName = relationName;
            return this;
        }
        /**
         * @param relationType the relationType to set
         */
        public RelationKeysBuilder relationType(RelationType relationType) {
            this.relationType = relationType;
            return this;
        }
        /**
         * @param fromEntityName the fromEntityName to set
         */
        public RelationKeysBuilder fromEntityName(String fromEntityName) {
            this.fromEntityName = fromEntityName;
            return this;
        }
        /**
         * @param toEntityName the toEntityName to set
         */
        public RelationKeysBuilder toEntityName(String toEntityName) {
            this.toEntityName = toEntityName;
            return this;
        }
        /**
         * New relation keys instance.
         * @return keys
         */
        @Override
        public RelationKeys build() {
            return new RelationKeys(this);
        }
    }
}

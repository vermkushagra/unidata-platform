package org.unidata.mdm.data.po.keys;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.unidata.mdm.data.type.data.RelationType;

/**
 * @author Mikhail Mikhailov
 * Relation keys. Difference to base type:
 * type relation_type,
 * from_key record_key,
 * to_key record_key,
 * origin_keys relation_origin_key[]
 */
public class RelationKeysPO extends AbstractKeysPO {
    /**
     * Relation type.
     */
    public static final String FIELD_TYPE = "type";
    /**
     * From key.
     */
    public static final String FIELD_FROM_KEY = "from_key";
    /**
     * tO key.
     */
    public static final String FIELD_TO_KEY = "to_key";
    /**
     * Origin keys array.
     */
    public static final String FIELD_ORIGIN_KEYS = "origin_keys";
    /**
     * Relatype.
     */
    private RelationType relationType;
    /**
     * Complete from keys.
     */
    private RecordKeysPO fromKeys;
    /**
     * Complete to keys.
     */
    private RecordKeysPO toKeys;
    /**
     * Collection of origin keys.
     */
    private List<RelationOriginKeyPO> originKeys;
    /**
     * Constructor.
     */
    public RelationKeysPO() {
        super();
    }
    /**
     * @return the relationType
     */
    public RelationType getRelationType() {
        return relationType;
    }
    /**
     * @param relationType the relationType to set
     */
    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }
    /**
     * @return the fromKeys
     */
    public RecordKeysPO getFromKeys() {
        return fromKeys;
    }
    /**
     * @param fromKeys the fromKeys to set
     */
    public void setFromKeys(RecordKeysPO fromKeys) {
        this.fromKeys = fromKeys;
    }
    /**
     * @return the toKeys
     */
    public RecordKeysPO getToKeys() {
        return toKeys;
    }
    /**
     * @param toKeys the toKeys to set
     */
    public void setToKeys(RecordKeysPO toKeys) {
        this.toKeys = toKeys;
    }
    /**
     * @return the originKeys
     */
    public List<RelationOriginKeyPO> getOriginKeys() {
        return Objects.isNull(originKeys) ? Collections.emptyList() : originKeys;
    }
    /**
     * @param originKeys the originKeys to set
     */
    public void setOriginKeys(List<RelationOriginKeyPO> originKeys) {
        this.originKeys = originKeys;
    }
}

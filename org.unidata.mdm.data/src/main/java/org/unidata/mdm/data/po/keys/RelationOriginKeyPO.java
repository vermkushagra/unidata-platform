package org.unidata.mdm.data.po.keys;

import java.util.UUID;

/**
 * @author Mikhail Mikhailov
 * Concrete relation origin key class.
 */
public class RelationOriginKeyPO extends AbstractOriginKeyPO {
    /**
     * Origin from id.
     */
    public static final String FIELD_FROM_KEY = "from_key";
    /**
     * Origin from id.
     */
    public static final String FIELD_TO_KEY = "to_key";
    /**
     * Origin from ID.
     */
    protected UUID fromKey;
    /**
     * Origin to ID.
     */
    protected UUID toKey;
    /**
     * Constructor.
     */
    public RelationOriginKeyPO() {
        super();
    }
    /**
     * @return the fromKey
     */
    public UUID getFromKey() {
        return fromKey;
    }
    /**
     * @param fromKey the fromKey to set
     */
    public void setFromKey(UUID fromKey) {
        this.fromKey = fromKey;
    }
    /**
     * @return the toKey
     */
    public UUID getToKey() {
        return toKey;
    }
    /**
     * @param toKey the toKey to set
     */
    public void setToKey(UUID toKey) {
        this.toKey = toKey;
    }
}

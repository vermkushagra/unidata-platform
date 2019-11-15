package org.unidata.mdm.data.po.keys;

import java.util.Objects;

/**
 * @author Mikhail Mikhailov
 * Record origin key java counterpart.
 */
public class RecordOriginKeyPO extends AbstractOriginKeyPO {
    /**
     * Origin external id.
     */
    public static final String FIELD_EXTERNAL_ID = "external_id";
    /**
     * Origin external id.
     */
    protected String externalId;
    /**
     * Constructor.
     */
    public RecordOriginKeyPO() {
        super();
    }
    /**
     * @return the externalId
     */
    public String getExternalId() {
        return externalId;
    }
    /**
     * @param externalId the externalId to set
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.externalId, this.sourceSystem);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RecordOriginKeyPO)) {
            return false;
        }

        RecordOriginKeyPO other = (RecordOriginKeyPO) obj;
        return Objects.equals(this.id, other.id)
            && Objects.equals(this.externalId, other.externalId)
            && Objects.equals(this.sourceSystem, other.sourceSystem);
    }


}

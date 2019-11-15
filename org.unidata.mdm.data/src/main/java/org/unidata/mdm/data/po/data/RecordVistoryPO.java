/**
 *
 */
package org.unidata.mdm.data.po.data;

import org.unidata.mdm.data.po.keys.AbstractVistoryPO;

/**
 * @author Mikhail Mikhailov
 * Immutable vistory (versions + history) records table.
 */
public class RecordVistoryPO extends AbstractVistoryPO {
    /**
     * Table name.
     */
    public static final String TABLE_NAME = "record_vistory";
    /**
     * Read-only data section from origins - external id.
     */
    public static final String FIELD_EXTERNAL_ID = "external_id";
    /**
     * Origin external id.
     */
    private String externalId;
    /**
     * Constructor.
     */
    public RecordVistoryPO() {
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
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return
            String.format(new StringBuilder()
                .append(TABLE_NAME)
                .append("%n")
                .append(FIELD_ID)
                .append(": %s%n")
                .append(FIELD_ORIGIN_ID)
                .append(": %s%n")
                .append(FIELD_REVISION)
                .append(": %d%n")
                .append(FIELD_VALID_FROM)
                .append(": %tc%n")
                .append(FIELD_VALID_TO)
                .append(": %tc%n")
                .append(FIELD_DATA_A)
                .append(": %s%n")
                .append(FIELD_CREATE_DATE)
                .append(": %tc%n")
                .append(FIELD_CREATED_BY)
                .append(": %s%n")
                .append(FIELD_STATUS)
                .append(": %s%n")
                .append(FIELD_APPROVAL)
                .append(": %s%n")
                .append(FIELD_SHIFT)
                .append(": %s%n")
                .toString(),
                id,
                originId,
                revision,
                validFrom,
                validTo,
                data == null ? "null" : "[record]",
                createDate,
                createdBy,
                status,
                approval,
                shift);
    }

}

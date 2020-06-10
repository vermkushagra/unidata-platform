package com.unidata.mdm.backend.common.integration.exits;

/**
 * @author Mikhail Mikhailov
 * Some useful constants.
 */
public enum ExitConstants {
    /**
     * IN context parameter Current etalon ID.
     */
    IN_UPSERT_CURRENT_RECORD_ETALON_ID,
    /**
     * IN context parameter Current origin ID.
     */
    IN_UPSERT_CURRENT_RECORD_ORIGIN_ID,
    /**
     * IN context parameter for valid from field, modified by user exit.
     */
    IN_UPSERT_CURRENT_RECORD_VALID_FROM,
    /**
     * IN context parameter for valid to field, modified by user exit.
     */
    IN_UPSERT_CURRENT_RECORD_VALID_TO,
    /**
     * OUT context parameter for valid from field, modified by user exit.
     */
    OUT_UPSERT_CURRENT_RECORD_VALID_FROM,
    /**
     * OUT context parameter for valid to field, modified by user exit.
     */
    OUT_UPSERT_CURRENT_RECORD_VALID_TO,
    /**
     * OUT context parameter for created by field, modified by user exit.
     */
    OUT_UPSERT_CURRENT_RECORD_CREATED_BY,
    /**
     * OUT context parameter for status field, modified by user exit.
     */
    OUT_UPSERT_CURRENT_RECORD_STATUS,
    /**
     * OUT record was modified mark (will be saved as PRISTINE version).
     */
    OUT_UPSERT_CURRENT_RECORD_IS_MODIFIED;
}

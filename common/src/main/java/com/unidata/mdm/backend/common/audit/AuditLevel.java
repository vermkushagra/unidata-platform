package com.unidata.mdm.backend.common.audit;

/**
 * @author Mikhail Mikhailov
 * Audit level.
 */
public final class AuditLevel {
    /**
     * Skip all events.
     */
    public static final short AUDIT_NONE = 0;
    /**
     * Audit errors only.
     */
    public static final short AUDIT_ERRORS = 1;
    /**
     * Audit errors and success.
     */
    public static final short AUDIT_SUCCESS = 2;
    /**
     * Constructor.
     */
    private AuditLevel() {
        super();
    }
}

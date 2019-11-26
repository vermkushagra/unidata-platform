package org.unidata.mdm.core.audit;

/**
 * @author Alexander Malyshev
 */
public final class SecurityAuditConstants {
    private SecurityAuditConstants() {}

    public static final String ROLE_CREATE_EVENT_TYPE = "ROLE_CREATE";
    public static final String ROLE_DELETE_EVENT_TYPE = "ROLE_DELETE";
    public static final String ROLE_UPDATE_EVENT_TYPE = "ROLE_UPDATE";
    public static final String ROLE_LABEL_ATTACH_EVENT_TYPE = "ROLE_LABEL_ATTACH";
    public static final String LABEL_CREATE_EVENT_TYPE = "LABEL_CREATE";
    public static final String LABEL_UPDATE_EVENT_TYPE = "LABEL_UPDATE";
    public static final String LABEL_DELETE_EVENT_TYPE = "LABEL_DELETE";


    public static final String LOGIN_EVENT_TYPE = "LOGIN";
    public static final String LOGOUT_EVENT_TYPE = "LOGOUT";
}

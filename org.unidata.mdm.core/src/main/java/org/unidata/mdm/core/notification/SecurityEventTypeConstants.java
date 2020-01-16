package org.unidata.mdm.core.notification;

/**
 * @author Alexander Malyshev
 */
public final class SecurityEventTypeConstants {
    private SecurityEventTypeConstants() {}

    public static final String ROLE_CREATE_EVENT_TYPE = "role_create";
    public static final String ROLE_DELETE_EVENT_TYPE = "role_delete";
    public static final String ROLE_UPDATE_EVENT_TYPE = "role_update";
    public static final String ROLE_LABEL_ATTACH_EVENT_TYPE = "role_label_attach";
    public static final String LABEL_CREATE_EVENT_TYPE = "label_create";
    public static final String LABEL_UPDATE_EVENT_TYPE = "label_update";
    public static final String LABEL_DELETE_EVENT_TYPE = "label_delete";


    public static final String LOGIN_TYPE = "login";
    public static final String LOGOUT_TYPE = "logout";
}

package com.unidata.mdm.backend.common.integration.auth;

/**
 * @author Denis Kostovarov
 */
public interface Right {
    SecuredResource getSecuredResource();

    boolean isCreate();

    boolean isUpdate();

    boolean isDelete();

    boolean isRead();
}

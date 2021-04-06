package com.unidata.mdm.backend.common.integration.auth;

/**
 * @author Denis Kostovarov
 */
public interface SecurityLabelAttribute {

    String getName();

    String getValue();

    String getPath();

    String getDescription();

    int getId();
}

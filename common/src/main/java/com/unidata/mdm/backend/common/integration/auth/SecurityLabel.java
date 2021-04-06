package com.unidata.mdm.backend.common.integration.auth;

import java.util.List;

/**
 * @author Denis Kostovarov
 */
public interface SecurityLabel {

	String getName();

	String getDisplayName();

	List<SecurityLabelAttribute> getAttributes();

	String getDescription();
}

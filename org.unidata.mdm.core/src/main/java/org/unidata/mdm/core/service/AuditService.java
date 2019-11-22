package org.unidata.mdm.core.service;

import java.util.Map;

/**
 * @author Alexander Malyshev
 */
public interface AuditService {
    void writeEvent(final String type, Map<String, Object> parameters);
}

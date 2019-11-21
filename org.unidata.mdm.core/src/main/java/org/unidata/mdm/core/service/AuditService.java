package org.unidata.mdm.core.service;

import org.unidata.mdm.core.type.audit.AuditEvent;

import java.util.Collection;

/**
 * @author Alexander Malyshev
 */
public interface AuditService {

    void writeEvent(AuditEvent auditEvent);
}

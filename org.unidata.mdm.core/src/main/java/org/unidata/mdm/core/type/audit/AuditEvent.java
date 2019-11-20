package org.unidata.mdm.core.type.audit;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author Alexander Malyshev
 */
public interface AuditEvent {

    @Nonnull
    String type();

    @Nonnull
    Map<String, String> parameters();

    boolean success();
}

package org.unidata.mdm.core.context;

import org.unidata.mdm.core.type.data.ApprovalState;

/**
 * @author Mikhail Mikhailov
 * Force approval state property on the context.
 */
public interface ApprovalStateSettingContext {

    /**
     * Approval state propery on the context.
     * @return the state or null
     */
    ApprovalState getApprovalState();
}

package com.unidata.mdm.backend.common.context;

import com.unidata.mdm.backend.common.types.ApprovalState;

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

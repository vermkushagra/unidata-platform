package com.unidata.mdm.backend.common.integration.exits;

import com.unidata.mdm.backend.common.context.JoinRequestContext;

@FunctionalInterface
public interface BeforeJoinListener {
    ExitResult beforeJoin(JoinRequestContext joinRequestContext);
}

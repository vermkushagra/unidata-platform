package com.unidata.mdm.backend.common.integration.exits;

import com.unidata.mdm.backend.common.dto.KeysJoinDTO;

@FunctionalInterface
public interface AfterJoinListener {
    ExitResult afterJoin(KeysJoinDTO keysJoin);
}

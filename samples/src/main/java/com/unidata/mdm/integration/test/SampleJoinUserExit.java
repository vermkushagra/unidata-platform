package com.unidata.mdm.integration.test;

import com.unidata.mdm.backend.common.context.JoinRequestContext;
import com.unidata.mdm.backend.common.dto.KeysJoinDTO;
import com.unidata.mdm.backend.common.integration.exits.AfterJoinListener;
import com.unidata.mdm.backend.common.integration.exits.BeforeJoinListener;
import com.unidata.mdm.backend.common.integration.exits.ExitResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleJoinUserExit implements AfterJoinListener, BeforeJoinListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleJoinUserExit.class);

    @Override
    public ExitResult afterJoin(KeysJoinDTO keysJoin) {
        LOGGER.info("afterJoin: " + keysJoin);
        return new ExitResult(ExitResult.Status.SUCCESS);
    }

    @Override
    public ExitResult beforeJoin(JoinRequestContext joinRequestContext) {
        LOGGER.info("beforeJoin: " + joinRequestContext);
        return new ExitResult(ExitResult.Status.SUCCESS);
    }
}

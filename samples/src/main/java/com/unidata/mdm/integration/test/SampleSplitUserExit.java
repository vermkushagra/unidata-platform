package com.unidata.mdm.integration.test;

import com.unidata.mdm.backend.common.context.SplitContext;
import com.unidata.mdm.backend.common.integration.exits.AfterSplitListener;
import com.unidata.mdm.backend.common.integration.exits.BeforeSplitListener;
import com.unidata.mdm.backend.common.integration.exits.ExitResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleSplitUserExit implements BeforeSplitListener, AfterSplitListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleSplitUserExit.class);

    @Override
    public ExitResult beforeSplit(final SplitContext splitContext) {
        LOGGER.info("beforeSplit: " + splitContext.getOriginKey());
        return new ExitResult(ExitResult.Status.SUCCESS);
    }

    @Override
    public ExitResult afterSplit(final SplitContext splitContext) {
        LOGGER.info("afterSplit: " + splitContext.getOriginKey());
        LOGGER.info("afterSplit: " + splitContext.getNewEtalonKey().getId());
        return new ExitResult(ExitResult.Status.SUCCESS);
    }
}

package com.unidata.mdm.backend.common.integration.exits;

import com.unidata.mdm.backend.common.context.SplitContext;

@FunctionalInterface
public interface AfterSplitListener {
    ExitResult afterSplit(SplitContext splitContext);
}

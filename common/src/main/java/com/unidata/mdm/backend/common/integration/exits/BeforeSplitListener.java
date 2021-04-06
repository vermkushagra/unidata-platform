package com.unidata.mdm.backend.common.integration.exits;

import com.unidata.mdm.backend.common.context.SplitContext;

@FunctionalInterface
public interface BeforeSplitListener {
    ExitResult beforeSplit(SplitContext splitContext);
}

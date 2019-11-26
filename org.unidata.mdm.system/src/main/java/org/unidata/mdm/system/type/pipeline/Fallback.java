package org.unidata.mdm.system.type.pipeline;

import org.unidata.mdm.system.context.PipelineExecutionContext;

import java.util.function.BiConsumer;

/**
 * @author Alexander Malyshev
 */
public interface Fallback extends BiConsumer<PipelineExecutionContext, Throwable> {
}

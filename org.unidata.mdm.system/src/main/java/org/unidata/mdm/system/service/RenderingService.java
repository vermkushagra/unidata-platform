package org.unidata.mdm.system.service;

import org.unidata.mdm.system.type.pipeline.fragment.InputFragmentCollector;
import org.unidata.mdm.system.type.pipeline.fragment.OutputFragmentContainer;
import org.unidata.mdm.system.type.rendering.InputRenderingAction;
import org.unidata.mdm.system.type.rendering.InputSource;
import org.unidata.mdm.system.type.rendering.OutputRenderingAction;
import org.unidata.mdm.system.type.rendering.OutputSink;

/**
 * @author Mikhail Mikhailov on Jan 15, 2020
 */
public interface RenderingService {
    /**
     * Renders input fragments for the given action.
     * @param action the action to render for
     * @param collector the collector for fragments
     * @param source the input data source
     */
    void renderInput(InputRenderingAction action, InputFragmentCollector<?> collector, InputSource source);
    /**
     * Renders output fragments for the given action.
     * @param action the action
     * @param container the container
     * @param sink the sink
     */
    void renderOutput(OutputRenderingAction action, OutputFragmentContainer container, OutputSink sink);
    /**
     * Loads renderers once upon startup.
     */
    void loadRendrerers();
}

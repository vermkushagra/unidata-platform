package org.unidata.mdm.system.type.rendering;

import org.unidata.mdm.system.type.pipeline.fragment.OutputFragmentContainer;

/**
 * @author Mikhail Mikhailov on Jan 15, 2020
 */
public interface OutputFragmentRenderer {
    void render(OutputFragmentContainer container, OutputSink sink);
}

package org.unidata.mdm.system.type.rendering;

import org.unidata.mdm.system.type.pipeline.fragment.InputFragmentCollector;

/**
 * @author Mikhail Mikhailov on Jan 15, 2020
 */
@FunctionalInterface
public interface InputFragmentRenderer {
    void render(InputFragmentCollector<?> collector, InputSource source);
}

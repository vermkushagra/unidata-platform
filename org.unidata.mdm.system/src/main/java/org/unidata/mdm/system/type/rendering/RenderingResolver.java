package org.unidata.mdm.system.type.rendering;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Mikhail Mikhailov on Jan 15, 2020
 */
public interface RenderingResolver {
    /**
     * Gets renderer for an action or null
     * @param action the action
     * @return renderer or null
     */
    @Nullable
    InputFragmentRenderer get(@Nonnull InputRenderingAction action);
    /**
     * Gets renderer for an action or null
     * @param action the action
     * @return renderer or null
     */
    @Nullable
    OutputFragmentRenderer get(@Nonnull OutputRenderingAction action);
}

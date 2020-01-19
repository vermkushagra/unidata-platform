package org.unidata.mdm.system.type.rendering;

/**
 * @author Mikhail Mikhailov on Jan 16, 2020
 */
public interface RenderingAction {

    enum RenderingActionType {
        INPUT,
        OUTPUT
    }

    String name();

    RenderingActionType actionType();
}

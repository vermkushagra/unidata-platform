package org.unidata.mdm.data.context;

import org.unidata.mdm.system.context.BooleanFlagsContext;

/**
 * Set up aware context.
 * @author Mikhail Mikhailov on Nov 21, 2019
 */
public interface SetupAwareContext extends BooleanFlagsContext {
    /**
     * Adds setup awareness to a context.
     * @return true for set up context, false otherwise
     */
    default boolean setUp() {
        return getFlag(DataContextFlags.FLAG_IS_SETUP);
    }
    /**
     * Sets a context to set up state.
     * @param value the value to set
     */
    default void setUp(boolean value) {
        setFlag(DataContextFlags.FLAG_IS_SETUP, value);
    }
}

package org.unidata.mdm.core.configuration;

import org.unidata.mdm.core.module.CoreModule;

/**
 * @author Mikhail Mikhailov on Oct 30, 2019
 */
public final class CoreConfigurationConstants {
    /**
     * Default replay timeout (in millis).
     */
    public static final String CORE_DEFAULT_EVENT_REPLAY_TIMEOUT = CoreModule.MODULE_ID + ".event.replay.timeout";
    /**
     * Constructor.
     */
    private CoreConfigurationConstants() {
        super();
    }
}

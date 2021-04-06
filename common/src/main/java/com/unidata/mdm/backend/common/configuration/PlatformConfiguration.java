package com.unidata.mdm.backend.common.configuration;

/**
 * @author Mikhail Mikhailov
 * Some system-wide visible configuration fields.
 */
public interface PlatformConfiguration extends PlatformIdentitySource {
    /**
     * Major number.
     */
    int getPlatformMajor();
    /**
     * Minor number.
     */
    int getPlatformMinor();
    /**
     * Revision number.
     */
    int getPlatformRevision();
    /**
     * Node ID.
     */
    String getNodeId();
    /**
     * The default dump target format.
     */
    DumpTargetFormat getDumpTargetFormat();
}

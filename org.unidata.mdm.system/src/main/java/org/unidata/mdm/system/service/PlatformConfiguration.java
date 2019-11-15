package org.unidata.mdm.system.service;

import org.unidata.mdm.system.type.format.DumpTargetFormat;

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
     * Patch number.
     */
    int getPlatformPatch();
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

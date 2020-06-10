package com.unidata.mdm.backend.common.configuration;

import java.util.UUID;

/**
 * @author Mikhail Mikhailov
 * Id generation routines.
 */
public interface PlatformIdentitySource {
    /**
     * Gets UUID v1 string.
     * @return string
     */
    String v1IdString();
    /**
     * Gets UUID v1.
     * @return {@link UUID}
     */
    UUID v1Id();
    /**
     * Gets UUID v4 string.
     * @return string
     */
    String v4IdString();
    /**
     * Gets UUID v4.
     * @return {@link UUID}
     * @return
     */
    UUID v4Id();
}

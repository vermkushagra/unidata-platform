package com.unidata.mdm.backend.util;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.unidata.mdm.backend.common.configuration.PlatformIdentitySource;

/**
 * ID source.
 * @author Mikhail Mikhailov
 */
public class IdUtils {
    /**
     * Standard logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(IdUtils.class);
    /**
     * Configuration.
     */
    private static PlatformIdentitySource platformIdentitySource;
    /**
     * Constructor.
     */
    private IdUtils() {
        super();
    }
    /**
     * Convenient init method.
     */
    public static void init(ApplicationContext ac) {
        try {
            platformIdentitySource = ac.getBean(PlatformIdentitySource.class);
        } catch (Exception exc) {
            LOGGER.warn("Platform configuration bean GET. Exception caught.", exc);
        }
    }
    /**
     * Gets UUID v1 string.
     * @return string
     */
    public static String v1String() {
        return platformIdentitySource.v1IdString();
    }
    /**
     * Gets UUID v1.
     * @return {@link UUID}
     */
    public static UUID v1() {
        return platformIdentitySource.v1Id();
    }
    /**
     * Gets UUID v4 string.
     * @return string
     */
    public static String v4String() {
        return platformIdentitySource.v4IdString();
    }
    /**
     * Gets UUID v4.
     * @return {@link UUID}
     * @return
     */
    public static UUID v4() {
        return platformIdentitySource.v4Id();
    }
}

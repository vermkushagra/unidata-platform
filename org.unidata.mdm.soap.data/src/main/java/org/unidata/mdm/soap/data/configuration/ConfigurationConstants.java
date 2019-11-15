package org.unidata.mdm.soap.data.configuration;

/**
 * @author Alexander Malyshev
 */
@Deprecated
public final class ConfigurationConstants {
    private ConfigurationConstants() { }

    /**
     * Current main API version..
     */
    public static final String API_VERSION_PROPERTY = "unidata.api.version";

    /**
     * Current main platform version..
     */
    public static final String PLATFORM_VERSION_PROPERTY = "unidata.platform.version";

    public static final String DATA_SOAP_UPSERT_MAX_ATTEMPT_COUNT = "unidata.data.soap.upsert.max.attempt.count";
}

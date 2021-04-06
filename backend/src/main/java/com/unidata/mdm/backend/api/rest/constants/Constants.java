package com.unidata.mdm.backend.api.rest.constants;

/**
 * Created by Michael Yashin on 11.02.2015.
 */
public interface Constants {

    public final static String DATABASE_REQUIRED_SCHEMA_VERSION = "1.0.2";

    public final static int REST_DEFAULT_PAGE_SIZE = 100;

    public final static String HTTP_HEADER_REQUEST_UUID = "X-EGAIS-Request-Uuid";
    public final static String HTTP_HEADER_TOKEN_VALIDITY = "X-EGAIS-Token-Validity-Seconds";

    public static final String APP_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public final static String USER_LOGIN_JOB = "timed-job";
}

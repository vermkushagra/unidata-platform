package com.unidata.mdm.backend.api.rest.dto.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Enum SecuredResourceTypeRO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public enum SecuredResourceTypeRO {
    /** The system. */
    SYSTEM,
    /** The user defined. */
    USER_DEFINED
}

package com.unidata.mdm.backend.api.rest.dto.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The Enum RoleTypeRO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public enum RoleTypeRO {

    /** The system. */
    SYSTEM,
    /** The user defined. */
    USER_DEFINED
}

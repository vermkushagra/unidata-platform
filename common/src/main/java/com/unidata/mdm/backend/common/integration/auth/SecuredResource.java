package com.unidata.mdm.backend.common.integration.auth;

/**
 * @author Denis Kostovarov
 */
public interface SecuredResource {
    /**
     * The name of the resource.
     * @return name
     */
    String getName();
    /**
     * The display name of the resource.
     * @return display name
     */
    String getDisplayName();
    /**
     * The type of the resource. One of the {@link SecuredResourceType}.
     * @return type
     */
    SecuredResourceType getType();
    /**
     * The category of the resource. One of the {@link SecuredResourceCategory}.
     * @return category
     */
    SecuredResourceCategory getCategory();
}

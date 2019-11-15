package org.unidata.mdm.core.type.model;

/**
 * @author Yaroslav Nikolaev
 * Top level model type wrapper.
 */
public interface IdentityModelElement extends ModelElement {
    /**
     * Gets the unique identifier.
     * @return identifier
     */
    String getId();
    /**
     * Gets version of wrapped element.
     * @return version
     */
    Long getVersion();
}

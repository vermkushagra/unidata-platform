package org.unidata.mdm.core.type.security;

/**
 * @author Mikhail Mikhailov
 * A custom property.
 */
public interface CustomProperty {
    /**
     * Gets the name of the custom property.
     * @return the name
     */
    String getName();
    /**
     * Gets the display name of the custom property.
     * @return display name
     */
    String getDisplayName();
    /**
     * Gets the value of the custom property.
     * @return value
     */
    String getValue();
}

package org.unidata.mdm.core.type.model;

/**
 * @author Mikhail Mikhailov
 * Short info about top level elements.
 */
public interface ContainerModelElement {
    /**
     * Gets the entity's name.
     * @return name
     */
    String getName();
    /**
     * Gets the entity's display name.
     * @return display name
     */
    String getDisplayName();
}

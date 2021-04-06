package com.unidata.mdm.backend.service.model.util.wrappers;

/**
 * @author Yaroslav Nikolaev
 * Top level model type wrapper.
 */
public abstract class ModelWrapper implements ValueWrapper {
    /**
     * Gets the unique identifier.
     * @return identifier
     */
    public abstract String getUniqueIdentifier();
    /**
     * Gets version of wrapped element.
     * @return version
     */
    public abstract Long getVersionOfWrappedElement();
}

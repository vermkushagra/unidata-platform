package com.unidata.mdm.backend.service.model.util.facades;

import com.unidata.mdm.backend.service.model.MetaModelValidationComponent;
import com.unidata.mdm.meta.VersionedObjectDef;

/**
 * In general, this interface responsible for verifying meta model elements.
 * Be careful, implementations of this interface, responsible for only for one element consistency,
 * For whole model consistency responds {@link MetaModelValidationComponent}
 *
 * @param <V> marker param , which show that all top level meta model elements allow version control
 */
public interface ModelElementVerifier<V extends VersionedObjectDef> {

    /**
     * Verify inner state of model element
     *
     * @param modelElement top level meta model element for verifying.
     */
    void verifyModelElement(V modelElement);

    /**
     * Check model element id for unique over current state of system model
     *
     * @param modelElement top level meta model element for verifying.
     * @return true if unique, otherwise false.
     */
    boolean isUniqueModelElementId(V modelElement);

}

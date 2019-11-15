package org.unidata.mdm.meta.service;

import org.unidata.mdm.meta.context.UpdateModelRequestContext;

/**
 * Class responsible for validating model's contexts.
 */
public interface MetaModelValidationComponent {


    /**
     * Method check a consistency of input param.
     *
     * @param ctx update model request context.     
     */
    void validateUpdateModelContext(UpdateModelRequestContext ctx);

}

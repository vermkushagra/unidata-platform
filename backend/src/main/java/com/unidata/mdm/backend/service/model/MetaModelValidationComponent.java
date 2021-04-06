package com.unidata.mdm.backend.service.model;

import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;

/**
 * Class responsible for validating model's contexts.
 */
public interface MetaModelValidationComponent {


    /**
     * Method check a consistency of input param.
     *
     * @param ctx
     */
    void validateUpdateModelContext(UpdateModelRequestContext ctx);

}

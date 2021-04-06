package com.unidata.mdm.backend.configuration;

import io.swagger.core.filter.AbstractSpecFilter;
import io.swagger.model.ApiDescription;
import io.swagger.models.Operation;
import io.swagger.models.parameters.Parameter;

import java.util.List;
import java.util.Map;

/**
 * @author Michael Yashin. Created on 27.05.2015.
 */
public class SwaggerJaxrsFilter extends AbstractSpecFilter {
    //Ignore is used since Swagger fails to process request parameters wrapper object => we define implicit params
    public final static String IGNORE_PARAM = "ignore";

    @Override
    public boolean isParamAllowed(Parameter parameter, Operation operation, ApiDescription api, Map<String, List<String>> params, Map<String, String> cookies, Map<String, List<String>> headers) {
        if (IGNORE_PARAM.equals(parameter.getAccess())) {
            return false;
        }

        return true;
    }

}

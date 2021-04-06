package com.unidata.mdm.backend.api.rest.dto.meta;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unidata.mdm.backend.api.rest.dto.meta.AbstractEntityDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.ComplexAttributeDefinition;

/**
 * @author Michael Yashin. Created on 25.05.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NestedEntityDefinition extends AbstractEntityDefinition {
    /**
     * Complex attributes.
     */
    protected List<ComplexAttributeDefinition> complexAttributes = new ArrayList<>();

    public List<ComplexAttributeDefinition> getComplexAttributes() {
        return complexAttributes;
    }

    public void setComplexAttributes(List<ComplexAttributeDefinition> complexAttributes) {
        this.complexAttributes = complexAttributes;
    }


}

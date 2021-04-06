package com.unidata.mdm.backend.api.rest.dto.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.unidata.mdm.backend.api.rest.dto.CodeDataType;
import com.unidata.mdm.backend.api.rest.util.serializer.CodeAttributeDeserializer;
import com.unidata.mdm.backend.api.rest.util.serializer.CodeAttributeSerializer;

/**
 * @author Michael Yashin. Created on 02.06.2015.
 */
@JsonDeserialize(using = CodeAttributeDeserializer.class)
@JsonSerialize(using = CodeAttributeSerializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CodeAttributeRO {
    /**
     * Name of the attribute.
     */
    private String name;
    /**
     * The value.
     */
    private Object value;
    /**
     * Its value.
     */
    private List<Object> supplementary;
    /**
     * Value data type.
     */
    private CodeDataType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

    public List<Object> getSupplementary() {
        return supplementary;
    }

    public void setSupplementary(List<Object> supplementary) {
        this.supplementary = supplementary;
    }

    public CodeDataType getType() {
        return type;
    }

    public void setType(CodeDataType type) {
        this.type = type;
    }
}

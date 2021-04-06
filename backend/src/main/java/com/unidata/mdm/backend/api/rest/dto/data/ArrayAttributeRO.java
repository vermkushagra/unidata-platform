package com.unidata.mdm.backend.api.rest.dto.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.unidata.mdm.backend.api.rest.dto.ArrayDataType;
import com.unidata.mdm.backend.api.rest.util.serializer.ArrayAttributeDeserializer;
import com.unidata.mdm.backend.api.rest.util.serializer.ArrayAttributeSerializer;

/**
 * @author Michael Yashin. Created on 02.06.2015.
 */
@JsonDeserialize(using = ArrayAttributeDeserializer.class)
@JsonSerialize(using = ArrayAttributeSerializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArrayAttributeRO {
    /**
     * Name of the attribute.
     */
    protected String name;
    /**
     * Its value.
     */
    protected List<ArrayObjectRO> value;
    /**
     * Value data type.
     */
    protected ArrayDataType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ArrayObjectRO> getValue() {
        return value;
    }

    public void setValue(List<ArrayObjectRO> value) {
        this.value = value;
    }

    public ArrayDataType getType() {
        return type;
    }

    public void setType(ArrayDataType type) {
        this.type = type;
    }
}

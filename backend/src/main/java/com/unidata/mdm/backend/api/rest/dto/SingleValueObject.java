package com.unidata.mdm.backend.api.rest.dto;

/**
 * Value object special for sencha.
 */
public class SingleValueObject {

    private Object value;

    public SingleValueObject() {
    }

    public SingleValueObject(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}

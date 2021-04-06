package com.unidata.mdm.backend.api.rest.dto.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Michael Yashin. Created on 20.05.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CleanseFunction extends CleanseFunctionTreeElement {
    protected String javaClass;
    protected CleanseFunctionType type;

    
    public String getJavaClass() {
        return javaClass;
    }

    public void setJavaClass(String javaClass) {
        this.javaClass = javaClass;
    }

    public CleanseFunctionType getType() {
        return type;
    }

    public void setType(CleanseFunctionType type) {
        this.type = type;
    }
}

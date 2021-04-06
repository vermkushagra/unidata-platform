package com.unidata.mdm.backend.api.rest.dto.data;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Michael Yashin. Created on 02.06.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComplexAttributeRO {
    /**
     * Name of the complex attribute.
     */
    protected String name;
    /**
     * List of nested records.
     */
    protected List<NestedRecordRO> nestedRecords = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NestedRecordRO> getNestedRecords() {
        return nestedRecords;
    }

    public void setNestedRecords(List<NestedRecordRO> nestedRecords) {
        this.nestedRecords = nestedRecords;
    }
}

package com.unidata.mdm.backend.api.rest.dto.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class presents information about group, place on UI and presented attributes.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttributeGroupsRO {

    /**
     * list of grouped attributes
     */
    private List<String> attributes = new ArrayList<>();
    /**
     * the column number
     */
    private int column;
    /**
     * the row number
     */
    private int row;
    /**
     * the title
     */
    private String title;

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public AttributeGroupsRO withAttributes(Collection<String> values) {
        if (values != null) {
            getAttributes().addAll(values);
        }
        return this;
    }

    public AttributeGroupsRO withRow(int value) {
        setRow(value);
        return this;
    }

    public AttributeGroupsRO withColumn(int value) {
        setColumn(value);
        return this;
    }

    public AttributeGroupsRO withTitle(String value) {
        setTitle(value);
        return this;
    }
}

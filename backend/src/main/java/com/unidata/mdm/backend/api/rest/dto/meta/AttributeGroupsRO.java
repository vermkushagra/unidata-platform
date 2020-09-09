/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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

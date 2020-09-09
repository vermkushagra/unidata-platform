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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The Class presents information about group, place on UI and presented relations.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelationGroupsRO {

    /**
     * list of grouped relations
     */
    private List<String> relations = new ArrayList<>();
    /**
     * the column number
     */
    private int column;
    /**
     * the row number
     */
    private int row;
    /**
     * relation type
     */
    private RelType relType;
    /**
     * the title
     */
    private String title;

    public List<String> getRelations() {
        return relations;
    }

    public void setRelations(List<String> relations) {
        this.relations = relations;
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

    /**
     * relation type
     */
    public RelType getRelType() {
        return relType;
    }

    public void setRelType(RelType relType) {
        this.relType = relType;
    }

    public RelationGroupsRO withRelations(Collection<String> values) {
        if (values != null) {
            getRelations().addAll(values);
        }
        return this;
    }

    public RelationGroupsRO withRow(int value) {
        setRow(value);
        return this;
    }

    public RelationGroupsRO withColumn(int value) {
        setColumn(value);
        return this;
    }

    public RelationGroupsRO withTitle(String value) {
        setTitle(value);
        return this;
    }

    public RelationGroupsRO withRelType(RelType relType) {
        setRelType(relType);
        return this;
    }

}

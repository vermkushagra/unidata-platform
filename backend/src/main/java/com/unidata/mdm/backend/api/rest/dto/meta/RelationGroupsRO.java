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

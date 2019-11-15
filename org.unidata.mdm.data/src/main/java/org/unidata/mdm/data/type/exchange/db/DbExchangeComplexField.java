package org.unidata.mdm.data.type.exchange.db;

import java.util.ArrayList;
import java.util.List;

import org.unidata.mdm.data.type.exchange.ExchangeField;

/**
 * @author Alexey Tsarapkin
 * Exchange field for import multiple complex attributes
 */
public class DbExchangeComplexField extends DbExchangeField {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Parent key column for select query.
     */
    private String parentKeyColumn;

    /**
     * Select complex attributes query.
     */
    private String query;

    /**
     * Complex attribute fields.
     */
    private List<ExchangeField> fields;

    /**
     * Get the parentKeyColumn.
     *
     * @return the parentKeyColumn
     */
    public String getParentKeyColumn() {
        return parentKeyColumn;
    }

    /**
     * Set the parentKeyColumn.
     *
     */
    public void setParentKeyColumn(String parentKeyColumn) {
        this.parentKeyColumn = parentKeyColumn;
    }

    /**
     * Get the query.
     *
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * Set the query.
     *
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * Gets the fields.
     *
     * @return the fields
     */
    public List<ExchangeField> getFields() {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        return fields;
    }

    /**
     * Sets the fields.
     *
     * @param fields the fields to set
     */
    public void setFields(List<ExchangeField> fields) {
        this.fields = fields;
    }
}

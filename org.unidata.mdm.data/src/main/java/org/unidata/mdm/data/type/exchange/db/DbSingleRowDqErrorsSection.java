package org.unidata.mdm.data.type.exchange.db;

import org.unidata.mdm.data.type.exchange.DqErrorsSection;

public class DbSingleRowDqErrorsSection extends DqErrorsSection {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -6121640813210053268L;

    /**
     * table
     */
    private String table;
    /**
     * Status.
     */
    private String statusField;
    /**
     * Rule name.
     */
    private String ruleNameField;
    /**
     * The message.
     */
    private String messageField;
    /**
     * The severity.
     */
    private String severityField;
    /**
     * The category.
     */
    private String categoryField;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getStatusField() {
        return statusField;
    }

    public void setStatusField(String statusField) {
        this.statusField = statusField;
    }

    public String getRuleNameField() {
        return ruleNameField;
    }

    public void setRuleNameField(String ruleNameField) {
        this.ruleNameField = ruleNameField;
    }

    public String getMessageField() {
        return messageField;
    }

    public void setMessageField(String messageField) {
        this.messageField = messageField;
    }

    public String getSeverityField() {
        return severityField;
    }

    public void setSeverityField(String severityField) {
        this.severityField = severityField;
    }

    public String getCategoryField() {
        return categoryField;
    }

    public void setCategoryField(String categoryField) {
        this.categoryField = categoryField;
    }
}

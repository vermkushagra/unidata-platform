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

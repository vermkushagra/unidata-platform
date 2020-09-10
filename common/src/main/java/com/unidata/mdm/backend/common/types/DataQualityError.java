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

package com.unidata.mdm.backend.common.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;

import com.unidata.mdm.backend.common.dq.DataQualityExecutionMode;

/**
 * DQ error type. Created from JAXB artefact.
 */
public class DataQualityError implements Serializable {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 3517375849731520977L;
    /**
     * Id.
     */
    private final String id;
    /**
     * Etalon id
     */
    private String etalonId;
    /**
     * Create date.
     */
    private final Date createDate;
    /**
     * Update date.
     */
    private final Date updateDate;
    /**
     * Originator.
     */
    private final String createdBy;
    /**
     * Paths and attributes (optional), caused this validation error.
     */
    private final List<Pair<String, Attribute>> values;
    /**
     * Status.
     */
    private final DataQualityStatus status;
    /**
     * Rule name.
     */
    private final String ruleName;
    /**
     * The message.
     */
    private final String message;
    /**
     * The severity.
     */
    private final SeverityType severity;
    /**
     * The category.
     */
    private final String category;
    /**
     * Execution mode.
     */
    private final DataQualityExecutionMode executionMode;
    /**
     * Constructor.
     * @param b the builder
     */
    private DataQualityError(DataQualityErrorBuilder b) {
        super();
        id = b.id;
        etalonId = b.etalonId;
        createDate = b.createDate;
        updateDate = b.updateDate;
        createdBy = b.createdBy;
        values = Objects.isNull(b.values) ? Collections.emptyList() : b.values;
        status = b.status;
        ruleName = b.ruleName;
        message = b.message;
        severity= b.severity;
        category = b.category;
        executionMode = b.executionMode;
    }
    /**
     * Gets the value of the errorId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the value of the etalonId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEtalonId() {
        return etalonId;
    }

    /**
     * Gets the value of the createDate property.
     *
     * @return
     *     possible object is
     *     {@link Date }
     *
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * Gets the value of the updateDate property.
     *
     * @return
     *     possible object is
     *     {@link Date }
     *
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * @return the createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }
    /**
     * @return the values
     */
    public List<Pair<String, Attribute>> getValues() {
        return values;
    }
    /**
     * Gets the value of the status property.
     *
     * @return
     *     possible object is
     *     {@link DataQualityStatus }
     *
     */
    public DataQualityStatus getStatus() {
        return status;
    }

    /**
     * Gets the value of the ruleName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRuleName() {
        return ruleName;
    }

    /**
     * Gets the value of the message property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the value of the severity property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public SeverityType getSeverity() {
        return severity;
    }

    /**
     * Gets the value of the category property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCategory() {
        return category;
    }

    /**
     * @return the executionMode
     */
    public DataQualityExecutionMode getExecutionMode() {
        return executionMode;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder()
            .append("DQ error: ")
            .append("rule name [")
            .append(ruleName)
            .append("], severity [")
            .append(severity != null ? severity.name() : "<null>")
            .append("], category [")
            .append(category)
            .append("], status [")
            .append(status != null ? status.name() : "<null>")
            .append("], message [")
            .append(message)
            .append("]");

        return sb.toString();
    }
    /**
     * New builder,
     * @param error the rror to copy
     * @return new builder
     */
    public static DataQualityErrorBuilder builder(DataQualityError error) {
        return new DataQualityErrorBuilder(error);
    }
    /**
     * New builder.
     * @return new builder
     */
    public static DataQualityErrorBuilder builder() {
        return new DataQualityErrorBuilder();
    }
    /**
     * @author Mikhail Mikhailov
     * Builder type.
     */
    public static class DataQualityErrorBuilder {
        /**
         * Id.
         */
        private String id;
        /**
         * Etalon id
         */
        private String etalonId;
        /**
         * Create date.
         */
        private Date createDate;
        /**
         * Update date.
         */
        private Date updateDate;
        /**
         * Originator.
         */
        private String createdBy;
        /**
         * Paths and attributes (optional), caused this validation error.
         */
        private List<Pair<String, Attribute>> values;
        /**
         * Status.
         */
        private DataQualityStatus status;
        /**
         * Rule name.
         */
        private String ruleName;
        /**
         * The message.
         */
        private String message;
        /**
         * The severity.
         */
        private SeverityType severity;
        /**
         * The category.
         */
        private String category;
        /**
         * Execution mode.
         */
        private DataQualityExecutionMode executionMode;
        /**
         * Constructor.
         */
        private DataQualityErrorBuilder() {
            super();
        }
        /**
         * Constructor.
         */
        private DataQualityErrorBuilder(DataQualityError error) {

            this();
            if (Objects.isNull(error)) {
                return;
            }

            id = error.id;
            etalonId = error.id;
            createDate = error.createDate;
            updateDate = error.updateDate;
            status = error.status;
            ruleName = error.ruleName;
            message = error.message;
            severity= error.severity;
            category = error.category;
            values = error.values;
        }

        /**
         * Sets error id.
         * @param value the value to set
         * @return self
         */
        public DataQualityErrorBuilder errorId(String value) {
            this.id = value;
            return this;
        }

        /**
         * Sets etalon id.
         * @param value the value to set
         * @return self
         */
        public DataQualityErrorBuilder etalonId(final String value) {
            this.etalonId = value;
            return this;
        }

        /**
         * Sets create date.
         * @param value the value to set
         * @return self
         */
        public DataQualityErrorBuilder createDate(Date value) {
            this.createDate = value;
            return this;
        }
        /**
         * Sets update date.
         * @param value the value to set
         * @return self
         */
        public DataQualityErrorBuilder updateDate(Date value) {
            this.updateDate = value;
            return this;
        }
        /**
         * Sets the originator.
         * @param createdBy the value to set
         * @return self
         */
        public DataQualityErrorBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }
        /**
         * Problematic local paths and attr values, caused the error, if any.
         * @param values the values to set
         * @return self
         */
        public DataQualityErrorBuilder values(List<Pair<String, Attribute>> values) {
            if (Objects.isNull(this.values)) {
                this.values = new ArrayList<>();
            }
            this.values.addAll(values);
            return this;
        }
        /**
         * Problematic local path and its attr value, caused the error, if any.
         * @param value the value to set
         * @return self
         */
        public DataQualityErrorBuilder value(Pair<String, Attribute> value) {
            if (Objects.isNull(this.values)) {
                this.values = new ArrayList<>();
            }
            this.values.add(value);
            return this;
        }
        /**
         * Sets status.
         * @param value the value to set
         * @return self
         */
        public DataQualityErrorBuilder status(DataQualityStatus value) {
            this.status = value;
            return this;
        }
        /**
         * Sets rule name.
         * @param value the value to set
         * @return self
         */
        public DataQualityErrorBuilder ruleName(String value) {
            this.ruleName = value;
            return this;
        }
        /**
         * Sets message.
         * @param value the value to set
         * @return self
         */
        public DataQualityErrorBuilder message(String value) {
            this.message = value;
            return this;
        }
        /**
         * Sets severity.
         * @param value the value to set
         * @return self
         */
        public DataQualityErrorBuilder severity(SeverityType value) {
            this.severity = value;
            return this;
        }
        /**
         * Sets severity.
         * @param value the value to set
         * @return self
         */
        public DataQualityErrorBuilder severity(String value) {
            this.severity = Objects.nonNull(value) ? SeverityType.valueOf(value) : null;
            return this;
        }
        /**
         * Sets category.
         * @param value the value to set
         * @return self
         */
        public DataQualityErrorBuilder category(String value) {
            this.category = value;
            return this;
        }
        /**
         * Sets execution mode.
         * @param value the value to set
         * @return self
         */
        public DataQualityErrorBuilder executionMode(DataQualityExecutionMode executionMode) {
            this.executionMode = executionMode;
            return this;
        }
        /**
         * Builds the object.
         * @return new error object
         */
        public DataQualityError build() {
            return new DataQualityError(this);
        }
    }
}

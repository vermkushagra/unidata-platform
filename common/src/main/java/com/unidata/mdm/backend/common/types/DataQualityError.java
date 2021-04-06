package com.unidata.mdm.backend.common.types;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * DQ error type. Created from JAXB artefact.
 */
public class DataQualityError implements Serializable
{
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 3517375849731520977L;
    /**
     * Id.
     */
    private final String id;
    /**
     * Create date.
     */
    private final Date createDate;
    /**
     * Update date.
     */
    private final Date updateDate;
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
     * Constructor.
     * @param b the builder
     */
    private DataQualityError(DataQualityErrorBuilder b) {
        super();
        this.id = b.id;
        this.createDate = b.createDate;
        this.updateDate = b.updateDate;
        this.status = b.status;
        this.ruleName = b.ruleName;
        this.message = b.message;
        this.severity= b.severity;
        this.category = b.category;
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
         * Create date.
         */
        private Date createDate;
        /**
         * Update date.
         */
        private Date updateDate;
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

            this.id = error.id;
            this.createDate = error.createDate;
            this.updateDate = error.updateDate;
            this.status = error.status;
            this.ruleName = error.ruleName;
            this.message = error.message;
            this.severity= error.severity;
            this.category = error.category;
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
         * Builds the object.
         * @return new error object
         */
        public DataQualityError build() {
            return new DataQualityError(this);
        }
    }
}

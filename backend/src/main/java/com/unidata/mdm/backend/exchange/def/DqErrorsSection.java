package com.unidata.mdm.backend.exchange.def;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.unidata.mdm.backend.exchange.def.db.DbJsonDqErrorsSection;
import com.unidata.mdm.backend.exchange.def.db.DbSingleRowDqErrorsSection;

/**
 * Dq errors section
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type")
@JsonSubTypes({ @JsonSubTypes.Type(value = DbJsonDqErrorsSection.class, name = "DB_JSON"),
                      @JsonSubTypes.Type(value = DbSingleRowDqErrorsSection.class, name = "DB_SINGLE") })
public class DqErrorsSection implements Serializable {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -6121640813210053268L;
    /**
     * entity name field
     */
    private String entityNameField;

    /**
     * external id field
     */
    private String externalIdField;

    /**
     * source system field
     */
    private String sourceSystemField;

    /**
     * etalon id field
     */
    private String etalonIdField;

    public String getEntityNameField() {
        return entityNameField;
    }

    public void setEntityNameField(String entityNameField) {
        this.entityNameField = entityNameField;
    }

    public String getExternalIdField() {
        return externalIdField;
    }

    public void setExternalIdField(String externalIdField) {
        this.externalIdField = externalIdField;
    }

    public String getSourceSystemField() {
        return sourceSystemField;
    }

    public void setSourceSystemField(String sourceSystemField) {
        this.sourceSystemField = sourceSystemField;
    }

    public String getEtalonIdField() {
        return etalonIdField;
    }

    public void setEtalonIdField(String etalonIdField) {
        this.etalonIdField = etalonIdField;
    }
}

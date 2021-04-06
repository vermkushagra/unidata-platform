package com.unidata.mdm.backend.api.rest.dto.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Mikhail Mikhailov
 * Etalon relation REST definition.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EtalonRelationToRO extends AbstractRelationToRO {
    /**
     * Etalon id of the RELATION.
     */
    private String etalonId;
    /**
     * Etalon id of the TO side.
     */
    private String etalonIdTo;
    /**
     * Etalon display name for 'To side'.
     */
    private String etalonDisplayNameTo;

    /**
     * Constructor.
     */
    public EtalonRelationToRO() {
        super();
    }

    /**
     * @return the etalonId
     */
    public String getEtalonId() {
        return etalonId;
    }

    /**
     * @param etalonId the etalonId to set
     */
    public void setEtalonId(String etalonId) {
        this.etalonId = etalonId;
    }

    /**
     * @return the etalonIdTo
     */
    public String getEtalonIdTo() {
        return etalonIdTo;
    }

    /**
     * @param etalonIdTo the etalonIdTo to set
     */
    public void setEtalonIdTo(String etalonIdTo) {
        this.etalonIdTo = etalonIdTo;
    }


    public String getEtalonDisplayNameTo() {
        return etalonDisplayNameTo;
    }

    public void setEtalonDisplayNameTo(String etalonDisplayNameTo) {
        this.etalonDisplayNameTo = etalonDisplayNameTo;
    }
}

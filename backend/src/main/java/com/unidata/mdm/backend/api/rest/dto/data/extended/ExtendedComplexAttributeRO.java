package com.unidata.mdm.backend.api.rest.dto.data.extended;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unidata.mdm.backend.api.rest.dto.data.ComplexAttributeRO;

/**
 * @author Dmitry Kopin. Created on 21.06.2017.
 * Contains additional information for complex attribute rest object
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtendedComplexAttributeRO extends ComplexAttributeRO {

    private boolean winner;

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }
}

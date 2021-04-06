package com.unidata.mdm.backend.api.rest.dto.data.extended;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.unidata.mdm.backend.api.rest.dto.data.ArrayAttributeRO;
import com.unidata.mdm.backend.api.rest.util.serializer.ArrayAttributeDeserializer;
import com.unidata.mdm.backend.api.rest.util.serializer.ArrayAttributeSerializer;

/**
 * @author Dmitry Kopin. Created on 21.06.2017.
 * Cointains additional information for array attribute rest object
 */
@JsonDeserialize(using = ArrayAttributeDeserializer.class)
@JsonSerialize(using = ArrayAttributeSerializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtendedArrayAttributeRO extends ArrayAttributeRO{

    private boolean winner;

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }
}

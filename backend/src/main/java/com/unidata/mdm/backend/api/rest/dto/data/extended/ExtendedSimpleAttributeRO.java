package com.unidata.mdm.backend.api.rest.dto.data.extended;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.unidata.mdm.backend.api.rest.dto.data.SimpleAttributeRO;
import com.unidata.mdm.backend.api.rest.util.serializer.SimpleAttributeDeserializer;
import com.unidata.mdm.backend.api.rest.util.serializer.SimpleAttributeSerializer;

/**
 * @author Dmitry Kopin. Created on 21.06.2017.
 * Contains additional information about simple attribute rest object
 */
@JsonDeserialize(using = SimpleAttributeDeserializer.class)
@JsonSerialize(using = SimpleAttributeSerializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtendedSimpleAttributeRO extends SimpleAttributeRO{

    private boolean winner;

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }
}

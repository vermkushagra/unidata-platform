package com.unidata.mdm.backend.api.rest.dto.data.extended;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.unidata.mdm.backend.api.rest.dto.data.CodeAttributeRO;
import com.unidata.mdm.backend.api.rest.util.serializer.CodeAttributeDeserializer;
import com.unidata.mdm.backend.api.rest.util.serializer.CodeAttributeSerializer;

/**
 * @author Dmitry Kopin. Created on 21.06.2017.
 * Contains additional information for code attribute rest object
 */
@JsonDeserialize(using = CodeAttributeDeserializer.class)
@JsonSerialize(using = CodeAttributeSerializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtendedCodeAttributeRO extends CodeAttributeRO{

    private boolean winner;

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }
}

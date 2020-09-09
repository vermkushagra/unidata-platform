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

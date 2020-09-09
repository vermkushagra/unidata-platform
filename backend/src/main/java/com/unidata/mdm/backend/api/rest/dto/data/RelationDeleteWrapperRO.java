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

package com.unidata.mdm.backend.api.rest.dto.data;


import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Wrapper object to delete context
 *
 * @author Dmitry Kopin on 19.06.2018.
 */
public class RelationDeleteWrapperRO {
    /**
     * Etalon id relation
     */
    @JsonProperty("etalonId")
    private String etalonRelationId;
    /**
     * Origin id relation
     */
    @JsonProperty("originId")
    private String originRelationId;
    /**
     * Valid from period
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime validFrom;
    /**
     * Valid to period
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime validTo;
    /**
     * Wipe flag
     */
    private boolean wipe = false;
    /**
     * Relation name
     */
    private String relName;

    public String getEtalonRelationId() {
        return etalonRelationId;
    }

    public void setEtalonRelationId(String etalonRelationId) {
        this.etalonRelationId = etalonRelationId;
    }

    public String getOriginRelationId() {
        return originRelationId;
    }

    public void setOriginRelationId(String originRelationId) {
        this.originRelationId = originRelationId;
    }


    public boolean isWipe() {
        return wipe;
    }

    public void setWipe(boolean wipe) {
        this.wipe = wipe;
    }

    public String getRelName() {
        return relName;
    }

    public void setRelName(String relName) {
        this.relName = relName;
    }

    /**
     * Optional validity range start date.
     */
    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * Optional validity range end date.
     */
    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }
}

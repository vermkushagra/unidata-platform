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
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.springframework.format.annotation.DateTimeFormat;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterByCriteriaRequestRO {

    private List<String> etalonIds;

    /**
     * Date from.
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime validFrom;

    /**
     * Date to.
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime validTo;

    /**
     * FULL | PARTIAL
     */
    private String timeIntervalIntersectType;

    public List<String> getEtalonIds() {
        return etalonIds;
    }

    public FilterByCriteriaRequestRO setEtalonIds(List<String> etalonIds) {
        this.etalonIds = etalonIds;
        return this;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public FilterByCriteriaRequestRO setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
        return this;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public FilterByCriteriaRequestRO setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
        return this;
    }

    public String getTimeIntervalIntersectType() {
        return timeIntervalIntersectType;
    }

    public FilterByCriteriaRequestRO setTimeIntervalIntersectType(String timeIntervalIntersectType) {
        this.timeIntervalIntersectType = timeIntervalIntersectType;
        return this;
    }
}

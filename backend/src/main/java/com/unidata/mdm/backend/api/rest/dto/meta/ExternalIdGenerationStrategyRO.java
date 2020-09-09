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

package com.unidata.mdm.backend.api.rest.dto.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Mikhail Mikhailov
 * Base class for gen. type REST descriptors.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type")
@JsonSubTypes({
    @Type(value = RandomExternalIdGenerationStrategyRO.class, name = "RANDOM"),
    @Type(value = ConcatExternalIdGenerationStrategyRO.class, name = "CONCAT"),
    @Type(value = SequenceExternalIdGenerationStrategyRO.class, name = "SEQUENCE")
})
public abstract class ExternalIdGenerationStrategyRO {
    /**
     * Constructor.
     */
    public ExternalIdGenerationStrategyRO() {
        super();
    }
    /**
     * @return the strategyType
     */
    public abstract ExternalIdGenerationTypeRO getStrategyType();
}

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

/**
 *
 */
package com.unidata.mdm.backend.exchange.def;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Mikhail Mikhailov
 *
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type")
@JsonSubTypes({
        @Type(value = ExchangeRegexFieldTransformer.class, name = "REGEX_SPLITTER"),
        @Type(value = ExchangeTemporalFieldTransformer.class, name = "TEMPORAL_PARSER"),
        @Type(value = ExchangeNoiseRemoveTransformer.class, name = "NOISE_REMOVER")})
public abstract class ExchangeFieldTransformer implements Serializable {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -5310354113397140212L;

    /**
     * Ctor.
     */
    public ExchangeFieldTransformer() {
        super();
    }

    /**
     * Implement in subclasses.
     * @return transformation result.
     */
    public abstract String transform(String input);

}

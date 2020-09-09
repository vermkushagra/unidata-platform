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

import java.util.List;

/**
 * @author Mikhail Mikhailov
 * Concat strategy mapped type descriptor.
 */
public class ConcatExternalIdGenerationStrategyRO extends ExternalIdGenerationStrategyRO {
    /**
     * Attribute names.
     */
    private List<String> attributes;
    /**
     * Separator char.
     */
    private String separator;
    /**
     * Constructor.
     */
    public ConcatExternalIdGenerationStrategyRO() {
        super();
    }
    /**
     * @return the sttributes
     */
    public List<String> getAttributes() {
        return attributes;
    }
    /**
     * @param sttributes the sttributes to set
     */
    public void setAttributes(List<String> sttributes) {
        this.attributes = sttributes;
    }
    /**
     * @return the separator
     */
    public String getSeparator() {
        return separator;
    }
    /**
     * @param separator the separator to set
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalIdGenerationTypeRO getStrategyType() {
        return ExternalIdGenerationTypeRO.CONCAT;
    }
}

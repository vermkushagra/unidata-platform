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

package org.unidata.mdm.search.dto;

/**
 * Extended value for search result.
 * Can contains special name for display and other additional information.
 */
public class SearchResultExtendedValueDTO {
    /**
     * Original value
     */
    private Object value;
    /**
     * Value for display
     */
    private String displayValue;
    /**
     * Linked etalon id
     */
    private String linkedEtalonId;

    public SearchResultExtendedValueDTO(Object value, String displayValue, String linkedEtalonId) {
        this.value = value;
        this.displayValue = displayValue;
        this.linkedEtalonId = linkedEtalonId;
    }

    public SearchResultExtendedValueDTO(Object value, String displayValue) {
        this.value = value;
        this.displayValue = displayValue;
        this.linkedEtalonId = null;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getLinkedEtalonId() {
        return linkedEtalonId;
    }

    public void setLinkedEtalonId(String linkedEtalonId) {
        this.linkedEtalonId = linkedEtalonId;
    }
}
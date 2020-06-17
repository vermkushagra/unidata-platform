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
package com.unidata.mdm.backend.common.dto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;

/**
* @author Mikhail Mikhailov
* Hit fields container.
*/
public class SearchResultHitFieldDTO {

   /**
    * The field name.
    */
   private final String field;
   /**
    * Field value.
    */
   private final List<Object> values;

    /**
     * Constructor.
     */
    public SearchResultHitFieldDTO(final String field, final List<Object> values) {
        super();
        this.field = field;
        this.values = values == null || values.isEmpty() ? Collections.emptyList() : values;
    }

   /**
    * @return the field
    */
   public String getField() {
       return field;
   }

   /**
    * @return the values
    */
   public List<Object> getValues() {
       return values;
   }

   public boolean isEmpty() {

       if (isNullField()) {
           return true;
       }

       return values.stream().allMatch(Objects::isNull);
   }
    /**
     *
     * @return return first value in values otherwise null
     */
    public Object getFirstValue() {
        return isNullField() ? null : getValues().isEmpty() ? null : getValues().get(0);
    }

    /**
     * @return true if non null field
     */
    public boolean isNonNullField() {
        return CollectionUtils.isNotEmpty(values);
    }

    /**
     * @return true is null field
     */
    public boolean isNullField() {
        return CollectionUtils.isEmpty(values);
    }

    /**
     * @return true if value is instance of Collection and contains more when 1 element
     */
    public boolean isCollection() {
        return !isNullField() && values.size() > 1;
    }

    /**
     * @return true if it is a simple single object
     */
    public boolean isSingleValue(){
        return !isCollection();
    }
}


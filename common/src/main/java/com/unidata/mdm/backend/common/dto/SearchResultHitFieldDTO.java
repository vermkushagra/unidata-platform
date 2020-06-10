/**
 *
 */
package com.unidata.mdm.backend.common.dto;

import java.util.ArrayList;
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
    * Diesplay values.
    */
   private final List<String> displayValues;
   /**
    * Hack.
    */
   private String systemId;
    /**
     * Constructor.
     */
    public SearchResultHitFieldDTO(final String field, final List<Object> values) {
        super();
        this.field = field;
        this.values = values == null || values.isEmpty() ? Collections.emptyList() : values;
        this.displayValues = this.values.isEmpty() ? Collections.emptyList() : new ArrayList<>(this.values.size());
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

    public List<String> getDisplayValues() {
        return displayValues;
    }
    /**
     *
     * @return return first value in values otherwise null
     */
    public String getFirstDisplayValue() {
        return isNullField() ? null : getDisplayValues().isEmpty() ? null : getDisplayValues().get(0);
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

    /**
     * @return the systemId
     */
    public String getSystemId() {
        return systemId;
    }

    /**
     * @param systemId the systemId to set
     */
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
}


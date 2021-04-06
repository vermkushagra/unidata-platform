/**
 *
 */
package com.unidata.mdm.backend.common.dto;

import java.util.List;

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
        this.values = values == null || values.isEmpty() ? null : values;
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
        return values != null;
    }

    /**
     * @return true is null field
     */
    public boolean isNullField() {
        return values == null;
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


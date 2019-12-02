package org.unidata.mdm.meta.dto;

import org.unidata.mdm.meta.EnumerationDataType;

/**
 * @author Mikhail Mikhailov on Dec 3, 2019
 */
public class GetModelEnumerationDTO {
    /**
     * The enumeration.
     */
    private EnumerationDataType enumeration;

    public GetModelEnumerationDTO() {
        super();
    }

    public GetModelEnumerationDTO(EnumerationDataType enumeration) {
        this();
        this.enumeration = enumeration;
    }

    /**
     * @return the enumeration
     */
    public EnumerationDataType getEnumeration() {
        return enumeration;
    }

    /**
     * @param enumeration the enumeration to set
     */
    public void setEnumeration(EnumerationDataType enumeration) {
        this.enumeration = enumeration;
    }
}

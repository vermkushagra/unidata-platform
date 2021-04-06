package com.unidata.mdm.api.wsdl.v5;

import javax.xml.bind.annotation.XmlTransient;

import com.unidata.mdm.data.v5.AbstractArrayAttribute;
import com.unidata.mdm.data.v5.ArrayDataType;

/**
 * @author Mikhail Mikhailov
 *
 */
@SuppressWarnings("serial")
public class ArrayAttributeImpl extends AbstractArrayAttribute {

    @XmlTransient
    private ArrayDataType type;

    /**
     * Constructor.
     */
    public ArrayAttributeImpl() {
        super();
    }

    /**
     * @return the type
     */
    @XmlTransient
    public ArrayDataType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(ArrayDataType type) {
        this.type = type;
    }


}

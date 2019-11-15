package org.unidata.mdm.data.serialization.jaxb;

import org.unidata.mdm.data.AbstractCodeAttribute;
import org.unidata.mdm.data.CodeDataType;

import java.util.Objects;

import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Mikhail Mikhailov
 *         Simple attribute value custom implementation.
 */
@SuppressWarnings("serial")
public class CodeAttributeImpl extends AbstractCodeAttribute {

    /**
     * Value data type.
     */
    @XmlTransient
    private CodeDataType type;

    /**
     * Constructor.
     */
    public CodeAttributeImpl() {
        super();
    }

    /**
     * @return the type
     */
    @XmlTransient
    public CodeDataType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    protected void setType(CodeDataType type) {
        this.type = type;
    }

    /**
     * Gets value of the attribute.
     *
     * @return the value
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue() {

        if (type != null) {
            switch (type) {
                case STRING:
                    return (T) stringValue;
                case INTEGER:
                    return (T) intValue;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIntValue(Long value) {
        super.setIntValue(value);
        this.type = CodeDataType.INTEGER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStringValue(String value) {
        super.setStringValue(value);
        this.type = CodeDataType.STRING;
    }

    /**
     * @see Object#hashCode()
     * TODO re-write this crap asap. Introduce solid value identity system instead.
     */
    @Override
    public int hashCode() {
        return Objects.hash(type, getValue());
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!getClass().isInstance(obj)) {
            return false;
        }

        CodeAttributeImpl other = (CodeAttributeImpl) obj;
        if (type != other.type) {
            return false;
        }

        return Objects.equals(getValue(), other.getValue());
    }
}

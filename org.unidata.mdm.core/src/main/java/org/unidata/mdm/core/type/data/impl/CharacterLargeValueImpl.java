package org.unidata.mdm.core.type.data.impl;

import org.unidata.mdm.core.type.data.CharacterLargeValue;

/**
 * @author Mikhail Mikhailov
 * Character large value holder.
 */
public class CharacterLargeValueImpl extends AbstractLargeValue implements CharacterLargeValue {

    /**
     * Constructor.
     */
    public CharacterLargeValueImpl() {
        super();
    }

    /**
     * Sets data value.
     * @param value the value
     * @return self
     */
    public CharacterLargeValueImpl withData(byte[] value) {
        setData(value);
        return this;
    }

    /**
     * Sets id.
     * @param value the id
     * @return self
     */
    public CharacterLargeValueImpl withId(String value) {
        setId(value);
        return this;
    }

    /**
     * Sets file name.
     * @param value the file name
     * @return self
     */
    public CharacterLargeValueImpl withFileName(String value) {
        setFileName(value);
        return this;
    }

    /**
     * Seats mime type.
     * @param value the mime type
     * @return self
     */
    public CharacterLargeValueImpl withMimeType(String value) {
        setMimeType(value);
        return this;
    }

    /**
     * Sets size.
     * @param value the size
     * @return self
     */
    public CharacterLargeValueImpl withSize(long value) {
        setSize(value);
        return this;
    }
}

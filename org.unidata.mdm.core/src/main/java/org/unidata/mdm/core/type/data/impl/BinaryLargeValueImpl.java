package org.unidata.mdm.core.type.data.impl;

import org.unidata.mdm.core.type.data.BinaryLargeValue;

/**
 * @author Mikhail Mikhailov
 * Binary large value.
 */
public class BinaryLargeValueImpl extends AbstractLargeValue implements BinaryLargeValue {
    /**
     * Constructor.
     */
    public BinaryLargeValueImpl() {
        super();
    }

    /**
     * Sets data value.
     * @param value the value
     * @return self
     */
    public BinaryLargeValueImpl withData(byte[] value) {
        setData(value);
        return this;
    }

    /**
     * Sets id.
     * @param value the id
     * @return self
     */
    public BinaryLargeValueImpl withId(String value) {
        setId(value);
        return this;
    }

    /**
     * Sets file name.
     * @param value the file name
     * @return self
     */
    public BinaryLargeValueImpl withFileName(String value) {
        setFileName(value);
        return this;
    }

    /**
     * Seats mime type.
     * @param value the mime type
     * @return self
     */
    public BinaryLargeValueImpl withMimeType(String value) {
        setMimeType(value);
        return this;
    }

    /**
     * Sets size.
     * @param value the size
     * @return self
     */
    public BinaryLargeValueImpl withSize(long value) {
        setSize(value);
        return this;
    }

}

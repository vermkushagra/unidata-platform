package org.unidata.mdm.data.serialization;

import java.time.LocalTime;

import org.unidata.mdm.core.type.data.impl.TimeArrayAttributeImpl;

/**
 * @author Mikhail Mikhailov
 * Time array attribute.
 */
public class SerializedTimeArrayAttributeImpl extends TimeArrayAttributeImpl implements VerifyableArrayAttribute<LocalTime> {
    /**
     * Constructor.
     */
    public SerializedTimeArrayAttributeImpl() {
        super();
    }
}

package org.unidata.mdm.data.serialization;

import java.time.LocalTime;

import org.unidata.mdm.core.type.data.impl.TimeSimpleAttributeImpl;

/**
 * @author Mikhail Mikhailov
 * Time attribute.
 */
public class SerializedTimeSimpleAttributeImpl extends TimeSimpleAttributeImpl implements VerifyableSimpleAttribute<LocalTime> {
    /**
     * Constructor.
     */
    public SerializedTimeSimpleAttributeImpl() {
        super();
    }
}

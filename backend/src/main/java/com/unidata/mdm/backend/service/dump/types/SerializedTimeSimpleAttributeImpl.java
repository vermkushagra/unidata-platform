package com.unidata.mdm.backend.service.dump.types;

import java.time.LocalTime;

import com.unidata.mdm.backend.common.types.impl.TimeSimpleAttributeImpl;

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

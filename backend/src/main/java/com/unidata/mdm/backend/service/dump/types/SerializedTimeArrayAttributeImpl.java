package com.unidata.mdm.backend.service.dump.types;

import java.time.LocalTime;

import com.unidata.mdm.backend.common.types.impl.TimeArrayAttributeImpl;

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

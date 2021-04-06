package com.unidata.mdm.backend.service.dump.types;

import java.time.LocalDateTime;

import com.unidata.mdm.backend.common.types.impl.TimestampSimpleAttributeImpl;

/**
 * @author Mikhail Mikhailov
 * Timestamp attribute.
 */
public class SerializedTimestampSimpleAttributeImpl extends TimestampSimpleAttributeImpl implements VerifyableSimpleAttribute<LocalDateTime> {
    /**
     * Constructor.
     */
    public SerializedTimestampSimpleAttributeImpl() {
        super();
    }
}

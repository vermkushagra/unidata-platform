package org.unidata.mdm.data.serialization;

import java.time.LocalDateTime;

import org.unidata.mdm.core.type.data.impl.TimestampSimpleAttributeImpl;

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

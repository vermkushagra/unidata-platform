package org.unidata.mdm.data.serialization;

import java.time.LocalDateTime;

import org.unidata.mdm.core.type.data.impl.TimestampArrayAttributeImpl;

/**
 * @author Mikhail Mikhailov
 * Timestamp array attribute.
 */
public class SerializedTimestampArrayAttributeImpl extends TimestampArrayAttributeImpl implements VerifyableArrayAttribute<LocalDateTime> {
    /**
     * Constructor.
     */
    public SerializedTimestampArrayAttributeImpl() {
        super();
    }
}

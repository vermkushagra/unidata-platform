package com.unidata.mdm.backend.service.dump.types;

import java.time.LocalDateTime;

import com.unidata.mdm.backend.common.types.impl.TimestampArrayAttributeImpl;

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

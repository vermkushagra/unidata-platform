package com.unidata.mdm.backend.service.dump.types;

import com.unidata.mdm.backend.common.types.impl.IntegerSimpleAttributeImpl;

/**
 * @author Mikhail Mikhailov
 * Integer attribute.
 */
public class SerializedIntegerSimpleAttributeImpl extends IntegerSimpleAttributeImpl implements VerifyableSimpleAttribute<Long> {
    /**
     * Constructor.
     */
    public SerializedIntegerSimpleAttributeImpl() {
        super();
    }
}

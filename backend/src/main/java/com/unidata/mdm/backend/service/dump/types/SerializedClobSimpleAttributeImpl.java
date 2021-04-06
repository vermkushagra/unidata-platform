package com.unidata.mdm.backend.service.dump.types;

import com.unidata.mdm.backend.common.types.CharacterLargeValue;
import com.unidata.mdm.backend.common.types.impl.ClobSimpleAttributeImpl;

/**
 * @author Mikhail Mikhailov
 * Clob attribute.
 */
public class SerializedClobSimpleAttributeImpl extends ClobSimpleAttributeImpl implements VerifyableSimpleAttribute<CharacterLargeValue> {
    /**
     * Constructor.
     */
    public SerializedClobSimpleAttributeImpl() {
        super();
    }
}

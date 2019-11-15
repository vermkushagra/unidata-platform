package org.unidata.mdm.data.serialization;

import org.unidata.mdm.core.type.data.CharacterLargeValue;
import org.unidata.mdm.core.type.data.impl.ClobSimpleAttributeImpl;

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

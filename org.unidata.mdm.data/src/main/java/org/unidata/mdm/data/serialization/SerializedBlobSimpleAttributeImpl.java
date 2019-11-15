package org.unidata.mdm.data.serialization;

import org.unidata.mdm.core.type.data.BinaryLargeValue;
import org.unidata.mdm.core.type.data.impl.BlobSimpleAttributeImpl;

/**
 * @author Mikhail Mikhailov
 * Blob attribute.
 */
public class SerializedBlobSimpleAttributeImpl extends BlobSimpleAttributeImpl implements VerifyableSimpleAttribute<BinaryLargeValue> {
    /**
     * Constructor.
     */
    public SerializedBlobSimpleAttributeImpl() {
        super();
    }
}

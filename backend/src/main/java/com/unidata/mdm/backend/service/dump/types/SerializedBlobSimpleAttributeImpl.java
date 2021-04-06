package com.unidata.mdm.backend.service.dump.types;

import com.unidata.mdm.backend.common.types.BinaryLargeValue;
import com.unidata.mdm.backend.common.types.impl.BlobSimpleAttributeImpl;

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

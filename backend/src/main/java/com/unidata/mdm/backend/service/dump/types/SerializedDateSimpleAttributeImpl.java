package com.unidata.mdm.backend.service.dump.types;

import java.time.LocalDate;

import com.unidata.mdm.backend.common.types.impl.DateSimpleAttributeImpl;

/**
 * @author Mikhail Mikhailov
 * Date attribute.
 */
public class SerializedDateSimpleAttributeImpl extends DateSimpleAttributeImpl implements VerifyableSimpleAttribute<LocalDate> {
    /**
     * Constructor.
     */
    public SerializedDateSimpleAttributeImpl() {
        super();
    }
}

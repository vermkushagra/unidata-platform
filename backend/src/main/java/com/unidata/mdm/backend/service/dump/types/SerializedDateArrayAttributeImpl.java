package com.unidata.mdm.backend.service.dump.types;

import java.time.LocalDate;

import com.unidata.mdm.backend.common.types.impl.DateArrayAttributeImpl;

/**
 * @author Mikhail Mikhailov
 * Date array attribute.
 */
public class SerializedDateArrayAttributeImpl extends DateArrayAttributeImpl implements VerifyableArrayAttribute<LocalDate> {
    /**
     * Constructor.
     */
    public SerializedDateArrayAttributeImpl() {
        super();
    }
}

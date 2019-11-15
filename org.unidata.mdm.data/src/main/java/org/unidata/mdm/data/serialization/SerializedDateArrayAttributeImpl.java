package org.unidata.mdm.data.serialization;

import java.time.LocalDate;

import org.unidata.mdm.core.type.data.impl.DateArrayAttributeImpl;

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

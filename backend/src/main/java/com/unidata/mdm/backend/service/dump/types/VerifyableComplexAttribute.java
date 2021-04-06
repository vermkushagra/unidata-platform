package com.unidata.mdm.backend.service.dump.types;

import java.util.Objects;

import com.unidata.mdm.backend.common.types.ComplexAttribute;

/**
 * @author Mikhail Mikhailov
 * Complex attribute, which can verify itself.
 */
public interface VerifyableComplexAttribute extends ComplexAttribute {
    /**
     * Default check for simple attributes.
     * @return true, if name is set, false otherwise
     */
    default boolean isValid() {
        return Objects.nonNull(getName());
    }
}

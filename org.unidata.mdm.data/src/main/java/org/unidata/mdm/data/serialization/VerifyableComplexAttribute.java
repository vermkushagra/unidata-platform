package org.unidata.mdm.data.serialization;

import java.util.Objects;

import org.unidata.mdm.core.type.data.ComplexAttribute;

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

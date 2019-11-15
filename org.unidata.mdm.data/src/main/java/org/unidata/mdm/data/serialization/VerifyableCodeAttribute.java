package org.unidata.mdm.data.serialization;

import java.util.Objects;

import org.unidata.mdm.core.type.data.CodeAttribute;

/**
 * @author Mikhail Mikhailov
 * Simple attribute, which can verify itself.
 */
public interface VerifyableCodeAttribute<T> extends CodeAttribute<T> {
    /**
     * Default check for simple attributes.
     * @return true, if name is set, false otherwise
     */
    default boolean isValid() {
        return Objects.nonNull(getName()) && Objects.nonNull(getValue());
    }
}

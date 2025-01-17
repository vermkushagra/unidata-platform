package com.unidata.mdm.backend.service.dump.types;

import java.util.Objects;

import com.unidata.mdm.backend.common.types.ArrayAttribute;

/**
 * @author Mikhail Mikhailov
 * Array attribute, which can verify itself.
 */
public interface VerifyableArrayAttribute<T> extends ArrayAttribute<T> {
    /**
     * Default check for array attributes.
     * @return true, if name is set, false otherwise
     */
    default boolean isValid() {
        return Objects.nonNull(getName());
    }
}

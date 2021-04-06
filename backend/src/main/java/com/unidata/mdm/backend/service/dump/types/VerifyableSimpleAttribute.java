package com.unidata.mdm.backend.service.dump.types;

import java.util.Objects;

import com.unidata.mdm.backend.common.types.SimpleAttribute;

/**
 * @author Mikhail Mikhailov
 * Simple attribute, which can verify itself.
 */
public interface VerifyableSimpleAttribute<T> extends SimpleAttribute<T> {
    /**
     * Default check for simple attributes.
     * @return true, if name is set, false otherwise
     */
    default boolean isValid() {
        return Objects.nonNull(getName());
    }
}

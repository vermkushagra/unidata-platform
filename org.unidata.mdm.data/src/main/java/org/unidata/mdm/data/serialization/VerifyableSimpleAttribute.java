package org.unidata.mdm.data.serialization;

import java.util.Objects;

import org.unidata.mdm.core.type.data.SimpleAttribute;

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

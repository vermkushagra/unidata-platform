package org.unidata.mdm.data.serialization;

import java.util.Objects;

import org.unidata.mdm.core.type.data.ArrayAttribute;

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

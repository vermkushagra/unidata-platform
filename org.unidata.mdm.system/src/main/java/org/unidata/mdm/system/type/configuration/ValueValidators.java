package org.unidata.mdm.system.type.configuration;

import java.util.Optional;
import java.util.function.Predicate;

public final class ValueValidators {
    private ValueValidators() {}

    public static final Predicate<Optional<String>> INT_VALIDATOR =
            value -> value.isPresent() && value.get().matches("\\d+");

    public static final Predicate<Optional<String>> DOUBLE_VALIDATOR =
            value -> value.isPresent() && value.get().matches("\\d+.\\d+");

    public static final Predicate<Optional<String>> STRING_VALIDATOR = Optional::isPresent;

    public static final Predicate<Optional<String>> BOOLEAN_VALIDATOR =
            value -> value.isPresent() && (
                    value.get().equalsIgnoreCase("true")
                    || value.get().equalsIgnoreCase("false"));

    public static final Predicate<Optional<String>> ANY_VALID = value -> true;
}

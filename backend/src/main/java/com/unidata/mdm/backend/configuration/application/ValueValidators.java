package com.unidata.mdm.backend.configuration.application;

import com.google.common.base.Optional;
import java.util.function.Predicate;

public final class ValueValidators {
    private ValueValidators() {}

    public static final Predicate<Optional<String>> INT_VALIDATOR =
            (value) -> value.isPresent() && value.get().matches("\\d+");

    public static final Predicate<Optional<String>> STRING_VALIDATOR =
            Optional::isPresent;

    public static final Predicate<Optional<String>> BOOLEAN_VALIDATOR =
            (value) -> value.isPresent() && (
                    value.get().equalsIgnoreCase("true")
                    || value.get().equalsIgnoreCase("false"));
}

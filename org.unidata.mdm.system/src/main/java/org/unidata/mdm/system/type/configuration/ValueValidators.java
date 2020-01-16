/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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

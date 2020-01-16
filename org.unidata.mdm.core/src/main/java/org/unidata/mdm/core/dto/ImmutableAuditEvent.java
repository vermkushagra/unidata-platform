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

package org.unidata.mdm.core.dto;

import org.unidata.mdm.core.type.audit.AuditEvent;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alexander Malyshev
 */
public class ImmutableAuditEvent implements AuditEvent {

    private final String type;

    private final Map<String, String> parameters = new HashMap<>();

    private final boolean success;

    public ImmutableAuditEvent(
            final String type,
            final Map<String, String> parameters,
            final boolean success
    ) {
        this.type = Objects.requireNonNull(type);
        if (parameters != null) {
            this.parameters.putAll(parameters);
        }
        this.success = success;
    }

    @Nonnull
    @Override
    public String type() {
        return type;
    }

    @Override
    public Map<String, String> parameters() {
        return Collections.unmodifiableMap(parameters);
    }

    @Override
    public boolean success() {
        return success;
    }
}

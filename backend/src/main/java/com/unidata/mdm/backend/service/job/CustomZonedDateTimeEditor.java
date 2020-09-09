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

package com.unidata.mdm.backend.service.job;

import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author Denis Kostovarov
 */
public class CustomZonedDateTimeEditor extends PropertyEditorSupport {

    public CustomZonedDateTimeEditor() {
        // no-op.
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(parseText(text));
    }

    @Override
    public String getAsText() {
        final ZonedDateTime value = parseText(String.valueOf(getValue()));
        return (value != null ? value.toString() : "");
    }

    private ZonedDateTime parseText(String text) {
        ZonedDateTime zdt;
        if (StringUtils.isEmpty(text)) {
            zdt = ZonedDateTime.now(ZoneId.systemDefault());
        } else {
            try {
                zdt = ZonedDateTime.parse(text);
            } catch(Exception ee) {
                zdt = null;
            }
        }

        return zdt;
    }
}

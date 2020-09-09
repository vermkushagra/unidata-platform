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

import static java.util.Objects.isNull;

import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemWriter;
import org.springframework.context.MessageSource;

import com.unidata.mdm.backend.common.context.UpsertRelationsRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.util.MessageUtils;

/**
 * Contains all necessary operations for handling
 *
 * @param <T>
 */
public abstract class AbstractUnidataWriter<T> implements ItemWriter<T> {
    protected String getErrorMessage(Exception e) {
        return MessageUtils.getExceptionMessage(e);
    }

    protected String getErrorMessage(Exception e, UpsertRequestContext context) {
        if (isNull(context.getDqErrors()) || context.getDqErrors().isEmpty()) {
            return getErrorMessage(e);
        } else {
            return context.getDqErrors().stream()
                    .map(DataQualityError::getMessage)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));
        }
    }

    protected String getErrorMessage(Exception e, UpsertRelationsRequestContext context) {
        if (isNull(context.getDqErrors()) || context.getDqErrors().isEmpty()) {
            return getErrorMessage(e);
        } else {
            return context.getDqErrors().stream()
                    .map(DataQualityError::getMessage)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));
        }
    }

    public void setMessageSource(MessageSource messageSource) {
    }
}

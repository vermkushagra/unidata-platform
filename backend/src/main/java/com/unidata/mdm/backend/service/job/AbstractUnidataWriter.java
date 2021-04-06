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

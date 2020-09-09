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

package com.unidata.mdm.backend.validator;

import org.springframework.util.Assert;
import org.springframework.validation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yashin. Created on 03.03.2015.
 */
public class ValidationErrors extends AbstractErrors {

    private String objectName;
    private List<ObjectError> errors = new ArrayList<>();
    private List<FieldError> fieldErrors = new ArrayList<>();
    private MessageCodesResolver messageCodesResolver = new DefaultMessageCodesResolver();

    public ValidationErrors(String objectName) {
        this.objectName = objectName;
    }

    @Override
    public String getObjectName() {
        return objectName;
    }

    @Override
    public void reject(String errorCode, Object[] errorArgs, String defaultMessage) {
        String[] codes = new String[]{errorCode};
        errors.add(new ObjectError(objectName, codes, errorArgs, defaultMessage));
    }

    @Override
    public void rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage) {
        fieldErrors.add(new FieldError(objectName, field, null, false, resolveMessageCodes(errorCode, field), errorArgs, defaultMessage));
    }

    @Override
    public void addAllErrors(Errors errors) {
        //TODO
    }

    @Override
    public List<ObjectError> getGlobalErrors() {
        return errors;
    }

    @Override
    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    @Override
    public Object getFieldValue(String field) {
        return null;
    }

    /**
     * Set the strategy to use for resolving errors into message codes.
     * Default is DefaultMessageCodesResolver.
     * @see DefaultMessageCodesResolver
     */
    public void setMessageCodesResolver(MessageCodesResolver messageCodesResolver) {
        Assert.notNull(messageCodesResolver, "MessageCodesResolver must not be null");
        this.messageCodesResolver = messageCodesResolver;
    }

    /**
     * Return the strategy to use for resolving errors into message codes.
     */
    public MessageCodesResolver getMessageCodesResolver() {
        return this.messageCodesResolver;
    }


    public String[] resolveMessageCodes(String errorCode, String field) {
        return getMessageCodesResolver().resolveMessageCodes(errorCode, objectName, field, null);
    }
}

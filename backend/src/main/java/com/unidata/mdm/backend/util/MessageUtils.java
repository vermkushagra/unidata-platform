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

package com.unidata.mdm.backend.util;

import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import com.unidata.mdm.backend.common.configuration.ConfigurationConstants;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.common.security.SecurityToken;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

/**
 * @author Michael Yashin. Created on 29.03.2015.
 */
public class MessageUtils {
    /**
     * Default fallback locale.
     */
    private static Locale defaultSystemLocale;
    /**
     * The message source.
     */
    private static MessageSource messageSource;
    /**
     * Constructor.
     */
    private MessageUtils() {
        super();
    }
    /**
     * Convenient init method.
     */
    public static void init(ApplicationContext ctx) {

        messageSource = ctx;

        String systemLocale = null;
        if (ctx.containsBean(ConfigurationConstants.UNIDATA_PROPERTIES_BEAN_NAME)) {
            Properties propsBean = ctx.getBean(ConfigurationConstants.UNIDATA_PROPERTIES_BEAN_NAME, Properties.class);
            systemLocale = propsBean.getProperty(ConfigurationConstants.DEFAULT_LOCALE_PROPERTY, "ru");
        } else {
            systemLocale = ctx.getEnvironment().getProperty(ConfigurationConstants.DEFAULT_LOCALE_PROPERTY, "ru");
        }

        defaultSystemLocale = new Locale(systemLocale);
    }
    /**
     * Select locale for translation.
     * @return locale the selected locale
     */
    public static Locale getCurrentLocale() {

        SecurityToken token = SecurityUtils.getSecurityTokenForCurrentUser();
        return token != null && token.getUser().getLocale() != null
                ? token.getUser().getLocale()
                : defaultSystemLocale;
    }
    /**
     * Gets translation for supplied code.
     * @param code the code
     * @param args optional arguments
     * @return translation
     */
    public static String getMessage(String code, Object... args) {
        return getMessageWithDefault(code, "ххх" + code + "ххх", args);
    }
    /**
     * Gets translation for supplied code, returning default value, if the translation was not found.
     * @param code the code
     * @param defaultMessage fallback to use
     * @param args optional arguments
     * @return translation
     */
    public static String getMessageWithDefault(String code, String defaultMessage, Object... args) {
        return messageSource.getMessage(code, args, defaultMessage, getCurrentLocale());
    }

    public static String getMessageWithLocaleAndDefault(Locale locale, String code, String defaultMessage, Object... args) {
        return messageSource.getMessage(code, args, defaultMessage, locale);
    }

    /**
     * Extracts code and does translation, if thic exception is a {@link SystemRuntimeException}, prints stack trace otherwise.
     * @param exception the exception to process
     * @return message
     */
    public static String getExceptionMessage(Exception exception) {
        if (exception instanceof SystemRuntimeException) {
            SystemRuntimeException systemRuntimeException = (SystemRuntimeException) exception;
            return MessageUtils.getMessage(systemRuntimeException.getId().getCode(), systemRuntimeException.getArgs());
        } else {
            return ExceptionUtils.getStackTrace(exception);
        }
    }
    /**
     * Gets a enum label translation.
     * @param en enum label
     * @return translation
     */
    public static String getEnumTranslation(Enum<?> en) {

        if (en == null) {
            return StringUtils.EMPTY;
        }

        return getMessage("enum." + en.name());
    }
}

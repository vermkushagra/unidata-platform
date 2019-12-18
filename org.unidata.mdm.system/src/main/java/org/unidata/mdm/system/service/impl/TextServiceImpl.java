package org.unidata.mdm.system.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.unidata.mdm.system.configuration.SystemConfigurationConstants;
import org.unidata.mdm.system.exception.PlatformFailureException;
import org.unidata.mdm.system.exception.PlatformRuntimeException;
import org.unidata.mdm.system.exception.SystemExceptionIds;
import org.unidata.mdm.system.service.TextService;

/**
 * @author Mikhail Mikhailov on Dec 18, 2019
 */
@Service
public class TextServiceImpl implements TextService {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TextServiceImpl.class);
    /**
     * Current locale method handle.
     */
    protected static final Method CURRENT_USER_LOCALE_METHOD;
    /**
     * Default fallback locale.
     */
    @Value("${" + SystemConfigurationConstants.UNIDATA_DEFAULT_LOCALE + ":ru}")
    protected Locale defaultLocale;
    /**
     * The underlaying message source.
     */
    protected MessageSource messageSource;
    /**
     * SI.
     */
    static {

        Method method = null;
        try {
            Class<?> klass = Class.forName("org.unidata.mdm.core.util.SecurityUtils");
            method = klass.getMethod("getCurrentUserLocale");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new PlatformFailureException(
                    "Reflection failure [org.unidata.mdm.core.util.SecurityUtils.getCurrentUserLocale].",
                    e, SystemExceptionIds.EX_SYSTEM_SECURITY_UTILS_CLASS);
        }

        CURRENT_USER_LOCALE_METHOD = method;
    }

    /**
     * Constructor.
     * @param messageSource the MS to use
     */
    protected TextServiceImpl(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Locale getDefaultLocale() {
        return defaultLocale;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Locale getCurrentLocale() {

        Locale current  = null;
        if (CURRENT_USER_LOCALE_METHOD != null) {
            try {
                current = (Locale) CURRENT_USER_LOCALE_METHOD.invoke(null, ArrayUtils.EMPTY_OBJECT_ARRAY);
            } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                LOGGER.warn("Reflection failure.", e);
            }
        }

        return current != null ? current : defaultLocale;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getText(String code, Object... args) {
        return getTextWithDefault(code, code, args);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getTextWithDefault(String code, String defaultMessage, Object... args) {
        return getTextWithLocaleAndDefault(getCurrentLocale(), code, defaultMessage, args);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getTextWithLocaleAndDefault(Locale locale, String code, String defaultMessage, Object... args) {
        return messageSource.getMessage(code, args, defaultMessage, locale);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getText(Enum<?> en) {
        return getTextWithLocaleAndDefault(getCurrentLocale(), en, en != null
                ? (en.getClass().getCanonicalName() + "." + en.name())
                : StringUtils.EMPTY);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getTextWithLocaleAndDefault(Locale locale, Enum<?> en, String defaultMessage) {

        if (en == null) {
            return StringUtils.EMPTY;
        }

        return getTextWithLocaleAndDefault(locale, en.getClass().getCanonicalName() + "." + en.name(), defaultMessage);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getText(Exception exception) {

        if (exception instanceof PlatformRuntimeException) {
            PlatformRuntimeException pre = (PlatformRuntimeException) exception;
            return getTextWithDefault(pre.getId().message(), pre.getMessage(), pre.getArgs());
        } else {
            return ExceptionUtils.getStackTrace(exception);
        }
    }
}

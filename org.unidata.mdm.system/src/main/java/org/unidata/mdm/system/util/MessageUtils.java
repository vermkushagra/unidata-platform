package org.unidata.mdm.system.util;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.unidata.mdm.system.exception.PlatformRuntimeException;

/**
 * system module message utils
 *
 * @author maria.chistyakova
 * @since  05.11.2019
 */
@Component
public class MessageUtils {
    /**
     * Default fallback locale.
     */
    @Value("${unidata.default.locale:ru}")
    protected static Locale defaultSystemLocale;
    /**
     * The message source.
     */
    private final MessageSource messageSource;

    public MessageUtils(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    /**
     * Get default system locale.
     * @return locale default system locale.
     */
    public Locale getDefaultSystemLocale() {
        return defaultSystemLocale;
    }

    /**
     * Gets translation for supplied code.
     * @param code the code
     * @param args optional arguments
     * @return translation
     */
    public String getMessage(String code, Object... args) {
        return getMessageWithDefault(code, code, args);
    }

    /**
     * Gets translation for supplied code, returning default value, if the translation was not found.
     * @param code the code
     * @param defaultMessage fallback to use
     * @param args optional arguments
     * @return translation
     */
    public String getMessageWithDefault(String code, String defaultMessage, Object... args) {
        return messageSource.getMessage(code, args, defaultMessage, getDefaultSystemLocale());
    }

    public String getMessageWithLocaleAndDefault(Locale locale, String code, String defaultMessage, Object... args) {
        return messageSource.getMessage(code, args, defaultMessage, locale);
    }

    public String getEnumTranslationWithLocaleAndDefault(Locale locale, Enum<?> en, String defaultMessage) {
        if (en == null) {
            return StringUtils.EMPTY;
        }

        return getMessageWithLocaleAndDefault(locale, "enum." + en.name(), defaultMessage);
    }

    /**
     * Extracts code and does translation, if thic exception is a {@link PlatformRuntimeException}, prints stack trace otherwise.
     * @param exception the exception to process
     * @return message
     */
    public String getExceptionMessage(Exception exception) {
        if (exception instanceof PlatformRuntimeException) {
            PlatformRuntimeException systemRuntimeException = (PlatformRuntimeException) exception;
            return getMessage(systemRuntimeException.getId().code(), systemRuntimeException.getArgs());
        } else {
            return ExceptionUtils.getStackTrace(exception);
        }
    }

    /**
     * Gets a enum label translation.
     * @param en enum label
     * @return translation
     */
    public String getEnumTranslation(Enum<?> en) {
        if (en == null) {
            return StringUtils.EMPTY;
        }

        return getMessage("enum." + en.name());
    }
}

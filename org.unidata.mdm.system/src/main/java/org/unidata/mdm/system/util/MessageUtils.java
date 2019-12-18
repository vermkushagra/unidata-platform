package org.unidata.mdm.system.util;

import java.util.Locale;

import org.unidata.mdm.system.configuration.SystemConfiguration;
import org.unidata.mdm.system.exception.PlatformRuntimeException;
import org.unidata.mdm.system.service.TextService;

/**
 * system module message utils
 *
 * @author maria.chistyakova
 * @since  05.11.2019
 */
public class MessageUtils {
    /**
     * The TT instance.
     */
    private static TextService textService;
    /**
     * Constructor.
     */
    private MessageUtils() {
        super();
    }
    /**
     * Public "internal" init method.
     */
    public static void init() {
        textService = SystemConfiguration.getBean(TextService.class);
    }
    /**
     * Get default system locale.
     * @return locale default system locale.
     */
    public static Locale getDefaultLocale() {
        return textService.getDefaultLocale();
    }
    /**
     * Get current user's locale.
     * @return locale default system locale.
     */
    public static Locale getCurrentLocale() {
        return textService.getCurrentLocale();
    }
    /**
     * Gets translation for supplied code.
     * @param code the code
     * @param args optional arguments
     * @return translation
     */
    public static String getMessage(String code, Object... args) {
        return textService.getText(code, args);
    }
    /**
     * Gets translation for supplied code, returning default value, if the translation was not found.
     * @param code the code
     * @param defaultMessage fallback to use
     * @param args optional arguments
     * @return translation
     */
    public static String getMessageWithDefault(String code, String defaultMessage, Object... args) {
        return textService.getTextWithDefault(code, defaultMessage, args);
    }

    public static String getMessageWithLocaleAndDefault(Locale locale, String code, String defaultMessage, Object... args) {
        return textService.getTextWithLocaleAndDefault(locale, code, defaultMessage, args);
    }

    public static String getEnumTranslationWithLocaleAndDefault(Locale locale, Enum<?> en, String defaultMessage) {
        return textService.getTextWithLocaleAndDefault(locale, en, defaultMessage);
    }
    /**
     * Extracts code and does translation, if thic exception is a {@link PlatformRuntimeException}, prints stack trace otherwise.
     * @param exception the exception to process
     * @return message
     */
    public static String getExceptionMessage(Exception exception) {
        return textService.getText(exception);
    }
    /**
     * Gets a enum label translation.
     * @param en enum label
     * @return translation
     */
    public static String getEnumTranslation(Enum<?> en) {
        return textService.getText(en);
    }
}

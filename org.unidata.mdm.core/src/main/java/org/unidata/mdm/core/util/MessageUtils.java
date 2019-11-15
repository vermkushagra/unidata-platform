package org.unidata.mdm.core.util;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.security.SecurityToken;

/**
 * @author Michael Yashin. Created on 29.03.2015.
 */
@Component
public class MessageUtils extends org.unidata.mdm.system.util.MessageUtils {


    public MessageUtils(MessageSource messageSource) {
        super(messageSource);
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

}

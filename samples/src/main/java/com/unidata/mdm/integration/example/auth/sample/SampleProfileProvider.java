package com.unidata.mdm.integration.example.auth.sample;

import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.unidata.mdm.backend.common.integration.auth.ProfileProvider;
import com.unidata.mdm.backend.common.integration.auth.SecurityDataProviderException;
import com.unidata.mdm.backend.common.integration.auth.User;

/**
 * @author Denis Kostovarov
 */
public class SampleProfileProvider implements ProfileProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(SampleProfileProvider.class);

    @Override
    public void load(final User user) throws SecurityDataProviderException {
        LOGGER.info("Fetching sample profile for external user");

        Assert.isTrue(user instanceof SampleUserInfo);

        if ("lionell".equals(user.getLogin()) && user instanceof SampleUserInfo) {
            final SampleUserInfo sui = (SampleUserInfo) user;
            sui.setEmail("sample@sample.com");
            sui.setLocale(new Locale("ru"));
            sui.setName("Внешний администратор Lionell Messi");
            sui.setAdmin(true);
            sui.setHasProfile(true);
            //set always new date
            sui.setUpdatedAt(new Date());

            LOGGER.info("User: " + sui.getLogin());
        }

    }
}

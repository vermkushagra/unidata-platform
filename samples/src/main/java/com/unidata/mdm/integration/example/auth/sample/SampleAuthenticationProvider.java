package com.unidata.mdm.integration.example.auth.sample;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.unidata.mdm.backend.common.context.AuthenticationRequestContext;
import com.unidata.mdm.backend.common.integration.auth.AuthenticationProvider;
import com.unidata.mdm.backend.common.integration.auth.AuthenticationSystemParameter;
import com.unidata.mdm.backend.common.integration.auth.Endpoint;
import com.unidata.mdm.backend.common.integration.auth.SecurityDataProviderException;
import com.unidata.mdm.backend.common.integration.auth.SecurityState;
import com.unidata.mdm.backend.common.integration.auth.User;

/**
 * @author Denis Kostovarov
 */
public class SampleAuthenticationProvider implements AuthenticationProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(SampleAuthenticationProvider.class);

    public enum SampleEndpoint implements Endpoint {
        REST("REST"),
        SOAP("SOAP");

        private SampleEndpoint(String name) {
            this.name = name;
        }

        private String name;
        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDisplayName() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getDescription() {
            // TODO Auto-generated method stub
            return null;
        }
    }

    /**
     * If a user can not be authenticated, null must be returned.
     */
    @SuppressWarnings("unused")
    @Override
    public User login(AuthenticationRequestContext context) {
        LOGGER.info("Authenticating user against AuthenticationSample");

        Map<AuthenticationSystemParameter, Object> params = context.getParams();
        HttpServletRequest hsr = (HttpServletRequest) params.get(AuthenticationSystemParameter.PARAM_HTTP_SERVLET_REQUEST);
        String externalToken = (String) params.get(AuthenticationSystemParameter.PARAM_EXTERNAL_TOKEN);
        String username = (String) params.get(AuthenticationSystemParameter.PARAM_USER_NAME);
        String password = (String) params.get(AuthenticationSystemParameter.PARAM_USER_PASSWORD);

        final SampleUserInfo sampleUserInfo = new SampleUserInfo();

        if (StringUtils.isEmpty(externalToken) && StringUtils.isEmpty(username)) {
            if (hsr != null) {
                // Do some magic with HttpServletRequest and cookies
                final Cookie[] cookies = hsr.getCookies();
                if (cookies != null) {
                    for (final Cookie cookie : cookies) {
                        final String domain = cookie.getDomain();
                        final String name = cookie.getName();
                    }
                }
            } else {
                throw new SecurityDataProviderException(SecurityState.AUTHENTICATION_FAILED, "No username or HttpServletRequest provided");
            }
        }
        else if (!StringUtils.isEmpty(externalToken)) {
            if ("externa1T0ken".equals(externalToken)) {
                sampleUserInfo.setLogin("batman");
                sampleUserInfo.setPassword("jocker");
                sampleUserInfo.setEmail("test@test.com"); // Mandatory. Why?
                sampleUserInfo.setLocale(new Locale("ru"));
                sampleUserInfo.setEndpoints(Arrays.asList(SampleEndpoint.REST, SampleEndpoint.SOAP));

                return sampleUserInfo;
            }
        }
        else {

            if ("lionell".equals(username)) {

                if ("messi".equals(password)) {
                    sampleUserInfo.setLogin(username);
                    sampleUserInfo.setPassword(password);
                    sampleUserInfo.setEmail("test@test.com"); // Mandatory. Why?
                    sampleUserInfo.setLocale(new Locale("ru"));
                    sampleUserInfo.setEndpoints(Arrays.asList(SampleEndpoint.REST, SampleEndpoint.SOAP));

                    return sampleUserInfo;
                }

                throw new SecurityDataProviderException(SecurityState.AUTHENTICATION_FAILED, "Invalid user name or password provided");
            }
        }

        return null;
    }
}

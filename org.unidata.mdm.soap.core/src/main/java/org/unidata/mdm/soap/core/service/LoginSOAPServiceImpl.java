package org.unidata.mdm.soap.core.service;


import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.annotations.SchemaValidation;
import org.apache.cxf.annotations.SchemaValidation.SchemaValidationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.unidata.mdm.core.service.SecurityService;
import org.unidata.mdm.core.type.security.AuthenticationSystemParameter;
import org.unidata.mdm.core.type.security.EndpointType;
import org.unidata.mdm.core.type.security.SecurityToken;
import org.unidata.mdm.error_handling.v1.ApiFaultType;
import org.unidata.mdm.login.api.v1.ApiFault;
import org.unidata.mdm.login.api.v1.LoginPortImpl;
import org.unidata.mdm.security.v1.CredentialsDef;
import org.unidata.mdm.security.v1.InfoType;
import org.unidata.mdm.security.v1.SessionTokenDef;
import org.unidata.mdm.system.util.ClientIpUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.util.EnumMap;
import java.util.Map;

/**
 * The Class LoginSOAPServiceImpl.
 */
@SchemaValidation(type = SchemaValidationType.NONE)
public class LoginSOAPServiceImpl extends LoginPortImpl {

    /** The security service. */
    @Autowired
    private SecurityService securityService;

    /** The jaxws context. */
    @Resource
    private WebServiceContext jaxwsContext;

    /* (non-Javadoc)
     * @see com.unidata.mdm.login.api.v5.LoginPortImpl#login(com.unidata.mdm.api.v5.CredentialsDef, javax.xml.ws.Holder)
     */
    @Override
    public SessionTokenDef login(CredentialsDef request, Holder<InfoType> info) throws ApiFault {
        if (StringUtils.isEmpty(request.getUsername()) || StringUtils.isEmpty(request.getPassword())) {
            throw new ApiFault("Username or password not provided!", new ApiFaultType().withErrorCode("INVALID_LOGIN")
                    .withErrorMessage("Username or password not provided!"));
        }
        SessionTokenDef result = new SessionTokenDef();
        SecurityToken securityToken = securityService
                .login(fillSecurityParams(null, request.getUsername(), request.getPassword(), "Вход"));
        result.setToken(securityToken.getToken());
        return result;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.login.api.v5.LoginPortImpl#logout(com.unidata.mdm.api.v5.SessionTokenDef, javax.xml.ws.Holder)
     */
    @Override
    public SessionTokenDef logout(SessionTokenDef request, Holder<InfoType> info) throws ApiFault {
        String token = request.getToken();
        if (StringUtils.isEmpty(token)) {
            throw new ApiFault("Token not provided!",
                    new ApiFaultType().withErrorCode("INVALID_TOKEN").withErrorMessage("Token not provided!"));
        }
        boolean isLoggedOut = securityService.logout(request.getToken(),
                fillSecurityParams(request.getToken(), null, null, "Самостоятельный выход"));
        if (!isLoggedOut) {
            throw new ApiFault("Token invalid!",
                    new ApiFaultType().withErrorCode("INVALID_TOKEN").withErrorMessage("Token invalid!"));
        }
        return request;
    }

    /**
     * Fill security params.
     *
     * @param token the token
     * @param userName the user name
     * @param password the password
     * @param details the details
     * @return the map
     */
    private Map<AuthenticationSystemParameter, Object> fillSecurityParams(String token, String userName,
                                                                          String password, String details) {
        HttpServletRequest h = (HttpServletRequest) jaxwsContext.getMessageContext()
                .get(MessageContext.SERVLET_REQUEST);
        if (StringUtils.isEmpty(userName)) {
            userName = securityService.getUserByToken(token).getLogin();
        }
        Map<AuthenticationSystemParameter, Object> params = new EnumMap<>(AuthenticationSystemParameter.class);
        params.put(AuthenticationSystemParameter.PARAM_USER_NAME, userName);
        if (!StringUtils.isEmpty(password)) {
            params.put(AuthenticationSystemParameter.PARAM_USER_PASSWORD, password);
        }
        params.put(AuthenticationSystemParameter.PARAM_HTTP_SERVLET_REQUEST, h);
        params.put(AuthenticationSystemParameter.PARAM_CLIENT_IP, ClientIpUtil.clientIp(h));
        params.put(AuthenticationSystemParameter.PARAM_SERVER_IP, h.getLocalAddr());
        params.put(AuthenticationSystemParameter.PARAM_ENDPOINT, EndpointType.SOAP);
        params.put(AuthenticationSystemParameter.PARAM_DETAILS, details);
        return params;
    }
}

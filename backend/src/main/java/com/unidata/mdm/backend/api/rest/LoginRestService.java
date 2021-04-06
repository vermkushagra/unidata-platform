package com.unidata.mdm.backend.api.rest;

import static com.unidata.mdm.backend.api.rest.converter.UsersConverter.convertUserDTO;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import com.unidata.mdm.backend.api.rest.converter.UsersConverter;
import com.unidata.mdm.backend.api.rest.dto.security.LicenseRO;
import com.unidata.mdm.backend.api.rest.dto.security.UserRO;
import com.unidata.mdm.backend.common.dto.security.UserWithPasswordDTO;
import com.unidata.mdm.backend.service.security.utils.LicenseHolder;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.LoginRequest;
import com.unidata.mdm.backend.api.rest.dto.LoginResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.SetPasswordRequest;
import com.unidata.mdm.backend.api.rest.dto.UpdateResponse;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.integration.auth.AuthenticationSystemParameter;
import com.unidata.mdm.backend.common.integration.auth.User;
import com.unidata.mdm.backend.common.runtime.MeasurementContextName;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.security.Endpoint;
import com.unidata.mdm.backend.common.security.SecurityToken;
import com.unidata.mdm.backend.service.security.SecurityServiceExt;
import com.unidata.mdm.backend.service.security.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The Class AuthenticationRestService.
 */
@Path("/authentication")
@Api(value = "login", description = "Аутентификация", produces = "application/json")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class LoginRestService extends AbstractRestService {

    /** The security service. */
    @Autowired
    private SecurityServiceExt securityService;

    /** The user service. */
    @Autowired
    private UserService userService;

    /**
     * Login.
     *
     * @param loginRequest
     *            the login request
     * @return the response
     * @throws Exception
     *             the exception
     */
    @POST
    @ApiOperation(value = "Создание нового токена", notes = "", response = LoginResponse.class)
    @Path(value = "/login")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response login(LoginRequest loginRequest){
        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_AUTH);
        MeasurementPoint.start();

        LoginResponse loginResponse = new LoginResponse();
        String login = loginRequest.getUserName();
        Map<AuthenticationSystemParameter, Object> params = new EnumMap<>(AuthenticationSystemParameter.class);
        params.put(AuthenticationSystemParameter.PARAM_USER_NAME, login);
        params.put(AuthenticationSystemParameter.PARAM_USER_PASSWORD, loginRequest.getPassword());
        params.put(AuthenticationSystemParameter.PARAM_CLIENT_IP, getClientIp());
        params.put(AuthenticationSystemParameter.PARAM_SERVER_IP, getServerIp());
        params.put(AuthenticationSystemParameter.PARAM_ENDPOINT, Endpoint.REST);
        params.put(AuthenticationSystemParameter.PARAM_HTTP_SERVLET_REQUEST, getHSR());

        try {
            final SecurityToken token = securityService.login(params);

            loginResponse.setToken(token.getToken());
            final UserWithPasswordDTO user = userService.getUserByName(token.getUser().getLogin());
            final UserRO userInfo = convertUserDTO(user);
            userInfo.setSecurityLabels(
                    UsersConverter.convertSecurityLabelDTOs(
                            SecurityUtils.mergeSecurityLabels(
                                    user.getSecurityLabels(),
                                    user.getRoles().stream()
                                            .flatMap(r -> r.getSecurityLabels().stream())
                                            .collect(Collectors.toList())
                            )
                    )
            );

            loginResponse.setUserInfo(userInfo);
            loginResponse.setRights(new ArrayList<>(token.getRightsMap().values()));
            loginResponse.setTokenTTL(securityService.getTokenTTL());
            loginResponse.setForcePasswordChange(token.getUser().getForcePasswordChangeFlag());

            LicenseRO licenseRO = new LicenseRO();
            licenseRO.setExpirationDate(LicenseHolder.getExpirationDate());
            licenseRO.setVersion(LicenseHolder.getVersion());
            loginResponse.setLicense(licenseRO);

            return ok(new RestResponse<>(loginResponse));
        }catch (org.springframework.transaction.CannotCreateTransactionException|org.springframework.transaction.TransactionSystemException e) {
        	//UN-3952
			throw new BusinessException("Cannot connect to database.", ExceptionId.EX_SYSTEM_DATABASE_CANNOT_CONNECT);
		} finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Returns info about current user.
     *
     * @return Information about current logged in user.
     * @throws Exception
     *             In case of some technical problem.
     */
    @GET
    @ApiOperation(value = "Возвращает информацию о текущем пользователе", notes = "", response = LoginResponse.class)
    @Path(value = "/get-current-user")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response getCurrentUserInfo(){

        LoginResponse loginResponse = new LoginResponse();
        String tokenString = null;
        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null) {
            tokenString = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }

        if (tokenString == null) {
            return notAuthorized(loginResponse);
        }

        final SecurityToken token = securityService.getTokenObjectByToken(tokenString);
        loginResponse.setForcePasswordChange(token.getUser().getForcePasswordChangeFlag());
        loginResponse.setToken(tokenString);
        loginResponse.setUserInfo(convertUserDTO(userService.getUserByName(token.getUser().getLogin())));
        loginResponse.setRights(new ArrayList<>(token.getRightsMap().values()));
        loginResponse.setTokenTTL(securityService.getTokenTTL());

        LicenseRO licenseRO = new LicenseRO();
        licenseRO.setExpirationDate(LicenseHolder.getExpirationDate());
        licenseRO.setVersion(LicenseHolder.getVersion());
        loginResponse.setLicense(licenseRO);
        return ok(loginResponse);
    }

    /**
     * Set password.
     *
     * @param setPasswordRequest
     *            the set password request
     * @return the response
     * @throws Exception
     *             the exception
     */
    @POST
    @ApiOperation(value = "Задание нового пароля пользователя", notes = "", response = UpdateResponse.class)
    @Path(value = "/setpassword")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response setPassword(SetPasswordRequest setPasswordRequest) throws Exception {
        boolean result = securityService.updatePassword(SecurityUtils.getCurrentUserName(), setPasswordRequest.getPassword(),
                setPasswordRequest.getOldPassword());
        return ok(new RestResponse<>(new UpdateResponse(result, setPasswordRequest.getUserName())));
    }

    /**
     * Logout.
     *
     * @param tokenString
     *            the token string
     * @return the response
     * @throws Exception
     *             the exception
     */
    @POST
    @ApiOperation(value = "Инвалидация токена", notes = "", response = String.class, responseContainer = "String")
    @Path(value = "/logout")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 202, message = "Request accepted"), @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response logout(@HeaderParam(value = HttpHeaders.AUTHORIZATION) String tokenString) throws Exception {
        User user = securityService.getUserByToken(tokenString);
        String userName = user != null ? user.getLogin() : null;
        Map<AuthenticationSystemParameter, Object> params = new EnumMap<>(AuthenticationSystemParameter.class);
        params.put(AuthenticationSystemParameter.PARAM_USER_NAME, userName);
        params.put(AuthenticationSystemParameter.PARAM_CLIENT_IP, getClientIp());
        params.put(AuthenticationSystemParameter.PARAM_SERVER_IP, getServerIp());
        params.put(AuthenticationSystemParameter.PARAM_ENDPOINT, Endpoint.REST);
        params.put(AuthenticationSystemParameter.PARAM_HTTP_SERVLET_REQUEST, getHSR());
        params.put(AuthenticationSystemParameter.PARAM_DETAILS, "Самостоятельный выход");
        securityService.logout(tokenString, params);
        return Response.accepted().build();
    }
}
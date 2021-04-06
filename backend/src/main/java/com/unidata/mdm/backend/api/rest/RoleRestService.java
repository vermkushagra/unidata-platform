package com.unidata.mdm.backend.api.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import com.unidata.mdm.backend.api.rest.converter.RolesConverter;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.UpdateResponse;
import com.unidata.mdm.backend.api.rest.dto.security.RolePropertyRO;
import com.unidata.mdm.backend.api.rest.dto.security.RoleRO;
import com.unidata.mdm.backend.common.dto.security.RolePropertyDTO;
import com.unidata.mdm.backend.common.dto.security.SecuredResourceDTO;
import com.unidata.mdm.backend.common.integration.auth.Role;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;
import com.unidata.mdm.backend.service.security.RoleServiceExt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The Class RoleRestService.
 */
@Path("/security/role")
@Api(value = "role", description = "Манипуляции с ролями", produces = "application/json")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class RoleRestService extends AbstractRestService {

    /** The role service. */
    @Autowired
    private RoleServiceExt roleServiceExt;

    /**
     * Creates the.
     *
     * @param role
     *            the role
     * @return the response
     * @throws Exception
     *             the exception
     */
    @POST
    @ApiOperation(value = "Создание новой роли", notes = "", response = UpdateResponse.class)
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response create(RoleRO role) {
        roleServiceExt.create(RolesConverter.convertRoleRO(role));
        return ok(new RestResponse<>(new UpdateResponse(true, role.getName())));
    }

    /**
     * Update.
     *
     * @param roleName
     *            the role name
     * @param role
     *            the role
     * @return the response
     * @throws Exception
     *             the exception
     */
    @PUT
    @Path(value = "{roleName}")
    @ApiOperation(value = "Модификация существующей роли", notes = "", response = UpdateResponse.class)
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response update(@PathParam(value = "roleName") String roleName, RoleRO role) throws Exception {
        roleServiceExt.update(roleName, RolesConverter.convertRoleRO(role));
        return ok(new RestResponse<>(new UpdateResponse(true, role.getName())));
    }

    /**
     * Read.
     *
     * @param roleName
     *            the role names
     * @return the response
     * @throws Exception
     *             the exception
     */
    @GET
    @Path(value = "{roleName}")
    @ApiOperation(value = "Возвращает существующую роль", notes = "", response = RoleRO.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response read(@PathParam(value = "roleName") String roleName) {
        final Role roleDTO = roleServiceExt.getRoleByName(roleName);
        return ok(new RestResponse<>(RolesConverter.convertRoleDTO(roleDTO)));
    }

    /**
     * Delete.
     *
     * @param roleName
     *            the role name
     * @return the response
     * @throws Exception
     *             the exception
     */
    @DELETE
    @Path(value = "{roleName}")
    @ApiOperation(value = "Создание новой роли", notes = "", response = UpdateResponse.class)
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response delete(@PathParam(value = "roleName") String roleName) throws Exception {
        roleServiceExt.delete(roleName);
        return ok(new RestResponse<>(new UpdateResponse(true, roleName)));
    }

    /**
     * Unlink all connected resources.
     * @param roleName role name
     * @param resourceName resource name
     * @return response
     */
    @DELETE
    @Path(value = "/unlink/{roleName}/{resourceName}")
    @ApiOperation(value = "Удаление всех связей между ролью, ресурсом и правами", notes = "", response = UpdateResponse.class)
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response unlink(
            @PathParam(value = "roleName") String roleName,
            @PathParam(value = "resourceName") String resourceName) {
        roleServiceExt.unlink(roleName, resourceName);
        return ok(new RestResponse<>(new UpdateResponse(true, roleName)));
    }
    /**
     * Read all.
     *
     * @return the response
     * @throws Exception
     *             the exception
     */
    @GET
    @ApiOperation(value = "Возвращает все роли", notes = "", response = List.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response readAll() {
        List<Role> roles = roleServiceExt.getAllRoles();
        return ok(new RestResponse<>(RolesConverter.convertRoleDTOs(roles)));
    }

    /**
     * Gets the all secured resources.
     *
     * @return the all secured resources
     */
    @GET
    @Path(value = "/get-all-secured-resources")
    @ApiOperation(value = "Возвращает все возможные SecuredResource", notes = "", response = List.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response getAllSecuredResources() {
        List<SecuredResourceDTO> securedResourceDTOs = roleServiceExt.getAllSecuredResources();
        return ok(new RestResponse<>(RolesConverter.convertSecuredResourceDTOs(securedResourceDTOs)));
    }

    /**
     * Gets the all security labels.
     *
     * @return the all security labels
     */
    @GET
    @Path(value = "/get-all-security-labels")
    @ApiOperation(value = "Возвращает все возможные SecuredResource", notes = "", response = List.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response getAllSecurityLabels() {
        final List<SecurityLabel> securityLabels = roleServiceExt.getAllSecurityLabels();
        return ok(new RestResponse<>(RolesConverter.convertSecurityLabelDTOs(securityLabels)));
    }

    /**
     *
     * @return
     * @throws Exception
     */
    @GET
    @Path("/role-properties/list")
    @ApiOperation(value = "Возвращает все свойства ролей", notes = "", response = List.class)
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response loadAllRoleProperties() {
        return ok(new RestResponse<>(RolesConverter.convertPropertiesDtoToRo(roleServiceExt.loadAllProperties())));
    }

    /**
     *
     * @param roleProperty
     * @return
     * @throws Exception
     */
    @PUT
    @Path("/role-properties/")
    @ApiOperation(value = "Сохранить новое свойство роли", notes = "", response = RolePropertyRO.class)
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()")
    @ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 401, message = "Unathorized"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
    public Response createRoleProperty(final RolePropertyRO roleProperty) {
        roleProperty.setId(null);
        final RolePropertyDTO dto = RolesConverter.convertPropertyRoToDto(roleProperty);
        roleServiceExt.saveProperty(dto);
        return ok(new RestResponse<>(RolesConverter.convertPropertyDtoToRo(dto)));
    }

    /**
     *
     * @param rolePropertyId
     * @param roleProperty
     * @return
     * @throws Exception
     */
    @PUT
    @Path("/role-properties/{rolePropertyId}")
    @ApiOperation(value = "Редактировать свойство роли", notes = "", response = RolePropertyRO.class)
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Request processed"),
        @ApiResponse(code = 401, message = "Unathorized"),
        @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response saveRoleProperty(@PathParam("rolePropertyId") final Long rolePropertyId,
        final RolePropertyRO roleProperty) {
        final RolePropertyDTO dto = RolesConverter.convertPropertyRoToDto(roleProperty);
        dto.setId(rolePropertyId);

        roleServiceExt.saveProperty(dto);

        return ok(new RestResponse<>(RolesConverter.convertPropertyDtoToRo(dto)));
    }

    /**
     *
     * @param rolePropertyId
     * @return
     * @throws Exception
     */
    @DELETE
    @Path("/role-properties/{rolePropertyId}")
    @ApiOperation(value = "Удалить свойство роли", notes = "", response = Boolean.class)
    @PreAuthorize("T(com.unidata.mdm.backend.service.security.utils.SecurityUtils).isAdminUser()")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Request processed"),
        @ApiResponse(code = 401, message = "Unathorized"),
        @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response removeRoleProperty(@PathParam("rolePropertyId") final Long rolePropertyId) {
        roleServiceExt.deleteProperty(rolePropertyId);
        return ok(new RestResponse<>(true));
    }
}
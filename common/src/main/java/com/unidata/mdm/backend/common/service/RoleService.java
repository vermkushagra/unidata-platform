package com.unidata.mdm.backend.common.service;

import java.util.List;

import com.unidata.mdm.backend.common.dto.security.SecuredResourceDTO;
import com.unidata.mdm.backend.common.integration.auth.Role;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;

public interface RoleService {
    /**
     * Gets the role by name.
     *
     * @param roleName
     *            the role name
     * @return the role by name
     */
    Role getRoleByName(String roleName);

    /**
     * Gets the all roles.
     *
     * @return the all roles
     */
    List<Role> getAllRoles();

    /**
     * Gets all roles by user login.
     * @param login the user login
     * @return list of roles
     */
    List<Role> getAllRolesByUserLogin(String login);

    /**
     * Gets the all secured resources.
     *
     * @return the all secured resources
     */
    List<SecuredResourceDTO> getAllSecuredResources();

    /**
     * Gets the all security labels.
     *
     * @return the all security labels
     */
    List<SecurityLabel> getAllSecurityLabels();

    /**
     * Determines is the provided user connected with provided role.
     *
     * @param userName
     *            User name
     * @param roleName
     *            Role name
     * @return <code>true</code> if provided user connected with role, otherwise
     *         <code>false</code>.
     */
    boolean isUserInRole(String userName, String roleName);
}

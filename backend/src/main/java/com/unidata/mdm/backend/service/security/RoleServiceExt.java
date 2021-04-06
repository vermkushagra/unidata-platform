package com.unidata.mdm.backend.service.security;

import java.util.List;

import com.unidata.mdm.backend.common.dto.security.RolePropertyDTO;
import com.unidata.mdm.backend.common.dto.security.SecuredResourceDTO;
import com.unidata.mdm.backend.common.integration.auth.CustomProperty;
import com.unidata.mdm.backend.common.integration.auth.Role;
import com.unidata.mdm.backend.common.integration.auth.SecuredResourceCategory;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;
import com.unidata.mdm.backend.common.service.RoleService;
import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;

public interface RoleServiceExt extends AfterContextRefresh, RoleService {

    /**
     * Creates the new role.
     *
     * @param role
     *            the role dto
     */
    void create(Role role);

    /**
     * Delete role.
     *
     * @param roleName
     *            the role name
     */
    void delete(String roleName);

    /**
     * Update role.
     *
     * @param roleName
     *            the role name
     * @param role
     *            the role dto
     */
    void update(String roleName, Role role);

    /**
     * Unlink resource.
     *
     * @param roleName
     *            the role name
     * @param resourceName
     *            the resource name
     */
    void unlink(String roleName, String resourceName);

    /**
     * Creates the label.
     *
     * @param label
     *            the label
     */
    void createLabel(SecurityLabel label);

    /**
     * Update label.
     *
     * @param label
     *            the label
     * @param labelName
     *            the label name
     */
    void updateLabel(SecurityLabel label, String labelName);

    /**
     * Find label.
     *
     * @param labelName
     *            the label name
     * @return the security label dto
     */
    SecurityLabel findLabel(String labelName);

    /**
     * Delete label.
     *
     * @param labelName
     *            the label name
     */
    void deleteLabel(String labelName);

    /**
     * Create secured resources.
     * @param resources list with secured resources.
     */
    void createResources(List<SecuredResourceDTO> resources);

    /**
     * Delete resource by name.
     * @param resourceName resource name.
     */
    void deleteResource(String resourceName);

    /**
     * Drop all security resources.
     * @param categories the categories to drop.
     */
    void dropResources(SecuredResourceCategory... categories);

    /**
     * Post construct.
     */
    @Override
    void afterContextRefresh();

    /**
     * Gets the all properties.
     *
     * @return the all properties
     */
    List<RolePropertyDTO> loadAllProperties();

    /**
     * @param property
     */
    void saveProperty(RolePropertyDTO property);

    /**
     * @param id
     */
    void deleteProperty(long id);

    /**
     * @param roleId
     * @return
     */
    List<RolePropertyDTO> loadPropertyValues(int roleId);

    /**
     * @param roleId
     * @param roleProperties
     */
    void savePropertyValues(long roleId, List<CustomProperty> roleProperties);

}
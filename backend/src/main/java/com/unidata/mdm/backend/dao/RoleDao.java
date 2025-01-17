package com.unidata.mdm.backend.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.integration.auth.SecuredResourceCategory;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;
import com.unidata.mdm.backend.service.security.po.LabelAttributePO;
import com.unidata.mdm.backend.service.security.po.LabelPO;
import com.unidata.mdm.backend.service.security.po.ResourcePO;
import com.unidata.mdm.backend.service.security.po.RightPO;
import com.unidata.mdm.backend.service.security.po.RolePO;
import com.unidata.mdm.backend.service.security.po.RolePropertyPO;
import com.unidata.mdm.backend.service.security.po.RolePropertyValuePO;

/**
 * The Interface RoleDao.
 */
public interface RoleDao {

	/**
	 * Insert a new role.
	 *
	 * @param role
	 *            the role to save
	 * @return the role po save role
	 */
	RolePO create(RolePO role);

	/**
	 * Find by name.
	 *
	 * @param name
	 *            the name
	 * @return the role po
	 */
	RolePO findByName(String name);

	/**
	 * Find right by name.
	 *
	 * @param name
	 *            the name
	 * @return the right po
	 */
	RightPO findRightByName(String name);

	/**
	 * Find resource by name.
	 *
	 * @param name
	 *            the name
	 * @return the resource po
	 */
	ResourcePO findResourceByName(String name);

	/**
	 * Update.
	 *
	 * @param name
	 *            the name
	 * @param role
	 *            the role
	 * @return the role po
	 */
	RolePO update(String name, RolePO role);

	/**
	 * Delete.
	 *
	 * @param name
	 *            the name
	 */
	void delete(String name);

	/**
	 * Gets the all.
	 *
	 * @return the all
	 */
	List<RolePO> getAll();

	/**
     * Gets combined roles, rights and resources by user login name.
     * @param login the user login
     * @return list of roles.
     */
    List<RolePO> findRolesByUserLogin(String login);

	/**
	 * Gets the all secured res.
	 *
	 * @return the all secured res
	 */
	List<ResourcePO> getAllSecurityResources();

	/**
	 * Gets the all security labels.
	 *
	 * @return the all security labels
	 */
	List<LabelPO> getAllSecurityLabels();

	/**
	 * Gets the security label by name.
	 *
	 * @param name
	 *            the name
	 * @return the security label by name
	 */
	LabelPO findSecurityLabelByName(String name);

	/**
	 * Delete security label by name.
	 *
	 * @param name
	 *            the name
	 */
	void deleteSecurityLabelByName(String name);

	/**
	 * Creates the security label.
	 *
	 * @param label
	 *            the new label
	 */
	void createSecurityLabel(LabelPO label);

	/**
	 * Update security label by name.
	 *
	 * @param name
	 *            the name
	 * @param label
	 *            the label
	 */
	void updateSecurityLabelByName(String name, LabelPO label);

	/**
	 * Adds the label attribute.
	 *
	 * @param toAdd
	 *            the to add
	 */
	void addLabelAttribute(LabelAttributePO toAdd);

	/**
	 * Update.
	 *
	 * @param existingName
	 *            the role name
	 * @param newRole
	 *            the to update
	 * @param labelNames
	 *            the label names
	 */
	void update(String existingName, RolePO newRole, List<SecurityLabel> securityLabels);

	/**
	 * Determines is user in role.
	 *
	 * @param userName
	 *            User name
	 * @param roleName
	 *            Role name
     * @param source
     *            User (external) source
	 * @return <code>true</code> if user in role, otherwise<code>false</code>
	 */
	boolean isUserInRole(String userName, String roleName, String source);

	/**
	 * Create secured resources.
	 *
	 * @param resourcePOs
	 *            list with secured resources.
	 */
	void createResources(List<ResourcePO> resourcePOs);
	/**
	 * Delete resource by name.
	 * @param resourceName resource name.
	 */
	void deleteResource(String resourceName);

	/**
	 * Find security labels by role name.
	 *
	 * @param roleName
	 *            the role name
	 * @return list with labels.
	 */
	List<LabelPO> findSecurityLabelsByRoleName(String roleName);
	/**
	 * Drop all secured resources.
	 * @param categories the categories to drop
	 */
	void dropResources(SecuredResourceCategory... categories);

	/**
	 * Load list of all user properties.
	 * @return
     */
	List<RolePropertyPO> loadAllProperties();

	/**
	 * Load property by name.
	 * @param name
	 * @return
     */
	RolePropertyPO loadPropertyByName(String name);

    /**
     * Load property by display name.
     * @param displayName    Property display name.
     * @return role property object
     */
    RolePropertyPO loadPropertyByDisplayName(String displayName);

	/**
	 *
	 * @param property
     */
	void saveProperty(RolePropertyPO property);

	/**
	 *
	 * @param id
     */
	void deleteProperty(long id);

	/**
	 *
	 * @param propertyValues
     */
	void saveRolePropertyValues(Collection<RolePropertyValuePO> propertyValues);

	/**
	 *
	 * @param ids
     */
	void deleteRolePropertyValuesByIds(Collection<Long> ids);

	/**
	 *
	 * @param roleId
     */
	void deleteRolePropertyValuesByRoleId(long roleId);
	/**
	 * Gets property values for role id
	 * @param roleId
	 * @return
	 */
	List<RolePropertyValuePO> loadRolePropertyValuesByRoleId(Integer roleId);
	/**
	 *
	 * @param roleIds
	 * @return
     */
	Map<Integer, List<RolePropertyValuePO>> loadRolePropertyValuesByRoleIds(Collection<Integer> roleIds);

}
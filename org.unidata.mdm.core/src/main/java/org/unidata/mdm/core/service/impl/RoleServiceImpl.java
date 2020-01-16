/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.unidata.mdm.core.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.unidata.mdm.core.convert.security.RoleConverter;
import org.unidata.mdm.core.convert.security.RolePropertyConverter;
import org.unidata.mdm.core.dao.RoleDao;
import org.unidata.mdm.core.dto.RoleDTO;
import org.unidata.mdm.core.dto.RolePropertyDTO;
import org.unidata.mdm.core.dto.SecuredResourceDTO;
import org.unidata.mdm.core.exception.CoreExceptionIds;
import org.unidata.mdm.core.notification.NotificationSystemConstants;
import org.unidata.mdm.core.notification.SecurityEventTypeConstants;
import org.unidata.mdm.core.po.security.LabelAttributePO;
import org.unidata.mdm.core.po.security.LabelPO;
import org.unidata.mdm.core.po.security.ResourcePO;
import org.unidata.mdm.core.po.security.ResourceRightPO;
import org.unidata.mdm.core.po.security.RightPO;
import org.unidata.mdm.core.po.security.RolePO;
import org.unidata.mdm.core.po.security.RolePropertyPO;
import org.unidata.mdm.core.po.security.RolePropertyValuePO;
import org.unidata.mdm.core.service.BusService;
import org.unidata.mdm.core.service.SecurityService;
import org.unidata.mdm.core.type.security.CustomProperty;
import org.unidata.mdm.core.type.security.Right;
import org.unidata.mdm.core.type.security.Role;
import org.unidata.mdm.core.type.security.SecuredResourceCategory;
import org.unidata.mdm.core.type.security.SecuredResourceType;
import org.unidata.mdm.core.type.security.SecurityLabel;
import org.unidata.mdm.core.type.security.SecurityLabelAttribute;
import org.unidata.mdm.core.util.Maps;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.system.exception.PlatformBusinessException;
import org.unidata.mdm.system.exception.PlatformValidationException;
import org.unidata.mdm.system.exception.ValidationResult;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * The Class RoleService.
 *
 * @author ilya.bykov
 */
@Component
public class RoleServiceImpl implements RoleServiceExt {
    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);

    /**
     * Parameter name length limit.
     */
    private static final int PARAM_NAME_LIMIT = 2044;

    /**
     * Parameter display name length limit.
     */
    private static final int PARAM_DISPLAY_NAME_LIMIT = 2044;

    private static final int ROLE_FIELD_LIMIT = 255;
    /**
     * Validation tags.
     */
    private static final String VIOLATION_REQUIRED_PROPERTY_VALUE = "app.role.property.validationError.value.not.set";
    private static final String VIOLATION_NAME_PROPERTY_EMPTY = "app.role.property.validationError.name.property.empty";
    private static final String VIOLATION_NAME_PROPERTY_LENGTH = "app.role.property.validationError.name.property.length";
    private static final String VIOLATION_DISPLAY_NAME_PROPERTY_EMPTY = "app.role.property.validationError.displayName.property.empty";
    private static final String VIOLATION_DISPLAY_NAME_PROPERTY_LENGTH = "app.role.property.validationError.displayName.property.length";
    private static final String VIOLATION_NAME_PROPERTY_NOT_UNIQUE = "app.role.property.validationError.name.not.unique";
    private static final String VIOLATION_DISPLAY_NAME_PROPERTY_NOT_UNIQUE = "app.role.property.validationError.displayName.not.unique";
    private static final String VIOLATION_ROLE_NAME_EMPTY = "app.role.data.validationError.roleName.empty";
    private static final String VIOLATION_ROLE_NAME_LENGTH = "app.role.data.validationError.roleName.length";
    private static final String VIOLATION_DISPLAY_NAME_LENGTH = "app.role.data.validationError.displayName.length";
    private static final String VIOLATION_ROLE_TYPE_EMPTY = "app.role.data.validationError.roleType.empty";
    private static final String VIOLATION_ROLE_TYPE_LENGTH = "app.role.data.validationError.roleType.length";
    private static final String ROLE_AUDIT_EVENT_PARAMETER = "role";
    private static final String LABLE_AUDIT_EVENT_PARAMETER = "lable";

    /**
     * The role dao impl.
     */
    @Autowired
    private RoleDao roleDAO;
    /**
     * Security service.
     */
    @Autowired
    private SecurityService securityService;

    @Autowired
    private BiConsumer<String, Object> coreSender;

    /**
     * The create.
     */
    private RightPO CREATE;

    /**
     * The update.
     */
    private RightPO UPDATE;

    /**
     * The delete.
     */
    private RightPO DELETE;

    /**
     * The read.
     */
    private RightPO READ;

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#create(com.unidata.mdm.backend.common.dto.security.RoleDTO)
     */
	@Override
	@Transactional
	public void create(Role role) {
		try {
			validateRole(role, true);
			RolePO toUpdate = convertRoleDTO(role);
			toUpdate.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			toUpdate.setCreatedBy(SecurityUtils.getCurrentUserName());
			toUpdate = roleDAO.create(toUpdate);
			final List<Right> rightDTOs = role.getRights();
			final List<ResourceRightPO> resourcesToCreate = new ArrayList<>();
			for (final Right rightDTO : rightDTOs) {
				if (rightDTO.getSecuredResource() == null || rightDTO.getSecuredResource().getName() == null) {
					break;
				}

				final ResourcePO resourcePO = roleDAO.findResourceByName(rightDTO.getSecuredResource().getName());
				resourcesToCreate.addAll(connectRolesWithRightsAndResources(toUpdate, resourcePO, rightDTO));
			}

			toUpdate.setConnectedResourceRights(resourcesToCreate);
			roleDAO.update(toUpdate.getName(), toUpdate, role.getSecurityLabels());

			savePropertyValues(toUpdate.getId(), role.getProperties());
            coreSender.accept(
                    SecurityEventTypeConstants.ROLE_CREATE_EVENT_TYPE,
                    Maps.of(ROLE_AUDIT_EVENT_PARAMETER, role.getName())
            );
		} catch (Exception e) {
            coreSender.accept(
                    SecurityEventTypeConstants.ROLE_CREATE_EVENT_TYPE,
                    Maps.of(ROLE_AUDIT_EVENT_PARAMETER, role.getName(), NotificationSystemConstants.EXCEPTION, e)
            );
			throw e;
		}
	}

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#delete(java.lang.String)
     */
    @Override
    @Transactional
	public void delete(String roleName) {
		try {
			roleDAO.delete(roleName);
			securityService.logoutByRoleName(roleName);
            coreSender.accept(
                    SecurityEventTypeConstants.ROLE_DELETE_EVENT_TYPE,
                    Maps.of(ROLE_AUDIT_EVENT_PARAMETER, roleName)

            );
        } catch (Exception e) {
            coreSender.accept(
                    SecurityEventTypeConstants.ROLE_DELETE_EVENT_TYPE,
                    Maps.of(ROLE_AUDIT_EVENT_PARAMETER, roleName, NotificationSystemConstants.EXCEPTION, e)

            );
			throw e;
		}

	}

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#update(java.lang.String, com.unidata.mdm.backend.common.integration.auth.dto.Role)
     */
	@Override
	@Transactional
	public void update(final String roleName, final Role role) {
		try {
			validateRole(role, false);
			final RolePO toUpdate = roleDAO.findByName(roleName);
			final List<ResourceRightPO> resourcesToConnect = new ArrayList<>();
			final List<ResourceRightPO> resourcesToDisconnect = new ArrayList<>();
			for (final Right right : role.getRights()) {
				final ResourcePO resource = roleDAO.findResourceByName(right.getSecuredResource().getName());
				resourcesToConnect.addAll(connectRolesWithRightsAndResources(toUpdate, resource, right));
				resourcesToDisconnect.addAll(disconnectRolesWithRightsAndResources(toUpdate, resource, right));
			}

			toUpdate.setDisplayName(role.getDisplayName());
			toUpdate.setName(role.getName());
			toUpdate.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			toUpdate.setUpdatedBy(SecurityUtils.getCurrentUserName());
			toUpdate.setConnectedResourceRights(resourcesToConnect);
			toUpdate.setDisconnectedResourceRights(resourcesToDisconnect);

			roleDAO.update(roleName, toUpdate, role.getSecurityLabels());
			if(role.getSecurityLabels()!=null) {
				role.getSecurityLabels().forEach(sl ->
                        coreSender.accept(
                                SecurityEventTypeConstants.ROLE_LABEL_ATTACH_EVENT_TYPE,
                                Maps.of(ROLE_AUDIT_EVENT_PARAMETER, roleName, "label", sl.getName())
                        )
                );
			}

			savePropertyValues(toUpdate.getId(), role.getProperties());
			securityService.logoutByRoleName(roleName);

            coreSender.accept(
                    SecurityEventTypeConstants.ROLE_UPDATE_EVENT_TYPE,
                    Maps.of(ROLE_AUDIT_EVENT_PARAMETER, role.getName())
            );
        } catch (Exception e) {
            coreSender.accept(
                    SecurityEventTypeConstants.ROLE_UPDATE_EVENT_TYPE,
                    Maps.of(ROLE_AUDIT_EVENT_PARAMETER, role.getName(), NotificationSystemConstants.EXCEPTION, e)
            );
			throw e;
		}
	}

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.common.service.RoleService#unlink(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void unlink(String roleName, String resourceName) {

        final RolePO toUpdate = roleDAO.findByName(roleName);
        final ResourcePO resource = roleDAO.findResourceByName(resourceName);
        final List<ResourceRightPO> resourcesToDisconnect
                = disconnectRolesWithRightsAndResources(toUpdate, resource, SecurityUtils.ALL_DISABLED);

        toUpdate.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        toUpdate.setUpdatedBy(SecurityUtils.getCurrentUserName());
        toUpdate.setDisconnectedResourceRights(resourcesToDisconnect);

        roleDAO.update(roleName, toUpdate, Collections.emptyList());
        securityService.logoutByRoleName(roleName);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#getRoleByName(java.lang.String)
     */
    @Override
    @Transactional
    public Role getRoleByName(String roleName) {
        MeasurementPoint.start();
        try {
            RolePO rolePO = roleDAO.findByName(roleName);
            return RoleConverter.convertRole(rolePO);
        } finally {
            MeasurementPoint.stop();
        }
    }


    /**
     * Convert role dto.
     *
     * @param source the source
     * @return the role po
     */
    private RolePO convertRoleDTO(Role source) {
        if (source == null) {
            return null;
        }
        RolePO target = new RolePO();
        target.setName(source.getName().trim());
        target.setDisplayName(source.getDisplayName().trim());
        target.setRType(source.getRoleType().name());
        return target;
    }

    /**
     * Disconnects rights from resources.
     *
     * @param role the role
     * @param resource the resource
     * @param right the right
     * @return list
     */
    private List<ResourceRightPO> disconnectRolesWithRightsAndResources(RolePO role, ResourcePO resource, Right right) {

        List<ResourceRightPO> matchByResourceName = role.getConnectedResourceRights().stream()
                .filter(po -> po.getResource().getName().equals(right.getSecuredResource().getName()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(matchByResourceName)) {
            return Collections.emptyList();
        }

        List<ResourceRightPO> result = new ArrayList<>();
        if (!right.isCreate() && matchByResourceName.stream().anyMatch(po -> po.getRight().equals(CREATE))) {
            result.add(createResourceRight(role, resource, CREATE));
        }

        if (!right.isUpdate() && matchByResourceName.stream().anyMatch(po -> po.getRight().equals(UPDATE))) {
            result.add(createResourceRight(role, resource, UPDATE));
        }

        if (!right.isDelete() && matchByResourceName.stream().anyMatch(po -> po.getRight().equals(DELETE))) {
            result.add(createResourceRight(role, resource, DELETE));
        }

        if (!right.isRead() && matchByResourceName.stream().anyMatch(po -> po.getRight().equals(READ))) {
            result.add(createResourceRight(role, resource, READ));
        }

        return result;
    }

    /**
     * Connect roles with rights and resources.
     *
     * @param role the to update
     * @param right the right dto
     * @param resource the resource po
     */
    private List<ResourceRightPO> connectRolesWithRightsAndResources(RolePO role, ResourcePO resource, Right right) {

        List<ResourceRightPO> result = new ArrayList<>();
        if (right.isCreate()) {
            result.add(createResourceRight(role, resource, CREATE));
        }
        if (right.isDelete()) {
            result.add(createResourceRight(role, resource, DELETE));
        }
        if (right.isUpdate()) {
            result.add(createResourceRight(role, resource, UPDATE));
        }
        if (right.isRead()) {
            result.add(createResourceRight(role, resource, READ));
        }

        for (int i = 0; i < result.size(); i++) {

            final ResourceRightPO resourceRightPO = result.get(i);
            int existingIdx = CollectionUtils.isEmpty(role.getConnectedResourceRights())
                    ? -1
                    : role.getConnectedResourceRights().indexOf(resourceRightPO);

            if (existingIdx != -1) {
                result.set(i, role.getConnectedResourceRights().get(existingIdx));
            }
        }

        return result;
    }

    /**
     * Creates the resource right.
     *
     * @param toUpdate the to update
     * @param resourcePO the resource po
     * @param right the right
     * @return new resource right
     */
    private ResourceRightPO createResourceRight(RolePO toUpdate, ResourcePO resourcePO, RightPO right) {

        ResourceRightPO resourceRightPO = new ResourceRightPO();
        resourceRightPO.setResource(resourcePO);
        resourceRightPO.setRight(right);
        resourceRightPO.setRole(toUpdate);
        resourceRightPO.setCreatedBy(SecurityUtils.getCurrentUserName());
        resourceRightPO.setUpdatedBy(SecurityUtils.getCurrentUserName());

        return resourceRightPO;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#getAllRoles()
     */
    @Override
    @Transactional
    public List<Role> getAllRoles() {
        final List<RolePO> rolePOs = roleDAO.getAll();
        return rolePOs.stream().map(RoleConverter::convertRole).collect(Collectors.toList());
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#getAllRolesByUserLogin(java.lang.String)
     */
    @Override
    public List<Role> getAllRolesByUserLogin(String login) {
        final List<RolePO> rolePOs = roleDAO.findRolesByUserLogin(login);
        return rolePOs.stream().map(RoleConverter::convertRole).collect(Collectors.toList());
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#getAllSecuredResources()
     */
    @Override
    @Transactional
    public List<SecuredResourceDTO> getAllSecuredResources() {
        List<ResourcePO> resourcePOs = roleDAO.getAllSecurityResources();

        LinkedHashMap<Integer, SecuredResourceDTO> links = resourcePOs.stream()
                .collect(Collectors.toMap(ResourcePO::getId, RoleServiceImpl::toDto, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        resourcePOs.forEach(po -> {
            if (Objects.nonNull(po.getParentId())) {
                SecuredResourceDTO dto = links.get(po.getId());
                SecuredResourceDTO parent = links.get(po.getParentId());
                parent.getChildren().add(dto);
                dto.setParent(parent);
            }
        });
        return links.values().stream().filter(v -> v.getParent() == null).collect(Collectors.toList());
    }

    private static SecuredResourceDTO toDto(ResourcePO po) {
        SecuredResourceDTO dto = new SecuredResourceDTO();
        dto.setName(po.getName());
        dto.setDisplayName(po.getDisplayName());
        dto.setType(po.getRType() == null
                ? SecuredResourceType.SYSTEM
                : SecuredResourceType.valueOf(po.getRType()));
        dto.setCategory(po.getCategory() == null
                ? SecuredResourceCategory.SYSTEM
                : SecuredResourceCategory.valueOf(po.getCategory()));
        return dto;
    }

    @Override
    public List<SecuredResourceDTO> getSecuredResourcesFlatList() {
        return roleDAO.getAllSecurityResources().stream()
                .map(r -> {
                    final SecuredResourceDTO securedResourceDTO = new SecuredResourceDTO();
                    securedResourceDTO.setName(r.getName());
                    securedResourceDTO.setDisplayName(r.getDisplayName());
                    securedResourceDTO.setType(r.getRType() == null
                            ? SecuredResourceType.SYSTEM
                            : SecuredResourceType.valueOf(r.getRType()));
                    securedResourceDTO.setCategory(r.getCategory() == null
                            ? SecuredResourceCategory.SYSTEM
                            : SecuredResourceCategory.valueOf(r.getCategory()));
                    return securedResourceDTO;
                })
                .collect(Collectors.toList());
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#getAllSecurityLabels()
     */
    @Override
    @Transactional
    public List<SecurityLabel> getAllSecurityLabels() {
        return RoleConverter.convertLabels(roleDAO.getAllSecurityLabels());
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#createLabel(com.unidata.mdm.backend.common.integration.auth.dto.SecurityLabel)
     */
    @Override
    @Transactional
	public void createLabel(SecurityLabel label) {
		try {
			roleDAO.createSecurityLabel(convertLabelDTOToPO(label));
            coreSender.accept(
                    SecurityEventTypeConstants.LABEL_CREATE_EVENT_TYPE,
                    Maps.of(ROLE_AUDIT_EVENT_PARAMETER, Maps.of(LABLE_AUDIT_EVENT_PARAMETER, label.getName()))
            );
        } catch (Exception e) {
            coreSender.accept(
                    SecurityEventTypeConstants.LABEL_CREATE_EVENT_TYPE,
                    Maps.of(ROLE_AUDIT_EVENT_PARAMETER, Maps.of(LABLE_AUDIT_EVENT_PARAMETER, label.getName()), NotificationSystemConstants.EXCEPTION, e)
            );
			throw e;
		}
	}

    /**
     * Convert label dto to po.
     *
     * @param source the source
     * @return the label po
     */
    private LabelPO convertLabelDTOToPO(SecurityLabel source) {
        if (source == null) {
            return null;
        }
        LabelPO target = new LabelPO();
        target.setDescription(source.getDescription());
        target.setDisplayName(source.getDisplayName());
        target.setName(source.getName());
        target.setLabelAttributePO(convertAttributesDTOToPO(source.getAttributes(), target));
        return target;
    }

    /**
     * Convert attributes dto to po.
     *
     * @param source the source
     * @param label the label
     * @return the list
     */
    private List<LabelAttributePO> convertAttributesDTOToPO(final List<SecurityLabelAttribute> source,
                                                            final LabelPO label) {
        if (source == null) {
            return Collections.emptyList();
        }

        final List<LabelAttributePO> target = new ArrayList<>();
        source.forEach(s -> target.add(convertAttributeDTOToPO(s, label)));
        return target;
    }

    /**
     * Convert attribute dto to po.
     *
     * @param source the source
     * @param label the label
     * @return the label attribute po
     */
    private LabelAttributePO convertAttributeDTOToPO(SecurityLabelAttribute source, LabelPO label) {
        if (source == null) {
            return null;
        }
        final LabelAttributePO target = new LabelAttributePO();
        target.setId(source.getId());
        target.setLabel(label);
        target.setName(source.getName());
        target.setValue(source.getValue());
        target.setPath(source.getPath());
        target.setDescription(source.getDescription());
        return target;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#updateLabel(com.unidata.mdm.backend.common.integration.auth.dto.SecurityLabel, java.lang.String)
     */
    @Override
    @Transactional
	public void updateLabel(SecurityLabel label, String labelName) {
		try {
			roleDAO.updateSecurityLabelByName(labelName, convertLabelDTOToPO(label));

            coreSender.accept(
                    SecurityEventTypeConstants.LABEL_UPDATE_EVENT_TYPE,
                    Maps.of(LABLE_AUDIT_EVENT_PARAMETER, label.getName())

            );
        } catch (Exception e) {
            coreSender.accept(
                    SecurityEventTypeConstants.LABEL_UPDATE_EVENT_TYPE,
                    Maps.of(LABLE_AUDIT_EVENT_PARAMETER, label.getName(), NotificationSystemConstants.EXCEPTION, e)
            );
			throw e;
		}
	}

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#findLabel(java.lang.String)
     */
    @Override
    @Transactional
    public SecurityLabel findLabel(String labelName) {
        return RoleConverter.convertLabel(roleDAO.findSecurityLabelByName(labelName));
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#deleteLabel(java.lang.String)
     */
    @Override
    @Transactional
	public void deleteLabel(String labelName) {
		try {
			roleDAO.deleteSecurityLabelByName(labelName);

            coreSender.accept(
                    SecurityEventTypeConstants.LABEL_DELETE_EVENT_TYPE,
                    Maps.of(LABLE_AUDIT_EVENT_PARAMETER, labelName)
            );
        } catch (Exception e) {
            coreSender.accept(
                    SecurityEventTypeConstants.LABEL_DELETE_EVENT_TYPE,
                    Maps.of(LABLE_AUDIT_EVENT_PARAMETER, labelName, NotificationSystemConstants.EXCEPTION, e)
            );
			throw e;
		}
	}

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#isUserInRole(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public boolean isUserInRole(String userName, String roleName) {
        // TODO Add external user support
        return roleDAO.isUserInRole(userName, roleName);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#createResources(java.util.List)
     */
    @Override
    @Transactional
    public void createResources(List<SecuredResourceDTO> resources) {

        List<ResourcePO> pos = new ArrayList<>();
        for (SecuredResourceDTO dto : resources) {
            ResourcePO po = new ResourcePO();
            po.setCreatedAt(new Timestamp(dto.getCreatedAt().getTime()));
            po.setUpdatedAt(new Timestamp(dto.getUpdatedAt().getTime()));
            po.setCreatedBy(dto.getCreatedBy());
            po.setUpdatedBy(dto.getUpdatedBy());
            po.setDisplayName(dto.getDisplayName());
            po.setName(dto.getName());
            po.setRType(dto.getType().name());
            po.setCategory(dto.getCategory().name());

            pos.add(po);

            if (!CollectionUtils.isEmpty(dto.getChildren())) {
                pos.addAll(createResourcesRecursive(dto.getChildren()));
            }
        }

        roleDAO.createResources(pos);
    }

    /**
     * Creates resources recursivly.
     *
     * @param resources DTOs to process
     * @return list of PO objects
     */
    private Collection<ResourcePO> createResourcesRecursive(Collection<SecuredResourceDTO> resources) {

        List<ResourcePO> result = new ArrayList<>();
        for (SecuredResourceDTO dto : resources) {

            ResourcePO po = new ResourcePO();
            po.setCreatedAt(new Timestamp(dto.getCreatedAt().getTime()));
            po.setUpdatedAt(new Timestamp(dto.getUpdatedAt().getTime()));
            po.setCreatedBy(dto.getCreatedBy());
            po.setUpdatedBy(dto.getUpdatedBy());
            po.setDisplayName(dto.getDisplayName());
            po.setName(dto.getName());
            po.setRType(dto.getType().name());
            po.setCategory(dto.getCategory().name());

            if (dto.getParent() != null) {
                po.setParentName(dto.getParent().getName());
            }

            result.add(po);

            if (!CollectionUtils.isEmpty(dto.getChildren())) {
                result.addAll(createResourcesRecursive(dto.getChildren()));
            }
        }

        return result;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#deleteResource(java.lang.String)
     */
    @Override
    public void deleteResource(String resourceName) {
        roleDAO.deleteResource(resourceName);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#deleteResources(java.util.List)
     */
    @Override
    public void deleteResources(List<String> resources) {

        if (CollectionUtils.isEmpty(resources)) {
            return;
        }

        for (String resource : resources) {
            deleteResource(resource);
        }
    }

    @Override
    public boolean updateResourceDisplayName(String resourceName, String resourceDisplayName) {
        return roleDAO.updateResourceDisplayName(resourceName, resourceDisplayName);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#dropResources()
     */
    @Override
    public void dropResources(SecuredResourceCategory... categories) {
        roleDAO.dropResources(categories);

    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#afterContextRefresh()
     */
    @Override
    public void afterContextRefresh() {
        this.CREATE = roleDAO.findRightByName("CREATE");
        this.UPDATE = roleDAO.findRightByName("UPDATE");
        this.DELETE = roleDAO.findRightByName("DELETE");
        this.READ = roleDAO.findRightByName("READ");
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#loadAllProperties()
     */
    @Override
    @Transactional
    public List<RolePropertyDTO> loadAllProperties() {
        return RolePropertyConverter.convertPropertiesPoToDto(roleDAO.loadAllProperties());
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#saveProperty(com.unidata.mdm.backend.common.dto.security.RolePropertyDTO)
     */
    @Override
    @Transactional
    public void saveProperty(final RolePropertyDTO property) {

        validateRoleProperty(property);
        final RolePropertyPO po = RolePropertyConverter.convertPropertyDtoToPo(property);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        String userName = SecurityUtils.getCurrentUserName();

        po.setCreatedAt(now);
        po.setCreatedBy(userName);
        po.setUpdatedAt(now);
        po.setUpdatedBy(userName);

        roleDAO.saveProperty(po);

        property.setId(po.getId());
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#deleteProperty(long)
     */
    @Override
    @Transactional
    public void deleteProperty(long id) {
        roleDAO.deleteProperty(id);

        LOGGER.debug("Delete role property [id={}]", id);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#loadRolePropertyValues(long)
     */
    @Override
    public List<RolePropertyDTO> loadPropertyValues(int roleId) {

        List<RolePropertyValuePO> valuePOs = roleDAO
                .loadRolePropertyValuesByRoleIds(Collections.singleton(roleId))
                .get(roleId);

        if (CollectionUtils.isEmpty(valuePOs)) {
            return Collections.emptyList();
        }

        return RoleConverter.convertPropertyValues(valuePOs)
                .stream()
                .map(rp -> (RolePropertyDTO) rp)
                .collect(Collectors.toList());
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#saveUserPropertyValues(long, java.util.List)
     */
    @Override
    @Transactional
    public void savePropertyValues(long roleId, List<CustomProperty> roleProperties) {

        validateRolePropertyValues(roleProperties);
        roleDAO.deleteRolePropertyValuesByRoleId(roleId);
        if (!CollectionUtils.isEmpty(roleProperties)) {

            final List<RolePropertyValuePO> valuePOs = RolePropertyConverter.convertPropertyValuesDtoToPo(roleProperties);
            valuePOs.forEach(valuePO -> {
                valuePO.setRoleId(roleId);

                Timestamp now = new Timestamp(System.currentTimeMillis());
                String userName = SecurityUtils.getCurrentUserName();

                valuePO.setCreatedAt(now);
                valuePO.setCreatedBy(userName);
                valuePO.setUpdatedAt(now);
                valuePO.setUpdatedBy(userName);
            });

            roleDAO.saveRolePropertyValues(valuePOs);
        }
    }

    private void validateRolePropertyValues(List<CustomProperty> toCheck) {
        if (CollectionUtils.isEmpty(toCheck)) {
            return;
        }
        final List<ValidationResult> validationResult = new ArrayList<>();
        List<RolePropertyDTO> allProperties = loadAllProperties();
        if (!CollectionUtils.isEmpty(allProperties)) {
            Map<String, RolePropertyDTO> propertiesMap = allProperties.stream()
                    .collect(Collectors.toMap(RolePropertyDTO::getName, r -> r));
            for (CustomProperty property : toCheck) {
                if (propertiesMap.get(property.getName()).isRequired() && StringUtils.isEmpty(property.getValue())) {
                    validationResult.add(new ValidationResult("Required value for role property {0} not set.",
                            VIOLATION_REQUIRED_PROPERTY_VALUE, property.getName()));
                }
            }
        }
        if (!CollectionUtils.isEmpty(validationResult)) {
            throw new PlatformValidationException("Role property values validation error.",
                    CoreExceptionIds.EX_ROLE_PROPERTY_VALUES_VALIDATION_ERROR, validationResult);
        }

    }

    private void validateRoleProperty(final RolePropertyDTO property) {

        final List<ValidationResult> validationResult = new ArrayList<>();

        property.setName(StringUtils.trim(property.getName()));
        property.setDisplayName(StringUtils.trim(property.getDisplayName()));

        if (StringUtils.isEmpty(property.getName())) {
            validationResult.add(new ValidationResult("Property 'name' is blank/empty. Rejected.",
                    VIOLATION_NAME_PROPERTY_EMPTY));
        } else if (property.getName().length() > PARAM_NAME_LIMIT) {
            validationResult.add(new ValidationResult("The lenght of the 'name' parameter is larger than {0} limit.",
                    VIOLATION_NAME_PROPERTY_LENGTH, PARAM_NAME_LIMIT));
        }

        if (StringUtils.isEmpty(property.getDisplayName())) {
            validationResult.add(new ValidationResult("Property 'displayName' is blank/empty. Rejected.",
                    VIOLATION_DISPLAY_NAME_PROPERTY_EMPTY));
        } else if (property.getDisplayName().length() > PARAM_DISPLAY_NAME_LIMIT) {
            validationResult.add(new ValidationResult("The lenght of the 'displayName' parameter is larger than {0} limit.",
                    VIOLATION_DISPLAY_NAME_PROPERTY_LENGTH, PARAM_DISPLAY_NAME_LIMIT));
        }

        if (validationResult.isEmpty()) {

            RolePropertyPO existProperty = roleDAO.loadPropertyByName(property.getName());
            if (existProperty != null && !existProperty.getId().equals(property.getId())) {
                validationResult.add(new ValidationResult("Role property 'name' must be unique. Found existing property with name {0}.",
                        VIOLATION_NAME_PROPERTY_NOT_UNIQUE, property.getName()));
            }

            existProperty = roleDAO.loadPropertyByDisplayName(property.getDisplayName());
            if (existProperty != null && !existProperty.getId().equals(property.getId())) {
                validationResult.add(new ValidationResult("Role property 'displayName' must be unique. Found existing property with displayName {0}.",
                        VIOLATION_DISPLAY_NAME_PROPERTY_NOT_UNIQUE, property.getDisplayName()));
            }
        }

        if (!CollectionUtils.isEmpty(validationResult)) {
            throw new PlatformValidationException("Role properties validation error.",
                    CoreExceptionIds.EX_ROLE_PROPERTY_VALIDATION_ERROR, validationResult);
        }
    }

    private void validateRole(final Role role, boolean isNew) {

        final List<ValidationResult> validationResult = new ArrayList<>();

        String roleName = StringUtils.trim(role.getName());
        String roleDisplayName = StringUtils.trim(role.getDisplayName());


        if (StringUtils.isBlank(roleName)) {
            validationResult.add(new ValidationResult("Role name is empty.",
                    VIOLATION_ROLE_NAME_EMPTY));
        } else if (roleName.length() > ROLE_FIELD_LIMIT) {
            validationResult.add(new ValidationResult("Role name is larger than the field limit.",
                    VIOLATION_ROLE_NAME_LENGTH, roleName, ROLE_FIELD_LIMIT));
        }
        if (StringUtils.isBlank(roleDisplayName)) {
            validationResult.add(new ValidationResult("Role display name is empty.",
                    VIOLATION_ROLE_NAME_EMPTY));
        }
        if (!StringUtils.isEmpty(roleDisplayName) && roleDisplayName.length() > ROLE_FIELD_LIMIT) {
            validationResult.add(new ValidationResult("Role display name is larger than the field limit.",
                    VIOLATION_DISPLAY_NAME_LENGTH, roleDisplayName, ROLE_FIELD_LIMIT));
        }

        if (role.getRoleType() == null) {
            validationResult.add(new ValidationResult("Role type is empty.",
                    VIOLATION_ROLE_TYPE_EMPTY));
        } else if (role.getRoleType().toString().length() > ROLE_FIELD_LIMIT) {
            validationResult.add(new ValidationResult("Role type exceeds field limit.",
                    VIOLATION_ROLE_TYPE_LENGTH, role.getRoleType(), ROLE_FIELD_LIMIT));
        }

        if (!validationResult.isEmpty()) {
            throw new PlatformValidationException("Role {} parameter validation error",
                    CoreExceptionIds.EX_ROLE_DATA_VALIDATION_ERROR, validationResult, roleName);
        }

        if (isNew && roleDAO.findByName(roleName) != null) {
            throw new PlatformBusinessException("Role name {} already exists.",
                    CoreExceptionIds.EX_SECURITY_ROLE_ALREADY_EXISTS, roleName);
        }
    }

    @Override
    public List<RoleDTO> loadAllRoles() {
        return RoleConverter.convertRoles(roleDAO.fetchRolesFullInfo());
    }

    @Override
    public void removeRolesByName(final List<String> roles) {
        roleDAO.removeRolesByName(roles);
    }

    @Override
    public void cleanRolesDataByName(List<String> roles) {
        roleDAO.cleanRolesDataByName(roles);
    }

    @Override
    public List<Role> loadRolesData(List<String> rolesName) {
        final List<RoleDTO> roles = RoleConverter.convertRoles(roleDAO.loadRoles(rolesName));
        return roles.stream().map(r -> (Role) r).collect(Collectors.toList());
    }
}

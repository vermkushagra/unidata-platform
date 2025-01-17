package com.unidata.mdm.backend.service.security.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.unidata.mdm.backend.dao.SecurityLabelDao;
import com.unidata.mdm.backend.dao.impl.SecurityLabelDaoImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.api.rest.converter.RolesConverter;
import com.unidata.mdm.backend.common.dto.security.RolePropertyDTO;
import com.unidata.mdm.backend.common.dto.security.SecuredResourceDTO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.UserRolePropertyException;
import com.unidata.mdm.backend.common.exception.ValidationResult;
import com.unidata.mdm.backend.common.integration.auth.CustomProperty;
import com.unidata.mdm.backend.common.integration.auth.Right;
import com.unidata.mdm.backend.common.integration.auth.Role;
import com.unidata.mdm.backend.common.integration.auth.SecuredResourceCategory;
import com.unidata.mdm.backend.common.integration.auth.SecuredResourceType;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabelAttribute;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.dao.RoleDao;
import com.unidata.mdm.backend.service.security.RoleServiceExt;
import com.unidata.mdm.backend.service.security.SecurityServiceExt;
import com.unidata.mdm.backend.service.security.converters.RoleConverter;
import com.unidata.mdm.backend.service.security.po.LabelAttributePO;
import com.unidata.mdm.backend.service.security.po.LabelPO;
import com.unidata.mdm.backend.service.security.po.ResourcePO;
import com.unidata.mdm.backend.service.security.po.ResourceRightPO;
import com.unidata.mdm.backend.service.security.po.RightPO;
import com.unidata.mdm.backend.service.security.po.RolePO;
import com.unidata.mdm.backend.service.security.po.RolePropertyPO;
import com.unidata.mdm.backend.service.security.po.RolePropertyValuePO;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

/**
 * The Class RoleService.
 * @author ilya.bykov
 */
@Component
public class RoleServiceExtImpl implements RoleServiceExt {
	/**
  * Logger instance.
  */
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceExtImpl.class);

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

    /** The role dao impl. */
    @Autowired
    private RoleDao roleDAO;

    /** Security service. */
    @Autowired
    private SecurityServiceExt securityService;

    /** The create. */
    private RightPO CREATE;

    /** The update. */
    private  RightPO UPDATE;

    /** The delete. */
    private  RightPO DELETE;

    /** The read. */
    private  RightPO READ;

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#create(com.unidata.mdm.backend.common.dto.security.RoleDTO)
     */
    @Override
    @Transactional
    public void create(Role role) {

        validateRole(role);

        RolePO toUpdate = convertRoleDTO(role);
        toUpdate.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        toUpdate.setCreatedBy(SecurityUtils.getCurrentUserName());
        toUpdate = roleDAO.create(toUpdate);
        final List<Right> rightDTOs = role.getRights();
        final List<ResourceRightPO> resourcesToCreate = new ArrayList<>();
        for (final Right rightDTO : rightDTOs) {
            if (rightDTO.getSecuredResource() == null
                    || rightDTO.getSecuredResource().getName() == null) {
                break;
            }

            final ResourcePO resourcePO = roleDAO.findResourceByName(rightDTO.getSecuredResource().getName());
            resourcesToCreate.addAll(connectRolesWithRightsAndResources(toUpdate, resourcePO, rightDTO));
        }

        toUpdate.setConnectedResourceRights(resourcesToCreate);
        roleDAO.update(toUpdate.getName(), toUpdate, role.getSecurityLabels());

        savePropertyValues(toUpdate.getId(), role.getProperties());
    }



    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#delete(java.lang.String)
     */
    @Override
    @Transactional
    public void delete(String roleName) {
        roleDAO.delete(roleName);
        securityService.logoutByRoleName(roleName);

    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#update(java.lang.String, com.unidata.mdm.backend.common.integration.auth.dto.Role)
     */
    @Override
    @Transactional
    public void update(final String roleName, final Role role) {

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

        savePropertyValues(toUpdate.getId(), role.getProperties());
        securityService.logoutByRoleName(roleName);
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
     * @param source
     *            the source
     * @return the role po
     */
    private RolePO convertRoleDTO(Role source) {
        if (source == null) {
            return null;
        }
        RolePO target = new RolePO();
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setRType(source.getRoleType().name());
        return target;
    }

    /**
     * Disconnects rights from resources.
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
     * @param role
     *            the to update
     * @param right
     *            the right dto
     * @param resource
     *            the resource po
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
     * @param toUpdate
     *            the to update
     * @param resourcePO
     *            the resource po
     * @param right
     *            the right
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
        return rolePOs.stream().map(r -> RoleConverter.convertRole(r)).collect(Collectors.toList());
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#getAllRolesByUserLogin(java.lang.String)
     */
    @Override
    public List<Role> getAllRolesByUserLogin(String login) {
        final List<RolePO> rolePOs = roleDAO.findRolesByUserLogin(login);
        return rolePOs.stream().map(r -> RoleConverter.convertRole(r)).collect(Collectors.toList());
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#getAllSecuredResources()
     */
    @Override
    @Transactional
    public List<SecuredResourceDTO> getAllSecuredResources() {

        Map<Integer, SecuredResourceDTO> links = new HashMap<>();
        List<ResourcePO> resourcePOs = roleDAO.getAllSecurityResources();
        List<SecuredResourceDTO> dtos = new ArrayList<>();
        for (ResourcePO po : resourcePOs) {

            SecuredResourceDTO dto = new SecuredResourceDTO();
            dto.setName(po.getName());
            dto.setDisplayName(po.getDisplayName());
            dto.setType(po.getRType() == null
                    ? SecuredResourceType.SYSTEM
                    : SecuredResourceType.valueOf(po.getRType()));
            dto.setCategory(po.getCategory() == null
                    ? SecuredResourceCategory.SYSTEM
                    : SecuredResourceCategory.valueOf(po.getCategory()));

            if (Objects.nonNull(po.getParentId())) {
                SecuredResourceDTO parent = links.get(po.getParentId());
                parent.getChildren().add(dto);
                dto.setParent(parent);
            } else {
                dtos.add(dto);
            }

            links.put(po.getId(), dto);
        }
        return dtos;
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
        roleDAO.createSecurityLabel(convertLabelDTOToPO(label));
    }

    /**
     * Convert label dto to po.
     *
     * @param source
     *            the source
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
     * @param source
     *            the source
     * @param label
     *            the label
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
     * @param source
     *            the source
     * @param label
     *            the label
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
        target.setDescription(source.getDescription());
        return target;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#updateLabel(com.unidata.mdm.backend.common.integration.auth.dto.SecurityLabel, java.lang.String)
     */
    @Override
    @Transactional
    public void updateLabel(SecurityLabel label, String labelName) {
        roleDAO.updateSecurityLabelByName(labelName, convertLabelDTOToPO(label));
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
        roleDAO.deleteSecurityLabelByName(labelName);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#isUserInRole(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public boolean isUserInRole(String userName, String roleName) {
        // TODO Add external user support
        return roleDAO.isUserInRole(userName, roleName, SecurityUtils.UNIDATA_SECURITY_DATA_SOURCE);
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
    public void deleteResource(String resourceName){
        roleDAO.deleteResource(resourceName);
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
    public List<RolePropertyDTO> loadAllProperties(){
        return RolesConverter.convertPropertiesPoToDto(roleDAO.loadAllProperties());
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.IRoleService#saveProperty(com.unidata.mdm.backend.common.dto.security.RolePropertyDTO)
     */
    @Override
    @Transactional
    public void saveProperty(final RolePropertyDTO property) {

        validateRoleProperty(property);
        final RolePropertyPO po = RolesConverter.convertPropertyDtoToPo(property);

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

        roleDAO.deleteRolePropertyValuesByRoleId(roleId);
        if (!CollectionUtils.isEmpty(roleProperties)) {

            final List<RolePropertyValuePO> valuePOs = RolesConverter.convertPropertyValuesDtoToPo(roleProperties);
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

    private void validateRoleProperty(final RolePropertyDTO property) {

        final List<ValidationResult> validationResult = new ArrayList<>();
        if (StringUtils.isEmpty(property.getName())) {
            validationResult.add(new ValidationResult("Property 'name' is blank/empty. Rejected.",
                    VIOLATION_NAME_PROPERTY_EMPTY));
        } else if (property.getName().length() > PARAM_NAME_LIMIT) {
            validationResult.add(new ValidationResult("The lenght of the 'name' parameter is larger than {0} limit.",
                    VIOLATION_NAME_PROPERTY_LENGTH, Integer.valueOf(PARAM_NAME_LIMIT)));
        }

        if (StringUtils.isEmpty(property.getDisplayName())) {
            validationResult.add(new ValidationResult("Property 'displayName' is blank/empty. Rejected.",
                    VIOLATION_DISPLAY_NAME_PROPERTY_EMPTY));
        } else if (property.getDisplayName().length() > PARAM_DISPLAY_NAME_LIMIT) {
            validationResult.add(new ValidationResult("The lenght of the 'displayName' parameter is larger than {0} limit.",
                    VIOLATION_DISPLAY_NAME_PROPERTY_LENGTH, Integer.valueOf(PARAM_DISPLAY_NAME_LIMIT)));
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
            throw new UserRolePropertyException("Role properties validation error.",
                    ExceptionId.EX_ROLE_PROPERTY_VALIDATION_ERROR, validationResult);
        }
    }

    private void validateRole(final Role role) {

        final List<ValidationResult> validationResult = new ArrayList<>();

        if (StringUtils.isBlank(role.getName())) {
            validationResult.add(new ValidationResult("Role name is empty.",
                    VIOLATION_ROLE_NAME_EMPTY));
        } else if (role.getName().length() > ROLE_FIELD_LIMIT) {
            validationResult.add(new ValidationResult("Role name is larger than the field limit.",
                    VIOLATION_ROLE_NAME_LENGTH, Integer.valueOf(ROLE_FIELD_LIMIT)));
        }

        if (!StringUtils.isEmpty(role.getDisplayName()) && role.getDisplayName().length() > ROLE_FIELD_LIMIT) {
            validationResult.add(new ValidationResult("Role display name is larger than the field limit.",
                    VIOLATION_DISPLAY_NAME_LENGTH, Integer.valueOf(ROLE_FIELD_LIMIT)));
        }

        if (role.getRoleType() == null) {
            validationResult.add(new ValidationResult("Role type is empty.",
                    VIOLATION_ROLE_TYPE_EMPTY));
        } else if (role.getRoleType().toString().length() > ROLE_FIELD_LIMIT) {
            validationResult.add(new ValidationResult("Role type exceeds field limit.",
                    VIOLATION_ROLE_TYPE_LENGTH, Integer.valueOf(ROLE_FIELD_LIMIT)));
        }

        if (!validationResult.isEmpty()) {
            throw new UserRolePropertyException("Role {} parameter validation error",
                    ExceptionId.EX_ROLE_DATA_VALIDATION_ERROR, validationResult, role.getName());
        }

        if (roleDAO.findByName(role.getName()) != null) {
            throw new UserRolePropertyException("Role name {} already exists.",
                    ExceptionId.EX_SECURITY_ROLE_ALREADY_EXISTS, null,
                    role.getName());
        }
    }
}

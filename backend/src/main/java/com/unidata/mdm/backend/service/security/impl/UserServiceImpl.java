package com.unidata.mdm.backend.service.security.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.api.rest.converter.UsersConverter;
import com.unidata.mdm.backend.common.context.UpsertUserEventRequestContext;
import com.unidata.mdm.backend.common.dto.security.SecurityLabelAttributeDTO;
import com.unidata.mdm.backend.common.dto.security.SecurityLabelDTO;
import com.unidata.mdm.backend.common.dto.security.UserDTO;
import com.unidata.mdm.backend.common.dto.security.UserEventDTO;
import com.unidata.mdm.backend.common.dto.security.UserPropertyDTO;
import com.unidata.mdm.backend.common.dto.security.UserWithPasswordDTO;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.UserRolePropertyException;
import com.unidata.mdm.backend.common.exception.ValidationResult;
import com.unidata.mdm.backend.common.integration.auth.Endpoint;
import com.unidata.mdm.backend.common.integration.auth.Role;
import com.unidata.mdm.backend.common.integration.auth.SecurityDataProviderException;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabelAttribute;
import com.unidata.mdm.backend.common.integration.auth.SecurityState;
import com.unidata.mdm.backend.common.integration.auth.User;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.security.SecurityToken;
import com.unidata.mdm.backend.dao.RoleDao;
import com.unidata.mdm.backend.dao.UserDao;
import com.unidata.mdm.backend.service.audit.AuditActions;
import com.unidata.mdm.backend.service.audit.AuditEventsWriter;
import com.unidata.mdm.backend.service.security.SecurityServiceExt;
import com.unidata.mdm.backend.service.security.UserService;
import com.unidata.mdm.backend.service.security.converters.UserConverter;
import com.unidata.mdm.backend.service.security.converters.UserDTOToPOConverter;
import com.unidata.mdm.backend.service.security.po.ApiPO;
import com.unidata.mdm.backend.service.security.po.LabelAttributePO;
import com.unidata.mdm.backend.service.security.po.PasswordPO;
import com.unidata.mdm.backend.service.security.po.RolePO;
import com.unidata.mdm.backend.service.security.po.TokenPO;
import com.unidata.mdm.backend.service.security.po.UserEventPO;
import com.unidata.mdm.backend.service.security.po.UserPO;
import com.unidata.mdm.backend.service.security.po.UserPropertyPO;
import com.unidata.mdm.backend.service.security.po.UserPropertyValuePO;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

/**
 * The Class UserService.
 */
@Component
public class UserServiceImpl implements UserService {
    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    /**
     * Parameter name length limit.
     */
    private static final int PARAM_NAME_LIMIT = 2044;
    /**
     * Ordinary field length limit.
     */
    private static final int USER_FIELD_LIMIT = 255;
    /**
     * Parameter display name length limit.
     */
    private static final int PARAM_DISPLAY_NAME_LIMIT = 2044;
    /**
     * Validation tags.
     */
    private static final String VIOLATION_NAME_PROPERTY_EMPTY = "app.user.property.validationError.name.property.empty";
    private static final String VIOLATION_NAME_PROPERTY_LENGTH = "app.user.property.validationError.name.property.length";
    private static final String VIOLATION_DISPLAY_NAME_PROPERTY_EMPTY = "app.user.property.validationError.displayName.property.empty";
    private static final String VIOLATION_DISPLAY_NAME_PROPERTY_LENGTH = "app.user.property.validationError.displayName.property.length";
    private static final String VIOLATION_NAME_PROPERTY_NOT_UNIQUE = "app.user.property.validationError.name.not.unique";
    private static final String VIOLATION_DISPLAY_NAME_PROPERTY_NOT_UNIQUE = "app.user.property.validationError.displayName.not.unique";
    private static final String VIOLATION_USER_NAME_EMPTY = "app.user.data.validationError.userName.empty";
    private static final String VIOLATION_USER_NAME_LENGTH = "app.user.data.validationError.userName.length";
    private static final String VIOLATION_PASSWORD_EMPTY = "app.user.data.validationError.password.empty";
    private static final String VIOLATION_PASSWORD_LENGTH = "app.user.data.validationError.password.length";
    private static final String VIOLATION_EMAIL_LENGTH = "app.user.data.validationError.email.length";
    private static final String VIOLATION_FIRSTNAME_LENGTH = "app.user.data.validationError.firstname.length";
    private static final String VIOLATION_LASTNAME_LENGTH = "app.user.data.validationError.lastname.length";

    /** The user dao. */
    @Autowired
    private UserDao userDAO;

    /** The role dao. */
    @Autowired
    private RoleDao roleDAO;
    /** The security service. */
    @Autowired
    private SecurityServiceExt securityService;

    @Autowired
    private AuditEventsWriter auditEventsWriter;

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.security.IUserService#create(com.unidata.
     * mdm.backend.service.security.dto.UserWithPasswordDTO)
     */
    @Override
    @Transactional
    public void create(final UserWithPasswordDTO user) {
        try {
            if (!user.isExternal()) {
                user.setSecurityDataSource(SecurityUtils.UNIDATA_SECURITY_DATA_SOURCE);
            }

            validateUser(user);

            final List<Role> roles = user.getRoles();
            final UserPO toSave = new UserPO();
            UserDTOToPOConverter.convert(user, toSave);

            final List<RolePO> rolePOs = new ArrayList<>();
            for (int i = 0; roles != null && i < roles.size(); i++) {

                final Role role = roles.get(i);
                if (StringUtils.isBlank(role.getName())) {
                    continue;
                }

                final RolePO rolePO = roleDAO.findByName(role.getName());
                if (rolePO != null) {
                    rolePOs.add(rolePO);
                }
            }

            toSave.setRoles(rolePOs);

            userDAO.create(toSave, user.getSecurityLabels());


            saveUserPropertyValues(toSave.getId(), user.getProperties());

            auditEventsWriter.writeSuccessEvent(AuditActions.USER_CREATE, user);
        } catch (Exception e) {
            auditEventsWriter.writeUnsuccessfulEvent(AuditActions.USER_CREATE, e, user);
            throw e;
        }
    }
    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.security.IUserService#updateUser(java.
     * lang.String,
     * com.unidata.mdm.backend.common.dto.security.UserWithPasswordDTO,
     * java.lang.String)
     */
    @Override
    @Transactional
    public void updateUser(final String login, final UserWithPasswordDTO toUpdateDTO, String tokenString) {
        try {

            final List<String> newRoles = toUpdateDTO.getRoles() == null ?
                    Collections.emptyList() :
                    toUpdateDTO.getRoles().stream().map(Role::getName).collect(Collectors.toList());

            // retrieve user from database
            final UserPO toUpdatePO = userDAO.findByLogin(login);
            boolean inactiveOrNotAdminUpdate = !toUpdateDTO.isActive() || !toUpdateDTO.isAdmin();
            if (toUpdatePO.isActive() && toUpdatePO.isAdmin() && userDAO.isLastAdmin() && inactiveOrNotAdminUpdate) {
                throw new BusinessException("Unable to deactivate user! At least one active admin user must exist!",
                        ExceptionId.EX_SECURITY_CANNOT_DEACTIVATE_USER);
            }

            final List<RolePO> rolesToUpdate = toUpdatePO.getRoles();
            // prepare list with user roles
            final List<RolePO> filteredRoles = rolesToUpdate.stream()
                                                            .filter(r -> newRoles.contains(r.getName()))
                                                            .collect(Collectors.toList());

            filteredRoles.forEach(r -> newRoles.remove(r.getName()));
            newRoles.forEach(roleName -> {
                RolePO toAdd = roleDAO.findByName(roleName);
                if (toAdd != null) {
                    filteredRoles.add(toAdd);
                }
            });

            if (!toUpdateDTO.isExternal()) {
                toUpdateDTO.setSecurityDataSource(SecurityUtils.UNIDATA_SECURITY_DATA_SOURCE);
            }

            UserDTOToPOConverter.convert(toUpdateDTO, toUpdatePO);
            toUpdatePO.setRoles(filteredRoles);
            if (!StringUtils.isEmpty(toUpdateDTO.getPassword())) {
                PasswordPO password = new PasswordPO();
                password.setActive(true);
                password.setPasswordText(BCrypt.hashpw(toUpdateDTO.getPassword(), BCrypt.gensalt()));
                password.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                password.setUpdatedBy(SecurityUtils.getCurrentUserName());
                password.setUser(toUpdatePO);
                List<PasswordPO> passwordPOs = new ArrayList<>();
                passwordPOs.add(password);
                toUpdatePO.setPassword(passwordPOs);
            } else {
                toUpdatePO.setPassword(null);
            }

            toUpdatePO.setRoles(filteredRoles);
            toUpdatePO.setProperties(UsersConverter.convertPropertyValuesDtoToPo(toUpdateDTO.getProperties()));

            userDAO.update(login, toUpdatePO, toUpdateDTO.getSecurityLabels());
            saveUserPropertyValues(toUpdatePO.getId(), toUpdateDTO.getProperties());

            securityService.logoutUserByName(toUpdateDTO.getLogin());

            auditEventsWriter.writeSuccessEvent(AuditActions.USER_UPDATE, toUpdateDTO);
        } catch (Exception e) {
            auditEventsWriter.writeUnsuccessfulEvent(AuditActions.USER_UPDATE, e, toUpdateDTO);
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.security.IUserService#updateUser(java.
     * lang.String,
     * com.unidata.mdm.backend.common.dto.security.UserWithPasswordDTO)
     */
    @Override
    @Transactional
    public void updateUser(String login, UserWithPasswordDTO user) {
        updateUser(login, user, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.security.IUserService#getLocalUserByName(
     * java.lang.String)
     */
    @Override
    @Transactional
    public UserWithPasswordDTO getUserByName(final String login) {
        MeasurementPoint.start();
        try {
            final UserPO user = userDAO.findByLogin(login);
            return enrichRegularUser(user);
        } finally {
            MeasurementPoint.stop();
        }
    }

    private UserWithPasswordDTO enrichRegularUser(final UserPO user) {
        if (user == null) {
            return null;
        }

        final UserWithPasswordDTO userDTO = UserConverter.convertPO(user);
        enrichSecurityLabels(userDTO.getSecurityLabels());
        userDTO.getRoles().forEach(u -> enrichSecurityLabels(u.getSecurityLabels()));

        userDTO.setProperties(UsersConverter.convertPropertyValuesPoToDto(user.getProperties()));

        return userDTO;
    }

    private void enrichSecurityLabels(List<SecurityLabel> slas) {
        if (slas != null) {
            for (final SecurityLabel label : slas) {
                final List<LabelAttributePO> toCheck = roleDAO.findSecurityLabelByName(label.getName())
                        .getLabelAttribute();

                final List<LabelAttributePO> toEnrich = toCheck.stream()
                        .filter(labelAttributePO -> label.getAttributes().stream()
                                .noneMatch(sla -> StringUtils.equals(sla.getName(), labelAttributePO.getName())))
                        .collect(Collectors.toList());

                for (final LabelAttributePO labelAttributePO : toEnrich) {
                    final SecurityLabelAttributeDTO attributeDTO = new SecurityLabelAttributeDTO();
                    attributeDTO.setId(labelAttributePO.getId());
                    attributeDTO.setDescription(labelAttributePO.getDescription());
                    attributeDTO.setPath(labelAttributePO.getPath());
                    attributeDTO.setName(labelAttributePO.getName());
                    attributeDTO.setValue("");
                    label.getAttributes().add(attributeDTO);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.security.IUserService#getAllUsers()
     */
    @Override
    @Transactional
    public List<UserDTO> getAllUsers() {
        List<UserPO> users = userDAO.getAll();
        return UserConverter.convertPOs(users);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.security.IUserService#deactivateUser(java
     * .lang.String)
     */
    @Override
    @Transactional
    public void deactivateUser(String login) {
        UserWithPasswordDTO user = getUserByName(login);
        user.setActive(false);
        updateUser(login, user);

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.security.IUserService#getAllProperties()
     */
    @Override
    @Transactional
    public List<UserPropertyDTO> getAllProperties() {
        return UsersConverter.convertPropertiesPoToDto(userDAO.loadAllProperties());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.security.IUserService#saveProperty(com.
     * unidata.mdm.backend.service.security.dto.UserPropertyDTO)
     */
    @Override
    @Transactional
    public void saveProperty(final UserPropertyDTO property) {

        validateUserProperty(property);

        final UserPropertyPO po = UsersConverter.convertPropertyDtoToPo(property);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        String userName = SecurityUtils.getCurrentUserName();

        po.setCreatedAt(now);
        po.setCreatedBy(userName);
        po.setUpdatedAt(now);
        po.setUpdatedBy(userName);

        userDAO.saveProperty(po);

        property.setId(po.getId());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.security.IUserService#deleteProperty(
     * long)
     */
    @Override
    @Transactional
    public void deleteProperty(long id) {
        userDAO.deleteProperty(id);

        LOGGER.debug("Delete user property [id={}]", id);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.security.IUserService#
     * loadUserPropertyValues(long)
     */
    @Override
    public List<UserPropertyDTO> loadUserPropertyValues(int userId) {
        return UsersConverter.convertPropertyValuesPoToDto(userDAO.loadUserPropertyValuesByUserId(userId));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.security.IUserService#
     * saveUserPropertyValues(long, java.util.List)
     */
    @Override
    @Transactional
    public void saveUserPropertyValues(long userId, List<UserPropertyDTO> userProperties) {

        userDAO.deleteUserPropertyValuesByUserId(userId);
        if (!CollectionUtils.isEmpty(userProperties)) {

            final List<UserPropertyValuePO> valuePOs = UsersConverter.convertPropertyValuesDtoToPo(userProperties);
            valuePOs.stream().forEach(valuePO -> {
                valuePO.setUserId(userId);

                Timestamp now = new Timestamp(System.currentTimeMillis());
                String userName = SecurityUtils.getCurrentUserName();

                valuePO.setCreatedAt(now);
                valuePO.setCreatedBy(userName);
                valuePO.setUpdatedAt(now);
                valuePO.setUpdatedBy(userName);
            });

            userDAO.saveUserPropertyValues(valuePOs);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.security.IUserService#insertToken(com.
     * unidata.mdm.backend.dto.storage.SecurityToken)
     */
    @Override
    @Transactional
    public void insertToken(SecurityToken token) {
        final TokenPO tokenPO = new TokenPO();
        tokenPO.setToken(token.getToken());
        tokenPO.setCreatedAt(new Timestamp(token.getCreatedAt().getTime()));
        tokenPO.setCreatedBy(token.getUser().getName());
        tokenPO.setUser(userDAO.findByLogin(token.getUser().getLogin()));
        userDAO.saveToken(tokenPO);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.security.IUserService#getUserEvents(java.
     * lang.String, java.util.Date, int, int)
     */
    @Override
    public List<UserEventDTO> getUserEvents(String login, Date from, int page, int count) {

        MeasurementPoint.start();
        try {
            List<UserEventPO> events = userDAO.loadUserEvents(login, from, page, count);
            List<UserEventDTO> result = new ArrayList<>();
            for (int j = 0; events != null && j < events.size(); j++) {

                UserEventDTO dto = new UserEventDTO(events.get(j).getId());
                dto.setBinaryDataId(events.get(j).getBinaryDataId());
                dto.setCharacterDataId(events.get(j).getCharacterDataId());
                dto.setContent(events.get(j).getContent());
                dto.setCreateDate(events.get(j).getCreateDate());
                dto.setCreatedBy(events.get(j).getCreatedBy());
                dto.setType(events.get(j).getType());

                result.add(dto);
            }

            return result;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.security.IUserService#countUserEvents(
     * java.lang.String)
     */
    @Override
    public Long countUserEvents(String login) {

        MeasurementPoint.start();
        try {
            return userDAO.countUserEvents(login);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.security.IUserService#deleteUserEvent(
     * java.lang.String)
     */
    @Override
    @Transactional
    public boolean deleteUserEvent(String eventId) {

        MeasurementPoint.start();
        try {
            return userDAO.deleteUserEvent(eventId);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteUserEvents(List<String> eventIds) {

        MeasurementPoint.start();
        try {
            return userDAO.deleteUserEvents(eventIds);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteAllEventsForCurrentUser(Date point) {

        MeasurementPoint.start();
        try {
            return userDAO.deleteAllUserEvents(SecurityUtils.getCurrentUserName(), point);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.security.IUserService#upsert(com.unidata.
     * mdm.backend.service.ctx.UpsertUserEventRequestContext)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserEventDTO upsert(UpsertUserEventRequestContext ueCtx) {

        MeasurementPoint.start();
        try {

            UserEventPO po = new UserEventPO();
            po.setContent(ueCtx.getContent());
            po.setCreatedBy(SecurityUtils.getCurrentUserName());
            po.setType(ueCtx.getType());

            if (ueCtx.getLogin() != null) {
                po = userDAO.create(po, ueCtx.getLogin());
            } else if (ueCtx.getUserId() != null) {
                po.setUserId(ueCtx.getUserId());
                po = userDAO.create(po);
            } else {
                final String message = "No user id/login supplied. Cannot upsert user event.";
                LOGGER.warn(message);
                throw new DataProcessingException(message, ExceptionId.EX_DATA_USER_EVENT_NO_USER);
            }

            return po != null ? new UserEventDTO(po.getId()) : null;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.security.IUserService#
     * verifyAndUpserExternalUser(com.unidata.mdm.backend.common.integration.auth.dto.User,
     * java.util.List, java.lang.String)
     */
    @Override
    @Transactional
    public void verifyAndUpserExternalUser(final User user) {

        if (StringUtils.isBlank(user.getLogin())) {
            final String message
                = "Error while saving externally authentified user with no Unidata profile: Mandatory fields are empty, check 'login'.";
            LOGGER.warn(message);
            throw new SecurityDataProviderException(SecurityState.AUTHENTICATION_FAILED, message);
        }

        final UserPO userPo = userDAO.findByLogin(user.getLogin());
        if (userPo == null) {
            LOGGER.debug("Creating external user with login [" + user.getLogin() + "]");
            create(createUserWithPasswordDTO(user));
        } else {
            final Date dateFromDb = userPo.getUpdatedAt() != null ? userPo.getUpdatedAt() : userPo.getCreatedAt();
            if (dateFromDb != null && user.getUpdatedAt() != null) {
                if (dateFromDb.before(user.getUpdatedAt())) {
                    LOGGER.debug("Updating external user with login [" + user.getLogin() + "]");
                    final UserWithPasswordDTO dto = createUserWithPasswordDTO(user);
                    dto.setUpdatedBy("SYSTEM");
                    dto.setUpdatedAt(new Date());
                    updateUser(user.getLogin(), dto);
                } else {
                    LOGGER.debug("External user with login [" + user.getLogin() + "] and provider ["
                            + user.getSecurityDataSource() + "] is up to date");
                }
            } else {
                LOGGER.warn("Cannot check user update/create date, login: " + userPo.getLogin());
            }
        }
    }

    /**
     * Creates user DTO for externally authenticated user.
     * @param authUser the user
     * @return DTO
     */
    private UserWithPasswordDTO createUserWithPasswordDTO(User authUser) {

        final UserWithPasswordDTO dto = new UserWithPasswordDTO();
        dto.setLogin(authUser.getLogin());
        dto.setPasswordLastChangedAt(authUser.getPasswordUpdatedAt());

        List<Role> roles = authUser.getRoles() == null ? Collections.emptyList() : authUser.getRoles();

        dto.setRoles(roles);
        dto.setEmail(authUser.getEmail());
        dto.setLocale(authUser.getLocale());
        dto.setAdmin(authUser.isAdmin());
        dto.setFullName(authUser.getName());
        dto.setActive(true);
        dto.setCreatedAt(new Date());
        dto.setCreatedBy("SYSTEM");
        dto.setExternal(true);
        dto.setSecurityDataSource(authUser.getSecurityDataSource());

        final List<SecurityLabel> secLabelList = new ArrayList<>();
        for (final Role r : roles) {

            if (CollectionUtils.isEmpty(r.getSecurityLabels())) {
                continue;
            }

            for (final SecurityLabel secLabel : r.getSecurityLabels()) {

                final SecurityLabelDTO target = new SecurityLabelDTO();
                target.setName(secLabel.getName());
                target.setDisplayName(secLabel.getDisplayName());

                final List<SecurityLabelAttribute> attributes = new ArrayList<>();
                for (final SecurityLabelAttribute attr : secLabel.getAttributes()) {

                    final SecurityLabelAttributeDTO attrDto = new SecurityLabelAttributeDTO();
                    attrDto.setId(attr.getId());
                    attrDto.setName(attr.getName());
                    attrDto.setDescription(attr.getDescription());
                    attrDto.setPath(attr.getPath());
                    attrDto.setValue(attr.getValue());
                    attributes.add(attrDto);
                }

                target.setAttributes(attributes);
                secLabelList.add(target);
            }
        }
        dto.setSecurityLabels(secLabelList);

        return dto;
    }

    private void validateUserProperty(final UserPropertyDTO property) {

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

            UserPropertyPO existProperty = userDAO.loadPropertyByName(property.getName());
            if (existProperty != null && !existProperty.getId().equals(property.getId())) {
                validationResult.add(new ValidationResult("User property 'name' must be unique. Found existing property with name {0}.",
                        VIOLATION_NAME_PROPERTY_NOT_UNIQUE, property.getName()));
            }

            existProperty = userDAO.loadPropertyByDisplayName(property.getDisplayName());
            if (existProperty != null && !existProperty.getId().equals(property.getId())) {
                validationResult.add(new ValidationResult("User property 'displayName' must be unique. Found existing property with displayName {0}.",
                        VIOLATION_DISPLAY_NAME_PROPERTY_NOT_UNIQUE, property.getDisplayName()));
            }
        }

        if (!CollectionUtils.isEmpty(validationResult)) {
            throw new UserRolePropertyException("User properties validation error.",
                    ExceptionId.EX_USER_PROPERTY_VALIDATION_ERROR, validationResult);
        }
    }

    /**
     * Checks the record for validity.
     * @param user the record to validate
     */
    private void validateUser(final UserWithPasswordDTO user) {

        final List<ValidationResult> validationResult = new ArrayList<>();

        // 1. Login name
        if (StringUtils.isBlank(user.getLogin())) {
            validationResult.add(new ValidationResult("User name is empty.",
                    VIOLATION_USER_NAME_EMPTY));
        } else if (user.getLogin().length() > USER_FIELD_LIMIT) {
            validationResult.add(new ValidationResult("User name is larger than the field limit.",
                    VIOLATION_USER_NAME_LENGTH, Integer.valueOf(USER_FIELD_LIMIT)));
        }

        // 2. Password
        if (!user.isExternal()) {
            if (StringUtils.isBlank(user.getPassword())) {
                validationResult.add(new ValidationResult("Supplied password is empty.", VIOLATION_PASSWORD_EMPTY));
            } else if (user.getPassword().length() > USER_FIELD_LIMIT) {
                validationResult.add(new ValidationResult("Supplied password is larger than the field limit.",
                        VIOLATION_PASSWORD_LENGTH, Integer.valueOf(USER_FIELD_LIMIT)));
            }
        }

        // 3. Email
        if (!StringUtils.isEmpty(user.getEmail()) && user.getEmail().length() > USER_FIELD_LIMIT) {
            validationResult.add(new ValidationResult("Email is larger than the field limit.",
                    VIOLATION_EMAIL_LENGTH, Integer.valueOf(USER_FIELD_LIMIT)));
        }

        if (!StringUtils.isEmpty(user.getFirstName()) && user.getFirstName().length() > USER_FIELD_LIMIT) {
            validationResult.add(new ValidationResult("First name is larger than the field limit.",
                    VIOLATION_FIRSTNAME_LENGTH,  Integer.valueOf(USER_FIELD_LIMIT)));
        }

        // 5. Last name
        if (!StringUtils.isEmpty(user.getLastName()) && user.getFirstName().length() > USER_FIELD_LIMIT) {
            validationResult.add(new ValidationResult("Last name is larger than the field limit.",
                    VIOLATION_LASTNAME_LENGTH,  Integer.valueOf(USER_FIELD_LIMIT)));
        }

        if (!CollectionUtils.isEmpty(validationResult)) {
            throw new UserRolePropertyException("User {} data validation error.",
                    ExceptionId.EX_USER_DATA_VALIDATION_ERROR, validationResult, user.getLogin());
        }

        // 6. Duplicate
        if (userDAO.isExist(user.getLogin())) {
            throw new UserRolePropertyException("User {} already exists",
                    ExceptionId.EX_SECURITY_USER_ALREADY_EXIST, null, user.getLogin());
        }
    }
    /**
     *
     */
	@Override
	public List<Endpoint> getAPIList() {
		List<ApiPO> source = userDAO.getAPIList();
		return UserConverter.convertAPIs(source);
	}

	@Override
    public boolean isAdminUser(String login){
        boolean result = false;
        if(StringUtils.isNotEmpty(login)){
            UserPO user = userDAO.findByLogin(login);
            if(user != null){
                result = user.isAdmin();
            }
        }
        return result;
    }
}

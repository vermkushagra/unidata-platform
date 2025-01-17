package com.unidata.mdm.backend.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.dto.security.UserPropertyDTO;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;
import com.unidata.mdm.backend.service.security.po.ApiPO;
import com.unidata.mdm.backend.service.security.po.LabelAttributeValuePO;
import com.unidata.mdm.backend.service.security.po.TokenPO;
import com.unidata.mdm.backend.service.security.po.UserEventPO;
import com.unidata.mdm.backend.service.security.po.UserPO;
import com.unidata.mdm.backend.service.security.po.UserPropertyPO;
import com.unidata.mdm.backend.service.security.po.UserPropertyValuePO;

/**
 * The Interface UserDao.
 */
public interface UserDao {

    /**
     * Save.
     *
     * @param user
     *            the user
     * @param userLabels
     *            the user labels
     * @return the user po
     */
    UserPO create(UserPO user, List<SecurityLabel> userLabels);

    /**
     * Find by login.
     *
     * @param login
     *            the login
     * @return the user po
     */
    UserPO findByLogin(String login);

    /**
     * Find by email.
     *
     * @param email
     *            the email
     * @param source
     *            external provider source name.
     * @return the user po
     */
    UserPO findByEmail(String email, String source);

    /**
     * Update.
     *
     * @param login
     *            the login
     * @param user
     *            the user
     * @param userLabels
     *            the user labels
     * @return the user po
     */
    UserPO update(String login, UserPO user, List<SecurityLabel> userLabels);

    /**
     * Gets the all.
     *
     * @return the all
     */
    List<UserPO> getAll();

    /**
     * Load list of all user properties.
     *
     * @return the list
     */
    List<UserPropertyPO> loadAllProperties();

    /**
     * Load list of all user properties.
     *
     * @param name
     *            the name
     * @return the user property po
     */
    UserPropertyPO loadPropertyByName(String name);

    /**
     * Load list of all user properties.
     *
     * @param displayName
     *            the display name
     * @return the user property po
     */
    UserPropertyPO loadPropertyByDisplayName(String displayName);

    /**
     * Save property.
     *
     * @param property
     *            the property
     */
    void saveProperty(UserPropertyPO property);

    /**
     * Delete property with all user values.
     *
     * @param id
     *            the id
     */
    void deleteProperty(long id);

    /**
     * Save properties.
     *
     * @param properties
     *            the properties
     * @param login
     *            the login
     */
    void saveProperties(List<UserPropertyDTO> properties, String login);

    /**
     * Insert or update user property values (w/o delete).
     *
     * @param propertyValues
     *            the property values
     */
    void saveUserPropertyValues(Collection<UserPropertyValuePO> propertyValues);

    /**
     * Delete property values by property value ID's.
     *
     * @param ids
     *            the ids
     */
    void deleteUserPropertyValuesByIds(Collection<Long> ids);

    /**
     * Delete property values by property value ID's.
     *
     * @param userId
     *            the user id
     */
    void deleteUserPropertyValuesByUserId(long userId);

    /**
     * Loads user property values by user id
     *
     * @param userId
     *            the id
     * @return property values
     */
    List<UserPropertyValuePO> loadUserPropertyValuesByUserId(Integer userId);

    /**
     * Load user property values by user ID's.
     *
     * @param userIds
     *            the user ids
     * @return the map
     */
    Map<Integer, List<UserPropertyValuePO>> loadUserPropertyValuesByUserIds(Collection<Integer> userIds);

    /**
     * Checks if is last admin.
     *
     * @return true, if is last admin
     */
    boolean isLastAdmin();

    /**
     * Save token.
     *
     * @param tokenPO
     *            the token po
     */
    void saveToken(TokenPO tokenPO);

    /**
     * Delete token.
     *
     * @param tokenString
     *            security token as string
     */
    void deleteToken(String tokenString);

    /**
     * Loads user events for a login name, starting from date.
     *
     * @param login
     *            user login
     * @param from
     *            the date to start loading events from (e. g. all events will
     *            be younger than the given date)
     * @param page
     *            the page number
     * @param count
     *            the records count
     * @return list
     */
    List<UserEventPO> loadUserEvents(String login, Date from, int page, int count);

    /**
     * Count user events for a login name.
     *
     * @param login
     *            user login
     * @return the records count
     */
    Long countUserEvents(String login);

    /**
     * Deletes an event.
     *
     * @param eventId
     *            the event id
     * @return true on success, false otherwise
     */
    boolean deleteUserEvent(String eventId);

    /**
     * Deletes several events at once.
     *
     * @param eventIds
     *            the ids
     * @return true, if successful, false otherwise
     */
    boolean deleteUserEvents(List<String> eventIds);

    /**
     * Deletes all events of a user, which are older then the given date.
     *
     * @param login
     *            user login
     * @param to
     *            the to
     * @return true, if successful, false otherwise
     */
    boolean deleteAllUserEvents(String login, Date to);

    /**
     * Creates user event.
     *
     * @param event
     *            the event to create
     * @return updated event
     */
    UserEventPO create(UserEventPO event);

    /**
     * Creates user event.
     *
     * @param event
     *            the event
     * @param login
     *            user login
     * @return updated event
     */
    UserEventPO create(UserEventPO event, String login);

    /**
     * Checks if user exist.
     *
     * @param login
     *            the login name.
     * @return <code>true</code> if user exist, otherwise <code>false</code>
     */
    boolean isExist(String login);

    /**
     * List of available APIs(e.g REST, SOAP)
     *
     * @return List of available APIs(e.g REST, SOAP)
     */
    List<ApiPO> getAPIList();
}
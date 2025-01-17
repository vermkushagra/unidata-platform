package com.unidata.mdm.backend.service.security;

import java.util.Date;
import java.util.List;

import com.unidata.mdm.backend.common.context.UpsertUserEventRequestContext;
import com.unidata.mdm.backend.common.dto.security.UserEndpointDTO;
import com.unidata.mdm.backend.common.dto.security.UserDTO;
import com.unidata.mdm.backend.common.dto.security.UserEventDTO;
import com.unidata.mdm.backend.common.dto.security.UserPropertyDTO;
import com.unidata.mdm.backend.common.dto.security.UserWithPasswordDTO;
import com.unidata.mdm.backend.common.integration.auth.Endpoint;
import com.unidata.mdm.backend.common.integration.auth.User;
import com.unidata.mdm.backend.common.security.SecurityToken;

public interface UserService {

    /**
     * Creates the new user.
     *
     * @param user
     *            the user
     */
    void create(UserWithPasswordDTO user);

    /**
     * Update user and invalidate token if password is updated.
     *
     * @param login
     *            the login
     * @param toUpdateDTO
     *            the to update dto
     * @param tokenString
     *            the user token
     */
    void updateUser(String login, UserWithPasswordDTO toUpdateDTO, String tokenString);

    /**
     * Update user.
     *
     * @param login
     *            the login
     * @param user
     *            the user
     */
    void updateUser(String login, UserWithPasswordDTO user);

    /**
     * Gets the user by name.
     *
     * @param login
     *            the login
     * @return the user by name
     */
    UserWithPasswordDTO getUserByName(String login);
    /**
     * Gets the all users.
     *
     * @return the all users
     */
    List<UserDTO> getAllUsers();

    /**
     * Deactivate user.
     *
     * @param login
     *            the login
     */
    void deactivateUser(String login);

    /**
     * Gets the all properties.
     *
     * @return the all properties
     */
    List<UserPropertyDTO> getAllProperties();

    /**
     *
     * @param property
     */
    void saveProperty(UserPropertyDTO property);

    /**
     *
     * @param id
     */
    void deleteProperty(long id);

    /**
     *
     * @param userId
     * @return
     */
    List<UserPropertyDTO> loadUserPropertyValues(int userId);

    /**
     *
     * @param userId
     * @param userProperties
     */
    void saveUserPropertyValues(long userId, List<UserPropertyDTO> userProperties);

    /**
     * Insert token.
     *
     * @param token
     *            the token
     */
    void insertToken(SecurityToken token);

    /**
     * Gets user events.
     * @return
     */
    List<UserEventDTO> getUserEvents(String login, Date from, int page, int count);

    /**
     * Count user events.
     * @param login user login
     * @return count
     */
    Long countUserEvents(String login);

    /**
     * Deletes an event by id.
     * @param eventId the event id
     * @return true, if successfl, false otherwise
     */
    boolean deleteUserEvent(String eventId);

    /**
     * Deletes selected events by ids.
     * @param eventIds the event ids
     * @return true, if successful, false otherwise
     */
    boolean deleteUserEvents(List<String> eventIds);

    /**
     * Deletes all events of a user.
     * @param point the point in time
     * @return true, if successful, false otherwise
     */
    boolean deleteAllEventsForCurrentUser(Date point);

    /**
     * Saves an event.
     * @param ueCtx the save context
     * @return DTO
     */
    UserEventDTO upsert(UpsertUserEventRequestContext ueCtx);
    /**
     * Verifies and creates external user for full external authentication.
     * @param user the user
     */
    void verifyAndUpserExternalUser(User user);
    /**
     * List of available APIs(eg SOAP, REST)
     * @return List of available APIs(eg SOAP, REST)
     */
    List<Endpoint> getAPIList();

    /**
     * Check whether the user with name is an administrator.
     * @param login user login
     * @return if login null or empty return false, else return value for user
     */
    boolean isAdminUser(String login);

}
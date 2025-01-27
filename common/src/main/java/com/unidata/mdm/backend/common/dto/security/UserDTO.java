package com.unidata.mdm.backend.common.dto.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.unidata.mdm.backend.common.integration.auth.CustomProperty;
import com.unidata.mdm.backend.common.integration.auth.Endpoint;
import com.unidata.mdm.backend.common.integration.auth.Role;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;


/**
 * The Class UserRO.
 * @author ilya.bykov
 */
public class UserDTO extends BaseSecurityDTO {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 1179950904997430308L;

    /** The login. */
    private String login;

    /** The first name. */
    private String firstName;

    /** The last name. */
    private String lastName;

    /** The full name. */
    private String fullName;

    /** The admin. */
    private boolean admin;

    /**  Is active. */
    private boolean active;

    /** The email. */
    private String email;

    /** Locale. */
    private Locale locale;

    /** The roles. */
    private transient List<Role> roles;

    /** The security labels. */
    private transient List<SecurityLabel> securityLabels;

    /** The properties. */
    private transient List<UserPropertyDTO> properties;

    /** The apis. */
    private List<Endpoint> endpoints;
    /**
     * User external mark.
     */
    private boolean external;
    /**
     * User external authentication/authorization/profile source name.
     */
    private String securityDataSource;

    /**
     * Gets the login.
     *
     * @return the login
     */
    public String getLogin() {
        return login;
    }

    /**
     * Gets the first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name.
     *
     * @param firstName
     *            the new first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Checks if is active.
     *
     * @return true, if is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active.
     *
     * @param active the new active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name.
     *
     * @param lastName
     *            the new last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the full name.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the full name.
     *
     * @param fullName
     *            the new full name
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Checks if is admin.
     *
     * @return true, if is admin
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * Sets the admin.
     *
     * @param admin
     *            the new admin
     */
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    /**
     * Sets the login.
     *
     * @param login
     *            the new login
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Gets the email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     *
     * @param email
     *            the new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * @param locale the locale to set
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Gets the roles.
     *
     * @return the roles
     */
    public List<Role> getRoles() {
        if (this.roles == null) {
            this.roles = new ArrayList<>();
        }
        return roles;
    }

    /**
     * Sets the roles.
     *
     * @param roles
     *            the new roles
     */
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    /**
     * Adds the role.
     *
     * @param role
     *            the role
     */
    public void addRole(Role role) {
        if (this.roles == null) {
            this.roles = new ArrayList<>();
        }
        this.roles.add(role);
    }

    /**
     * Adds the roles.
     *
     * @param roles
     *            the roles
     */
    public void addRoles(List<Role> roles) {
        if (this.roles == null) {
            this.roles = new ArrayList<>();
        }
        this.roles.addAll(roles);
    }

    /**
     * Gets the security labels.
     *
     * @return the security labels
     */
    public List<SecurityLabel> getSecurityLabels() {
        return securityLabels;
    }

    /**
     * Sets the security labels.
     *
     * @param securityLabels the new security labels
     */
    public void setSecurityLabels(final List<SecurityLabel> securityLabels) {
        this.securityLabels = securityLabels;
    }

    /**
     * Gets the properties.
     *
     * @return the properties
     */
    public List<UserPropertyDTO> getProperties() {
        return properties;
    }


    /**
     * Sets the properties.
     *
     * @param properties the new properties
     */
    public void setProperties(List<UserPropertyDTO> properties) {
        this.properties = properties;
    }

    /**
     * Whether user has external authentication/authorization/profile.
     *
     * @return true, if is external
     */
    public boolean isExternal() {
        return external;
    }

    /**
     * Sets user external authentication/authorization/profile source.
     * @param external    external parameter.
     */
    public void setExternal(boolean external) {
        this.external = external;
    }

    /**
     * Get user authentication provider source.
     * @return Authentication provider source.
     */
    public String getSecurityDataSource() {
        return securityDataSource;
    }

    /**
     * Set authentication provider source.
     * @param source    authentication provider source.
     */
    public void setSecurityDataSource(String source) {
        this.securityDataSource = source;
    }

    /**
     * Gets the custom properties.
     *
     * @return the customProperties
     */
    public List<CustomProperty> getCustomProperties() {
        return Objects.isNull(properties) ? Collections.emptyList() : Collections.unmodifiableList(properties);
    }

    /**
     * Gets the apis.
     *
     * @return the apis
     */
    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    /**
     * Sets the apis.
     *
     * @param endpoints the new apis
     */
    public void setEnpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }
}

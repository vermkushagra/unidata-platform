package com.unidata.mdm.integration.example.auth.sample;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.unidata.mdm.backend.common.integration.auth.AbstractUserInfo;
import com.unidata.mdm.backend.common.integration.auth.Endpoint;
import com.unidata.mdm.backend.common.integration.auth.Role;

/**
 * @author Denis Kostovarov
 */
public class SampleUserInfo extends AbstractUserInfo {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -4007459978839733373L;
    /**
     * Login name.
     */
    private String login;
    /**
     * Password.
     */
    private String password;
    /**
     * List of roles.
     */
    private List<Role> roles;

    private List<Endpoint> endpoints;

    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public List<Role> getRoles() {
        return Objects.isNull(roles) ? Collections.emptyList() : roles;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public List<Endpoint> getEndpoints() {
        return endpoints == null ? Collections.emptyList() : endpoints;
    }

    @Override
    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }

    @Override
    public boolean isExternal() {
        return false;
    }
}

/**
 * Date: 05.07.2016
 */

package com.unidata.mdm.backend.service.security.po;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class RolePropertyValuePO extends BaseSecurityPO {
    private Long id;
    private Long roleId;
    private RolePropertyPO property;
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public RolePropertyPO getProperty() {
        return property;
    }

    public void setProperty(RolePropertyPO property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public enum FieldColumns {
        ID,
        ROLE_ID,
        PROPERTY_ID,
        VALUE
    }
}

/**
 * Date: 05.07.2016
 */

package com.unidata.mdm.backend.service.security.po;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class UserPropertyPO extends BaseSecurityPO {
    private Long id;
    private String name;
    private String displayName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public enum FieldColumns {
        ID,
        NAME,
        DISPLAY_NAME
    }
}

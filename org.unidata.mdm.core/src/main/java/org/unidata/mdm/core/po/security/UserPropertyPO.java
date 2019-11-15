/**
 * Date: 05.07.2016
 */

package org.unidata.mdm.core.po.security;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class UserPropertyPO extends BaseSecurityPO {
    private Long id;
    private String name;
    private boolean required;
    private String displayName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
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
        REQUIRED,
        NAME,
        DISPLAY_NAME
    }
}

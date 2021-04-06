package com.unidata.mdm.backend.api.rest.dto.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.security.RightRO;
import com.unidata.mdm.backend.common.dto.CustomPropertyDefinition;
import org.apache.commons.collections.CollectionUtils;

/**
 * @author Michael Yashin. Created on 25.05.2015.
 */
public abstract class AbstractAttributeDefinition {
    /**
     * Name - [_a-zA-Z].
     */
    protected String name;
    /**
     * Display name - any char sequence.
     */
    protected String displayName;
    /**
     * Description - any char sequence.
     */
    protected String description;
    /**
     * Attribute is read only.
     */
    protected boolean readOnly;
    /**
     * Attribute is hidden.
     */
    protected boolean hidden;
    /**
     * Rights object.
     */
    protected RightRO rights;

    protected final List<CustomPropertyDefinition> customProperties = new ArrayList<>();

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the readOnly
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * @param readOnly the readOnly to set
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * @return the hidden
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * @param hidden the hidden to set
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * @return the rights
     */
    public RightRO getRights() {
        return rights;
    }

    /**
     * @param rights the rights to set
     */
    public void setRights(RightRO rights) {
        this.rights = rights;
    }

    public List<CustomPropertyDefinition> getCustomProperties() {
        return Collections.unmodifiableList(customProperties);
    }

    public void setCustomProperties(final Collection<CustomPropertyDefinition> customProperties) {
        if (CollectionUtils.isEmpty(customProperties)) {
            return;
        }
        this.customProperties.clear();
        this.customProperties.addAll(customProperties);
    }

    public void addCustomProperties(final Collection<CustomPropertyDefinition> customProperties) {
        if (CollectionUtils.isEmpty(customProperties)) {
            return;
        }
        this.customProperties.addAll(customProperties);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractAttributeDefinition)) return false;

        AbstractAttributeDefinition that = (AbstractAttributeDefinition) o;

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

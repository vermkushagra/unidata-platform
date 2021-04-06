package com.unidata.mdm.backend.service.model.util.wrappers;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.meta.DQRuleDef;

import java.util.HashSet;
import java.util.Set;

/**
 * Holds attribute info including its full name name and attribute info holder.
 * @author Ruslan Trachuk
 */
public class AttributeWrapper implements ValueWrapper {
    /**
     * Full attribute name including nested attributes name.
     */
    private final String fullAttributeName;
    /**
     * Attribute info.
     */
    private final AttributeInfoHolder attribute;
    /**
     *
     */
    private final Set<DQRuleDef> relatedDQRules;


    public AttributeWrapper(String fullAttributeName, AttributeInfoHolder attribute) {
        this.fullAttributeName = fullAttributeName;
        this.attribute = attribute;
        relatedDQRules = new HashSet<>();
    }

    /**
     * Get full attribute name.
     * @return full attribute name.
     */
    public String getFullAttributeName() {
        return fullAttributeName;
    }

    /**
     * Get attribute info.
     * @return attribute info
     */
    public AttributeInfoHolder getAttribute() {
        return attribute;
    }

    /**
     * Get set of DQ rules that refer to the attribute.
     * @return set of DQ rules that refer to the attribute
     */
    public Set<DQRuleDef> getRelatedDQRules() {
        return relatedDQRules;
    }

    @Override
    public String toString() {
        return "AttributeWrapper{" +
                "fullAttributeName='" + fullAttributeName + '\'' +
                '}';
    }
}

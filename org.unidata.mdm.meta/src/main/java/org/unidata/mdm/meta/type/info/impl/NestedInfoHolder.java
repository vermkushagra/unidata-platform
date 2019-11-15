/**
 *
 */
package org.unidata.mdm.meta.type.info.impl;

import java.util.Map;

import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.core.type.model.IdentityModelElement;
import org.unidata.mdm.meta.NestedEntityDef;

/**
 * @author Mikhail Mikhailov
 * Nested entities wrapper.
 */
public class NestedInfoHolder extends AbstractAttributesInfoHolder implements IdentityModelElement {
    /**
     * The entity.
     */
    private final NestedEntityDef entity;
    /**
     * Entity name
     */
    private final String id;
    /**
     * Constructor.
     * @param entity nested entity
     * @param id its ID
     * @param attrs attributes
     */
    public NestedInfoHolder(NestedEntityDef entity, String id, Map<String, AttributeModelElement> attrs) {
        super(attrs);
        this.entity = entity;
        this.id = id;
    }

    /**
     * @return the entity
     */
    public NestedEntityDef getEntity() {
        return entity;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Long getVersion() {
        return entity.getVersion();
    }
}

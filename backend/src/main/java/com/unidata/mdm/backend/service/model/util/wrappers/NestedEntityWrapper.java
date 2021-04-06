/**
 *
 */
package com.unidata.mdm.backend.service.model.util.wrappers;

import java.util.Map;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.service.model.util.wrappers.AttributesWrapper;
import com.unidata.mdm.meta.NestedEntityDef;

/**
 * @author Mikhail Mikhailov
 * Nested entities wrapper.
 */
public class NestedEntityWrapper extends AttributesWrapper {

    /**
     * The entity.
     */
    private final NestedEntityDef entity;

    /**
     * Constructor.
     * @param entity nested entity
     * @param id its ID
     * @param attrs attributes
     */
    public NestedEntityWrapper(NestedEntityDef entity, String id, Map<String, AttributeInfoHolder> attrs) {
        super(id, attrs);
        this.entity = entity;
    }

    /**
     * @return the entity
     */
    public NestedEntityDef getEntity() {
        return entity;
    }

    @Override
    public String getUniqueIdentifier() {
        return getId();
    }

    @Override
    public Long getVersionOfWrappedElement() {
        return entity.getVersion();
    }
}

/**
 *
 */
package com.unidata.mdm.backend.service.model.util.wrappers;

import java.util.Map;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.meta.AbstractEntityDef;
import com.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov
 *         Relation wrapper.
 */
public class RelationWrapper extends AttributesWrapper implements AbstractEntityWrapper {

    /**
     * Relation.
     */
    private final RelationDef relation;

    /**
     * Constructor.
     */
    public RelationWrapper(RelationDef relation, final String id, final Map<String, AttributeInfoHolder> attrs) {
        super(id, attrs);
        this.relation = relation;
    }

    /**
     * @return the relation
     */
    public RelationDef getRelation() {
        return relation;
    }

    @Override
    public String getUniqueIdentifier() {
        return getId();
    }

    @Override
    public Long getVersionOfWrappedElement() {
        return relation.getVersion();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractEntityDef getAbstractEntity() {
        return relation;
    }
}

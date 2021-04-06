package com.unidata.mdm.backend.service.model.util.wrappers;

import com.unidata.mdm.meta.AbstractEntityDef;

/**
 * @author Mikhail Mikhailov
 * Marks an abstract entity holder.
 */
public interface AbstractEntityWrapper {
    /**
     * Gets the underlaying object (relation, entity, nested or lookup) as abstract entity.
     * @return abstract entity or null.
     */
    AbstractEntityDef getAbstractEntity();
}

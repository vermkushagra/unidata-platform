/**
 *
 */
package com.unidata.mdm.backend.service.model.util.parsers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.model.util.wrappers.NestedEntityWrapper;
import com.unidata.mdm.meta.Model;
import com.unidata.mdm.meta.NestedEntityDef;

/**
 * @author Mikhail Mikhailov
 *
 */
public class NestedEntitiesParser implements ModelParser<NestedEntityWrapper> {

    /**
     * Constructor.
     */
    public NestedEntitiesParser() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, NestedEntityWrapper> parse(Model model){
        final Map<String, NestedEntityWrapper> entities = new ConcurrentHashMap<>();
        for (NestedEntityDef e : model.getNestedEntities()) {
        	
            Map<String, AttributeInfoHolder> attrs = ModelUtils.createAttributesMap(e, model.getNestedEntities());
            entities.put(e.getName(), new NestedEntityWrapper(e, e.getName(), attrs));
        }

        return entities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<NestedEntityWrapper> getValueType() {
        return NestedEntityWrapper.class;
    }

}

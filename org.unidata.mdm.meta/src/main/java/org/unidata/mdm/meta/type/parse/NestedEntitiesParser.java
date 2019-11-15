/**
 *
 */
package org.unidata.mdm.meta.type.parse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.meta.Model;
import org.unidata.mdm.meta.NestedEntityDef;
import org.unidata.mdm.meta.type.info.impl.NestedInfoHolder;
import org.unidata.mdm.meta.util.ModelUtils;

/**
 * @author Mikhail Mikhailov
 *
 */
public class NestedEntitiesParser implements ModelParser<NestedInfoHolder> {

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
    public Map<String, NestedInfoHolder> parse(Model model){
        final Map<String, NestedInfoHolder> entities = new ConcurrentHashMap<>();
        for (NestedEntityDef e : model.getNestedEntities()) {

            Map<String, AttributeModelElement> attrs = ModelUtils.createAttributesMap(e, model.getNestedEntities());
            entities.put(e.getName(), new NestedInfoHolder(e, e.getName(), attrs));
        }

        return entities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<NestedInfoHolder> getValueType() {
        return NestedInfoHolder.class;
    }

}

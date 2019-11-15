/**
 *
 */
package org.unidata.mdm.meta.type.parse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.meta.EntityDef;
import org.unidata.mdm.meta.Model;
import org.unidata.mdm.meta.type.info.impl.EntityInfoHolder;
import org.unidata.mdm.meta.util.ModelUtils;
/**
 * @author Mikhail Mikhailov
 * Entities parser type.
 */
public class EntitiesParser implements ModelParser<EntityInfoHolder> {

    /**
     * Constructor.
     */
    public EntitiesParser() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, EntityInfoHolder> parse(Model model){

        final Map<String, EntityInfoHolder> entities = new ConcurrentHashMap<>();
        for (EntityDef e : model.getEntities()) {

            Map<String, AttributeModelElement> attrs = ModelUtils.createAttributesMap(e, model.getNestedEntities());
            Map<String, Map<String, Integer>> bvtMap = ModelUtils.createBvtMap(e, model.getSourceSystems(), attrs);
            EntityInfoHolder ew = new EntityInfoHolder(e, e.getName(), attrs, bvtMap);
            // TODO: Commented out in scope of UN-11834. Reenable ASAP.
            // DQUtils.removeSystemRules(ew, e.getDataQualities());
            // DQUtils.addSystemRules(ew, e.getDataQualities());
            entities.put(e.getName(), ew);
        }

        return entities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<EntityInfoHolder> getValueType() {
        return EntityInfoHolder.class;
    }

}

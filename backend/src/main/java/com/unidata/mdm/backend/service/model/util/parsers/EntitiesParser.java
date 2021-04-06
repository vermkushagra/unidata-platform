/**
 *
 */
package com.unidata.mdm.backend.service.model.util.parsers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.Model;
import static com.unidata.mdm.backend.service.cleanse.DQUtils.removeSystemRules;
import static com.unidata.mdm.backend.service.cleanse.DQUtils.addSystemRules;
/**
 * @author Mikhail Mikhailov
 * Entities parser type.
 */
public class EntitiesParser implements ModelParser<EntityWrapper> {

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
    public Map<String, EntityWrapper> parse(Model model){

        final Map<String, EntityWrapper> entities = new ConcurrentHashMap<>();
        for (EntityDef e : model.getEntities()) {

            Map<String, AttributeInfoHolder> attrs = ModelUtils.createAttributesMap(e, model.getNestedEntities());
            Map<String, Map<String, Integer>> bvtMap = ModelUtils.createBvtMap(e, model.getSourceSystems(), attrs);
            EntityWrapper ew = new EntityWrapper(e, e.getName(), attrs, bvtMap);
            removeSystemRules(ew, e.getDataQualities());
            addSystemRules(ew, e.getDataQualities());
            entities.put(e.getName(), ew);
        }

        return entities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<EntityWrapper> getValueType() {
        return EntityWrapper.class;
    }

}

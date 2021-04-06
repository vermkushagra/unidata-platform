/**
 *
 */
package com.unidata.mdm.backend.service.model.util.parsers;

import static com.unidata.mdm.backend.service.cleanse.DQUtils.addSystemRules;
import static com.unidata.mdm.backend.service.cleanse.DQUtils.removeSystemRules;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.Model;

/**
 * @author Mikhail Mikhailov
 * Meta model parser for lookup entity type.
 */
public class LookupEntitiesParser implements ModelParser<LookupEntityWrapper> {

    /**
     * Constructor.
     */
    public LookupEntitiesParser() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, LookupEntityWrapper> parse(Model model){

        final Map<String, LookupEntityWrapper> lookupEntities = new ConcurrentHashMap<>();
        for (LookupEntityDef le : model.getLookupEntities()) {

            Map<String, AttributeInfoHolder> attrs = ModelUtils.createAttributesMap(le, model.getNestedEntities());
            Map<String, Map<String, Integer>> bvtMap  = ModelUtils.createBvtMap(le, model.getSourceSystems(), attrs);
            LookupEntityWrapper lw =  new LookupEntityWrapper(le, le.getName(), attrs, bvtMap);
            removeSystemRules(lw, le.getDataQualities());
            addSystemRules(lw, le.getDataQualities());
            lookupEntities.put(le.getName(), lw);
        }

        return lookupEntities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<LookupEntityWrapper> getValueType() {
        return LookupEntityWrapper.class;
    }

}

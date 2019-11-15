/**
 *
 */
package org.unidata.mdm.meta.type.parse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.meta.LookupEntityDef;
import org.unidata.mdm.meta.Model;
import org.unidata.mdm.meta.type.info.impl.LookupInfoHolder;
import org.unidata.mdm.meta.util.ModelUtils;

/**
 * @author Mikhail Mikhailov
 * Meta model parser for lookup entity type.
 */
public class LookupEntitiesParser implements ModelParser<LookupInfoHolder> {

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
    public Map<String, LookupInfoHolder> parse(Model model){

        final Map<String, LookupInfoHolder> lookupEntities = new ConcurrentHashMap<>();
        for (LookupEntityDef le : model.getLookupEntities()) {

            Map<String, AttributeModelElement> attrs = ModelUtils.createAttributesMap(le, model.getNestedEntities());
            Map<String, Map<String, Integer>> bvtMap  = ModelUtils.createBvtMap(le, model.getSourceSystems(), attrs);
            LookupInfoHolder lw = new LookupInfoHolder(le, le.getName(), attrs, bvtMap);
            // TODO: Commented out in scope of UN-11834. Reenable ASAP.
            // removeSystemRules(lw, le.getDataQualities());
            // addSystemRules(lw, le.getDataQualities());
            lookupEntities.put(le.getName(), lw);
        }

        return lookupEntities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<LookupInfoHolder> getValueType() {
        return LookupInfoHolder.class;
    }

}

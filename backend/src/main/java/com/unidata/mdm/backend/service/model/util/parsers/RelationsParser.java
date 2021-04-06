/**
 *
 */
package com.unidata.mdm.backend.service.model.util.parsers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.model.util.wrappers.RelationWrapper;
import com.unidata.mdm.meta.Model;
import com.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov
 * Relations parser.
 */
public class RelationsParser implements ModelParser<RelationWrapper> {

    /**
     * Constructor.
     */
    public RelationsParser() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, RelationWrapper> parse(Model model){
        final Map<String, RelationWrapper> relations = new ConcurrentHashMap<>();
        List<RelationDef> defs = model.getRelations();
        for (int i = 0; defs != null && i < defs.size(); i++) {
            RelationDef def = defs.get(i);
            if(def.getName()==null){
            	continue;
            }
            Map<String, AttributeInfoHolder> attrs = ModelUtils.createAttributesMap(def, model.getNestedEntities());
            relations.put(def.getName(), new RelationWrapper(def, def.getName(), attrs));
        }
        return relations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<RelationWrapper> getValueType() {
        return RelationWrapper.class;
    }

}

package org.unidata.mdm.meta.type.parse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.unidata.mdm.meta.Model;
import org.unidata.mdm.meta.SourceSystemDef;
import org.unidata.mdm.meta.type.info.impl.SourceSystemInfoHolder;

/**
 * @author Mikhail Mikhailov
 *
 */
public class SourceSystemsParser implements ModelParser<SourceSystemInfoHolder> {

    /**
     * Constructor.
     */
    public SourceSystemsParser() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, SourceSystemInfoHolder> parse(Model model){

        final Map<String, SourceSystemInfoHolder> sourceSystems = new ConcurrentHashMap<>();
        List<SourceSystemDef> defs = model.getSourceSystems();
        for (int i = 0; defs != null && i < defs.size(); i++) {
            String systemName = defs.get(i).getName();
            sourceSystems.put(systemName, new SourceSystemInfoHolder(defs.get(i)));
        }

        return sourceSystems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<SourceSystemInfoHolder> getValueType() {
        return SourceSystemInfoHolder.class;
    }

}

/**
 *
 */
package com.unidata.mdm.backend.service.model.util.parsers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.unidata.mdm.backend.service.model.util.wrappers.SourceSystemWrapper;
import com.unidata.mdm.meta.Model;
import com.unidata.mdm.meta.SourceSystemDef;

/**
 * @author Mikhail Mikhailov
 *
 */
public class SourceSystemsParser implements ModelParser<SourceSystemWrapper> {

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
    public Map<String, SourceSystemWrapper> parse(Model model){

        final Map<String, SourceSystemWrapper> sourceSystems = new ConcurrentHashMap<>();
        List<SourceSystemDef> defs = model.getSourceSystems();
        for (int i = 0; defs != null && i < defs.size(); i++) {
            String systemName = defs.get(i).getName();
            sourceSystems.put(systemName, new SourceSystemWrapper(defs.get(i)));
        }

        return sourceSystems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<SourceSystemWrapper> getValueType() {
        return SourceSystemWrapper.class;
    }

}

/**
 *
 */
package com.unidata.mdm.backend.service.model.util.parsers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.unidata.mdm.backend.service.model.util.wrappers.EnumerationWrapper;
import com.unidata.mdm.meta.EnumerationDataType;
import com.unidata.mdm.meta.Model;

/**
 * @author Mikhail Mikhailov
 * Enumerations parser.
 */
public class EnumerationsParser implements ModelParser<EnumerationWrapper> {

    /**
     * Constructor.
     */
    public EnumerationsParser() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, EnumerationWrapper> parse(Model model){
        final Map<String, EnumerationWrapper> enumeratios = new ConcurrentHashMap<>();
        List<EnumerationDataType> defs = model.getEnumerations();
        for (int i = 0; defs != null && i < defs.size(); i++) {
            enumeratios.put(defs.get(i).getName(), new EnumerationWrapper(defs.get(i)));
        }
        return enumeratios;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<EnumerationWrapper> getValueType() {
        return EnumerationWrapper.class;
    }

}

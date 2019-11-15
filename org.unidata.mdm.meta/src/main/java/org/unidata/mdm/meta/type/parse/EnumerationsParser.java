/**
 *
 */
package org.unidata.mdm.meta.type.parse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.unidata.mdm.meta.EnumerationDataType;
import org.unidata.mdm.meta.Model;
import org.unidata.mdm.meta.type.info.impl.EnumerationInfoHolder;

/**
 * @author Mikhail Mikhailov
 * Enumerations parser.
 */
public class EnumerationsParser implements ModelParser<EnumerationInfoHolder> {

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
    public Map<String, EnumerationInfoHolder> parse(Model model){
        final Map<String, EnumerationInfoHolder> enumeratios = new ConcurrentHashMap<>();
        List<EnumerationDataType> defs = model.getEnumerations();
        for (int i = 0; defs != null && i < defs.size(); i++) {
            enumeratios.put(defs.get(i).getName(), new EnumerationInfoHolder(defs.get(i)));
        }
        return enumeratios;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<EnumerationInfoHolder> getValueType() {
        return EnumerationInfoHolder.class;
    }

}

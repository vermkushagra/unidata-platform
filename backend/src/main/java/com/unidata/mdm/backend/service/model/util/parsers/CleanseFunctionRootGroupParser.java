/**
 *
 */
package com.unidata.mdm.backend.service.model.util.parsers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.unidata.mdm.backend.service.model.util.wrappers.CleanseFunctionRootGroupWrapper;
import com.unidata.mdm.meta.Model;

/**
 * @author Mikhail Mikhailov
 * Enumerations parser.
 */
public class CleanseFunctionRootGroupParser implements ModelParser<CleanseFunctionRootGroupWrapper> {

    /**
     * Constructor.
     */
    public CleanseFunctionRootGroupParser() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, CleanseFunctionRootGroupWrapper> parse(Model model){
        final Map<String, CleanseFunctionRootGroupWrapper> groupSingleton = new ConcurrentHashMap<>();
        if (model.getCleanseFunctions().getGroup() != null) {
            groupSingleton.put(model.getCleanseFunctions().getGroup().getGroupName(),
                    new CleanseFunctionRootGroupWrapper(model.getCleanseFunctions().getGroup()));
        }
        return groupSingleton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<CleanseFunctionRootGroupWrapper> getValueType() {
        return CleanseFunctionRootGroupWrapper.class;
    }

}

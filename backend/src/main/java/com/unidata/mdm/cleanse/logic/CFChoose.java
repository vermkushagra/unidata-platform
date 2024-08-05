package com.unidata.mdm.cleanse.logic;

import java.util.Map;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * The Class CFChoose.
 */
public class CFChoose extends BasicCleanseFunctionAbstract {

    /**
     * Instantiates a new CF choose.
     *
     */
    public CFChoose()  {
        super(CFChoose.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.cleanse.CleanseFunction#execute(java.util.Map)
     */
    @Override
    public void execute(Map<String, Object> input, Map<String, Object> result) throws CleanseFunctionExecutionException {
        result.put(getDefinition().getOutputPorts().get(0).getName(),
                new BooleanSimpleAttributeImpl(getDefinition().getOutputPorts().get(0).getName(), false));
    }

}
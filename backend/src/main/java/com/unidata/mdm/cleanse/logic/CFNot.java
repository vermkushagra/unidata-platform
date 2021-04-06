package com.unidata.mdm.cleanse.logic;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import java.util.Map;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Cleanse function performs NOT.
 */
public class CFNot extends BasicCleanseFunctionAbstract {

    /**
     * Instantiates a new CF not.
     */
    public CFNot() {
        super(CFNot.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.cleanse.CleanseFunction#execute(java.util.Map)
     */
    @Override
    public void execute(Map<String, Object> input, Map<String, Object> result) throws CleanseFunctionExecutionException {
        Boolean input1 = (Boolean) super.getValueByPort(INPUT1, input);
        Boolean resultBoolean = !input1;

        result.put(OUTPUT1, new BooleanSimpleAttributeImpl(OUTPUT1, resultBoolean));
    }

}

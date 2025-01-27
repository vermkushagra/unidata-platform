package com.unidata.mdm.cleanse.math;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT2;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import java.util.Map;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.NumberSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Cleanse function performs division operation.
 * @author ilya.bykov
 */
public class CFDivide extends BasicCleanseFunctionAbstract {

    /**
     * Instantiates a new CF divide.
     */
    public CFDivide() {
        super(CFDivide.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.cleanse.CleanseFunction#execute(java.util.Map)
     */
    @Override
    public void execute(Map<String, Object> input, Map<String, Object> result) throws CleanseFunctionExecutionException {

        Number input1 = (Number) super.getValueByPort(INPUT1, input);
        Number input2 = (Number) super.getValueByPort(INPUT2, input);
        if(input2.doubleValue()==0){
        	throw new CleanseFunctionExecutionException(getDefinition().getFunctionName(),
					"Division by zero.");
        }
        Number resultNumber = input1.doubleValue() / input2.doubleValue();
        result.put(OUTPUT1, new NumberSimpleAttributeImpl(OUTPUT1, resultNumber.doubleValue()));
    }

}

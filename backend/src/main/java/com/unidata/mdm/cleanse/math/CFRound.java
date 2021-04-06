package com.unidata.mdm.cleanse.math;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT2;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import java.math.BigDecimal;
import java.util.Map;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.NumberSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Cleanse function execute round operation.
 * @author ilya.bykov
 */
public class CFRound extends BasicCleanseFunctionAbstract {

    /**
     * Instantiates a new CF round.
     */
    public CFRound(){
        super(CFRound.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.cleanse.CleanseFunction#execute(java.util.Map)
     */
    @Override
    public void execute(Map<String, Object> input, Map<String, Object> result) throws CleanseFunctionExecutionException{

        Number input1 = (Number) super.getValueByPort(INPUT1, input);
        Long input2 = (Long) super.getValueByPort(INPUT2, input);
        Number resultNumber = new BigDecimal(
                String.valueOf(input1.doubleValue())).setScale(input2.intValue(),
                BigDecimal.ROUND_HALF_UP);
        result.put(OUTPUT1, new NumberSimpleAttributeImpl(OUTPUT1, resultNumber.doubleValue()));
    }
}
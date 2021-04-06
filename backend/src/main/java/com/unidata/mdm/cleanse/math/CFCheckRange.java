package com.unidata.mdm.cleanse.math;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT2;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT3;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import java.util.Map;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Cleanse function determine is given number in the provided range or not(for integer values).
 * @author ilya.bykov
 */
public class CFCheckRange  extends BasicCleanseFunctionAbstract {

    /**
     * Instantiates a new CF check value.
     */
    public CFCheckRange() {
        super(CFCheckRange.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.cleanse.CleanseFunction#execute(java.util.Map)
     */
    @Override
    public void execute(Map<String, Object> input, Map<String, Object> result) throws CleanseFunctionExecutionException {

        Long valueToCheck = (Long) super.getValueByPort(INPUT1, input);
        Long minValue = (Long) super.getValueByPort(INPUT2, input);
        minValue = minValue == null ? Integer.MIN_VALUE : minValue;
        Long maxValue = (Long) super.getValueByPort(INPUT3, input);
        maxValue = maxValue == null ? Integer.MAX_VALUE : maxValue;
        boolean resultTmp = false;
        if (valueToCheck != null) {
            resultTmp = (valueToCheck >= minValue) && (valueToCheck <= maxValue);
        }

        result.put(OUTPUT1, new BooleanSimpleAttributeImpl(OUTPUT1, resultTmp));
    }

}

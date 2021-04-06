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
 * Cleanse function determine is given number in the provided range or not(for number values).
 * @author ilya.bykov
 */
public class CFCheckRangeNumber extends BasicCleanseFunctionAbstract {

    /**
     * Instantiates a new CF check value.
     *
     */
    public CFCheckRangeNumber() {
        super(CFCheckRangeNumber.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.cleanse.CleanseFunction#execute(java.util.Map)
     */
    @Override
    public void execute(Map<String, Object> input, Map<String, Object> result) throws CleanseFunctionExecutionException {

        Double valueToCheck = (Double) super.getValueByPort(INPUT1, input);
        Double minValue = (Double) super.getValueByPort(INPUT2, input);
        minValue = minValue == null ? Double.MIN_VALUE : minValue;
        Double maxValue = (Double) super.getValueByPort(INPUT3, input);
        maxValue = maxValue == null ? Double.MAX_VALUE : maxValue;
        boolean resultTmp = false;
        if (valueToCheck != null) {
            resultTmp = (valueToCheck >= minValue) && (valueToCheck <= maxValue);
        }

        result.put(OUTPUT1, new BooleanSimpleAttributeImpl(OUTPUT1, resultTmp));
    }

}

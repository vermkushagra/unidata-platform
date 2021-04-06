package com.unidata.mdm.cleanse.math;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT2;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.NumberSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Cleanse function determine minimum.
 * @author ilya.bykov
 */
public class CFMin extends BasicCleanseFunctionAbstract {

    /**
     * Instantiates a new CF min.
     */
    public CFMin()  {
        super(CFMin.class);
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
        Number resultNumber = NumberUtils.min(input1.doubleValue(), input2.doubleValue());
        result.put(OUTPUT1, new NumberSimpleAttributeImpl(OUTPUT1, resultNumber.doubleValue()));
    }
}

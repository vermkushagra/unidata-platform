package com.unidata.mdm.cleanse.logic;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT2;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Cleanse function performs AND operation.
 */
public class CFAnd extends BasicCleanseFunctionAbstract {

    /**
     * Instantiates a new CF and.
     */
    public CFAnd() {
        super(CFAnd.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.cleanse.CleanseFunction#execute(java.util.Map)
     */
    @Override
    public void execute(Map<String, Object> input, Map<String, Object> result) throws CleanseFunctionExecutionException {
        Boolean input1 = (Boolean) super.getValueByPort(INPUT1, input);
        Boolean input2 = (Boolean) super.getValueByPort(INPUT2, input);
        Boolean resultBoolean = BooleanUtils.and((Boolean[]) Arrays.asList(input1, input2).toArray());

        result.put(OUTPUT1, new BooleanSimpleAttributeImpl(OUTPUT1, resultBoolean));
    }

}

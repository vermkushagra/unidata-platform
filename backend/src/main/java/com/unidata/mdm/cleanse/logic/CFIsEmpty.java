package com.unidata.mdm.cleanse.logic;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Cleanse function check is provided string empty.
 * @author ilya.bykov
 */
public class CFIsEmpty extends BasicCleanseFunctionAbstract {

    /**
     * Instantiates a new CF is empty.
     *
     */
    public CFIsEmpty() {
        super(CFIsEmpty.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.cleanse.CleanseFunction#execute(java.util.Map)
     */
    @Override
    public void execute(Map<String, Object> input, Map<String, Object> result) throws CleanseFunctionExecutionException {
        result.put(OUTPUT1, new BooleanSimpleAttributeImpl(OUTPUT1, StringUtils.isEmpty((String) super.getValueByPort(INPUT1, input))));
    }

}
package com.unidata.mdm.cleanse.misc;

import java.util.Map;

import com.unidata.mdm.backend.common.cleanse.CleanseFunction;
import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.meta.CleanseFunctionExtendedDef;
/**
 * TODO: implementation
 *
 */
public class CFCheckPhoneNumber implements CleanseFunction {

    @Override
    public CleanseFunctionExtendedDef getDefinition() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> input) throws CleanseFunctionExecutionException {

        System.out.println("foo");
        return null;
    }

}

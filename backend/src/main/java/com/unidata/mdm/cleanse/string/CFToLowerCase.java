package com.unidata.mdm.cleanse.string;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * String to lower case.
 *
 * @author ilya.bykov
 */
public class CFToLowerCase extends BasicCleanseFunctionAbstract {

	/**
	 * Instantiates a new CF to lower case.
	 *
	 */
	public CFToLowerCase() {
		super(CFToLowerCase.class);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.unidata.mdm.cleanse.CleanseFunction#execute(java.util.Map)
	 */
	@Override
	public void execute(Map<String, Object> input, Map<String, Object> result)
			throws CleanseFunctionExecutionException {
		result.put(OUTPUT1, new StringSimpleAttributeImpl(OUTPUT1,
		        StringUtils.lowerCase((String) super.getValueByPort(INPUT1, input))));
	}

}

package com.unidata.mdm.cleanse.string;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT2;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT3;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT4;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import java.util.Map;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Concatenate provided strings.
 *
 * @author ilya.bykov
 */
public class CFConcatenate extends BasicCleanseFunctionAbstract {

	/**
	 * Instantiates a new CF concatenate.
	 */
	public CFConcatenate() {
		super(CFConcatenate.class);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.unidata.mdm.cleanse.CleanseFunction#execute(java.util.Map)
	 */
	@Override
	public void execute(Map<String, Object> input, Map<String, Object> result)
			throws CleanseFunctionExecutionException {

	    StringBuilder stringBuilder = new StringBuilder();
		// concatenate all values found in input ports
		stringBuilder.append(super.getValueByPort(INPUT1, input));
		stringBuilder.append(super.getValueByPort(INPUT2, input));
		stringBuilder.append(super.getValueByPort(INPUT3, input)==null?"":super.getValueByPort(INPUT3, input));
		stringBuilder.append(super.getValueByPort(INPUT4, input)==null?"":super.getValueByPort(INPUT4, input));

		result.put(OUTPUT1, new StringSimpleAttributeImpl(OUTPUT1, stringBuilder.toString()));
	}

}

package com.unidata.mdm.cleanse.string;

import static com.unidata.mdm.cleanse.common.CleanseConstants.END_INDEX;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.START_INDEX;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Extract substring from string.
 *
 * @author ilya.bykov
 */
public class CFSubstring extends BasicCleanseFunctionAbstract {

	/**
	 * Instantiates a new CF substring.
	 */
	public CFSubstring() {
		super(CFSubstring.class);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.unidata.mdm.cleanse.CleanseFunction#execute(java.util.Map)
	 */
	@Override
	public void execute(Map<String, Object> input, Map<String, Object> result)
			throws CleanseFunctionExecutionException {
		String inputString = (String) super.getValueByPort(INPUT1, input);
		int start = ((Long) super.getValueByPort(START_INDEX, input)).intValue();
		Long longEnd = ((Long) super.getValueByPort(END_INDEX, input));
		int end = longEnd == null || longEnd.intValue() == 0 ? inputString.length()
				: longEnd.intValue() + 1;

		result.put(OUTPUT1, new StringSimpleAttributeImpl(OUTPUT1, StringUtils.substring(inputString, start, end)));
	}

}

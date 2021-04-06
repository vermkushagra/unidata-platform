package com.unidata.mdm.cleanse.string;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.PAD_RIGHT;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Pad string to the right.
 *
 * @author ilya.bykov
 */
public class CFPadRight extends BasicCleanseFunctionAbstract {

	/**
	 * Instantiates a new CF pad right.
	 */
	public CFPadRight() {
		super(CFPadRight.class);
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
		int rightPad = ((Long) super.getValueByPort(PAD_RIGHT, input)).intValue();
		result.put(OUTPUT1, new StringSimpleAttributeImpl(OUTPUT1, StringUtils.rightPad(inputString, rightPad)));
	}

}

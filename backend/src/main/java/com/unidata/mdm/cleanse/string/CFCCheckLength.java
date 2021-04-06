package com.unidata.mdm.cleanse.string;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT2;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT3;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import java.util.Map;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Checks string length.
 * @author ilya.bykov
 */
public class CFCCheckLength extends BasicCleanseFunctionAbstract {

	/**
	 * Instantiates a new CF check value.
	 */
	public CFCCheckLength() {
		super(CFCCheckLength.class);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.unidata.mdm.cleanse.CleanseFunction#execute(java.util.Map)
	 */
	@Override
	public void execute(Map<String, Object> input, Map<String, Object> result)
			throws CleanseFunctionExecutionException {

		String valueToCheck = (String) super.getValueByPort(INPUT1, input);
		Long minLength = (Long) super.getValueByPort(INPUT2, input);
		minLength = minLength == null ? 0 : minLength;
		Long maxLength = (Long)  super.getValueByPort(INPUT3, input);
		maxLength = (maxLength == null||maxLength==0) ? Integer.MAX_VALUE : maxLength;
		boolean resultTmp = false;
		if (valueToCheck != null) {
			resultTmp = (valueToCheck.length() >= minLength) && (valueToCheck.length() <= maxLength);
		}

		result.put(OUTPUT1, new BooleanSimpleAttributeImpl(OUTPUT1).withValue(resultTmp));
	}

}

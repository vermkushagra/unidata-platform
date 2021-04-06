package com.unidata.mdm.cleanse.misc;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT2;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT3;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Checks that input string matches provided regular expression.
 *
 * @author ilya.bykov
 *
 */
public class CFCheckValue extends BasicCleanseFunctionAbstract {

	/**
	 * Instantiates a new CF check value.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public CFCheckValue() {
		super(CFCheckValue.class);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.unidata.mdm.cleanse.CleanseFunction#execute(java.util.Map)
	 */
	@Override
	public void execute(Map<String, Object> input, Map<String, Object> result)
			throws CleanseFunctionExecutionException {
		String regexp = (String) super.getValueByPort(INPUT1, input);
		String in1 = (String) super.getValueByPort(INPUT2, input);
		String toCheck = "";
		if (StringUtils.isEmpty(in1)) {
			toCheck = ((Long) super.getValueByPort(INPUT3, input)) == null ? null
					: "" + super.getValueByPort(INPUT3, input);
		} else {
			toCheck = in1;
		}

		result.put(OUTPUT1, new BooleanSimpleAttributeImpl(OUTPUT1, toCheck == null ? false : toCheck.matches(regexp)));
	}

}

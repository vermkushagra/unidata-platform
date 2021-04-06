package com.unidata.mdm.cleanse.string;

import static com.unidata.mdm.cleanse.common.CleanseConstants.DEFAULT_VALUE;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * In case if value not provided(null) returns default value.
 *
 * @author ilya.bykov
 */
public class CFDefaultValue extends BasicCleanseFunctionAbstract {

	/**
	 * Instantiates a new CF default value.
	 */
	public CFDefaultValue() {
		super(CFDefaultValue.class);
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
		String defaultValue = (String) super.getValueByPort(DEFAULT_VALUE, input);

		result.put(OUTPUT1, new StringSimpleAttributeImpl(OUTPUT1, StringUtils.isEmpty(inputString) ? defaultValue : inputString));
	}

}

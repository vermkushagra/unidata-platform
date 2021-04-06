package com.unidata.mdm.cleanse.convert;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Cleanse function parse string to boolean.
 *
 * @author ilya.bykov
 */
public class CFParseBoolean extends BasicCleanseFunctionAbstract {

	/**
	 * Instantiates a new CF parse boolean.
	 */
	public CFParseBoolean() {
		super(CFParseBoolean.class);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.unidata.mdm.cleanse.CleanseFunction#execute(java.util.Map)
	 */
	@Override
	public void execute(Map<String, Object> input, Map<String, Object> result)
			throws CleanseFunctionExecutionException {
		String value = (String) super.getValueByPort(INPUT1, input);
		boolean isBlank = StringUtils.isBlank(value);
		result.put(OUTPUT1, new BooleanSimpleAttributeImpl(OUTPUT1,isBlank ? null : BooleanUtils.toBooleanObject(value)));
	}
}
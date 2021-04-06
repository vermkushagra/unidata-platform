package com.unidata.mdm.cleanse.convert;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.IntegerSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Cleanse function parse string to integer.
 * 
 * @author ilya.bykov
 */
public class CFParseInteger extends BasicCleanseFunctionAbstract {

	/**
	 * Instantiates a new CF parse integer.
	 */
	public CFParseInteger() {
		super(CFParseInteger.class);
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
		Long number = null;
		if (!isBlank) {
			try {
				number = Long.parseLong(value);
			} catch (NumberFormatException ex) {
				throw new CleanseFunctionExecutionException(getDefinition().getFunctionName(),
						"Unable to parse string to long");
			}
		}
		result.put(OUTPUT1, new IntegerSimpleAttributeImpl(OUTPUT1, number));
	}

}

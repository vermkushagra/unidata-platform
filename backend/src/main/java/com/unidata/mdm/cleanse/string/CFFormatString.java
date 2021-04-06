package com.unidata.mdm.cleanse.string;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.PATTERN;

import java.util.Formatter;
import java.util.Map;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Format string according to provided pattern.
 *
 * @author ilya.bykov
 */
public class CFFormatString extends BasicCleanseFunctionAbstract {

	/**
	 * Instantiates a new CF format string.
	 */
	public CFFormatString(){
		super(CFFormatString.class);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.unidata.mdm.cleanse.common.SimpleCleanseFunctionAbstract#execute
	 * (java.util.Map, java.util.Map)
	 */
	@Override
	public void execute(Map<String, Object> input, Map<String, Object> result)
			throws CleanseFunctionExecutionException {

		StringBuilder sb = new StringBuilder();
		try (Formatter formatter = new Formatter(sb)) {
		    formatter.format(
		            (String) super.getValueByPort(PATTERN, input),
		            (String) super.getValueByPort(INPUT1, input));
		}

		result.put(OUTPUT1, new StringSimpleAttributeImpl(OUTPUT1, sb.toString()));
	}

}

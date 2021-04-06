package com.unidata.mdm.cleanse.string;

import static com.unidata.mdm.cleanse.common.CleanseConstants.GROUP;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.PATTERN;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Execute regular expression on the input string.
 *
 * @author ilya.bykov
 */
public class CFRegExp extends BasicCleanseFunctionAbstract {

	/**
	 * Instantiates a new CF reg exp.
	 *
	 */
	public CFRegExp() {
		super(CFRegExp.class);
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
		inputString = inputString == null ? "" : inputString;
		String patternString = (String) super.getValueByPort(PATTERN, input);
		int group = ((Long) super.getValueByPort(GROUP, input)).intValue();
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(inputString);
		result.put(OUTPUT1, new StringSimpleAttributeImpl(OUTPUT1, matcher.find() ? matcher.group(group) : null));
	}

}

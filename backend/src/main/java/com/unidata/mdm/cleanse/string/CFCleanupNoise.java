package com.unidata.mdm.cleanse.string;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;
/**
 * Cleanup 'noise' from the input string.
 * @author ilya.bykov
 *
 */
public class CFCleanupNoise extends BasicCleanseFunctionAbstract {
	/**
	 *
	 * @param clazz
	 */
	public CFCleanupNoise() {
		super(CFCleanupNoise.class);
	}
	/**
	 *
	 */
	@Override
	public void execute(Map<String, Object> input, Map<String, Object> result)
			throws CleanseFunctionExecutionException {
		result.put(OUTPUT1, new StringSimpleAttributeImpl(OUTPUT1,
		        cleanup((String) super.getValueByPort(INPUT1, input))));
	}
	/**
	 * Cleanup string.</br>
	 * Method makes the following:
	 * <ul>
	 * 	<li>Trim whitespaces.</li>
	 * 	<li>Removes all repeating whitespaces.</li>
	 * 	<li>Capitalize first letter of every sentence.</li>
	 * 	<li>Converts to lower case all other characters if they are:
	 * 		<ul>
	 * 			<li>Not surrounded by quotes</li>
	 * 			<li>Not first letter in a sentence</li>
	 * 		</ul
	 * 	</li>
	 * </ul>
	 * @param input input string.
	 * @return output string.
	 */
	private static final String cleanup(String input) {
		if (input == null) {
			return null;
		}
		input = StringUtils.normalizeSpace(StringUtils.trim(input));
		char[] chars = input.toCharArray();
		boolean isQuotes = false;
		boolean isNew = false;
		for (int i = 0; i < chars.length; i++) {
			if (i != 0 && Character.isUpperCase(chars[i]) && !isQuotes) {
				chars[i] = Character.toLowerCase(chars[i]);
			}
			if (chars[i] == '.' || i == 0) {
				isNew = true;
			}
			if (isNew && chars[i] != ' ' && chars[i] != '.') {
				chars[i] = Character.toTitleCase(chars[i]);
				isNew = false;
			}
			if (chars[i] == '\"'
					|| chars[i] == '\''
					|| chars[i] == '„' || chars[i] == '“'
					|| chars[i] == '“' || chars[i] == '”'
					|| chars[i] == '«' || chars[i] == '»') {
				isQuotes = !isQuotes;
			}
		}
		return String.valueOf(chars);
	}
}

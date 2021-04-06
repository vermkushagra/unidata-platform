package com.unidata.mdm.cleanse.string;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * The Class RegexpUtils.
 */
public class RegexpUtils {

	/** The Constant NUMBER. */
	private static final Pattern NUMBER = Pattern.compile("\\([^()]*\\)|(9)",Pattern.UNICODE_CHARACTER_CLASS);

	/** The Constant CAPITAL. */
	private static final Pattern CAPITAL = Pattern.compile("\\([^()]*\\)|(L)",Pattern.UNICODE_CHARACTER_CLASS);

	/** The Constant LOWER. */
	private static final Pattern LOWER = Pattern.compile("\\([^()]*\\)|(l)",Pattern.UNICODE_CHARACTER_CLASS);

	/** The Constant ALPHANUMERIC. */
	private static final Pattern ALPHANUMERIC = Pattern.compile("\\([^()]*\\)|(A)",Pattern.UNICODE_CHARACTER_CLASS);

	/** The Constant START_INNER. */
	private static final Pattern START_INNER = Pattern.compile("\\([^()]*\\)|(X\\[)|(X#\\[)",Pattern.UNICODE_CHARACTER_CLASS);

	/** The Constant END_INNER. */
	private static final Pattern END_INNER = Pattern.compile("\\([^()]*\\)|(\\]X)|(\\]#X)",Pattern.UNICODE_CHARACTER_CLASS);

	/** The Constant NUMBER_REG. */
	private static final String NUMBER_REG = "([0-9])";

	/** The Constant CAPITAL_REG. */
	private static final String CAPITAL_REG = "(\\p{javaUpperCase})";

	/** The Constant LOWER_REG. */
	private static final String LOWER_REG = "(\\p{javaLowerCase})";

	/** The Constant ALPHANUMERIC_REG. */
	private static final String ALPHANUMERIC_REG = "([\\pL\\pN])";

	/** The Constant START_INNER_REG. */
	private static final String START_INNER_REG = "([";

	/** The Constant END_INNER_REG. */
	private static final String END_INNER_REG = "])";

	/**
	 * Validate.
	 *
	 * @param regexp
	 *            the regexp
	 * @param toCheck
	 *            the to check
	 * @return true, if successful
	 */
	public static final boolean validate(String regexp, String toCheck) {
		return Pattern.compile(regexp).matcher(toCheck).matches();
	}
	/**
	 * Validate.
	 *
	 * @param regexp
	 *            the regexp
	 * @param toCheck
	 *            the to check
	 * @return true, if successful
	 */
	public static final boolean validate(Pattern regexp, String toCheck) {
		return regexp.matcher(toCheck).matches();
	}
	/**
	 * Convert mask to regex.
	 *
	 * @param mask
	 *            the mask
	 * @return the string
	 */
	public static final Pattern convertMaskToRegexPattern(String mask) {
		if(mask ==null){
			return null;
		}
		String string = convertMaskToRegexString(mask);
		return Pattern.compile(string);
	}
	/**
	 * Convert mask to regex.
	 *
	 * @param mask
	 *            the mask
	 * @return the string
	 */
	public static final String convertMaskToRegexString(String mask) {
		if (StringUtils.isEmpty(mask)) {
			return null;
		}
		mask = mask.replace("(", "#[");
		mask = mask.replace(")", "]#");
		mask = replace(START_INNER, mask, START_INNER_REG);
		mask = replace(END_INNER, mask, END_INNER_REG);
		mask = mask.replace("X#[", "(");
		mask = mask.replace("]#X", ")");
		mask = replace(NUMBER, mask, NUMBER_REG);
		mask = replace(CAPITAL, mask, CAPITAL_REG);
		mask = replace(LOWER, mask, LOWER_REG);
		mask = replace(ALPHANUMERIC, mask, ALPHANUMERIC_REG);
		mask = mask.replace("(p{javaUpperCase})", CAPITAL_REG);
		mask = mask.replace("(p{javaLowerCase})", LOWER_REG);
		mask = mask.replace("[pLpN]", "[\\p{L}\\p{N}]");
		mask = mask.replace("+", "\\+");
		mask = mask.replace("#[", "\\(");
		mask = mask.replace("]#", "\\)");
		mask = mask.replaceAll("([\\\\]+)u", "\\\\u");
		mask = mask.replaceAll("([\\\\]+)d", "\\\\d");
		mask = mask.replaceAll("([\\\\]+)w", "\\\\w");
		mask = mask.replaceAll("([\\\\]+)s", "\\\\s");
		mask = mask.replaceAll("([\\\\]+)D", "\\\\D");
		mask = mask.replaceAll("([\\\\]+)W", "\\\\W");
		mask = mask.replaceAll("([\\\\]+)S", "\\\\S");
		mask = mask.replaceAll("([\\\\]+)n", "\\\\n");
		mask = "(" + mask + ")";
		return mask;
	}

	/**
	 * Replace.
	 *
	 * @param pattern
	 *            the pattern
	 * @param toReplace
	 *            the to replace
	 * @param replaceWith
	 *            the replace with
	 * @return the string
	 */
	public static String replace(Pattern pattern, String toReplace, String replaceWith) {
		Matcher m = pattern.matcher(toReplace);
		StringBuffer b = new StringBuffer();
		while (m.find()) {
			if (m.group(1) != null){
				m.appendReplacement(b, replaceWith);
			}else{
				m.appendReplacement(b, m.group(0));
			}
		}
		m.appendTail(b);
		String replaced = b.toString();
		return StringEscapeUtils.escapeJava(replaced);
	}

}

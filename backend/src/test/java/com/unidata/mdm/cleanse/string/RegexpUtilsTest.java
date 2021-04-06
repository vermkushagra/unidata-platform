package com.unidata.mdm.cleanse.string;

import static org.junit.Assert.*;

import java.util.regex.Pattern;

import org.junit.Test;

/**
 * The Class RegexpUtilsTest.
 */
public class RegexpUtilsTest {

	/**
	 * Test validate string string.
	 */
	@Test
	public void testValidateStringString() {
		assertEquals(true, RegexpUtils.validate("([0-9]{3,4})", "123"));
		assertEquals(false, RegexpUtils.validate("([0-9]{3,4})", "12345"));
	}

	/**
	 * Test validate pattern string.
	 */
	@Test
	public void testValidatePatternString() {
		Pattern pattern = Pattern.compile("([0-9]{3,4})");
		assertEquals(true, RegexpUtils.validate(pattern, "123"));
		assertEquals(false, RegexpUtils.validate(pattern, "12345"));
	}

	/**
	 * Test convert mask to regex pattern.
	 */
	@Test
	public void testConvertMaskToRegexPattern() {
		assertEquals("(([0-9])([0-9])([0-9]).(\\p{javaUpperCase})([0-8]))",
				RegexpUtils.convertMaskToRegexPattern("999.LX([0-8])X").toString());
		assertEquals(true, RegexpUtils.convertMaskToRegexPattern("999.LX([0-8])X").matcher("123.A8").matches());
		assertEquals(false, RegexpUtils.convertMaskToRegexPattern("999.LX([0-8])X").matcher("123.A9").matches());
	}

	/**
	 * Test convert mask to regex string.
	 */
	@Test
	public void testConvertMaskToRegexString() {
		assertEquals("(([0-9])([0-9])([0-9]).(\\p{javaUpperCase})([0-8]))",
				RegexpUtils.convertMaskToRegexString("999.LX([0-8])X"));
	}

	/**
	 * Test replace.
	 */
	@Test
	public void testReplace() {
		Pattern pattern = Pattern.compile("([0-9]{3,4})");
		assertEquals("Test 4567 to replace", RegexpUtils.replace(pattern, "Test 1234 to replace", "4567"));
	}
}

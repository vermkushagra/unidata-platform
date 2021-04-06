package com.unidata.mdm.cleanse.string;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * 
 * @author ilya.bykov
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ CFCCheckLengthTest.class, CFCheckMaskTest.class, CFCleanupNoiseTest.class,
		CFCompressWhitespacesTest.class, CFConcatenateTest.class, CFDefaultValueTest.class, CFFormatStringTest.class,
		CFPadLeftTest.class, CFPadRightTest.class, CFRegExpTest.class, CFSubstringTest.class, CFToLowerCaseTest.class,
		CFToTitleCaseTest.class, CFToUpperCaseTest.class, CFTrimTest.class, RegexpUtilsTest.class })
public class CFStringTests {

}

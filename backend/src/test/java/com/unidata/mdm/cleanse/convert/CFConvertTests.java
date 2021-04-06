package com.unidata.mdm.cleanse.convert;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
/**
 * 
 * @author ilya.bykov
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ CFParseBooleanTest.class, CFParseDateTest.class, CFParseIntegerTest.class, CFParseNumberTest.class })
public class CFConvertTests {

}

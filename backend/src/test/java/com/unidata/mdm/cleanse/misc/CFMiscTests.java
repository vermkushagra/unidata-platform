package com.unidata.mdm.cleanse.misc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
/**
 * 
 * @author ilya.bykov
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ CFCheckINNTest.class, CFCheckValueTest.class, CFInnerFetchTest.class, CFIsExistsTest.class })
public class CFMiscTests {

}

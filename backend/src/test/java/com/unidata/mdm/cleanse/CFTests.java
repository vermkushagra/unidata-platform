package com.unidata.mdm.cleanse;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.unidata.mdm.cleanse.convert.CFConvertTests;
import com.unidata.mdm.cleanse.logic.CFLogicTests;
import com.unidata.mdm.cleanse.math.CFMathTests;
import com.unidata.mdm.cleanse.misc.CFMiscTests;
import com.unidata.mdm.cleanse.string.CFStringTests;

/**
 * 
 * @author ilya.bykov
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ CFConvertTests.class, CFMathTests.class, CFLogicTests.class, CFMiscTests.class, CFStringTests.class })
public class CFTests {

}

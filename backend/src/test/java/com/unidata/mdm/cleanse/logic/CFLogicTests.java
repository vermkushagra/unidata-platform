package com.unidata.mdm.cleanse.logic;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
/**
 * 
 * @author ilya.bykov
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ CFAndTest.class,  CFIsEmptyTest.class, CFNotTest.class,
		CFOrTest.class, CFXorTest.class })
public class CFLogicTests {

}

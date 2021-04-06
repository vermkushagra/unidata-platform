package com.unidata.mdm.backend.service.cleanse;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.unidata.mdm.backend.service.cleanse.impl.CleanseFunctionServiceImplTest;
import com.unidata.mdm.cleanse.CFTests;
/**
 * 
 * @author ilya.bykov
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ CFTests.class, CleanseFunctionServiceImplTest.class, CFUtilsTest.class,  DataQualityServiceImplTest.class, DQUtilsTest.class})
public class DQTests {

}

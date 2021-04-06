package com.unidata.mdm.backend.service.cleanse;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.parsers.NestedEntitiesParser;
import com.unidata.mdm.backend.service.model.util.wrappers.NestedEntityWrapper;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.meta.DQRuleDef;

/**
 * The Class DQUtilsTest.
 * @author ilya.bykov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/test-dq-context.xml" })
@PrepareForTest({ CFAppContext.class, MessageUtils.class })
@Transactional
public class DQUtilsTest {
	
	/** The nested entities parser. */
	private NestedEntitiesParser nestedEntitiesParser = new NestedEntitiesParser();
	/** The metamodel service. */
	@Autowired
	private MetaModelServiceExt metamodelService;

	/**
	 * Test add system rules.
	 */
	@Test
	public void testAddSystemRules() {
		List<DQRuleDef> rules = new ArrayList<>();
		Map<String, NestedEntityWrapper> result = nestedEntitiesParser.parse(metamodelService.exportModel(null));
		DQUtils.addSystemRules(result.get("documents"), rules);
		assertEquals(2, rules.size());
		assertEquals("attr__docType__Check_Ref_Range", rules.get(0).getName());
		assertEquals(true, rules.get(0).isSpecial());
		assertEquals("attr__docType__Check_Ref", rules.get(1).getName());
		assertEquals(true, rules.get(1).isSpecial());
	}

	/**
	 * Test remove system rules.
	 */
	@Test
	public void testRemoveSystemRules() {
		List<DQRuleDef> rules = new ArrayList<>();
		Map<String, NestedEntityWrapper> result = nestedEntitiesParser.parse(metamodelService.exportModel(null));
		DQUtils.addSystemRules(result.get("documents"), rules);
		assertEquals(2, rules.size());
		assertEquals("attr__docType__Check_Ref_Range", rules.get(0).getName());
		assertEquals(true, rules.get(0).isSpecial());
		assertEquals("attr__docType__Check_Ref", rules.get(1).getName());
		DQUtils.removeSystemRules(result.get("documents"), rules);
		assertEquals(0, rules.size());
	}

}

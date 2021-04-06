package com.unidata.mdm.backend.service.cleanse;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.traverse.TopologicalOrderIterator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.common.cleanse.CleanseFunction;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.backend.service.cleanse.composite.CompositeFunctionMDAGRep;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.cleanse.string.CFConcatenate;
import com.unidata.mdm.meta.CleanseFunctionDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionLogic.Node;
import com.unidata.mdm.meta.CompositeCleanseFunctionLogic.NodeLink;
import com.unidata.mdm.meta.ListOfCleanseFunctions;
import com.unidata.mdm.meta.Model;


/**
 * The Class CFUtilsTest.
 * @author ilya.bykov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/test-dq-context.xml" })
@PrepareForTest({ CFAppContext.class, MessageUtils.class })
@Transactional
public class CFUtilsTest {
	
	/** The metamodel service. */
	@Autowired
	private MetaModelServiceExt metamodelService;

	/**
	 * Test topological iterator.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testTopologicalIterator() throws IOException {
		CompositeCleanseFunctionDef cleanseFunctionDef = null;
		Model model = JaxbUtils.createModelFromInputStream(ClassLoader.getSystemResourceAsStream("model/dq/model.xml"));
		ListOfCleanseFunctions cleanseFunctions = model.getCleanseFunctions();
		List<Serializable> list = cleanseFunctions.getGroup().getGroupOrCleanseFunctionOrCompositeCleanseFunction();
		for (Serializable el : list) {
			if (el instanceof CompositeCleanseFunctionDef) {
				cleanseFunctionDef = (CompositeCleanseFunctionDef) el;
			}
		}
		assertNotNull(cleanseFunctionDef);
		assertEquals("ПолноеИмя", cleanseFunctionDef.getFunctionName());
		CompositeFunctionMDAGRep graph = CFUtils.convertToGraph(cleanseFunctionDef);
		assertEquals(6, graph.vertexSet().size());
		assertEquals(9, graph.edgeSet().size());
		assertEquals(0, CFUtils.findCycles(graph).size());
		TopologicalOrderIterator<Node, NodeLink> iterator = CFUtils.topologicalIterator(graph);
		assertNotNull(iterator);
		while (iterator.hasNext()) {
			Node node = iterator.next();
			assertNotNull(node.getFunctionName());

		}
	}

	/**
	 * Test find cycles.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testFindCycles() throws IOException {
		CompositeCleanseFunctionDef cleanseFunctionDef = null;
		Model model = JaxbUtils.createModelFromInputStream(ClassLoader.getSystemResourceAsStream("model/dq/model.xml"));
		ListOfCleanseFunctions cleanseFunctions = model.getCleanseFunctions();
		List<Serializable> list = cleanseFunctions.getGroup().getGroupOrCleanseFunctionOrCompositeCleanseFunction();
		for (Serializable el : list) {
			if (el instanceof CompositeCleanseFunctionDef) {
				cleanseFunctionDef = (CompositeCleanseFunctionDef) el;
			}
		}
		assertNotNull(cleanseFunctionDef);
		assertEquals("ПолноеИмя", cleanseFunctionDef.getFunctionName());
		CompositeFunctionMDAGRep graph = CFUtils.convertToGraph(cleanseFunctionDef);
		assertEquals(6, graph.vertexSet().size());
		assertEquals(9, graph.edgeSet().size());
		assertEquals(0, CFUtils.findCycles(graph).size());

		graph.addEdge(cleanseFunctionDef.getLogic().getNodes().get(0), cleanseFunctionDef.getLogic().getNodes().get(1));
		graph.addEdge(cleanseFunctionDef.getLogic().getNodes().get(1), cleanseFunctionDef.getLogic().getNodes().get(0));
		assertEquals(6, CFUtils.findCycles(graph).size());
	}

	/**
	 * Test convert to graph.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testConvertToGraph() throws IOException {
		CompositeCleanseFunctionDef cleanseFunctionDef = null;
		Model model = JaxbUtils.createModelFromInputStream(ClassLoader.getSystemResourceAsStream("model/dq/model.xml"));
		ListOfCleanseFunctions cleanseFunctions = model.getCleanseFunctions();
		List<Serializable> list = cleanseFunctions.getGroup().getGroupOrCleanseFunctionOrCompositeCleanseFunction();
		for (Serializable el : list) {
			if (el instanceof CompositeCleanseFunctionDef) {
				cleanseFunctionDef = (CompositeCleanseFunctionDef) el;
			}
		}
		assertNotNull(cleanseFunctionDef);
		assertEquals("ПолноеИмя", cleanseFunctionDef.getFunctionName());
		CompositeFunctionMDAGRep graph = CFUtils.convertToGraph(cleanseFunctionDef);
		assertEquals(6, graph.vertexSet().size());
		assertEquals(9, graph.edgeSet().size());
	}

	/**
	 * Test get function.
	 */
	@Test
	public void testGetFunction() {
		CleanseFunctionDef cleanseFunctionDef = CFUtils.getFunction("", "Строковые.Соединить",
				metamodelService.getCleanseFunctionRootGroup());
		assertNotNull(cleanseFunctionDef);
		assertEquals("Соединить", cleanseFunctionDef.getFunctionName());
		assertEquals(CFConcatenate.class.getCanonicalName(), cleanseFunctionDef.getJavaClass());
	}

	/**
	 * Test create cleanse function.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testCreateCleanseFunction() throws Exception {
		CleanseFunctionDef cleanseFunctionDef = new CleanseFunctionDef().withFunctionName("Соединить")
				.withJavaClass(CFConcatenate.class.getCanonicalName());
		CleanseFunction cleanseFunction = CFUtils.createCleanseFunction(cleanseFunctionDef);
		assertNotNull(cleanseFunction);
		Map<String, Object> input = new HashMap<>();
		StringSimpleAttributeImpl port1 = new StringSimpleAttributeImpl("port1", "1");
		input.put("port1", port1);
		StringSimpleAttributeImpl port2 = new StringSimpleAttributeImpl("port2", "2");
		input.put("port2", port2);
		StringSimpleAttributeImpl port3 = new StringSimpleAttributeImpl("port3", "3");
		input.put("port3", port3);
		StringSimpleAttributeImpl port4 = new StringSimpleAttributeImpl("port4", "4");
		input.put("port4", port4);
		Map<String, Object> output = cleanseFunction.execute(input);
		assertNotNull(output);
		assertEquals(1, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof StringSimpleAttributeImpl);
		assertEquals("1234", ((StringSimpleAttributeImpl) output.get("port1")).getValue());
	}

}

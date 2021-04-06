package com.unidata.mdm.cleanse.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.unidata.mdm.backend.common.cleanse.CleanseFunction;
import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.NumberSimpleAttributeImpl;
import com.unidata.mdm.meta.SimpleDataType;
/**
 * 
 * @author ilya.bykov
 *
 */
public class CFDivideTest {

	/** The cf. */
	private CleanseFunction cf = null;

	/**
	 * Test execute TRUE.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testExecute() throws Exception {
		Map<String, Object> input = new HashMap<>();
		NumberSimpleAttributeImpl port1 = new NumberSimpleAttributeImpl("port1", 12d);
		NumberSimpleAttributeImpl port2 = new NumberSimpleAttributeImpl("port2", 11d);
		input.put("port1", port1);
		input.put("port2", port2);
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(1, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof NumberSimpleAttributeImpl);
		assertEquals(new Double(12d/11d), ((NumberSimpleAttributeImpl) output.get("port1")).getValue());

	}

	/**
	 * Test execute FALSE.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test(expected = CleanseFunctionExecutionException.class)
	public void testExecuteFailure() throws Exception {
		Map<String, Object> input = new HashMap<>();
		NumberSimpleAttributeImpl port1 = new NumberSimpleAttributeImpl("port1", 12d);
		NumberSimpleAttributeImpl port2 = new NumberSimpleAttributeImpl("port2", 0d);
		input.put("port1", port1);
		input.put("port2", port2);
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(1, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof NumberSimpleAttributeImpl);
		assertEquals(new Double(12d/11d), ((NumberSimpleAttributeImpl) output.get("port1")).getValue());

	}

	/**
	 * Test execute N oinput.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test(expected = CleanseFunctionExecutionException.class)
	public void testExecuteNOinput() throws Exception {
		Map<String, Object> input = new HashMap<>();
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(1, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof BooleanSimpleAttributeImpl);
		assertEquals(null, ((BooleanSimpleAttributeImpl) output.get("port1")).getValue());

	}

	/**
	 * Test execute NULL.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test(expected = CleanseFunctionExecutionException.class)
	public void testExecuteNULL() throws Exception {
		Map<String, Object> input = new HashMap<>();
		BooleanSimpleAttributeImpl port1 = new BooleanSimpleAttributeImpl("port1", null);
		input.put("port1", port1);
		BooleanSimpleAttributeImpl port2 = new BooleanSimpleAttributeImpl("port2", null);
		input.put("port2", port2);
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(1, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof BooleanSimpleAttributeImpl);
		assertEquals(null, ((BooleanSimpleAttributeImpl) output.get("port1")).getValue());

	}



	/**
	 * Test get definition.
	 */
	@Test
	public void testGetDefinition() {
		assertNotNull(cf);
		assertNotNull(cf.getDefinition());
		assertEquals("Деление", cf.getDefinition().getFunctionName());
		assertEquals("com.unidata.mdm.cleanse.math.CFDivide", cf.getDefinition().getJavaClass());

	}

	/**
	 * Test input ports.
	 */
	@Test
	public void testInputPorts() {
		assertNotNull(cf);
		assertNotNull(cf.getDefinition());
		assertEquals(2, cf.getDefinition().getInputPorts().size());
		assertEquals("port1", cf.getDefinition().getInputPorts().get(0).getName());
		assertEquals(SimpleDataType.NUMBER, cf.getDefinition().getInputPorts().get(0).getDataType());
		assertEquals("port2", cf.getDefinition().getInputPorts().get(1).getName());
		assertEquals(SimpleDataType.NUMBER, cf.getDefinition().getInputPorts().get(1).getDataType());
		assertEquals(true, cf.getDefinition().getInputPorts().get(0).isRequired());
		assertEquals(true, cf.getDefinition().getInputPorts().get(1).isRequired());
	}

	/**
	 * Test output ports.
	 */
	@Test
	public void testOutputPorts() {
		assertNotNull(cf);
		assertNotNull(cf.getDefinition());
		assertEquals(1, cf.getDefinition().getOutputPorts().size());
		assertEquals("port1", cf.getDefinition().getOutputPorts().get(0).getName());
		assertEquals(SimpleDataType.NUMBER, cf.getDefinition().getOutputPorts().get(0).getDataType());
		assertEquals(true, cf.getDefinition().getOutputPorts().get(0).isRequired());
	}

	/**
	 * Inits test case.
	 */
	@Before
	public void init() {
		this.cf = new CFDivide();

	}

}

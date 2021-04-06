package com.unidata.mdm.cleanse.string;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.unidata.mdm.backend.common.cleanse.CleanseFunction;
import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.meta.SimpleDataType;
/**
 * 
 * @author ilya.bykov
 *
 */
public class CFConcatenateTest {

	/** The cf. */
	private CleanseFunction cf = null;

	/**
	 * Test execute TRUE.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testExecuteTRUEInteger() throws Exception {
		Map<String, Object> input = new HashMap<>();
		StringSimpleAttributeImpl port1 = new StringSimpleAttributeImpl("port1", "1");
		input.put("port1", port1);
		StringSimpleAttributeImpl port2 = new StringSimpleAttributeImpl("port2", "2");
		input.put("port2", port2);
		StringSimpleAttributeImpl port3 = new StringSimpleAttributeImpl("port3", "3");
		input.put("port3", port3);
		StringSimpleAttributeImpl port4 = new StringSimpleAttributeImpl("port4", "4");
		input.put("port4", port4);
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(1, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof StringSimpleAttributeImpl);
		assertEquals("1234", ((StringSimpleAttributeImpl) output.get("port1")).getValue());
	}

	@Test
	public void testExecuteTRUEString() throws Exception {
		Map<String, Object> input = new HashMap<>();
		StringSimpleAttributeImpl port1 = new StringSimpleAttributeImpl("port1", "Test");
		input.put("port1", port1);
		StringSimpleAttributeImpl port2 = new StringSimpleAttributeImpl("port2", " just");
		input.put("port2", port2);
		StringSimpleAttributeImpl port3 = new StringSimpleAttributeImpl("port3", " for");
		input.put("port3", port3);
		StringSimpleAttributeImpl port4 = new StringSimpleAttributeImpl("port4", " test!");
		input.put("port4", port4);
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(1, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof StringSimpleAttributeImpl);
		assertEquals("Test just for test!", ((StringSimpleAttributeImpl) output.get("port1")).getValue());
		

	}

	/**
	 * Test execute FALSE.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testExecuteFALSENumber() throws Exception {
		Map<String, Object> input = new HashMap<>();
		StringSimpleAttributeImpl port1 = new StringSimpleAttributeImpl("port1", "Test");
		input.put("port1", port1);
		StringSimpleAttributeImpl port2 = new StringSimpleAttributeImpl("port2", " just");
		input.put("port2", port2);
		StringSimpleAttributeImpl port3 = new StringSimpleAttributeImpl("port3", "\n for");
		input.put("port3", port3);
		StringSimpleAttributeImpl port4 = new StringSimpleAttributeImpl("port4", " test!");
		input.put("port4", port4);
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(1, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof StringSimpleAttributeImpl);
		assertEquals("Test just\n for test!", ((StringSimpleAttributeImpl) output.get("port1")).getValue());

	}

	@Test
	public void testExecuteFALSEString() throws Exception {
		Map<String, Object> input = new HashMap<>();
		StringSimpleAttributeImpl port1 = new StringSimpleAttributeImpl("port1", "Test");
		input.put("port1", port1);
		StringSimpleAttributeImpl port2 = new StringSimpleAttributeImpl("port2", " just\r");
		input.put("port2", port2);
		StringSimpleAttributeImpl port3 = new StringSimpleAttributeImpl("port3", "\n for");
		input.put("port3", port3);
		StringSimpleAttributeImpl port4 = new StringSimpleAttributeImpl("port4", " test!");
		input.put("port4", port4);
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(1, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof StringSimpleAttributeImpl);
		assertEquals("Test just\r\n for test!", ((StringSimpleAttributeImpl) output.get("port1")).getValue());
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
		assertEquals(false, ((BooleanSimpleAttributeImpl) output.get("port1")).getValue());

	}

	/**
	 * Test execute NULL.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testExecuteNULL() throws Exception {
		Map<String, Object> input = new HashMap<>();
		StringSimpleAttributeImpl port1 = new StringSimpleAttributeImpl("port1", null);
		input.put("port1", port1);
		StringSimpleAttributeImpl port2 = new StringSimpleAttributeImpl("port2", null);
		input.put("port2", port2);
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(1, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof StringSimpleAttributeImpl);
		assertEquals("", ((StringSimpleAttributeImpl) output.get("port1")).getValue());

	}

	/**
	 * Test execute EMPTY.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testExecuteEMPTY() throws Exception {
		Map<String, Object> input = new HashMap<>();
		StringSimpleAttributeImpl port1 = new StringSimpleAttributeImpl("port1", "");
		input.put("port1", port1);
		StringSimpleAttributeImpl port2 = new StringSimpleAttributeImpl("port2", "");
		input.put("port2", port2);
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(1, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof StringSimpleAttributeImpl);
		assertEquals("", ((StringSimpleAttributeImpl) output.get("port1")).getValue());

	}

	/**
	 * Test get definition.
	 */
	@Test
	public void testGetDefinition() {
		assertNotNull(cf);
		assertNotNull(cf.getDefinition());
		assertEquals("Соединить", cf.getDefinition().getFunctionName());
		assertEquals("com.unidata.mdm.cleanse.string.CFConcatenate", cf.getDefinition().getJavaClass());

	}

	/**
	 * Test input ports.
	 */
	@Test
	public void testInputPorts() {
		assertNotNull(cf);
		assertNotNull(cf.getDefinition());
		assertEquals(4, cf.getDefinition().getInputPorts().size());
		assertEquals("port1", cf.getDefinition().getInputPorts().get(0).getName());
		assertEquals(SimpleDataType.STRING, cf.getDefinition().getInputPorts().get(0).getDataType());
		assertEquals(true, cf.getDefinition().getInputPorts().get(0).isRequired());
		
		assertEquals("port2", cf.getDefinition().getInputPorts().get(1).getName());
		assertEquals(SimpleDataType.STRING, cf.getDefinition().getInputPorts().get(1).getDataType());
		assertEquals(true, cf.getDefinition().getInputPorts().get(1).isRequired());
		
		assertEquals("port3", cf.getDefinition().getInputPorts().get(2).getName());
		assertEquals(SimpleDataType.STRING, cf.getDefinition().getInputPorts().get(2).getDataType());
		assertEquals(false, cf.getDefinition().getInputPorts().get(2).isRequired());
		
		assertEquals("port4", cf.getDefinition().getInputPorts().get(3).getName());
		assertEquals(SimpleDataType.STRING, cf.getDefinition().getInputPorts().get(3).getDataType());
		assertEquals(false, cf.getDefinition().getInputPorts().get(3).isRequired());
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
		assertEquals(SimpleDataType.STRING, cf.getDefinition().getOutputPorts().get(0).getDataType());
		assertEquals(true, cf.getDefinition().getOutputPorts().get(0).isRequired());
	}

	/**
	 * Inits test case.
	 */
	@Before
	public void init() {
		this.cf = new CFConcatenate();
	}
}

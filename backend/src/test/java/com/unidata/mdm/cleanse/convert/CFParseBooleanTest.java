package com.unidata.mdm.cleanse.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.unidata.mdm.backend.common.cleanse.CleanseFunction;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.meta.SimpleDataType;

/**
 * The Class CFParseBooleanTest.
 * 
 * @author ilya.bykov
 */
@RunWith(MockitoJUnitRunner.class)
public class CFParseBooleanTest {

	/** The cf. */
	private CleanseFunction cf = null;

	/**
	 * Test execute TRUE.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testExecuteTRUE() throws Exception {
		Map<String, Object> input = new HashMap<>();
		StringSimpleAttributeImpl port1 = new StringSimpleAttributeImpl("port1", "TRUE");
		input.put("port1", port1);
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(1, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof BooleanSimpleAttributeImpl);
		assertEquals(true, ((BooleanSimpleAttributeImpl) output.get("port1")).getValue());

	}

	/**
	 * Test execute FALSE.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testExecuteFALSE() throws Exception {
		Map<String, Object> input = new HashMap<>();
		StringSimpleAttributeImpl port1 = new StringSimpleAttributeImpl("port1", "FALSE");
		input.put("port1", port1);
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(1, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof BooleanSimpleAttributeImpl);
		assertEquals(false, ((BooleanSimpleAttributeImpl) output.get("port1")).getValue());

	}

	/**
	 * Test execute N oinput.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
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
	@Test
	public void testExecuteNULL() throws Exception {
		Map<String, Object> input = new HashMap<>();
		StringSimpleAttributeImpl port1 = new StringSimpleAttributeImpl("port1", null);
		input.put("port1", port1);
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(1, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof BooleanSimpleAttributeImpl);
		assertEquals(null, ((BooleanSimpleAttributeImpl) output.get("port1")).getValue());

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
		assertEquals("РазобратьBoolean", cf.getDefinition().getFunctionName());
		assertEquals("com.unidata.mdm.cleanse.convert.CFParseBoolean", cf.getDefinition().getJavaClass());

	}

	/**
	 * Test input ports.
	 */
	@Test
	public void testInputPorts() {
		assertNotNull(cf);
		assertNotNull(cf.getDefinition());
		assertEquals(1, cf.getDefinition().getInputPorts().size());
		assertEquals("port1", cf.getDefinition().getInputPorts().get(0).getName());
		assertEquals(SimpleDataType.STRING, cf.getDefinition().getInputPorts().get(0).getDataType());
		assertEquals(false, cf.getDefinition().getInputPorts().get(0).isRequired());
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
		assertEquals(SimpleDataType.BOOLEAN, cf.getDefinition().getOutputPorts().get(0).getDataType());
		assertEquals(true, cf.getDefinition().getOutputPorts().get(0).isRequired());
	}

	/**
	 * Inits test case.
	 */
	@Before
	public void init() {
		this.cf = new CFParseBoolean();

	}
}

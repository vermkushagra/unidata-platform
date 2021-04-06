package com.unidata.mdm.cleanse.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.unidata.mdm.backend.common.cleanse.CleanseFunction;
import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.TimestampSimpleAttributeImpl;
import com.unidata.mdm.meta.SimpleDataType;


/**
 * The Class CFParseBooleanTest.
 * @author ilya.bykov
 */
@RunWith(MockitoJUnitRunner.class)
public class CFParseDateTest {

	/** The cf. */
	private CleanseFunction cf = null;
	
	/** The Constant SDF. */
	private static final SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yyyy");

	/**
	 * Test execute TRUE.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testExecuteWithDate() throws Exception {
		Map<String, Object> input = new HashMap<>();
		StringSimpleAttributeImpl port1 = new StringSimpleAttributeImpl("port1", "21.01.2001");
		StringSimpleAttributeImpl port2 = new StringSimpleAttributeImpl("port2", "dd.MM.yyyy");
		input.put("port1", port1);
		input.put("port2", port2);
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(1, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof TimestampSimpleAttributeImpl);
		assertEquals(LocalDateTime.ofInstant(SDF.parse("21.01.2001").toInstant(), ZoneId.systemDefault()),
				((TimestampSimpleAttributeImpl) output.get("port1")).getValue());

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
		cf.execute(input);

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
		StringSimpleAttributeImpl port2 = new StringSimpleAttributeImpl("port2", "dd.MM.yyyy");
		input.put("port1", port1);
		input.put("port2", port2);
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(1, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof TimestampSimpleAttributeImpl);
		assertEquals(null, ((TimestampSimpleAttributeImpl) output.get("port1")).getValue());

	}

	/**
	 * Test execute no port 1.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testExecuteNoPort1() throws Exception {
		Map<String, Object> input = new HashMap<>();
		StringSimpleAttributeImpl port2 = new StringSimpleAttributeImpl("port2", "dd.MM.yyyy");
		input.put("port2", port2);
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(1, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof TimestampSimpleAttributeImpl);
		assertEquals(null, ((TimestampSimpleAttributeImpl) output.get("port1")).getValue());

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
		StringSimpleAttributeImpl port2 = new StringSimpleAttributeImpl("port2", "dd.MM.yyyy");
		input.put("port1", port1);
		input.put("port2", port2);
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(1, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof TimestampSimpleAttributeImpl);
		assertEquals(null, ((TimestampSimpleAttributeImpl) output.get("port1")).getValue());

	}

	/**
	 * Test execute wrong format.
	 *
	 * @throws Exception the exception
	 */
	@Test(expected = CleanseFunctionExecutionException.class)
	public void testExecuteWrongFormat() throws Exception {
		Map<String, Object> input = new HashMap<>();
		StringSimpleAttributeImpl port1 = new StringSimpleAttributeImpl("port1", "TEST");
		StringSimpleAttributeImpl port2 = new StringSimpleAttributeImpl("port2", "dd.MM.yyyy");
		input.put("port1", port1);
		input.put("port2", port2);
		cf.execute(input);

	}

	/**
	 * Test get definition.
	 */
	@Test
	public void testGetDefinition() {
		assertNotNull(cf);
		assertNotNull(cf.getDefinition());
		assertEquals("РазобратьDate", cf.getDefinition().getFunctionName());
		assertEquals("com.unidata.mdm.cleanse.convert.CFParseDate", cf.getDefinition().getJavaClass());

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
		assertEquals("port2", cf.getDefinition().getInputPorts().get(1).getName());
		assertEquals(SimpleDataType.STRING, cf.getDefinition().getInputPorts().get(0).getDataType());
		assertEquals(SimpleDataType.STRING, cf.getDefinition().getInputPorts().get(1).getDataType());
		assertEquals(false, cf.getDefinition().getInputPorts().get(0).isRequired());
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
		assertEquals(SimpleDataType.TIMESTAMP, cf.getDefinition().getOutputPorts().get(0).getDataType());
		assertEquals(true, cf.getDefinition().getOutputPorts().get(0).isRequired());
	}

	/**
	 * Inits test case.
	 */
	@Before
	public void init() {
		this.cf = new CFParseDate();

	}
}

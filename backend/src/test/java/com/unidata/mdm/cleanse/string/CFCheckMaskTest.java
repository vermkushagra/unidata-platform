package com.unidata.mdm.cleanse.string;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.unidata.mdm.backend.common.cleanse.CleanseFunction;
import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.backend.service.cleanse.CFAppContext;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.meta.SimpleDataType;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CFAppContext.class, MessageUtils.class })
/**
 * 
 * @author ilya.bykov
 *
 */
public class CFCheckMaskTest {
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
		// StringSimpleAttributeImpl port1 = new
		// StringSimpleAttributeImpl("port1", "1234");
		// input.put("port1", port1);
		StringSimpleAttributeImpl port2 = new StringSimpleAttributeImpl("port2", "9999");
		input.put("port2", port2);
		StringSimpleAttributeImpl port3 = new StringSimpleAttributeImpl("port3", "1234");
		input.put("port3", port3);
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(2, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof BooleanSimpleAttributeImpl);
		assertEquals(true, ((BooleanSimpleAttributeImpl) output.get("port1")).getValue());
		input = new HashMap<>();
		StringSimpleAttributeImpl port1 = new StringSimpleAttributeImpl("port1", "([0-2][0-3][0-4][0-5])");
		input.put("port1", port1);
		port2 = new StringSimpleAttributeImpl("port2", "");
		input.put("port2", port2);
		port3 = new StringSimpleAttributeImpl("port3", "1234");
		input.put("port3", port3);
		output = cf.execute(input);
		assertNotNull(output);
		assertEquals(2, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof BooleanSimpleAttributeImpl);
		assertEquals(true, ((BooleanSimpleAttributeImpl) output.get("port1")).getValue());
	}
	@Test
	public void testExecuteFALSEInteger() throws Exception {
		Map<String, Object> input = new HashMap<>();
		// StringSimpleAttributeImpl port1 = new
		// StringSimpleAttributeImpl("port1", "1234");
		// input.put("port1", port1);
		StringSimpleAttributeImpl port2 = new StringSimpleAttributeImpl("port2", "9999");
		input.put("port2", port2);
		StringSimpleAttributeImpl port3 = new StringSimpleAttributeImpl("port3", "1234");
		input.put("port3", port3);
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(2, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof BooleanSimpleAttributeImpl);
		assertEquals(true, ((BooleanSimpleAttributeImpl) output.get("port1")).getValue());
		input = new HashMap<>();
		StringSimpleAttributeImpl port1 = new StringSimpleAttributeImpl("port1", "([0-2][0-3][0-4][0-5])");
		input.put("port1", port1);
		port2 = new StringSimpleAttributeImpl("port2", "");
		input.put("port2", port2);
		port3 = new StringSimpleAttributeImpl("port3", "122dd34");
		input.put("port3", port3);
		output = cf.execute(input);
		assertNotNull(output);
		assertEquals(2, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof BooleanSimpleAttributeImpl);
		assertEquals(false, ((BooleanSimpleAttributeImpl) output.get("port1")).getValue());
	}
	@Test
	public void testExecuteTRUEString() throws Exception {
		Map<String, Object> input = new HashMap<>();
		StringSimpleAttributeImpl port2 = new StringSimpleAttributeImpl("port2", "llll");
		input.put("port2", port2);
		StringSimpleAttributeImpl port3 = new StringSimpleAttributeImpl("port3", "abcd");
		input.put("port3", port3);
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(2, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof BooleanSimpleAttributeImpl);
		assertEquals(true, ((BooleanSimpleAttributeImpl) output.get("port1")).getValue());
		input = new HashMap<>();
		StringSimpleAttributeImpl port1 = new StringSimpleAttributeImpl("port1", "([a-z][a-z][a-z][a-z])");
		input.put("port1", port1);
		port2 = new StringSimpleAttributeImpl("port2", "");
		input.put("port2", port2);
		port3 = new StringSimpleAttributeImpl("port3", "abcd");
		input.put("port3", port3);
		output = cf.execute(input);
		assertNotNull(output);
		assertEquals(2, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof BooleanSimpleAttributeImpl);
		assertEquals(true, ((BooleanSimpleAttributeImpl) output.get("port1")).getValue());

	}
	@Test
	public void testExecuteTRUEEmpty() throws Exception {
		Map<String, Object> input = new HashMap<>();
		StringSimpleAttributeImpl port2 = new StringSimpleAttributeImpl("port2", "");
		input.put("port2", port2);
		StringSimpleAttributeImpl port3 = new StringSimpleAttributeImpl("port3", "abcd");
		input.put("port3", port3);
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(2, output.size());
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
	public void testExecuteFALSENumber() throws Exception {
		Map<String, Object> input = new HashMap<>();
		StringSimpleAttributeImpl port2 = new StringSimpleAttributeImpl("port2", "llll");
		input.put("port2", port2);
		StringSimpleAttributeImpl port3 = new StringSimpleAttributeImpl("port3", "1234");
		input.put("port3", port3);
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(2, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof BooleanSimpleAttributeImpl);
		assertEquals(false, ((BooleanSimpleAttributeImpl) output.get("port1")).getValue());
		assertNotNull(((StringSimpleAttributeImpl) output.get("port2")));

	}

	@Test
	public void testExecuteFALSEString() throws Exception {
		Map<String, Object> input = new HashMap<>();
		StringSimpleAttributeImpl port2 = new StringSimpleAttributeImpl("port2", "9999");
		input.put("port2", port2);
		StringSimpleAttributeImpl port3 = new StringSimpleAttributeImpl("port3", "abcd");
		input.put("port3", port3);
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(2, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof BooleanSimpleAttributeImpl);
		assertEquals(false, ((BooleanSimpleAttributeImpl) output.get("port1")).getValue());
		assertNotNull(((StringSimpleAttributeImpl) output.get("port2")));
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
	@Test(expected = CleanseFunctionExecutionException.class)
	public void testExecuteNULL() throws Exception {
		Map<String, Object> input = new HashMap<>();
		StringSimpleAttributeImpl port1 = new StringSimpleAttributeImpl("port1", null);
		input.put("port1", port1);
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(1, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof BooleanSimpleAttributeImpl);
		assertEquals(true, ((BooleanSimpleAttributeImpl) output.get("port1")).getValue());

	}
    
    /**
     * Test execute NULL optional.
     *
     * @throws Exception the exception
     */
	@Test
    public void testExecuteOptional() throws Exception {
        Map<String, Object> input = new HashMap<>();
        StringSimpleAttributeImpl port2 = new StringSimpleAttributeImpl("port2", "llll");
        input.put("port2", port2);
        StringSimpleAttributeImpl port3 = new StringSimpleAttributeImpl("port3", null);
        input.put("port3", port3);
        BooleanSimpleAttributeImpl port4 = new BooleanSimpleAttributeImpl("port4", false);
        input.put("port4", port4);
        Map<String, Object> output = cf.execute(input);
        assertNotNull(output);
        assertEquals(2, output.size());
        assertNotNull(output.get("port1"));
        assertEquals(true, output.get("port1") instanceof BooleanSimpleAttributeImpl);
        assertEquals(true, ((BooleanSimpleAttributeImpl) output.get("port1")).getValue());
        input = new HashMap<>();
        StringSimpleAttributeImpl port1 = new StringSimpleAttributeImpl("port1", "([a-z][a-z][a-z][a-z])");
        input.put("port1", port1);
        port2 = new StringSimpleAttributeImpl("port2", "");
        input.put("port2", port2);
        port3 = new StringSimpleAttributeImpl("port3", "abcd");
        input.put("port3", port3);
        output = cf.execute(input);
        assertNotNull(output);
        assertEquals(2, output.size());
        assertNotNull(output.get("port1"));
        assertEquals(true, output.get("port1") instanceof BooleanSimpleAttributeImpl);
        assertEquals(true, ((BooleanSimpleAttributeImpl) output.get("port1")).getValue());

    }
    /**
     * Test execute NULL optional.
     *
     * @throws Exception the exception
     */
	@Test
    public void testExecuteRequired() throws Exception {
        Map<String, Object> input = new HashMap<>();
        StringSimpleAttributeImpl port2 = new StringSimpleAttributeImpl("port2", "llll");
        input.put("port2", port2);
        StringSimpleAttributeImpl port3 = new StringSimpleAttributeImpl("port3", null);
        input.put("port3", port3);
        BooleanSimpleAttributeImpl port4 = new BooleanSimpleAttributeImpl("port4", true);
        input.put("port4", port4);
        Map<String, Object> output = cf.execute(input);
        assertNotNull(output);
        assertEquals(2, output.size());
        assertNotNull(output.get("port1"));
        assertEquals(true, output.get("port1") instanceof BooleanSimpleAttributeImpl);
        assertEquals(false, ((BooleanSimpleAttributeImpl) output.get("port1")).getValue());
        input = new HashMap<>();
        StringSimpleAttributeImpl port1 = new StringSimpleAttributeImpl("port1", "([a-z][a-z][a-z][a-z])");
        input.put("port1", port1);
        port2 = new StringSimpleAttributeImpl("port2", "");
        input.put("port2", port2);
        port3 = new StringSimpleAttributeImpl("port3", "abcd");
        input.put("port3", port3);
        output = cf.execute(input);
        assertNotNull(output);
        assertEquals(2, output.size());
        assertNotNull(output.get("port1"));
        assertEquals(true, output.get("port1") instanceof BooleanSimpleAttributeImpl);
        assertEquals(true, ((BooleanSimpleAttributeImpl) output.get("port1")).getValue());

    }
	/**
	 * Test execute EMPTY.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test(expected=CleanseFunctionExecutionException.class)
	public void testExecuteEMPTY() throws Exception {
		Map<String, Object> input = new HashMap<>();
		StringSimpleAttributeImpl port1 = new StringSimpleAttributeImpl("port1", "");
		input.put("port1", port1);
		Map<String, Object> output = cf.execute(input);
		assertNotNull(output);
		assertEquals(1, output.size());
		assertNotNull(output.get("port1"));
		assertEquals(true, output.get("port1") instanceof BooleanSimpleAttributeImpl);
		assertEquals(true, ((BooleanSimpleAttributeImpl) output.get("port1")).getValue());

	}

	/**
	 * Test get definition.
	 */
	@Test
	public void testGetDefinition() {
		assertNotNull(cf);
		assertNotNull(cf.getDefinition());
		assertEquals("ПроверкаПоМаске", cf.getDefinition().getFunctionName());
		assertEquals("com.unidata.mdm.cleanse.string.CFCheckMask", cf.getDefinition().getJavaClass());

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
		assertEquals(false, cf.getDefinition().getInputPorts().get(0).isRequired());
		assertEquals("port2", cf.getDefinition().getInputPorts().get(1).getName());
		assertEquals(SimpleDataType.STRING, cf.getDefinition().getInputPorts().get(1).getDataType());
		assertEquals(true, cf.getDefinition().getInputPorts().get(1).isRequired());
		assertEquals("port3", cf.getDefinition().getInputPorts().get(2).getName());
		assertEquals(SimpleDataType.STRING, cf.getDefinition().getInputPorts().get(2).getDataType());
		assertEquals("port4", cf.getDefinition().getInputPorts().get(3).getName());
        assertEquals(SimpleDataType.BOOLEAN, cf.getDefinition().getInputPorts().get(3).getDataType());
		assertEquals(false, cf.getDefinition().getInputPorts().get(3).isRequired());
	}

	/**
	 * Test output ports.
	 */
	@Test
	public void testOutputPorts() {
		assertNotNull(cf);
		assertNotNull(cf.getDefinition());
		assertEquals(2, cf.getDefinition().getOutputPorts().size());
		assertEquals("port1", cf.getDefinition().getOutputPorts().get(0).getName());
		assertEquals(SimpleDataType.BOOLEAN, cf.getDefinition().getOutputPorts().get(0).getDataType());
		assertEquals(true, cf.getDefinition().getOutputPorts().get(0).isRequired());
		assertEquals("port2", cf.getDefinition().getOutputPorts().get(1).getName());
		assertEquals(SimpleDataType.STRING, cf.getDefinition().getOutputPorts().get(1).getDataType());
		assertEquals(true, cf.getDefinition().getOutputPorts().get(1).isRequired());
	}

	/**
	 * Inits test case.
	 */
	@Before
	public void init() {
		PowerMockito.mockStatic(CFAppContext.class);
		PowerMockito.mockStatic(MessageUtils.class);
		this.cf = new CFCheckMask();
	}
}

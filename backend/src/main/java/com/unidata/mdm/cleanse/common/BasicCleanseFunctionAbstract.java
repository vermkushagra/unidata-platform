/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.unidata.mdm.cleanse.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.cleanse.CleanseFunction;
import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.Attribute.AttributeType;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.BinaryLargeValueImpl;
import com.unidata.mdm.backend.common.types.impl.CharacterLargeValueImpl;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.meta.CleanseFunctionExtendedDef;
import com.unidata.mdm.meta.DQCleanseFunctionPortApplicationMode;
import com.unidata.mdm.meta.DQRuleExecutionContext;
import com.unidata.mdm.meta.Port;
import com.unidata.mdm.meta.SimpleDataType;

/**
 * Basic cleanse function class.
 * It is used for "BASIC" type of cleanse functions( kind of functions build in unidata platform).
 * Contains common methods for cleanse function validation, execution, etc.
 *
 * @author ilya.bykov
 *
 */
public abstract class BasicCleanseFunctionAbstract implements CleanseFunction {

	/** The Constant CLASS_NAME. */
	private static final String CLASS_NAME = ".className";

	/** The Constant FUNCTION_NAME. */
	private static final String FUNCTION_NAME = ".functionName";

	/** The Constant DEFINITION2. */
	private static final String DEFINITION2 = ".definition";

	private static final String SUPPORTED_EXECUTION_CONTEXTS = ".supportedExecutionContexts";

	/** The Constant OUTPUT_PORT. */
	private static final String OUTPUT_PORT = ".outputPort[";

	/** The Constant DATA_TYPE. */
	private static final String DATA_TYPE = "].dataType";

	/** The Constant DESCRIPTION. */
	private static final String DESCRIPTION = "].description";

	/** The Constant REQUIRED. */
	private static final String REQUIRED = "].required";

	/** The Constant APPLICATION_MODE. */
	private static final String APPLICATION_MODE = "].applicationMode";

	/** The Constant NAME. */
	private static final String NAME = "].name";

	/** The Constant INPUT_PORT. */
	private static final String INPUT_PORT = ".inputPort[";
	/** The definition. */
	protected CleanseFunctionExtendedDef definition;

	/** The class name. */
	private String className;

	/** The props. */
	private Properties props;

	/** The input ports. */
	private Map<String, Port> inputPorts;

	/**
	 * Instantiates a new cleanse function abstract.
	 *
	 * @param clazz
	 *            cleanse function class.
	 */
	protected BasicCleanseFunctionAbstract(Class<?> clazz) {
		this.className = clazz.getCanonicalName();
		loadProperties();
		initDefinition();
		initInputPorts();
		initOutputPorts();
	}

	/**
	 * Fill cleanse function definition.
	 */
	private void initDefinition() {
		definition = new CleanseFunctionExtendedDef();
		definition.setDescription(props.getProperty(className + DEFINITION2));
		definition.setFunctionName(props.getProperty(className + FUNCTION_NAME));
		definition.setJavaClass(props.getProperty(className + CLASS_NAME));

		String[] supportedExecutionContexts = StringUtils.split(props.getProperty(className + SUPPORTED_EXECUTION_CONTEXTS), ',');
		DQRuleExecutionContext[] supported;

		if (supportedExecutionContexts == null || supportedExecutionContexts.length == 0) {
		    supported = DQRuleExecutionContext.values();
		} else {
		    supported = Stream.of(supportedExecutionContexts)
		            .map(DQRuleExecutionContext::valueOf)
		            .toArray(sz -> new DQRuleExecutionContext[sz]);
		}

		definition.withSupportedExecutionContexts(supported);
	}

	/**
	 * Initialize output ports of cleanse function.
	 *
	 */
	private void initOutputPorts() {
		int count = 0;
		while (true) {
			String nameString = props.getProperty(className + OUTPUT_PORT + count + NAME);
			String requiredString = props.getProperty(className + OUTPUT_PORT + count + REQUIRED);
			String descriptionString = props.getProperty(className + OUTPUT_PORT + count + DESCRIPTION);
			String dataTypeString = props.getProperty(className + OUTPUT_PORT + count + DATA_TYPE);
			if (nameString == null || requiredString == null || dataTypeString == null) {
				break;
			}
			if (StringUtils.isBlank(nameString) || StringUtils.isBlank(dataTypeString)
					|| StringUtils.isBlank(requiredString)) {
				throw new SystemRuntimeException("Required fields not set in configuration",
						ExceptionId.EX_SYSTEM_CLEANSE_INIT_OUTPUT, nameString);
			}

			Port port = JaxbUtils.getMetaObjectFactory().createPort();
			port.setRequired(Boolean.parseBoolean(requiredString));
			port.setName(nameString);
			port.setDescription(descriptionString);
			port.setDataType(SimpleDataType.valueOf(dataTypeString));

			definition.getOutputPorts().add(port);
			count++;
		}

	}

	/**
	 * Fill input ports.
	 *
	 */
	private void initInputPorts() {

		inputPorts = new HashMap<>();
		int count = 0;
		while (true) {

		    String nameString = props.getProperty(className + INPUT_PORT + count + NAME);
			String requiredString = props.getProperty(className + INPUT_PORT + count + REQUIRED);
			String descriptionString = props.getProperty(className + INPUT_PORT + count + DESCRIPTION);
			String dataTypeString = props.getProperty(className + INPUT_PORT + count + DATA_TYPE);
			String applicationModeAsString = props.getProperty(className + INPUT_PORT + count + APPLICATION_MODE);

			if (nameString == null || requiredString == null || dataTypeString == null) {
				break;
			}
			if (StringUtils.isBlank(nameString) || StringUtils.isBlank(dataTypeString)
					|| StringUtils.isBlank(requiredString)) {
				throw new SystemRuntimeException("Required field is not defined in configuration",
						ExceptionId.EX_SYSTEM_CLEANSE_INIT_INPUT, nameString);
			}

			Port port = JaxbUtils.getMetaObjectFactory().createPort();
			port.setRequired(Boolean.parseBoolean(requiredString));
			port.setName(nameString);
			port.setDescription(descriptionString);
			port.setDataType(SimpleDataType.valueOf(dataTypeString));
			port.setApplicationMode(StringUtils.isBlank(applicationModeAsString)
			        ? null
			        : DQCleanseFunctionPortApplicationMode.fromValue(applicationModeAsString));

			definition.getInputPorts().add(port);
			inputPorts.put(nameString, port);
			count++;
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.unidata.mdm.cleanse.CleanseFunction#getDefinition()
	 */
	@Override
	public final CleanseFunctionExtendedDef getDefinition() {
		return this.definition;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.unidata.mdm.cleanse.CleanseFunction#execute(java.util.Map)
	 */
	@Override
	public final Map<String, Object> execute(Map<String, Object> input) throws Exception {
		Map<String, Object> result = new HashMap<>();
		validateInput(input);
		execute(input, result);
		validateOutput(result);
		return result;
	}

	/**
	 * Execute cleanse function.
	 *
	 * @param input
	 *            the input data.
	 * @param result
	 *            the result data.
	 * @throws CleanseFunctionExecutionException
	 *             If some error occurs while cleanse function execution this
	 *             exception should be thrown.
	 * @throws Exception
	 *
	 */
	public void execute(Map<String, Object> input, Map<String, Object> result) throws Exception {
	    // NOPE
	};

	/**
	 * Validate input data according to cleanse function description:
	 * <ul>
	 * <li>Checks required ports</li>
	 * <li>Checks data types</li>
	 * </ul>
	 *
	 * @param input
	 *            the input data.
	 * @throws CleanseFunctionExecutionException
	 *             if input validation failed.
	 */
	protected void validateInput(Map<String, Object> input) {
		List<Port> inputPorts = getDefinition().getInputPorts();
		List<String> errorMessages = new ArrayList<>();
		for (Port port : inputPorts) {
			if (port.isRequired() && !input.containsKey(port.getName())) {
				errorMessages.add("Value for required port " + port.getName() + " not set!");
			}
			if (port.isRequired() && getValueByPort(port.getName(), input) == null) {
				errorMessages.add("Incorrect data type recieved for " + port.getName() + ". Expected data type "
						+ port.getDataType().value());
			}
		}
		if (!errorMessages.isEmpty()) {
			throw new CleanseFunctionExecutionException(this.getDefinition().getFunctionName(),
			        errorMessages.toArray(new String[errorMessages.size()]));
		}
	}

	/**
	 * Validate output.
	 *
	 * @param output
	 *            the output
	 */
	private void validateOutput(Map<String, Object> output) {
		// TODO: output validation(if needed)
	}

	/**
	 * Return value by port name.
	 *
	 * @param name
	 *            port name.
	 * @param input
	 *            map with input objects.
	 * @return value by port name.
	 * @throws CleanseFunctionExecutionException
	 *             if some error occurs while execution of cleanse function.
	 */
	protected Object getValueByPort(String name, Map<String, Object> input) throws CleanseFunctionExecutionException {

		List<Port> inputPorts = definition.getInputPorts();
		for (Port port : inputPorts) {
			if (StringUtils.equals(port.getName(), name)) {
				Attribute value = (Attribute) input.get(port.getName());
				if (port.isRequired() && (!input.containsKey(port.getName()) || value == null)) {
					throw new CleanseFunctionExecutionException(
					        "Error while executing '{}'. Required value of '{}' is missing!",
					        ExceptionId.EX_DQ_CLEANSE_FUNCTION_REQUIRED_VALUE_MISSING,
					        getDefinition().getFunctionName(), port.getName());
				}

				if (value == null) {
					return null;
				} else if (value.getAttributeType() == AttributeType.SIMPLE) {
				    return getValueFromSimpleAttribute(port, (SimpleAttribute<?>) value);
				} else if (value.getAttributeType() == AttributeType.CODE) {
				    return getValueFromCodeAttribute(port, (CodeAttribute<?>) value);
				} else if (value.getAttributeType() == AttributeType.ARRAY) {
				    return getValueFromArrayAttribute(port, (ArrayAttribute<?>) value);
				}

				break;
			}
		}

		return null;
	}

	private Object getValueFromCodeAttribute(Port port, CodeAttribute<?> attribute) {

        switch (port.getDataType()) {
            case INTEGER:
                return attribute == null ? null : attribute.getValue();
            case STRING:
                return attribute == null || attribute.getValue() == null
                    ? StringUtils.EMPTY
                    : attribute.getValue();
            case ANY:
                return attribute;
            default:
                break;
        }

        return null;
    }

	private Object getValueFromSimpleAttribute(Port port, SimpleAttribute<?> attribute) {

	    switch (port.getDataType()) {
            case BLOB:
                return attribute == null ? null : ((BinaryLargeValueImpl) attribute.getValue()).getData();
            case CLOB:
                return attribute == null ? null : ((CharacterLargeValueImpl) attribute.getValue()).getData();
            case BOOLEAN:
                return attribute == null ? Boolean.FALSE : attribute.getValue();
            case DATE:
            case TIME:
            case TIMESTAMP:
            case INTEGER:
            case NUMBER:
                return attribute == null ? null : attribute.getValue();
            case STRING:
                return attribute == null || attribute.getValue() == null
                        ? StringUtils.EMPTY
                        : attribute.getValue();
            case ANY:
                return attribute;
            default:
                break;
	    }

	    return null;
	}

	private Object getValueFromArrayAttribute(Port port, ArrayAttribute<?> attribute) {

        switch (port.getDataType()) {
            case DATE:
            case TIME:
            case TIMESTAMP:
            case INTEGER:
            case STRING:
            case NUMBER:
                return attribute == null
                    ? null
                    : attribute.toArray();
            case ANY:
                return attribute;
            default:
                break;
        }

        return Collections.emptyList();
    }

	/**
	 * Read properties with cleanse functions description from the property
	 * file.
	 *
	 */
	private void loadProperties() {
		props = new Properties();
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("cleanse.properties");
		try {
			props.load(new InputStreamReader(in, "UTF-8"));
		} catch (IOException ex) {
			throw new SystemRuntimeException("Unable to read property file with cleanse functions description",
					ExceptionId.EX_SYSTEM_CLEANSE_READ_PROPERTIES, ex);
		}
	}

	/**
	 * Gets the input ports.
	 *
	 * @return Map with input ports.
	 */
	protected Map<String, Port> getInputPorts() {
		return inputPorts;
	}
}

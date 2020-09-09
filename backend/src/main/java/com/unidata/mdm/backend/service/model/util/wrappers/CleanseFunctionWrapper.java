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

package com.unidata.mdm.backend.service.model.util.wrappers;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.common.cleanse.CleanseFunction;
import com.unidata.mdm.backend.service.cleanse.CFUtils;
import com.unidata.mdm.backend.service.cleanse.composite.CompositeFunctionMDAGRep;
import com.unidata.mdm.meta.CleanseFunctionDef;
import com.unidata.mdm.meta.CleanseFunctionExtendedDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionDef;
import com.unidata.mdm.meta.DQRuleExecutionContext;
import com.unidata.mdm.meta.Port;

/**
 * Cleanse function wrapper.
 *
 * @author ilya.bykov
 */

public class CleanseFunctionWrapper implements ValueWrapper {
    /**
     * This class logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CleanseFunctionWrapper.class);
	/** The id. */
	private String id;

	/** The cleanse function. */
	private CleanseFunction cleanseFunction;

	/** The cleanse function definition. */
	private CleanseFunctionExtendedDef cleanseFunctionDef;
	/** Input ports */
	private final Map<String, Port> inputPorts = new HashMap<>();
	/** Output ports */
	private final Map<String, Port> outputPorts = new HashMap<>();

	/** Composite CF flag. */
	private boolean composite;
	/**
	 * Representation of composite cleanse function as multiple edged directed
	 * graph
	 */
	private CompositeFunctionMDAGRep mdagRep;

	/**
	 * Instantiates a new cleanse function wrapper.
	 */
	private CleanseFunctionWrapper() {
	    super();
	}

	private void repackagePorts() {

	    for (Port port : this.cleanseFunctionDef.getInputPorts()) {
	        inputPorts.put(port.getName(), port);
	    }

	    for (Port port : this.cleanseFunctionDef.getOutputPorts()) {
            outputPorts.put(port.getName(), port);
        }
	}

	private void addContextSupportDefaults(CleanseFunctionExtendedDef cfed) {

	    // Adds all as default at user's own.
	    if (CollectionUtils.isEmpty(cfed.getSupportedExecutionContexts())) {
	        cfed.withSupportedExecutionContexts(DQRuleExecutionContext.GLOBAL, DQRuleExecutionContext.LOCAL);
	    }
	}
	/**
	 * Instantiates a new cleanse function wrapper. Creates cleanse function
	 * instance using reflection.
	 *
	 * @param id
	 *            cleanse function id(unique name plus path in this case)
	 * @param cleanseFunctionDef
	 *            the cleanse function definition
	 */
	public CleanseFunctionWrapper(String id, CleanseFunctionDef cleanseFunctionDef) {
		this();
		this.id = id;
		this.cleanseFunction = CFUtils.createCleanseFunction(cleanseFunctionDef);
		this.cleanseFunctionDef = this.cleanseFunction == null ? null : this.cleanseFunction.getDefinition();
		this.composite = cleanseFunctionDef instanceof CompositeCleanseFunctionDef;

		if (isValid()) {
		    repackagePorts();
		    addContextSupportDefaults(this.cleanseFunctionDef);
		}
	}

	/**
	 * Instantiates a new cleanse function wrapper.
	 *
	 * @param clazz
	 *            cleanse function class
	 */
	public CleanseFunctionWrapper(Class<CleanseFunction> clazz) {
		try {
			this.cleanseFunction = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
		    LOGGER.warn("Unable to instantiate cleanse function with id {} and class {}",
                    cleanseFunctionDef.getFunctionName(),
                    cleanseFunctionDef.getJavaClass());
		}
		this.cleanseFunctionDef = this.cleanseFunction.getDefinition();
		this.id = this.cleanseFunction.getDefinition().getFunctionName();

		if (isValid()) {
            repackagePorts();
            addContextSupportDefaults(this.cleanseFunctionDef);
        }
	}

	/**
	 * Instantiates a new cleanse function wrapper.
	 *
	 * @param functionDef
	 *            composite cleanse function definition.
	 */
	public CleanseFunctionWrapper(CompositeCleanseFunctionDef functionDef) {
		this.cleanseFunctionDef = functionDef;
		this.id = functionDef.getFunctionName();
		this.mdagRep = CFUtils.convertToGraph(functionDef);
		this.composite = true;

		if (isValid()) {
            repackagePorts();
            addContextSupportDefaults(this.cleanseFunctionDef);
        }
	}

	public boolean isValid() {

	    if (composite) {
	        return Objects.nonNull(cleanseFunctionDef)
	                && Objects.nonNull(((CompositeCleanseFunctionDef) cleanseFunctionDef).getLogic())
	                && Objects.nonNull(mdagRep)
	                && StringUtils.isNotBlank(id);
	    }

	    return Objects.nonNull(cleanseFunctionDef)
                && StringUtils.isNotBlank(id)
                && Objects.nonNull(cleanseFunction);
	}

	/**
	 * Gets the id.
	 *
	 * @return cleanse function id(path plus name)
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the cleanse function.
	 *
	 * @return the cleanse function
	 */
	public CleanseFunction getCleanseFunction() {
		return cleanseFunction;
	}

	/**
	 * Gets the cleanse function definition.
	 *
	 * @return the cleanse function definition
	 */
	public CleanseFunctionExtendedDef getCleanseFunctionDef() {
		return cleanseFunctionDef;
	}

	/**
	 * Gets the composite cleanse function as multiple edged directed graph
	 *
	 * @return Representation of composite cleanse function as multiple edged
	 *         directed graph
	 */
	public CompositeFunctionMDAGRep getMdagRep() {
		return mdagRep;
	}

    /**
     * @return the composite
     */
    public boolean isComposite() {
        return composite;
    }

    /**
     * @return the inputPorts
     */
    public Map<String, Port> getInputPorts() {
        return inputPorts;
    }

    /**
     * @return the outputPorts
     */
    public Map<String, Port> getOutputPorts() {
        return outputPorts;
    }

    public Port getInputPortByName(String name) {
        return inputPorts.get(name);
    }

    public Port getOutputPortByName(String name) {
        return outputPorts.get(name);
    }
}

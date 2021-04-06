package com.unidata.mdm.cleanse.common;

import com.unidata.mdm.backend.common.cleanse.CleanseFunction;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.service.cleanse.CFUtils;
import com.unidata.mdm.backend.service.cleanse.composite.CompositeFunctionMDAGRep;
import com.unidata.mdm.backend.service.model.util.wrappers.ValueWrapper;
import com.unidata.mdm.meta.CleanseFunctionDef;
import com.unidata.mdm.meta.CleanseFunctionExtendedDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionDef;

/**
 * Cleanse function wrapper.
 * 
 * @author ilya.bykov
 */

public class CleanseFunctionWrapper implements ValueWrapper {

	/** The id. */
	private String id;

	/** The cleanse function. */
	private CleanseFunction cleanseFunction;

	/** The cleanse function definition. */
	private CleanseFunctionExtendedDef cleanseFunctionDef;

	/**
	 * Representation of composite cleanse function as multiple edged directed
	 * graph
	 */
	private CompositeFunctionMDAGRep mdagRep;

	/**
	 * Instantiates a new cleanse function wrapper.
	 */
	private CleanseFunctionWrapper() {

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
		this.cleanseFunctionDef = this.cleanseFunction==null?null:this.cleanseFunction.getDefinition();
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
			 new SystemRuntimeException("Unable to instantiate cleanse function with id {} and class {}",
					ExceptionId.EX_SYSTEM_CLEANSE_BASIC_INIT_FAILED, cleanseFunctionDef.getFunctionName(),
					cleanseFunctionDef.getJavaClass());
		}
		this.cleanseFunctionDef = this.cleanseFunction.getDefinition();
		this.id = this.cleanseFunction.getDefinition().getFunctionName();

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

}

package com.unidata.mdm.cleanse.string;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import java.util.Map;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Removes repeating whitespaces.
 *
 * @author ilya.bykov
 */
public class CFCompressWhitespaces extends BasicCleanseFunctionAbstract {

	/**
	 * Instantiates a new CF compress whitespaces.
	 *
	 */
	public CFCompressWhitespaces() {
		super(CFCompressWhitespaces.class);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.unidata.mdm.cleanse.CleanseFunction#execute(java.util.Map)
	 */
	@Override
	public void execute(Map<String, Object> input, Map<String, Object> result)
			throws CleanseFunctionExecutionException {
		result.put(OUTPUT1, new StringSimpleAttributeImpl(OUTPUT1,
		        ((String) super.getValueByPort(INPUT1, input)).replaceAll("\\s+", " ")));
	}
}

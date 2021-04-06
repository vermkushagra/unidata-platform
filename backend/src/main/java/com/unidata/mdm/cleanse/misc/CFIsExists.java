package com.unidata.mdm.cleanse.misc;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Check that value exist.
 * @author ilya.bykov
 */
public class CFIsExists extends BasicCleanseFunctionAbstract {

	/**
	 * Instantiates a new CF is exists.
	 */
	public CFIsExists() {
		super(CFIsExists.class);
	}

	/* (non-Javadoc)
	 * @see com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract#execute(java.util.Map, java.util.Map)
	 */
	@Override
	public void execute(Map<String, Object> input, Map<String, Object> result) throws Exception {
		SimpleAttribute<?> toCheck =  (SimpleAttribute<?>) input.get(INPUT1);
		result.put(OUTPUT1, new BooleanSimpleAttributeImpl(OUTPUT1,
		        toCheck != null
		     && toCheck.getValue() != null
		     && (toCheck.getDataType() == DataType.STRING
		         ? StringUtils.isNoneBlank((String) toCheck.castValue()) : true)));
	}

}

package com.unidata.mdm.cleanse.convert;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.NumberSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;
/**
 * Cleanse function parse string to number.
 * @author ilya.bykov
 *
 */
public class CFParseNumber extends BasicCleanseFunctionAbstract {
	/**
	 * Cleanse function constructor.
	 */
    public CFParseNumber() {
        super(CFParseNumber.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.cleanse.CleanseFunction#execute(java.util.Map)
     */
	@Override
	public void execute(Map<String, Object> input, Map<String, Object> result)
			throws CleanseFunctionExecutionException {
		String value = (String) super.getValueByPort(INPUT1, input);
		boolean isBlank = StringUtils.isBlank(value);
		Double number = null;
		if (!isBlank) {
			try {
				number = NumberUtils.createDouble(value);
			} catch (NumberFormatException ex) {
				throw new CleanseFunctionExecutionException(getDefinition().getFunctionName(),
						"Unable to parse string to number");
			}
		}
		result.put(OUTPUT1, new NumberSimpleAttributeImpl(OUTPUT1, number));
	}

}

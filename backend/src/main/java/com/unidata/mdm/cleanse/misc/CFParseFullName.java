package com.unidata.mdm.cleanse.misc;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;

import java.util.Map;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * The Class CFParseFullName.
 * TODO: this class needed only for ocrv demo.
 * It is currently obsolete, should be removed.
 */
@Deprecated
public class CFParseFullName extends BasicCleanseFunctionAbstract {

	/**
	 * Instantiates a new CF parse full name.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public CFParseFullName() throws Exception {
		super(CFParseFullName.class);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract#execute(
	 * java.util.Map, java.util.Map)
	 */
	// TODO: REWRITE!!!! MOCK
	@Override
	public void execute(Map<String, Object> input, Map<String, Object> result)
			throws CleanseFunctionExecutionException {

		String inputString = (String) super.getValueByPort(INPUT1, input);
		String[] results = inputString.split(";");
		/*
		SimpleAttribute output1 = JaxbUtils.getDataObjectFactory().createSimpleAttribute();
		output1.setName(OUTPUT1);
		SimpleAttribute output2 = JaxbUtils.getDataObjectFactory().createSimpleAttribute();
		output2.setName(OUTPUT2);

		if (results != null && results.length == 2) {
			output1.setStringValue(JaxbUtils.getDataObjectFactory(), results[0].trim());
			output2.setStringValue(JaxbUtils.getDataObjectFactory(), results[1].trim());
		}
		if (results == null || results.length == 1) {
			results = inputString.split(",");
			if (results != null && results.length == 2) {
				output1.setStringValue(JaxbUtils.getDataObjectFactory(), results[0].trim());
				output2.setStringValue(JaxbUtils.getDataObjectFactory(), results[1].trim());
			}
		}
		if (results == null | results.length == 1) {
			results = inputString.split("[ ]{3,}");
			if (results != null && results.length == 2) {
				output1.setStringValue(JaxbUtils.getDataObjectFactory(), results[0].trim());
				output2.setStringValue(JaxbUtils.getDataObjectFactory(), results[1].trim());
			}
		}
		if (results == null | results.length == 1) {
			output1.setStringValue(JaxbUtils.getDataObjectFactory(), inputString);
			output2.setStringValue(JaxbUtils.getDataObjectFactory(), "");
		}
        */
		throw new RuntimeException("CFParseFullName usage! Uncomment and rewrite the code!");
		/*
		result.put(OUTPUT1, output1);
		result.put(OUTPUT2, output2);
		*/
	}

}

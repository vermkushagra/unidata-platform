package com.unidata.mdm.cleanse.misc;

import java.util.Map;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * The Class CFFixPhoneCode.
 * TODO: this class was needed only for ocrv demo.
 * It is currently obsolete, should be removed.
 */
@Deprecated
public class CFFixPhoneCode extends BasicCleanseFunctionAbstract {

    /** The Constant PHONE_REGEXP. */
    private static final String PHONE_REGEXP = "^(8-?|\\+?7-?)?\\(?(\\d{3})\\)?-?((\\d-?){6}\\d$)";

    /**
     * Instantiates a new CF fix phone code.
     *
     * @throws Exception
     *             the exception
     */
    public CFFixPhoneCode() throws Exception {
        super(CFFixPhoneCode.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract#execute(
     * java.util.Map, java.util.Map)
     */
    //TODO: REWRITE!!!! MOCK
    @Override
    public void execute(Map<String, Object> input, Map<String, Object> result) throws CleanseFunctionExecutionException {
        /*
        String phoneString = (String) super.getValueByPort(INPUT1, input);
        String cityString = (String) super.getValueByPort(INPUT2, input);
        cityString = cityString.substring(0, cityString.indexOf(","));
        Pattern pattern = Pattern.compile(PHONE_REGEXP);
        Matcher matcher = pattern.matcher(phoneString);
        SimpleAttribute output2 = JaxbUtils.getDataObjectFactory().createSimpleAttribute();
        output2.setName(OUTPUT2);
        SimpleAttribute output1 = JaxbUtils.getDataObjectFactory().createSimpleAttribute();
        output1.setName(OUTPUT1);

        if (matcher.find()) {
            String countryCode = matcher.group(1);
            String cityCode = matcher.group(2);
            if (!StringUtils.equals("495", cityCode) && StringUtils.equalsIgnoreCase(cityString, "МОСКВА")) {
                cityCode = "495";
                output2.setBoolValue(JaxbUtils.getDataObjectFactory(), false);
            } else if (!StringUtils.equals("812", cityCode)
                    && StringUtils.equalsIgnoreCase(cityString, "САНКТ-ПЕТЕРБУРГ")) {
                cityCode = "812";
                output2.setBoolValue(JaxbUtils.getDataObjectFactory(), false);
            } else if ((StringUtils.equals("495", cityCode) && StringUtils.equalsIgnoreCase(cityString, "МОСКВА"))
                    || (StringUtils.equals("812", cityCode) && StringUtils.equalsIgnoreCase(cityString,
                            "САНКТ-ПЕТЕРБУРГ"))) {
                output2.setBoolValue(JaxbUtils.getDataObjectFactory(), true);
            }else{
                output2.setBoolValue(JaxbUtils.getDataObjectFactory(), false);
            }
            String number = matcher.group(3);
            output1.setStringValue(JaxbUtils.getDataObjectFactory(), countryCode + cityCode + number);
        }

        result.put(OUTPUT1, output1);
        result.put(OUTPUT2, output2);
        */
        throw new RuntimeException("CFFixPhoneCode usage! Uncommetn and rewrite code!");
    }

}

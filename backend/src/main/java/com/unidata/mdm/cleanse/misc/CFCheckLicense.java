package com.unidata.mdm.cleanse.misc;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * The Class CFCheckLicense.
 * TODO: this class was needed only for ocrv demo.
 * It is currently obsolete, should be removed.
 */
@Deprecated
public class CFCheckLicense extends BasicCleanseFunctionAbstract {

    /** The Constant LICENSE_REGEXP. */
    private static final String LICENSE_REGEXP = "([А-Я]{2}-[0-9]{2}-[0-9]{2}-[0-9]{6})";

    /**
     * Constructor.
     *
     * @throws Exception
     *             the exception
     */
    public CFCheckLicense() {
        super(CFCheckLicense.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Map<String, Object> input, Map<String, Object> result) throws CleanseFunctionExecutionException {
        /*
        SimpleAttribute simpleAttribute = (SimpleAttribute) JaxbUtils.getDataObjectFactory().createSimpleAttribute()
                .withName(OUTPUT1)
                .withBoolValue(JaxbUtils.getDataObjectFactory(),
                        isValidLicense((String) super.getValueByPort(INPUT1, input)));

        SimpleAttribute simpleAttribute2 = (SimpleAttribute) JaxbUtils.getDataObjectFactory().createSimpleAttribute()
                .withName(OUTPUT2)
                .withStringValue(JaxbUtils.getDataObjectFactory(),
                        isValidLicense((String) super.getValueByPort(INPUT1, input)) ? (String) super
                    .getValueByPort(INPUT1, input) : ("ЛО" + (String) super.getValueByPort(INPUT1, input)));

        result.put(OUTPUT1, simpleAttribute);
        result.put(OUTPUT2, simpleAttribute2);
        */
        throw new RuntimeException("CFCheckLicense usage! Uncomment and rewrite!");
    }

    /**
     * Checks if is valid license.
     *
     * @param licenseString
     *            the license string
     * @return the boolean
     */
    private Boolean isValidLicense(String licenseString) {
        Pattern pattern = Pattern.compile(LICENSE_REGEXP);
        Matcher matcher = pattern.matcher(licenseString);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

}

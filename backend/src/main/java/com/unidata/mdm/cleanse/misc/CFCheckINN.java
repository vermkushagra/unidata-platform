package com.unidata.mdm.cleanse.misc;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Validates INN.
 * @author ilya.bykov
 */
public class CFCheckINN extends BasicCleanseFunctionAbstract {

    /** The Constant INN_PATTERN. */
    private static final Pattern INN_PATTERN = Pattern.compile("\\d{10}|\\d{12}");

    /** The Constant IN_CHECK_ARR. */
    private static final int[] IN_CHECK_ARR = new int[] { 3, 7, 2, 4, 10, 3, 5, 9, 4, 6, 8 };

    /**
     * Instantiates a new CF check inn.
     */
    public CFCheckINN(){
        super(CFCheckINN.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.cleanse.CleanseFunction#execute(java.util.Map)
     */
    @Override
    public void execute(Map<String, Object> input, Map<String, Object> result) throws CleanseFunctionExecutionException{
        result.put(OUTPUT1, new BooleanSimpleAttributeImpl(OUTPUT1, isValidINN((String) super.getValueByPort(INPUT1, input))));
    }

    /**
     * Checks if is valid inn.
     *
     * @param innString
     *            the inn string
     * @return true, if is valid inn
     */
    private static boolean isValidINN(String innString) {
    	if(StringUtils.isEmpty(innString)){
    		return false;
    	}
        innString = innString.trim();
        if (!INN_PATTERN.matcher(innString).matches()) {
            return false;
        }
        int length = innString.length();
        if (length == 12) {
            return checkINNSum(innString, 2, 1) && checkINNSum(innString, 1, 0);
        } else {
            return checkINNSum(innString, 1, 2);
        }
    }

    /**
     * Check INN control sum.
     *
     * @param inn
     *            the inn
     * @param offset
     *            the offset
     * @param arrOffset
     *            the arr offset
     * @return true, if successful
     */
    private static boolean checkINNSum(String inn, int offset, int arrOffset) {
        int sum = 0;
        int length = inn.length();
        for (int i = 0; i < length - offset; i++) {
            sum += (inn.charAt(i) - '0') * IN_CHECK_ARR[i + arrOffset];
        }
        return (sum % 11) % 10 == inn.charAt(length - offset) - '0';
    }
}

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

package com.unidata.mdm.cleanse.misc;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionOutputParam;
import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;
import com.unidata.mdm.cleanse.common.CleanseConstants;

/**
 * Validates INN.
 * @author ilya.bykov
 */
public class CFCheckINN extends BasicCleanseFunctionAbstract implements CFSystemCleanseFunction {

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
    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CleanseFunctionContext ctx) {

        CleanseFunctionInputParam param1 = ctx.getInputParamByPortName(CleanseConstants.INPUT1);

        if (param1 == null || param1.isEmpty()) {
            ctx.putOutputParam(CleanseFunctionOutputParam.of(CleanseConstants.OUTPUT1, Boolean.FALSE));
            return;
        }

        Map<Object, List<Attribute>> check = extractAndMapAttributes(param1);
        if (check.isEmpty()) {
            ctx.putOutputParam(CleanseFunctionOutputParam.of(CleanseConstants.OUTPUT1, Boolean.FALSE));
            return;
        }

        boolean hasFailed = false;
        for (Entry<Object, List<Attribute>> entry : check.entrySet()) {

            boolean isValid = isValidINN(entry.getKey().toString());
            if (!isValid) {
                hasFailed = true;
                ctx.failedValidations().addAll(entry.getValue().stream()
                        .map(attr -> new ImmutablePair<>(attr.toLocalPath(), attr))
                        .collect(Collectors.toList()));
            }
        }

        ctx.putOutputParam(CleanseFunctionOutputParam.of(CleanseConstants.OUTPUT1, !hasFailed));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isContextAware() {
        return true;
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

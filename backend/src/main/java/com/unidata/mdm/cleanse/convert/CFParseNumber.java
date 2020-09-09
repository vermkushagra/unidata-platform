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

package com.unidata.mdm.cleanse.convert;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionOutputParam;
import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;
import com.unidata.mdm.cleanse.misc.CFSystemCleanseFunction;
/**
 * Cleanse function parse string to number.
 * @author ilya.bykov
 *
 */
public class CFParseNumber extends BasicCleanseFunctionAbstract implements CFSystemCleanseFunction {
	/**
	 * Cleanse function constructor.
	 */
    public CFParseNumber() {
        super(CFParseNumber.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CleanseFunctionContext ctx) {

        CleanseFunctionInputParam valueParam = ctx.getInputParamByPortName(INPUT1);
        if (valueParam.isEmpty()) {
            ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT1, (Long) null));
        } else {
            // Multiple values were filtered
            if (!ensureAllSingletons(ctx, valueParam)) {
                return;
            }

            String valueString = valueParam.toSingletonValue();
            try {
                Double value = StringUtils.isBlank(valueString) ? null : NumberUtils.createDouble(valueString);;
                ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT1, value));
            } catch (Exception e) {
                throw new CleanseFunctionExecutionException(ctx.getCleanseFunctionName(), e, "Unable to parse string to double");
            }
        }

        super.execute(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isContextAware() {
        return true;
    }
}

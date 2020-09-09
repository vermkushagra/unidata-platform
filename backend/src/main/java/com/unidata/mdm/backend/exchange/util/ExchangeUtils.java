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

package com.unidata.mdm.backend.exchange.util;

import com.unidata.mdm.backend.exchange.ExchangeContext;
import com.unidata.mdm.backend.exchange.ExchangeContext.Action;
import com.unidata.mdm.backend.exchange.chain.ProcessingChain;
import com.unidata.mdm.backend.exchange.chain.ProcessingChainFactory;

/**
 * @author Mikhail Mikhailov
 * Utility methods for data exchange.
 */
public class ExchangeUtils {

    public static final String[] SIMON_HEADERS = {
        "Name",
        "Active count",
        "Count",
        "Max ts",
        "Min ts",
        "Min time",
        "Mean (Avg) time",
        "Max time",
        "Total time"
    };

    /**
     * Instances disabled.
     */
    private ExchangeUtils() {
        super();
    }

    /**
     * Migrate Model
     *
     * @param ctx
     */
    public static boolean modelMigration(ExchangeContext ctx) {
        if (ctx.getMigrationClasses() == null || ctx.getMigrationClasses().isEmpty()) {
            throw new IllegalArgumentException("Invalid or insufficient input params");
        }
        ProcessingChain chain = ProcessingChainFactory.getMigrationChain(ctx);
        boolean success = chain.execute(ctx, Action.MIGRATION);

        System.out.print("MIGRATION finished " + (success ? "" : "NOT") + " successfully.");
        return success;
    }
}

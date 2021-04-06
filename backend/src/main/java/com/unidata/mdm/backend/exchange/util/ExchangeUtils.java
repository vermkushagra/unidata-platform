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

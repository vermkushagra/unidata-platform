/**
 *
 */
package com.unidata.mdm.backend.exchange.chain;

import java.util.ArrayList;
import java.util.Collection;

import com.unidata.mdm.backend.exchange.ExchangeContext;
import com.unidata.mdm.backend.exchange.ExchangeContext.InputFormat;
import com.unidata.mdm.backend.exchange.chain.csv.CsvTransformImportChainMember;
import com.unidata.mdm.backend.exchange.chain.db.DbEntitiesTransformChainMember;
import com.unidata.mdm.backend.exchange.chain.db.DbRelationsTransformChainMember;
import com.unidata.mdm.backend.exchange.util.ExchangeUtils;

/**
 * @author Mikhail Mikhailov
 * Factory methods for creation of import / export / processing chains.
 */
public class ProcessingChainFactory {

    /**
     * Instantiation disabled.
     */
    private ProcessingChainFactory() {
        super();
    }

    /**
     * Gets import chain as defined by the supplied context.
     * @param ctx the context
     * @return chain
     */
    public static ProcessingChain getDataImportChain(ExchangeContext ctx) {

        DefaultProcessingChain chain = new DefaultProcessingChain();

        // Storage + init countdown latches.
        if (ctx.getInputFormats().contains(InputFormat.DB)) {
            // Read and process DB
            chain.getMembers().add(new DbEntitiesTransformChainMember());
            chain.getMembers().add(new DbRelationsTransformChainMember());
        }

        if (ctx.getInputFormats().contains(InputFormat.CSV)) {
            // Read and process CSV
            chain.getMembers().add(new CsvTransformImportChainMember());
        }
        return chain;
    }

    /**
     * Gets model import chain.
     * @param ctx the context
     * @return chain
     */
    public static ProcessingChain getModelImportChain(ExchangeContext ctx) {

        DefaultProcessingChain chain = new DefaultProcessingChain();
        // Import metadata
        chain.getMembers().add(new ImportMetaModelChainMember());
        return chain;
    }

    /**
     * Get migration chain
     *
     * @param ctx the context
     * @return chain
     */
    public static ProcessingChain getMigrationChain(ExchangeContext ctx) {
        DefaultProcessingChain chain = new DefaultProcessingChain();
        Collection<ChainMember> migrationChainMembers = new ArrayList<>();
        for (String className : ctx.getMigrationClasses()) {
            try {
                ChainMember migrationChainMember = ExchangeUtils.class.getClassLoader()
                        .loadClass(className).asSubclass(ChainMember.class).newInstance();
                migrationChainMembers.add(migrationChainMember);
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        chain.getMembers().addAll(migrationChainMembers);
        return chain;
    }
}

/**
 *
 */
package com.unidata.mdm.backend.exchange.chain;

import java.util.List;

import com.unidata.mdm.backend.exchange.ExchangeContext;
import com.unidata.mdm.backend.exchange.ExchangeContext.Action;

/**
 * @author Mikhail Mikhailov
 * Processing chain for an {@link Action}.
 */
public interface ProcessingChain {

    /**
     * Executes tasks.
     * @param ctx the context
     * @param currentAction the action being currently executed
     * @return true, if successful, false otherwise
     */
    public boolean execute(ExchangeContext ctx, Action currentAction);

    /**
     * Gets the members of the chain.
     * @return chain members
     */
    public List<ChainMember> getMembers();
}

/**
 *
 */
package com.unidata.mdm.backend.exchange.chain;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.exchange.ExchangeContext;
import com.unidata.mdm.backend.exchange.ExchangeContext.Action;

/**
 * @author Mikhail Mikhailov
 * Import specific chain implementation.
 */
public class DefaultProcessingChain implements ProcessingChain {

    /**
     * The chain itself.
     */
    private List<ChainMember> chainMembers = new ArrayList<ChainMember>();

    /**
     * Ctor.
     */
    public DefaultProcessingChain() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(ExchangeContext ctx, Action currentAction) {
        for (ChainMember member : chainMembers) {
            if (!member.execute(ctx, currentAction)) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChainMember> getMembers() {
        return chainMembers;
    }

}

/**
 *
 */
package com.unidata.mdm.backend.exchange.chain;

import com.unidata.mdm.backend.exchange.ExchangeContext;
import com.unidata.mdm.backend.exchange.ExchangeContext.Action;

/**
 * @author Mikhail Mikhailov
 * Bulk output member.
 */
public class BulkOutputChainMember implements ChainMember {

    /**
     * Ctor.
     */
    public BulkOutputChainMember() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(ExchangeContext ctx, Action currentAction) {
        // TODO Auto-generated method stub
        return false;
    }

}

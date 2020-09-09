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
    private List<ChainMember> chainMembers = new ArrayList<>();

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

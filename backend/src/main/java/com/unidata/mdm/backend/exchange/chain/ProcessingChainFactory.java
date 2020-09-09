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
import java.util.Collection;

import com.unidata.mdm.backend.exchange.ExchangeContext;
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

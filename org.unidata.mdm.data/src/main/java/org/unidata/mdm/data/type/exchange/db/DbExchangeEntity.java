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

package org.unidata.mdm.data.type.exchange.db;

import java.util.List;

import org.unidata.mdm.data.type.exchange.ExchangeEntity;

/**
 * @author Mikhail Mikhailov
 *
 */
public class DbExchangeEntity extends ExchangeEntity {
    /**
     * SVUID
     */
    private static final long serialVersionUID = -32975122397337368L;

    /**
     * DB URL.
     */
    private List<String> tables;

    /**
     * DB URL.
     */
    private List<String> joins;

    /**
     * Order by elements.
     */
    private String orderBy;

    /**
     * Limit predicate instead of standard (limit, offset).
     * Something like id > ${current offset value}.
     */
    private String limitPredicate;
    /**
     * Clean tables after load/import.
     */
    private boolean cleanAfter;
    /**
     * Drop tables after load/import.
     */
    private boolean dropAfter;

    /**
     * Constructor.
     */
    public DbExchangeEntity() {
        super();
    }

    /**
     * @return the tables
     */
    public List<String> getTables() {
        return tables;
    }

    /**
     * @param tables the tables to set
     */
    public void setTables(List<String> tables) {
        this.tables = tables;
    }

    /**
     * @return the joins
     */
    public List<String> getJoins() {
        return joins;
    }

    /**
     * @param joins the joins to set
     */
    public void setJoins(List<String> joins) {
        this.joins = joins;
    }

    /**
     * @return the orderBy
     */
    public String getOrderBy() {
        return orderBy;
    }

    /**
     * @param orderBy the orderBy to set
     */
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * @return the limitPredicate
     */
    public String getLimitPredicate() {
        return limitPredicate;
    }

    /**
     * @param limitPredicate the limitPredicate to set
     */
    public void setLimitPredicate(String limitPredicate) {
        this.limitPredicate = limitPredicate;
    }

    /**
     * @return the cleanAfter
     */
    public boolean isCleanAfter() {
        return cleanAfter;
    }

    /**
     * @param cleanAfter the cleanAfter to set
     */
    public void setCleanAfter(boolean cleanAfter) {
        this.cleanAfter = cleanAfter;
    }

    /**
     * @return the dropAfter
     */
    public boolean isDropAfter() {
        return dropAfter;
    }

    /**
     * @param dropAfter the dropAfter to set
     */
    public void setDropAfter(boolean dropAfter) {
        this.dropAfter = dropAfter;
    }
}

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

package com.unidata.mdm.backend.service.job.exchange.out;

import org.springframework.jdbc.core.JdbcTemplate;

import com.unidata.mdm.backend.exchange.def.ExchangeEntity;
import com.unidata.mdm.backend.exchange.def.ExchangeObject;
import com.unidata.mdm.backend.exchange.def.ExchangeRelation;

/**
 * @author Mikhail Mikhailov
 * Base class for export data step chain members.
 */
public abstract class ExportDataStepChainMember {
    /**
     * The foreign data source, initialized once before step run.
     */
    protected static ThreadLocal<JdbcTemplate> jdbcTemplateStorage = new ThreadLocal<>();
    /**
     * Step's exchange object.
     */
    protected static ThreadLocal<ExchangeObject> exchangeObjectStorage = new ThreadLocal<>();
    /**
     * Count storage.
     */
    protected static ThreadLocal<ExportDataStatisticPage> statisticCountStorage = new ThreadLocal<>();
    /**
     * Gets the foreign data source or null.
     * @return data source or null, if not initialized
     */
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplateStorage.get();
    }
    /**
     * Sets a foreign data source.
     * @param jdbcTemplate the data source to set
     */
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        jdbcTemplateStorage.set(jdbcTemplate);
    }
    /**
     * Removes data source.
     */
    public void removeJdbcTemplate() {
        jdbcTemplateStorage.remove();
    }
    /**
     * Tells if this exchange object denotes an entity.
     * @return true, if so, false otherwise
     */
    public boolean exchangeObjectIsEntity() {
        ExchangeObject obj = exchangeObjectStorage.get();
        return obj != null && obj instanceof ExchangeEntity;
    }
    /**
     * Tells if this exchange object denotes a relation.
     * @return true, if so, false otherwise
     */
    public boolean exchangeObjectIsRelation() {
        ExchangeObject obj = exchangeObjectStorage.get();
        return obj != null && obj instanceof ExchangeRelation;
    }
    /**
     * Gets exchange object.
     * @return exchange object
     */
    @SuppressWarnings("unchecked")
    public<T extends ExchangeObject> T getExchangeObject() {
        return (T) exchangeObjectStorage.get();
    }
    /**
     * Sets exchange object.
     * @param eo the object to set
     */
    public void setExchangeObject(ExchangeObject eo) {
        exchangeObjectStorage.set(eo);
    }
    /**
     * Removes current exchange object.
     */
    public void removeExchangeObject() {
        exchangeObjectStorage.remove();
    }
    /**
     * @return the statistic page
     */
    public ExportDataStatisticPage getStatisticPage() {
        return statisticCountStorage.get();
    }
    /**
     * @return the statistic page
     */
    public void removeStatisticPage() {
        statisticCountStorage.remove();
    }
}

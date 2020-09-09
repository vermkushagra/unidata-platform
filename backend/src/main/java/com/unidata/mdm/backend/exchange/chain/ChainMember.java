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

import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.dao.impl.ImportErrorDao;
import com.unidata.mdm.backend.exchange.ExchangeContext;
import com.unidata.mdm.backend.exchange.ExchangeContext.Action;
import com.unidata.mdm.backend.service.audit.AuditEventsWriter;
import com.unidata.mdm.backend.service.data.RecordsServiceComponent;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.search.SearchServiceExt;

/**
 * @author Mikhail Mikhailov
 * Processing chain member, executed by {@link ProcessingChain}.
 */
public interface ChainMember {

    /**
     * Execute task.
     * @param ctx he context
     * @param currentAction current action
     * @return true, if successful, false otherwise
     */
    public boolean execute(ExchangeContext ctx, Action currentAction);

    /**
     * Creates a meta model service instance.
     * @param ctx current context
     * @return instance
     */
    public default MetaModelServiceExt getMetaModelService(ExchangeContext ctx) {
        ApplicationContext appCtx = ctx.getFromStorage(StorageId.COMMON_APPLICATION_CONTEXT);
        return appCtx != null ? appCtx.getBean(MetaModelServiceExt.class) : null;
    }

    /**
     * Creates a search service instance.
     * @param ctx the context
     * @return service instance
     */
    public default SearchServiceExt getSearchService(ExchangeContext ctx) {
        ApplicationContext appCtx = ctx.getFromStorage(StorageId.COMMON_APPLICATION_CONTEXT);
        return appCtx != null ? appCtx.getBean(SearchServiceExt.class) : null;
    }

    /**
     * Creates a meta model service instance.
     * @param ctx current context
     * @return instance
     */
    default RecordsServiceComponent getRecordsServiceComponent(ExchangeContext ctx) {
        ApplicationContext appCtx = ctx.getFromStorage(StorageId.COMMON_APPLICATION_CONTEXT);
        return appCtx != null ? appCtx.getBean(RecordsServiceComponent.class) : null;
    }

    /**
     * Creates a meta model service instance.
     * @param ctx current context
     * @return instance
     */
    default CommonRecordsComponent getCommonServiceComponent(ExchangeContext ctx) {
        ApplicationContext appCtx = ctx.getFromStorage(StorageId.COMMON_APPLICATION_CONTEXT);
        return appCtx != null ? appCtx.getBean(CommonRecordsComponent.class) : null;
    }

    /**
     * Creates a data record service instance.
     * @param ctx the context
     * @return service instance
     */
    static DataRecordsService createDataRecordsService(ExchangeContext ctx) {
        ApplicationContext appCtx = ctx.getFromStorage(StorageId.COMMON_APPLICATION_CONTEXT);
        return appCtx != null ? appCtx.getBean(DataRecordsService.class) : null;
    }

    static ImportErrorDao createErrorDao(ExchangeContext ctx){
        ApplicationContext appCtx = ctx.getFromStorage(StorageId.COMMON_APPLICATION_CONTEXT);
        return appCtx != null ? appCtx.getBean(ImportErrorDao.class) : null;
    }

    static AuditEventsWriter createAuditWriter(ExchangeContext ctx){
        ApplicationContext appCtx = ctx.getFromStorage(StorageId.COMMON_APPLICATION_CONTEXT);
        return appCtx != null ? appCtx.getBean(AuditEventsWriter.class) : null;
    }

    static MessageSource createMessageSource(ExchangeContext ctx){
        ApplicationContext appCtx = ctx.getFromStorage(StorageId.COMMON_APPLICATION_CONTEXT);
        return appCtx != null ? appCtx.getBean(MessageSource.class) : null;
    }
}

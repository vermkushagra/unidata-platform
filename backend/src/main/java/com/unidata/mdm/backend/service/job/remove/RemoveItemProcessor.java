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

package com.unidata.mdm.backend.service.job.remove;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Required;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;

public class RemoveItemProcessor implements ItemProcessor<String, DeleteRequestContext> {

    /**
     * entity name
     */
    private String entityName;

    /**
     * wipe
     */
    private boolean wipe;

    /**
     * operation id
     */
    private String operationId;
    /**
     * operation executor
     */
    private String operationExecutor;

    @Override
    public DeleteRequestContext process(String etalonId) throws Exception {
        DeleteRequestContext ctx = DeleteRequestContext.builder()
                .etalonKey(etalonId)
                .entityName(entityName)
                .cascade(false)
                .wipe(wipe)
                .inactivateEtalon(!wipe)
                .batchUpsert(true)
                .build();
        ctx.setOperationId(operationId);
        return ctx;
    }

    @Required
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    @Required
    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    @Required
    public void setWipe(boolean wipe) {
        this.wipe = wipe;
    }


    public String getOperationExecutor() {
        return operationExecutor;
    }

    public void setOperationExecutor(String operationExecutor) {
        this.operationExecutor = operationExecutor;
    }
}

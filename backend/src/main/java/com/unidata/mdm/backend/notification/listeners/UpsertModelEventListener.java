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

package com.unidata.mdm.backend.notification.listeners;

import com.hazelcast.core.Message;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.notification.events.UpsertModelEvent;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Listener responsible for processing upsert model events.
 */
@Component
public class UpsertModelEventListener extends AbstractOwnRejectMessageListener<UpsertModelEvent> {

    @Autowired
    private MetaModelServiceExt metaModelService;

    @Override
    public void onForeignMessage(Message<UpsertModelEvent> message) {
        UpdateModelRequestContext updateModelRequestContext = message.getMessageObject().getUpdateModelRequestContext();
        metaModelService.synchronizationUpsertModel(updateModelRequestContext);
    }
}

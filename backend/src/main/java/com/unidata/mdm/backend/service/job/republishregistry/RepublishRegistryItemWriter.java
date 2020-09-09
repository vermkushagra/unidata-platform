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

package com.unidata.mdm.backend.service.job.republishregistry;

import static com.unidata.mdm.backend.service.notification.NotificationUtils.createEtalonUpsertNotification;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.api.UnidataMessageDef;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.service.notification.Notification;
import com.unidata.mdm.backend.service.notification.NotificationService;
import com.unidata.mdm.backend.service.notification.ProcessedAction;
import com.unidata.mdm.backend.service.notification.configs.NotificationConfig;
import com.unidata.mdm.backend.service.notification.messages.UnidataMessage;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class RepublishRegistryItemWriter implements ItemWriter<List<Pair<RecordKeys,EtalonRecord>>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepublishRegistryItemWriter.class);

    @Autowired
    private NotificationService notificationService;

    @Value("#{stepExecutionContext[entityName]}")
    private String entityName;

    @Value("#{stepExecutionContext[operationId]}")
    private String operationId;

    @Override
    public void write(final List<? extends List<Pair<RecordKeys, EtalonRecord>>> items) throws Exception {
        if (CollectionUtils.isEmpty(items)) {
            return;
        }
        for (final List<Pair<RecordKeys, EtalonRecord>> item : items) {
            final List<Notification<?>> notList = item.stream().map(this::createNotification).collect(toList());
            notificationService.notify(notList);
        }
        LOGGER.info("Operation resend etalon done for items: " + items);
    }

    private Notification<UnidataMessage> createNotification(final Pair<RecordKeys, EtalonRecord> pair) {
        EtalonRecord etalonRecord = pair.getValue();
        RecordKeys recordKeys = pair.getKey();
        final UnidataMessageDef message = createEtalonUpsertNotification(etalonRecord, null, UpsertAction.NO_ACTION,
                recordKeys.getSupplementaryKeys(), operationId);
        final UnidataMessage unidataMessage = new UnidataMessage(message);
        final NotificationConfig notificationConfig = new NotificationConfig(ProcessedAction.RESEND_ETALON, recordKeys);
        return new Notification<>(notificationConfig, unidataMessage);
    }
}
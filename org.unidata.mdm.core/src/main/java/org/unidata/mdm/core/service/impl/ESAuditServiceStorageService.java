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

package org.unidata.mdm.core.service.impl;

import org.springframework.stereotype.Service;
import org.unidata.mdm.core.context.AuditEventWriteContext;
import org.unidata.mdm.core.dto.EnrichedAuditEvent;
import org.unidata.mdm.core.service.AuditServiceStorageService;
import org.unidata.mdm.core.type.search.AuditIndexType;
import org.unidata.mdm.search.context.IndexRequestContext;
import org.unidata.mdm.search.service.SearchService;
import org.unidata.mdm.search.type.indexing.Indexing;
import org.unidata.mdm.search.type.indexing.IndexingField;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Alexander Malyshev
 */
@Service("esAuditServiceStorageService")
public class ESAuditServiceStorageService implements AuditServiceStorageService {

    private static final String FIELD_VALUE_DELIMITER = "=>>";

    private final SearchService searchService;

    public ESAuditServiceStorageService(final SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public void write(Collection<AuditEventWriteContext> auditEventWriteContexts) {
        final IndexRequestContext indexRequestContext = IndexRequestContext.builder()
                .entity(AuditIndexType.INDEX_NAME)
                .index(auditEventWriteContexts.stream().map(this::createIndexing).collect(Collectors.toList()))
                .build();
        searchService.process(indexRequestContext);
    }

    private Indexing createIndexing(AuditEventWriteContext auditEventWriteContext) {
        List<IndexingField> fields = new ArrayList<>();
        fields.add(IndexingField.of(AuditIndexType.AUDIT, EnrichedAuditEvent.TYPE_FIELD, auditEventWriteContext.getType()));
        fields.add(IndexingField.ofStrings(
                AuditIndexType.AUDIT,
                EnrichedAuditEvent.PARAMETERS_FIELD,
                toIndexStrings(auditEventWriteContext.getParameters())
        ));
        fields.add(IndexingField.of(AuditIndexType.AUDIT, EnrichedAuditEvent.SUCCESS_FIELD, auditEventWriteContext.isSuccess()));
        fields.add(IndexingField.of(AuditIndexType.AUDIT, EnrichedAuditEvent.LOGIN_FIELD, auditEventWriteContext.getUserLogin()));
        fields.add(IndexingField.of(AuditIndexType.AUDIT, EnrichedAuditEvent.CLIENT_IP_FIELD, auditEventWriteContext.getClientIp()));
        fields.add(IndexingField.of(AuditIndexType.AUDIT, EnrichedAuditEvent.SERVER_IP_FIELD, auditEventWriteContext.getServerIp()));
        fields.add(IndexingField.of(AuditIndexType.AUDIT, EnrichedAuditEvent.ENDPOINT_FIELD, auditEventWriteContext.getEndpoint()));
        fields.add(IndexingField.of(AuditIndexType.AUDIT, EnrichedAuditEvent.WHEN_HAPPENED_FIELD, auditEventWriteContext.getWhenHappened()));
        return new Indexing(AuditIndexType.AUDIT, null).withFields(fields);
    }

    private Collection<String> toIndexStrings(Map<String, Object> parameters) {
        return parameters.entrySet().stream()
                .map(e -> String.format("%s%s%s", e.getKey(), FIELD_VALUE_DELIMITER, e.getValue()))
                .collect(Collectors.toList());
    }
}

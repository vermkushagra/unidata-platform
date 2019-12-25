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

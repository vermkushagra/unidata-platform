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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Alexander Malyshev
 */
@Service
public class ESAuditServiceStorageService implements AuditServiceStorageService {

    private static final String FIELD_VALUE_DELIMITER = "=>>";

    private final SearchService searchService;

    public ESAuditServiceStorageService(final SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public String id() {
        return "es";
    }

    @Override
    public void write(AuditEventWriteContext auditEventWriteContext) {
        final EnrichedAuditEvent enricheedAuditEvent = auditEventWriteContext.getEnhancedAuditEvent();
        List<IndexingField> fields = new ArrayList<>();
        fields.add(IndexingField.of(AuditIndexType.AUDIT, EnrichedAuditEvent.TYPE_FIELD, enricheedAuditEvent.type()));
        fields.add(IndexingField.ofStrings(
                AuditIndexType.AUDIT,
                EnrichedAuditEvent.PARAMETERS_FIELD,
                toIndexStrings(enricheedAuditEvent.parameters())
        ));
        fields.add(IndexingField.of(AuditIndexType.AUDIT, EnrichedAuditEvent.SUCCESS_FIELD, enricheedAuditEvent.success()));
        fields.add(IndexingField.of(AuditIndexType.AUDIT, EnrichedAuditEvent.LOGIN_FIELD, enricheedAuditEvent.getLogin()));
        fields.add(IndexingField.of(AuditIndexType.AUDIT, EnrichedAuditEvent.CLIENT_IP_FIELD, enricheedAuditEvent.getClientIp()));
        fields.add(IndexingField.of(AuditIndexType.AUDIT, EnrichedAuditEvent.SERVER_IP_FIELD, enricheedAuditEvent.getServerIp()));
        fields.add(IndexingField.of(AuditIndexType.AUDIT, EnrichedAuditEvent.WHEN_HAPPENED_FIELD, enricheedAuditEvent.getWhenwHappened()));
        final IndexRequestContext indexRequestContext = IndexRequestContext.builder()
                .storageId(auditEventWriteContext.getCurrentUserStorageId())
                .entity(AuditIndexType.INDEX_NAME)
                .index(new Indexing(AuditIndexType.AUDIT, null).withFields(fields))
                .build();
        searchService.process(indexRequestContext);
    }

    private Collection<String> toIndexStrings(Map<String, String> parameters) {
        return parameters.entrySet().stream()
                .map(e -> String.format("%s%s%s", e.getKey(), FIELD_VALUE_DELIMITER, e.getValue()))
                .collect(Collectors.toList());
    }
}

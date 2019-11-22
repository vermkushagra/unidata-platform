package org.unidata.mdm.core.service.impl;

import org.springframework.stereotype.Service;
import org.unidata.mdm.core.context.AuditEventWriteContext;
import org.unidata.mdm.core.dto.EnhancedAuditEvent;
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
        final EnhancedAuditEvent enhancedAuditEvent = auditEventWriteContext.getEnhancedAuditEvent();
        List<IndexingField> fields = new ArrayList<>();
        fields.add(IndexingField.of(AuditIndexType.AUDIT, EnhancedAuditEvent.TYPE_FIELD, enhancedAuditEvent.type()));
        fields.add(IndexingField.ofStrings(
                AuditIndexType.AUDIT,
                EnhancedAuditEvent.PARAMETERS_FIELD,
                toIndexStrings(enhancedAuditEvent.parameters())
        ));
        fields.add(IndexingField.of(AuditIndexType.AUDIT, EnhancedAuditEvent.SUCCESS_FIELD, enhancedAuditEvent.success()));
        fields.add(IndexingField.of(AuditIndexType.AUDIT, EnhancedAuditEvent.LOGIN_FIELD, enhancedAuditEvent.getLogin()));
        fields.add(IndexingField.of(AuditIndexType.AUDIT, EnhancedAuditEvent.CLIENT_IP_FIELD, enhancedAuditEvent.getClientIp()));
        fields.add(IndexingField.of(AuditIndexType.AUDIT, EnhancedAuditEvent.SERVER_IP_FIELD, enhancedAuditEvent.getServerIp()));
        fields.add(IndexingField.of(AuditIndexType.AUDIT, EnhancedAuditEvent.WHEN_HAPPENED_FIELD, enhancedAuditEvent.getWhenwHappened()));
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

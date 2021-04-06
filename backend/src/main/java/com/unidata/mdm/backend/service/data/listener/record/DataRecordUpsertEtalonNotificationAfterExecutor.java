/**
 *
 */
package com.unidata.mdm.backend.service.data.listener.record;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.unidata.mdm.backend.service.notification.NotificationUtils.createEtalonUpsertNotification;

import com.google.common.base.Optional;
import com.unidata.mdm.api.UnidataMessageDef;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.CodeLinkValue;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.configuration.application.ConfigurationUpdatesConsumer;
import com.unidata.mdm.backend.configuration.application.UnidataConfigurationProperty;
import com.unidata.mdm.backend.dao.DataRecordsDao;
import com.unidata.mdm.backend.service.data.listener.AbstractExternalNotificationExecutor;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.notification.ProcessedAction;
import com.unidata.mdm.data.ExternalSourceId;
import org.apache.commons.lang3.tuple.Pair;
import reactor.core.publisher.Flux;

/**
 * @author Mikhail Mikhailov Upsert etalon notification executor.
 */
public class DataRecordUpsertEtalonNotificationAfterExecutor
        extends AbstractExternalNotificationExecutor<UpsertRequestContext>
        implements DataRecordAfterExecutor<UpsertRequestContext>, ConfigurationUpdatesConsumer {

    private final AtomicBoolean addExternalIdsToAttributes = new AtomicBoolean(
            (Boolean) UnidataConfigurationProperty.UNIDATA_NOTIFICATION_ADD_EXTERNAL_ID_TO_ATTRIBUTES.getDefaultValue().get()
    );

    private final DataRecordsDao dataRecordsDao;

    /**
     * Constructor.
     * @param dataRecordsDao
     */
    public DataRecordUpsertEtalonNotificationAfterExecutor(final DataRecordsDao dataRecordsDao) {
        super();
        this.dataRecordsDao = dataRecordsDao;
    }



    /**
     * {@inheritDoc}
     */
    @Override
    protected UnidataMessageDef createMessage(UpsertRequestContext ctx) {

        EtalonRecord etalon = ctx.getFromStorage(StorageId.DATA_UPSERT_ETALON_RECORD);
        UpsertAction action = ctx.getFromStorage(StorageId.DATA_UPSERT_EXACT_ACTION);

        if (etalon != null
                && etalon.getInfoSection().getApproval() == ApprovalState.APPROVED
                && action != UpsertAction.NO_ACTION) {
            RecordKeys keys = ctx.keys();
            return createEtalonUpsertNotification(
                    etalon,
                    keys.getOriginKey(),
                    action,
                    keys.getSupplementaryKeys(),
                    ctx.getOperationId(),
                    addExternalIdsToAttributes.get() ? findExternalIds(etalon) : Collections.emptyMap()
            );
        }

        return null;
    }

    private Map<String, List<ExternalSourceId>> findExternalIds(EtalonRecord etalon) {
        return dataRecordsDao.findAllActiveOriginsForEtlaons(
                etalon.getAllAttributes().stream()
                        .filter(attribute -> attribute instanceof CodeLinkValue)
                        .map(CodeLinkValue.class::cast)
                        .filter(CodeLinkValue::hasLinkEtalonId)
                        .map(CodeLinkValue::getLinkEtalonId)
                        .collect(Collectors.toList())
        ).entrySet().stream()
                .map(record -> Pair.of(
                        record.getKey(),
                        record.getValue().stream()
                                .map(originPO ->
                                        new ExternalSourceId()
                                                .withExternalId(originPO.getExternalId())
                                                .withSourceSystem(originPO.getSourceSystem())
                                )
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    @Override
    protected ProcessedAction getProcessedAction() {
        return ProcessedAction.UPSERT_ETALON;
    }

    @Override
    protected RecordKeys getRecordKeys(UpsertRequestContext upsertRequestContext) {
        return upsertRequestContext.getFromStorage(StorageId.DATA_UPSERT_KEYS);
    }

    @Override
    public void subscribe(Flux<Map<String, Optional<? extends Serializable>>> updates) {
        final String key = UnidataConfigurationProperty.UNIDATA_NOTIFICATION_ADD_EXTERNAL_ID_TO_ATTRIBUTES.getKey();
        updates
                .filter(values -> values.containsKey(key) && values.get(key).isPresent())
                .map(values -> (Boolean) values.get(key).get())
                .subscribe(addExternalIdsToAttributes::set);
    }
}

package com.unidata.mdm.backend.service.data.classifiers;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.ClassifierIdentityContext;
import com.unidata.mdm.backend.common.context.CommonRequestContext;
import com.unidata.mdm.backend.common.context.UpsertClassifierDataRequestContext;
import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.dao.ClassifiersDAO;
import com.unidata.mdm.backend.po.ClassifierKeysPO;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;

/**
 * @author Mikhail Mikhailov
 * Common stuff.
 */
@Component
public class ClassifiersCommonComponent {
    /**
     * Classifier data DAO.
     */
    @Autowired
    private ClassifiersDAO classifierDAO;
    /**
     * MMS.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
    /**
     * CRC.
     */
    @Autowired
    private CommonRecordsComponent commonRecordsComponent;
    /**
     * Identifies target.
     * @param ctx the context
     * @return keys or null
     */
    public ClassifierKeys identify(ClassifierIdentityContext ctx) {

        MeasurementPoint.start();
        try {

            ClassifierKeysPO po = null;
            if (ctx.isClassifierEtalonKey()) {
                po = classifierDAO.loadClassifierKeysByClassifierEtalonId(
                        metaModelService.getAdminSourceSystem().getName(), ctx.getClassifierEtalonKey());
            } else if (ctx.isClassifierOriginKey()) {
                po = classifierDAO.loadClassifierKeysByClassifierOriginId(ctx.getClassifierOriginKey());
            } else if (ctx.isRecordKeyAndNodeId() || ctx.isRecordKeyAndNodeCode() || ctx.isRecordKeyAndNodeName()) {

                RecordKeys parentKeys = ctx.keys();
                if (parentKeys == null) {
                    parentKeys = commonRecordsComponent.identify(ctx);
                }

                if (parentKeys == null) {
                    return null;
                }

                // Skip pointless keys resolution upon initial load.
                // May quite have an impact on millions of records
                boolean isInitialLoad = ctx instanceof UpsertClassifierDataRequestContext
                        ? ((UpsertClassifierDataRequestContext) ctx).isInitialLoad()
                        : false;

                if (!isInitialLoad) {

                    if (Objects.nonNull(parentKeys.getOriginKey())) {
                        po = classifierDAO.loadClassifierKeysByRecordOriginIdAndClassifierName(
                                parentKeys.getOriginKey().getId(),
                                ctx.getClassifierName());
                    }

                    if (Objects.isNull(po) && Objects.nonNull(parentKeys.getEtalonKey())) {
                        po = classifierDAO.loadClassifierKeysByRecordEtalonIdAndClassifierName(
                                metaModelService.getAdminSourceSystem().getName(),
                                parentKeys.getEtalonKey().getId(),
                                ctx.getClassifierName());
                    }
                }
            }

            if (Objects.isNull(po)) {
                return null;
            }

            ClassifierKeys keys = ClassifierKeys.builder()
                    .record(RecordKeys.builder()
                        .entityName(po.getEtalonRecordName())
                        .etalonKey(EtalonKey.builder().id(po.getEtalonIdRecord()).build())
                        .etalonState(po.getEtalonRecordState())
                        .etalonStatus(po.getEtalonRecordStatus())
                        .originKey(po.getOriginIdRecord() == null
                            ? null
                            : OriginKey.builder()
                                .entityName(po.getOriginRecordName())
                                .externalId(po.getOriginRecordExternalId())
                                .id(po.getOriginIdRecord())
                                .sourceSystem(po.getOriginRecordSourceSystem())
                                .build())
                        .originStatus(po.getOriginRecordStatus())
                        .build())
                    .name(po.getEtalonName())
                    .nodeId(po.getOriginNodeId())
                    .etalonId(po.getEtalonId())
                    .etalonStatus(po.getEtalonStatus())
                    .etalonState(po.getEtalonState())
                    .originId(po.getOriginId())
                    .originSourceSystem(po.getOriginSourceSystem())
                    .originStatus(po.getOriginStatus())
                    .originRevision(po.getOriginRevision())
                    .build();

            ((CommonRequestContext) ctx).putToStorage(ctx.classifierKeysId(), keys);

            return keys;
        } finally {
            MeasurementPoint.stop();
        }
    }
}

package com.unidata.mdm.backend.service.data.common;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.common.context.CommonRequestContext;
import com.unidata.mdm.backend.common.context.JoinRequestContext;
import com.unidata.mdm.backend.common.context.RecordIdentityContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext.UpsertRequestContextBuilder;
import com.unidata.mdm.backend.common.dto.KeysJoinDTO;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.dao.DataRecordsDao;
import com.unidata.mdm.backend.po.EtalonRecordPO;
import com.unidata.mdm.backend.po.OriginRecordPO;
import com.unidata.mdm.backend.po.RecordKeysPO;
import com.unidata.mdm.backend.service.data.util.DataRecordUtils;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.IdUtils;

/**
 * @author Mikhail Mikhailov
 * Common data functionality.
 */
@Component
public class CommonRecordsComponent {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonRecordsComponent.class);

    /**
     * Data record DAO.
     */
    @Autowired
    private DataRecordsDao dataRecordsDao;

    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;

    /**
     * Constructor.
     */
    public CommonRecordsComponent() {
        super();
    }

    /**
     * Identifies a record returning full key.
     * @param key known origi key
     * @return full key
     */
    public RecordKeys identify(OriginKey key) {

        if (key == null) {
            return null;
        }

        RecordKeys keys = null;
        if (key.getExternalId() != null
         && key.getEntityName() != null
         && key.getSourceSystem() != null) {

            List<RecordKeysPO> pos = dataRecordsDao.loadRecordKeysByExternalId(key.getExternalId(),
                    key.getSourceSystem(), key.getEntityName());

            Predicate<RecordKeysPO> byExternalId = po ->
                    StringUtils.equals(key.getExternalId(), po.getOriginExternalId())
                 && StringUtils.equals(key.getEntityName(), po.getEtalonName())
                 && StringUtils.equals(key.getSourceSystem(), po.getOriginSourceSystem());

            keys = pos2keys(pos, byExternalId);
        }

        if (keys == null && key.getId() != null) {

            List<RecordKeysPO> pos = dataRecordsDao.loadRecordKeysByOriginId(key.getId());

            Predicate<RecordKeysPO> byOriginId = po -> StringUtils.equals(po.getOriginId(), key.getId());

            keys = pos2keys(pos, byOriginId);
        }

        return keys;
    }

    /**
     * Identifies a record returning full key.
     * @param key known etalon key
     * @return full key
     */
    public RecordKeys identify(EtalonKey key) {

        if (key == null) {
            return null;
        }

        RecordKeys keys = null;
        if (key.getId() != null) {
            String adminSourceSystem = metaModelService.getAdminSourceSystem().getName();
            List<RecordKeysPO> pos = dataRecordsDao.loadRecordKeysByEtalonId(key.getId());

            Predicate<RecordKeysPO> bySource = po -> StringUtils.equals(po.getOriginSourceSystem(), adminSourceSystem)
                    && (po.isEnriched() == null || !po.isEnriched());

            keys = pos2keys(pos, bySource);
        }

        return keys;
    }

    /**
     * Identifies a record returning full key.
     * @param key known etalon key
     * @return full key
     */
    public RecordKeys identify(long gsn) {

        String adminSourceSystem = metaModelService.getAdminSourceSystem().getName();
        List<RecordKeysPO> pos = dataRecordsDao.loadRecordKeysByGSN(gsn);

        Predicate<RecordKeysPO> bySource = po -> StringUtils.equals(po.getOriginSourceSystem(), adminSourceSystem)
                && (po.isEnriched() == null || !po.isEnriched());

        return pos2keys(pos, bySource);
    }

    /**
     * Identifies a data record.
     * @param ctx the context
     * @return pair of keys
     */
    public RecordKeys identify(RecordIdentityContext ctx) {

        RecordKeys keys = null;
        if (ctx.isOriginExternalId()) {

            List<RecordKeysPO> pos = dataRecordsDao.loadRecordKeysByExternalId(ctx.getExternalId(),
                    ctx.getSourceSystem(), ctx.getEntityName());

            Predicate<RecordKeysPO> byExternalId = po ->
                    StringUtils.equals(ctx.getExternalId(), po.getOriginExternalId())
                            && StringUtils.equals(ctx.getEntityName(), po.getEtalonName())
                            && StringUtils.equals(ctx.getSourceSystem(), po.getOriginSourceSystem());

            keys = pos2keys(pos, byExternalId);
        }

        if (keys == null && ctx.isOriginRecordKey()) {

            List<RecordKeysPO> pos = dataRecordsDao.loadRecordKeysByOriginId(ctx.getOriginKey());

            Predicate<RecordKeysPO> byOriginId = po -> StringUtils.equals(po.getOriginId(), ctx.getOriginKey());

            keys = pos2keys(pos, byOriginId);

        }

        if (keys == null && ctx.isEtalonRecordKey()) {
            String adminSourceSystem = metaModelService.getAdminSourceSystem().getName();
            List<RecordKeysPO> pos = dataRecordsDao.loadRecordKeysByEtalonId(ctx.getEtalonKey());

            Predicate<RecordKeysPO> bySource = po -> StringUtils.equals(po.getOriginSourceSystem(), adminSourceSystem)
                    && (po.isEnriched() == null || !po.isEnriched());

            keys = pos2keys(pos, bySource);
        }

        if (keys == null && ctx.isEnrichmentKey()) {

            List<RecordKeysPO> pos = dataRecordsDao.loadRecordKeysByEtalonId(ctx.getEtalonKey());

            Predicate<RecordKeysPO> byExternalId = po ->
                    StringUtils.equals(ctx.getExternalId(), po.getOriginExternalId())
                            && StringUtils.equals(ctx.getEntityName(), po.getEtalonName())
                            && StringUtils.equals(ctx.getSourceSystem(), po.getOriginSourceSystem())
                            && (po.isEnriched() != null && po.isEnriched());

            keys = pos2keys(pos, byExternalId);
        }

        if (keys == null && ctx.getGsn() != null) {
            keys = identify(ctx.getGsn());
        }

        if (keys != null) {
            ((CommonRequestContext) ctx).putToStorage(ctx.keysId(), keys);
        }

        return keys;
    }

    /**
     * Converts PO to keys object.
     * @param pos the collection of PO
     * @return key object
     */
    private RecordKeys pos2keys(List<RecordKeysPO> pos, Predicate<RecordKeysPO> mainOriginPredicate) {

        if (pos.isEmpty()) {
            return null;
        }

        RecordKeysPO anyPo = pos.get(0);
        RecordKeysPO requestedPo = pos.stream().filter(mainOriginPredicate).findAny().orElse(null);

        EtalonKey etalonKey = EtalonKey.builder()
                .id(anyPo.getEtalonId())
                .gsn(anyPo.getEtalonGsn())
                .status(anyPo.getEtalonStatus())
                .build();

        OriginKey originKey = toOriginKey(requestedPo);
        List<OriginKey> supplementaryKeys = pos.stream()
                                               .map(this::toOriginKey)
                                               .filter(Objects::nonNull)
                                               .collect(Collectors.toList());
        return RecordKeys.builder()
                         .etalonKey(etalonKey)
                         .originKey(originKey)
                         .supplementaryKeys(supplementaryKeys)
                         .entityName(anyPo.getEtalonName())
                         .etalonStatus(anyPo.getEtalonStatus())
                         .originStatus(requestedPo == null ? null : requestedPo.getOriginStatus())
                         .etalonState(anyPo.getEtalonState())
                         .published(originKey != null && originKey.hasApprovedRevisions()
                             ? true
                             : supplementaryKeys.stream().anyMatch(OriginKey::hasApprovedRevisions))
                         .build();
    }

    private OriginKey toOriginKey(RecordKeysPO po) {

        if (po == null || StringUtils.isBlank(po.getOriginId())) {
            return null;
        }

        return OriginKey.builder()
                        .id(po.getOriginId())
                        .externalId(po.getOriginExternalId())
                        .entityName(po.getOriginName())
                        .sourceSystem(po.getOriginSourceSystem())
                        .enrichment(po.isEnriched() == null ?  false : po.isEnriched())
                        .approvedRevisions(po.hasApprovedRevisions() == null ? false : po.hasApprovedRevisions())
                        .gsn(po.getOriginGsn())
                        .revision(po.getOriginRevision())
                        .status(po.getOriginStatus())
                        .build();
    }

    /**
     * Creates a system origin record.
     *
     * @param etalonId the etalon ID
     * @param entityName the entity name
     * @return new key
     */
    public OriginKey createSystemOriginRecord(String etalonId, String entityName) {

        String name = entityName;
        if (name == null) {
            EtalonRecordPO epo = dataRecordsDao.loadEtalonRecord(etalonId, false, false);
            name = epo.getName();
        }

        String adminSourceSystem = metaModelService.getAdminSourceSystem().getName();
        UpsertRequestContext uCtx = new UpsertRequestContextBuilder()
                .sourceSystem(adminSourceSystem)
                .entityName(name)
                .build();

        OriginRecordPO po = DataRecordUtils.createSystemOriginRecordPO(uCtx, etalonId);
        dataRecordsDao.upsertOriginRecords(Collections.singletonList(po), true);

        return OriginKey.builder()
                .entityName(name)
                .externalId(po.getExternalId())
                .sourceSystem(adminSourceSystem)
                .id(po.getId())
                .build();
    }

    /**
     * Changes etalon state.
     * @param etalonId the etalon id
     * @param state the state
     * @return true, if successful, false otherwise
     */
    @Transactional
    public boolean changeApproval(String etalonId, ApprovalState state) {
        return dataRecordsDao.changeEtalonApproval(etalonId, state);
    }

    /**
     * Possibly reset keys.
     * @param ctx the context
     * @param storageKey storage key
     * @param newState the new state
     * @return new keys
     */
    public RecordKeys possiblyResetApprovalState(CommonRequestContext ctx, StorageId storageKey, ApprovalState newState) {

        RecordKeys keys = ctx.getFromStorage(storageKey);
        boolean resetState = keys.getEtalonState() != newState;
        if (resetState) {

            RecordKeys newKeys = RecordKeys.builder(keys)
                    .etalonState(newState)
                    .build();

            ctx.putToStorage(storageKey, newKeys);
            return newKeys;
        }

        return keys;
    }

    /**
     * Does etalon draft cleanup.
     * @param etalonId the etalon id
     * @return
     */
    public boolean cleanupEtalonStateDrafts(String etalonId) {
        return dataRecordsDao.cleanupEtalonStateDrafts(etalonId);
    }

    /**
     * Puts draft state for an etalon.
     * @param etalonId
     * @param status
     * @param user
     * @return
     */
    public boolean putEtalonStateDraft(String etalonId, RecordStatus status, String user) {
        return dataRecordsDao.putEtalonStateDraft(etalonId, status, user);
    }

    /**
     * Joins a new external id to an existing etalon key.
     * @param ctx the context
     * @return result
     */
    public KeysJoinDTO join(JoinRequestContext ctx) {

        // 1. Check
        EtalonKey etalonKey = EtalonKey.builder().id(ctx.getEtalonKey()).build();
        RecordKeys existingByEtalonId = identify(etalonKey);
        if (Objects.isNull(existingByEtalonId)) {
            final String message = "External ID can not be joined. Etalon ID not found.";
            LOGGER.warn(message);
            throw new BusinessException(message, ExceptionId.EX_DATA_JOIN_ETALON_ID_NOT_FOUND);
        }

        if (StringUtils.isBlank(ctx.getExternalId()) || StringUtils.isBlank(ctx.getSourceSystem()) || StringUtils.isBlank(ctx.getEntityName())) {
            final String message = "External ID can not be joined. Invalid input.";
            LOGGER.warn(message);
            throw new BusinessException(message, ExceptionId.EX_DATA_JOIN_INVALID_INPUT);
        }

        if (!StringUtils.equals(existingByEtalonId.getEntityName(), ctx.getEntityName())) {
            final String message = "External ID can not be joined. Target register and the supplied one do not match.";
            LOGGER.warn(message);
            throw new BusinessException(message, ExceptionId.EX_DATA_JOIN_TARGET_REGISTER_DONT_MATCH);
        }

        for (OriginKey ok : existingByEtalonId.getNotEnrichedSupplementaryKeys()) {
            if (ok.getEntityName().equals(ctx.getEntityName())
             && ok.getExternalId().equals(ctx.getExternalId())
             && ok.getSourceSystem().equals(ctx.getSourceSystem())) {
                final String message = "External ID can not be joined. The key is already defined for the target.";
                LOGGER.warn(message);
                throw new BusinessException(message, ExceptionId.EX_DATA_JOIN_KEY_IS_ALREADY_DEFINED_IN_TARGET);
            }
        }

        RecordKeys existingByExternalId = identify(OriginKey.builder()
                .entityName(ctx.getEntityName())
                .externalId(ctx.getExternalId())
                .sourceSystem(ctx.getSourceSystem())
                .build());
        if (Objects.nonNull(existingByExternalId)) {
            final String message = "External ID can not be joined. The key is already used by another record.";
            LOGGER.warn(message);
            throw new BusinessException(message, ExceptionId.EX_DATA_JOIN_KEY_IS_ALREADY_USED_BY_ANOTHER);
        }

        // 2. Join
        OriginRecordPO result = new OriginRecordPO();
        result.setId(IdUtils.v1String());
        result.setEtalonId(existingByEtalonId.getEtalonKey().getId());
        result.setExternalId(ctx.getExternalId());
        result.setSourceSystem(ctx.getSourceSystem());
        result.setName(ctx.getEntityName());
        result.setEnrichment(false);
        result.setStatus(RecordStatus.ACTIVE);
        result.setCreateDate(new Date(System.currentTimeMillis()));
        result.setCreatedBy(SecurityUtils.getCurrentUserName());
        result.setVersion(1);

        dataRecordsDao.upsertOriginRecords(Collections.singletonList(result), true);

        // 3. Re-Fetch keys
        RecordKeys refetched = identify(etalonKey);

        return new KeysJoinDTO(
                Objects.nonNull(refetched.findByExternalId(ctx.getExternalId(), ctx.getEntityName(), ctx.getSourceSystem())),
                refetched);
    }

    /**
     * Tells, whether all origins of the record are already inactive.
     * @param keys the keys to check
     * @return true, if all inactive, false otherwise
     */
    public boolean allOriginsAlreadyInactive(RecordKeys keys) {
        return keys.getSupplementaryKeys()
                .stream()
                .allMatch(supplementaryKey -> RecordStatus.INACTIVE == supplementaryKey.getStatus() ||
                        supplementaryKey.getRevision() == 0 ||
                        supplementaryKey.getId().equals(keys.getOriginKey().getId()));
    }
}

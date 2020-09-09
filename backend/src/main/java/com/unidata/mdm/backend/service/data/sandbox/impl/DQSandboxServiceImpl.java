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

package com.unidata.mdm.backend.service.data.sandbox.impl;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.common.configuration.DumpTargetFormat;
import com.unidata.mdm.backend.common.configuration.PlatformConfiguration;
import com.unidata.mdm.backend.common.context.DataQualityContext;
import com.unidata.mdm.backend.common.context.GetMultipleRequestContext;
import com.unidata.mdm.backend.common.context.RunDQRulesContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.data.CalculableHolder;
import com.unidata.mdm.backend.common.data.ModificationBox;
import com.unidata.mdm.backend.common.dq.DataQualityExecutionMode;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.common.dto.data.model.GetEntityDTO;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.service.DataQualityService;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.common.service.MetaDraftServiceExt;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.DataShift;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.EtalonRecordInfoSection;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.OriginRecordInfoSection;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.EtalonRecordImpl;
import com.unidata.mdm.backend.common.types.impl.OriginRecordImpl;
import com.unidata.mdm.backend.dao.DQSandboxDao;
import com.unidata.mdm.backend.po.SandboxRecordPO;
import com.unidata.mdm.backend.service.cleanse.DQUtils;
import com.unidata.mdm.backend.service.data.sandbox.DQSandboxService;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.DumpUtils;
import com.unidata.mdm.backend.util.IdUtils;
import com.unidata.mdm.meta.AbstractEntityDef;
import com.unidata.mdm.meta.DQRuleDef;

@Service
public class DQSandboxServiceImpl implements DQSandboxService {

    private final PlatformConfiguration platformConfiguration;

    private final DQSandboxDao dqSandboxDao;

    private final DataQualityService dataQualityService;

    private final MetaDraftServiceExt metaDraftService;

    private final DataRecordsService dataRecordsService;

    private final MetaModelService metaModelService;

    @Autowired
    public DQSandboxServiceImpl(
            final PlatformConfiguration platformConfiguration,
            final DQSandboxDao dqSandboxDao,
            final DataQualityService dataQualityService,
            final MetaDraftServiceExt metaDraftService,
            final DataRecordsService dataRecordsService,
            final MetaModelService metaModelService
    ) {
        this.platformConfiguration = platformConfiguration;
        this.dqSandboxDao = dqSandboxDao;
        this.dataQualityService = dataQualityService;
        this.metaDraftService = metaDraftService;
        this.dataRecordsService = dataRecordsService;
        this.metaModelService = metaModelService;
    }

    @Transactional
    @Override
    public EtalonRecord upsert(final EtalonRecord etalonRecord) {
        final byte[] data = targetFromatActionSelector(
                () -> DumpUtils.dumpOriginRecordToJaxb(etalonRecord).getBytes(),
                () -> DumpUtils.dumpToProtostuff(etalonRecord)
        );
        final EtalonRecordInfoSection infoSection = etalonRecord.getInfoSection();
        final Long recordId =
                infoSection.getEtalonKey().getId() != null ? Long.valueOf(infoSection.getEtalonKey().getId()) : null;
        final long id = dqSandboxDao.save(new SandboxRecordPO(recordId, infoSection.getEntityName(), data));
        return updateEtalonKey(id, etalonRecord);
    }

    @Transactional
    @Override
    public EtalonRecord findRecordById(long recordId) {
        final SandboxRecordPO sandboxRecord = dqSandboxDao.findRecordById(recordId);
        return toEtalonRecord(sandboxRecord);
    }

    @Transactional
    @Override
    public void deleteRecords(final List<Long> recordsIds, final String entityName) {
        if (CollectionUtils.isNotEmpty(recordsIds)) {
            dqSandboxDao.deleteByIds(recordsIds);
        }
        else if (StringUtils.isNoneBlank(entityName)) {
            dqSandboxDao.deleteByEntityName(entityName);
        }
    }

    @Transactional
    @Override
    public SearchResultDTO searchRecords(final SearchRequestContext searchRequestContext) {
        final String entityName = searchRequestContext.getEntity();
        final List<SandboxRecordPO> sandboxRecordPOS = dqSandboxDao.find(
                entityName,
                searchRequestContext.getPage(),
                searchRequestContext.getCount()
        );
        final SearchResultDTO searchResultDTO = new SearchResultDTO();
        searchResultDTO.setTotalCount(dqSandboxDao.count(entityName));
        searchResultDTO.setHits(
                sandboxRecordPOS.stream()
                        .map(sr -> extractSearchHit(sr, searchRequestContext.getReturnFields()))
                        .collect(Collectors.toList())
        );
        return searchResultDTO;
    }

    private SearchResultHitDTO extractSearchHit(final SandboxRecordPO sandboxRecordPO, final List<String> returnFields) {
        final EtalonRecord etalonRecord = toEtalonRecord(sandboxRecordPO);
        final Map<String, SearchResultHitFieldDTO> hits = CollectionUtils.isNotEmpty(returnFields) ?
                returnFields.stream()
                        .map(field -> Pair.of(field, toHitField(etalonRecord, field)))
                        .collect(Collectors.toMap(Pair::getKey, Pair::getValue)) :
                Collections.emptyMap();
        return new SearchResultHitDTO(
                String.valueOf(sandboxRecordPO.getId()),
                String.valueOf(sandboxRecordPO.getId()),
                .0f,
                hits,
                null
        );
    }

    private SearchResultHitFieldDTO toHitField(final EtalonRecord etalonRecord, final String field) {
        final boolean compoundPath = ModelUtils.isCompoundPath(field);
        return new SearchResultHitFieldDTO(
                field,
                compoundPath ?
                        extractCompoundValues(etalonRecord.getAttributeRecursive(field)) :
                        extractSigleValue(etalonRecord.getAttribute(field))
        );
    }

    private List<Object> extractCompoundValues(final Collection<Attribute> attributes) {
        return attributes.stream()
                .flatMap(attribute -> extractSigleValue(attribute).stream())
                .collect(Collectors.toList());
    }

    private List<Object> extractSigleValue(Attribute attribute) {
        if (attribute instanceof SimpleAttribute) {
            return Collections.singletonList(((SimpleAttribute<?>) attribute).getValue());
        }
        else if (attribute instanceof CodeAttribute) {
            return Collections.singletonList(((CodeAttribute<?>) attribute).getValue());
        }
        else if (attribute instanceof ArrayAttribute) {
            return ((ArrayAttribute<?>) attribute).toList();
        }
        return Collections.emptyList();
    }

    private <T> T targetFromatActionSelector(Supplier<T> onJaxb, Supplier<T> onProtostuff) {
        return platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB ? onJaxb.get() : onProtostuff.get();
    }

    private EtalonRecord toEtalonRecord(SandboxRecordPO sandboxRecord) {
        final byte[] data = sandboxRecord.getData();
        final DataRecord dataRecord = targetFromatActionSelector(
                () -> DumpUtils.restoreOriginRecordFromJaxb(new String(data, StandardCharsets.UTF_8)),
                () -> DumpUtils.restoreFromProtostuff(data)
        );
        final EtalonRecordImpl etalonRecord = new EtalonRecordImpl(dataRecord);
        final EtalonRecordInfoSection etalonRecordInfoSection = new EtalonRecordInfoSection();
        etalonRecordInfoSection.setEtalonKey(EtalonKey.builder().id(String.valueOf(sandboxRecord.getId())).build());
        etalonRecordInfoSection.setEntityName(sandboxRecord.getEntityName());
        etalonRecord.setInfoSection(etalonRecordInfoSection);
        return etalonRecord;
    }

    private EtalonRecord updateEtalonKey(final long id, final EtalonRecord etalonRecord) {
        etalonRecord.getInfoSection().setEtalonKey(EtalonKey.builder().id(String.valueOf(id)).build());
        return etalonRecord;
    }

    @Override
    public Map<EtalonRecord, List<DataQualityError>> runDataQualityRules(final RunDQRulesContext runDQRulesContext) {

        final List<String> etalonRecordsIds = runDQRulesContext.getEtalonRecordsIds();
        if (CollectionUtils.isEmpty(etalonRecordsIds) || CollectionUtils.isEmpty(runDQRulesContext.getRules())) {
            return Collections.emptyMap();
        }

        final String entityName = runDQRulesContext.getEntityName();
        List<EtalonRecord> etalonRecords = runDQRulesContext.isSandbox() ?
                findTestEtalonRecords(etalonRecordsIds) :
                findRealEtalonRecords(etalonRecordsIds, entityName);

        final Set<String> rulesNames = new HashSet<>(runDQRulesContext.getRules());

        AbstractEntityDef entity = metaDraftService.getLookupEntityById(entityName);
        Map<String, AttributeInfoHolder> attributes = null;
        if (Objects.isNull(entity)) {
            GetEntityDTO info = metaDraftService.getEntityById(entityName);
            if (Objects.nonNull(info)) {
                entity = info.getEntity();
                attributes = ModelUtils.createAttributesMap(entity, info.getRefs());
            }
        } else {
            attributes = ModelUtils.createAttributesMap(entity, Collections.emptyList());
        }

        final List<DQRuleDef> dqRuleDefs = Optional.ofNullable(entity)
                .map(AbstractEntityDef::getDataQualities)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(r -> rulesNames.contains(r.getName()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(dqRuleDefs)) {
            return Collections.emptyMap();
        }

        // Init UPath elements
        DQUtils.prepareMappings(entityName, dqRuleDefs, attributes);

        Date dt = new Date();
        return etalonRecords.stream()
                .map(etalonRecord -> {

                    OriginRecord origin = new OriginRecordImpl()
                            .withDataRecord(etalonRecord)
                            .withInfoSection(new OriginRecordInfoSection()
                                    .withApproval(ApprovalState.APPROVED)
                                    .withCreateDate(dt)
                                    .withCreatedBy(SecurityUtils.getCurrentUserName())
                                    .withMajor(platformConfiguration.getPlatformMajor())
                                    .withMinor(platformConfiguration.getPlatformMinor())
                                    .withOriginKey(OriginKey.builder()
                                            .entityName(entityName)
                                            .externalId(IdUtils.v1String())
                                            .sourceSystem(metaModelService.getAdminSourceSystem().getName())
                                            .status(RecordStatus.ACTIVE)
                                            .build())
                                    .withShift(DataShift.PRISTINE)
                                    .withStatus(RecordStatus.ACTIVE)
                                    .withUpdateDate(dt)
                                    .withUpdatedBy(SecurityUtils.getCurrentUserName())
                                    .withValidFrom(etalonRecord.getInfoSection().getValidFrom())
                                    .withValidTo(etalonRecord.getInfoSection().getValidTo()));

                    DataQualityContext dCtx = DataQualityContext.builder()
                            .entityName(entityName)
                            .rules(dqRuleDefs)
                            .validFrom(etalonRecord.getInfoSection().getValidFrom())
                            .validTo(etalonRecord.getInfoSection().getValidTo())
                            .sourceSystem(metaModelService.getAdminSourceSystem().getName())
                            .externalId(origin.getInfoSection().getOriginKey().getExternalId())
                            .modificationBox(ModificationBox.of(Collections.emptyList(), origin))
                            .executionMode(DataQualityExecutionMode.MODE_PREVIEW)
                            .build();

                    dataQualityService.apply(dCtx);

                    CalculableHolder<DataRecord> ch
                        = dCtx.getModificationBox().pop(ModificationBox.toBoxKey(origin.getInfoSection().getOriginKey()));

                    EtalonRecord result = new EtalonRecordImpl()
                            .withDataRecord(ch.getValue())
                            .withInfoSection(etalonRecord.getInfoSection());

                    return Pair.of(
                            result,
                            CollectionUtils.isNotEmpty(dCtx.getErrors()) ?
                                    dCtx.getErrors() : Collections.<DataQualityError>emptyList()
                    );
                })
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    private List<EtalonRecord> findRealEtalonRecords(final List<String> etalonRecordsIds, final String entityName) {
        final GetMultipleRequestContext multipleRequestContext = new GetMultipleRequestContext.GetMultipleRequestContextBuilder()
                .entityName(entityName)
                .etalonKeys(etalonRecordsIds)
                .build();
        return dataRecordsService.getRecords(multipleRequestContext).getEtalons();
    }

    private List<EtalonRecord> findTestEtalonRecords(final List<String> etalonRecordsIds) {
        return dqSandboxDao.findByRecordsIds(etalonRecordsIds.stream().map(Long::valueOf).collect(Collectors.toList()))
                .stream()
                .map(sr -> updateEtalonKey(sr.getId(), toEtalonRecord(sr)))
                .collect(Collectors.toList());
    }
}

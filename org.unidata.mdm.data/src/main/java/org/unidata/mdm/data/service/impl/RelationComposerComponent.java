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

package org.unidata.mdm.data.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.calculables.CalculableHolder;
import org.unidata.mdm.core.type.calculables.CompositionDriver;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.OperationType;
import org.unidata.mdm.core.type.model.EntityModelElement;
import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.core.util.PeriodIdUtils;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.po.data.RelationVistoryPO;
import org.unidata.mdm.data.po.keys.RecordOriginKeyPO;
import org.unidata.mdm.data.po.keys.RelationKeysPO;
import org.unidata.mdm.data.po.keys.RelationOriginKeyPO;
import org.unidata.mdm.data.type.calculables.AbstractDataCompositionDriver;
import org.unidata.mdm.data.type.calculables.DataBvrCaclulationInfo;
import org.unidata.mdm.data.type.calculables.DataBvtCaclulationInfo;
import org.unidata.mdm.data.type.calculables.impl.RelationRecordHolder;
import org.unidata.mdm.data.type.data.EtalonRelation;
import org.unidata.mdm.data.type.data.EtalonRelationInfoSection;
import org.unidata.mdm.data.type.data.OriginRelation;
import org.unidata.mdm.data.type.data.OriginRelationInfoSection;
import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.data.type.data.impl.EtalonRelationImpl;
import org.unidata.mdm.data.type.data.impl.OriginRelationImpl;
import org.unidata.mdm.data.type.keys.RecordEtalonKey;
import org.unidata.mdm.data.type.keys.RelationEtalonKey;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.data.type.keys.RelationOriginKey;
import org.unidata.mdm.data.type.timeline.RelationTimeline;
import org.unidata.mdm.data.util.StorageUtils;
import org.unidata.mdm.system.exception.PlatformFailureException;

/**
 * Relation composer methods.
 * @author Mikhail Mikhailov
 */
@Component("relationComposer")
public class RelationComposerComponent extends AbstractComposer {
    /**
     * Name of the type driver
     */
    public static final String DRIVER_NAME = "RELATION_COMPOSITION_DRIVER";
    /**
     * Relation composition driver.
     */
    public final CompositionDriver<OriginRelation, DataBvrCaclulationInfo<OriginRelation>, DataBvtCaclulationInfo<OriginRelation>> relationCompositionDriver
        = new AbstractDataCompositionDriver<OriginRelation, DataBvrCaclulationInfo<OriginRelation>, DataBvtCaclulationInfo<OriginRelation>>() {
        /**
         * {@inheritDoc}
         */
        @Override
        public String getName() {
            return DRIVER_NAME;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getDescription() {
            return "Relations data calculation type.";
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasActiveBVR(DataBvrCaclulationInfo<OriginRelation> info) {
            return super.composeDefaultBVR(info) != null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public OriginRelation toBVR(DataBvrCaclulationInfo<OriginRelation> info) {
            return super.composeDefaultBVR(info);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DataRecord toBVT(DataBvtCaclulationInfo<OriginRelation> info) {
            return super.composeDefaultBVT(info);
        }
    };
    /**
     * The record composer.
     */
    @Autowired
    private RecordComposerComponent recordComposer;
    /**
     * Constructor.
     */
    private RelationComposerComponent() {
        super();
    }
    /**
     * Checks versions list being active.
     * @param versions the list to check
     * @return true, if active, false otherwise
     */
    public boolean isActive(List<CalculableHolder<OriginRelation>> versions) {

        if (CollectionUtils.isEmpty(versions)) {
            return false;
        }

        DataBvrCaclulationInfo<OriginRelation> info = DataBvrCaclulationInfo.<OriginRelation>builder()
                .bvrMap(metaModelService.getReversedSourceSystems())
                .versions(versions)
                .build();

        return relationCompositionDriver.hasActiveBVR(info);
    }

    /**
     * Runs driver's BVR.
     * @param versions calculables
     * @param includeInactive honor inactivity
     * @param includeWinners include winners info
     * @return selected
     */
    public OriginRelation toBVR(List<CalculableHolder<OriginRelation>> versions, boolean includeInactive, boolean includeWinners) {

        if (CollectionUtils.isEmpty(versions)) {
            return null;
        }

        DataBvrCaclulationInfo<OriginRelation> info = DataBvrCaclulationInfo.<OriginRelation>builder()
                .bvrMap(metaModelService.getReversedSourceSystems())
                .versions(versions)
                .includeInactive(includeInactive)
                .build();

        return relationCompositionDriver.toBVR(info);
    }
    /**
     * Runs driver's BVT.
     * @param versions calculables
     * @param includeInactive honor inactivity
     * @param includeWinners include winners info
     * @return calculated
     */
    public DataRecord toBVT(List<CalculableHolder<OriginRelation>> versions, boolean includeInactive, boolean includeWinners) {

        if (CollectionUtils.isEmpty(versions)) {
            return null;
        }

        EntityModelElement element = ensureBvtMapElement(versions);
        DataBvtCaclulationInfo<OriginRelation> info = DataBvtCaclulationInfo.<OriginRelation>builder()
                .bvtMap(element.getBvt().getBvtMap())
                .attrsMap(element.getAttributes())
                .versions(versions)
                .includeInactive(includeInactive)
                .includeWinners(includeWinners)
                .build();

        return relationCompositionDriver.toBVT(info);
    }
    /**
     * Full etalon calculation.
     * Should be called on modified data.
     * Makes use of timestamp and originator in update(Date|edBy) fields.
     * @param keys the keys
     * @param interval the interval
     * @param updateDate update timestamp
     * @param updatedBy the updater, who modified the data
     */
    public void toEtalon(RelationKeys keys, TimeInterval<OriginRelation> interval, Date updateDate, String updatedBy) {

        Date from = interval.getValidFrom();
        Date to = interval.getValidTo();

        List<CalculableHolder<OriginRelation>> calculables = interval.toList();
        interval.setActive(isActive(calculables));
        interval.setPending(isPending(calculables));
        interval.setCalculationResult(toEtalon(keys, calculables, from, to, true, false, updateDate, updatedBy));
    }
    /**
     * Full etalon calculation.
     * @param keys
     * @param interval
     */
    public void toEtalon(RelationKeys keys, TimeInterval<OriginRelation> interval) {

        Date from = interval.getValidFrom();
        Date to = interval.getValidTo();

        List<CalculableHolder<OriginRelation>> calculables = interval.toList();
        interval.setActive(isActive(calculables));
        interval.setPending(isPending(calculables));
        interval.setCalculationResult(toEtalon(keys, calculables, from, to, true, false));
    }
    /**
     * Calculates etalon and creates info section.
     * @param keys current record keys
     * @param versions the data
     * @param from the from boundary
     * @param to the to boundary
     * @param includeInactive include inactive into calculations (used to show deleted periods)
     * @param includeWinners whether to include winners info  into result or not
     * @param updateDate the update date
     * @param updatedBy the update originator
     * @return etalon record
     */
    private EtalonRelation toEtalon(RelationKeys keys, List<CalculableHolder<OriginRelation>> versions,
            Date from, Date to, boolean includeInactive, boolean includeWinners, Date updateDate, String updatedBy) {

        DataRecord result = keys.getRelationType() == RelationType.CONTAINS
                ? toBVT(versions, includeInactive, includeWinners)
                : toBVR(versions, includeInactive, includeWinners);

        OriginRelation bvrSelected = toBVR(versions, false, false);
        OperationType operationType = bvrSelected == null ? null : bvrSelected.getInfoSection().getOperationType();

        EtalonRelationInfoSection is = new EtalonRelationInfoSection()
                .withRelationEtalonKey(keys.getEtalonKey().getId())
                .withRelationName(keys.getRelationName())
                .withRelationType(keys.getRelationType())
                .withPeriodId(PeriodIdUtils.ensureDateValue(to))
                .withValidFrom(from)
                .withValidTo(to)
                .withStatus(keys.getEtalonKey().getStatus())
                .withApproval(keys.getEtalonKey().getState())
                .withOperationType(operationType)
                .withCreateDate(keys.getCreateDate())
                .withUpdateDate(updateDate)
                .withCreatedBy(keys.getCreatedBy())
                .withUpdatedBy(updatedBy)
                .withFromEntityName(keys.getFromEntityName())
                .withFromEtalonKey(keys.getEtalonKey().getFrom())
                .withToEntityName(keys.getToEntityName())
                .withToEtalonKey(keys.getEtalonKey().getTo());

        return new EtalonRelationImpl()
                .withInfoSection(is)
                .withDataRecord(result);
    }
    /**
     * Calculates etalon and creates info section.
     * @param keys current record keys
     * @param versions the data
     * @param from the from boundary
     * @param to the to boundary
     * @param includeInactive include inactive into calculations (used to show deleted periods)
     * @param includeWinners whether to include winners info  into result or not
     * @return etalon record
     */
    public EtalonRelation toEtalon(RelationKeys keys, List<CalculableHolder<OriginRelation>> versions,
            Date from, Date to, boolean includeInactive, boolean includeWinners) {
        return toEtalon(keys, versions, from, to, includeInactive, includeWinners, keys.getUpdateDate(), keys.getUpdatedBy());
    }
    /**
     * Creates rel. timeline input.
     * @param k the keys
     * @param pos persistent objects
     * @return timeline input
     */
    public Timeline<OriginRelation> toRelationTimeline(RelationKeys k, Collection<RelationVistoryPO> pos) {

        List<CalculableHolder<OriginRelation>> calculables = Collections.emptyList();
        if (CollectionUtils.isNotEmpty(pos)) {

            calculables = new ArrayList<>(pos.size());
            for (RelationVistoryPO po : pos) {

                if (po != null) {

                    RelationOriginKey selected = k.findByOriginId(po.getOriginId());
                    if (Objects.isNull(selected)) {
                        continue;
                    }

                    OriginRelationImpl origin = new OriginRelationImpl()
                            .withDataRecord(po.getData())
                            .withInfoSection(new OriginRelationInfoSection()
                                    .withValidFrom(po.getValidFrom())
                                    .withValidTo(po.getValidTo())
                                    .withCreateDate(selected.getCreateDate())
                                    .withUpdateDate(po.getCreateDate())
                                    .withCreatedBy(selected.getCreatedBy())
                                    .withUpdatedBy(po.getCreatedBy())
                                    .withRevision(po.getRevision())
                                    .withStatus(po.getStatus())
                                    .withApproval(po.getApproval())
                                    .withOperationType(po.getOperationType())
                                    .withShift(po.getShift())
                                    .withMajor(po.getMajor())
                                    .withMinor(po.getMinor())
                                    .withFromEntityName(k.getFromEntityName())
                                    .withToEntityName(k.getToEntityName())
                                    .withRelationName(k.getRelationName())
                                    .withRelationOriginKey(selected)
                                    .withRelationType(k.getRelationType()));

                    calculables.add(new RelationRecordHolder(origin));
                }
            }
        }

        return new RelationTimeline(k, calculables);
    }
    /**
     * Converts PO to keys object.
     * @param po the key PO
     * @param op test predicate for the main origin
     * @return key object
     */
    public RelationKeys toRelationKeys(RelationKeysPO po, BiPredicate<RelationKeysPO, RelationOriginKeyPO> op) {

        // Key functions return a record with all fields nullified or unset on no keys
        // Check this and return null as appropriate.
        if (po == null || po.getId() == null || po.getFromKeys() == null || po.getToKeys() == null || op == null) {
            return null;
        }

        RelationEtalonKey etalonKey = RelationEtalonKey.builder()
                .from(RecordEtalonKey.builder()
                        .id(po.getFromKeys().getId())
                        .lsn(po.getFromKeys().getLsn())
                        .status(po.getFromKeys().getStatus())
                        .state(po.getFromKeys().getState())
                        .build())
                .to(RecordEtalonKey.builder()
                        .id(po.getToKeys().getId())
                        .lsn(po.getToKeys().getLsn())
                        .status(po.getToKeys().getStatus())
                        .state(po.getToKeys().getState())
                        .build())
                .id(po.getId())
                .lsn(po.getLsn())
                .state(po.getState())
                .status(po.getStatus())
                .build();

        RelationOriginKey originKey = null;
        List<RelationOriginKey> supplementaryKeys = new ArrayList<>(po.getOriginKeys().size());
        for (int i = 0; i < po.getOriginKeys().size(); i++) {

            RelationOriginKeyPO okpo = po.getOriginKeys().get(i);
            RelationOriginKey ok = toOriginKey(po, okpo);
            if (Objects.isNull(ok)) {
                continue;
            }

            if (originKey == null && op.test(po, okpo)) {
                originKey = ok;
            }

            supplementaryKeys.add(ok);
        }

        if (originKey == null) {
            throw new PlatformFailureException("No valid origin key found for relation key!",
                    DataExceptionIds.EX_DATA_RELATION_INVALID_KEYS);
        }

        return RelationKeys.builder()
                .etalonKey(etalonKey)
                .originKey(originKey)
                .supplementaryKeys(supplementaryKeys)
                .published(po.isApproved())
                .shard(po.getShard())
                .node(StorageUtils.node(po.getShard()))
                .fromEntityName(po.getFromKeys().getName())
                .toEntityName(po.getToKeys().getName())
                .relationName(po.getName())
                .relationType(po.getRelationType())
                .createDate(po.getCreateDate())
                .createdBy(po.getCreatedBy())
                .updateDate(po.getUpdateDate())
                .updatedBy(po.getUpdatedBy())
                .build();
    }
    /**
     * Converts origin po object.
     * @param record parent PO object
     * @param origin the origin to convert
     * @return converted key
     */
    public RelationOriginKey toOriginKey(RelationKeysPO record, RelationOriginKeyPO origin) {

        RelationType type = record.getRelationType();
        if (origin == null) {
            return null;
        }

        RecordOriginKeyPO from = record.getFromKeys().getOriginKeys().stream()
                .filter(fpo -> fpo.getId().equals(origin.getFromKey()))
                .findFirst()
                .orElse(null);

        RecordOriginKeyPO to = record.getToKeys().getOriginKeys().stream()
                .filter(tpo -> tpo.getId().equals(origin.getToKey()))
                .findFirst()
                .orElse(null);

        if ((Objects.isNull(from) && type != RelationType.CONTAINS) || Objects.isNull(to)) {
            return null;
        }

        return RelationOriginKey.builder()
                    .id(origin.getId().toString())
                    .initialOwner(origin.getInitialOwner())
                    .sourceSystem(origin.getSourceSystem())
                    .enrichment(BooleanUtils.toBoolean(origin.isEnrichment()))
                    .revision(origin.getRevision())
                    .status(origin.getStatus())
                    .from(recordComposer.toOriginKey(record.getFromKeys(), from))
                    .to(recordComposer.toOriginKey(record.getToKeys(), to))
                    .createDate(origin.getCreateDate())
                    .createdBy(origin.getCreatedBy())
                    .updateDate(origin.getUpdateDate())
                    .updatedBy(origin.getUpdatedBy())
                    .build();
    }
}

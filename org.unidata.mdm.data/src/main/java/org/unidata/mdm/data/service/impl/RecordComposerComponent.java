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
import org.unidata.mdm.core.type.model.EntityModelElement;
import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.core.util.PeriodIdUtils;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.po.data.RecordVistoryPO;
import org.unidata.mdm.data.po.keys.RecordKeysPO;
import org.unidata.mdm.data.po.keys.RecordOriginKeyPO;
import org.unidata.mdm.data.type.calculables.AbstractDataCompositionDriver;
import org.unidata.mdm.data.type.calculables.DataBvrCaclulationInfo;
import org.unidata.mdm.data.type.calculables.DataBvtCaclulationInfo;
import org.unidata.mdm.data.type.calculables.impl.DataRecordHolder;
import org.unidata.mdm.data.type.data.EtalonRecord;
import org.unidata.mdm.data.type.data.EtalonRecordInfoSection;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.data.OriginRecordInfoSection;
import org.unidata.mdm.data.type.data.impl.EtalonRecordImpl;
import org.unidata.mdm.data.type.data.impl.OriginRecordImpl;
import org.unidata.mdm.data.type.keys.RecordEtalonKey;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.type.keys.RecordOriginKey;
import org.unidata.mdm.data.type.timeline.RecordTimeline;
import org.unidata.mdm.data.util.StorageUtils;
import org.unidata.mdm.meta.service.impl.MeasuredAttributeValueConverter;
import org.unidata.mdm.system.exception.PlatformFailureException;

/**
 * @author Mikhail Mikhailov
 *         Record etalon composer.
 */
@Component("recordComposer")
public final class RecordComposerComponent extends AbstractComposer {
    /**
     * Name of the type driver
     */
    public static final String DRIVER_NAME = "RECORD_COMPOSITION_DRIVER";
    /**
     * Measured attribute converter.
     */
    @Autowired
    private MeasuredAttributeValueConverter measuredAttributeValueConverter;
    /**
     * Transformer chain.
     */
    @Autowired
    private TransformerChain transformerChain;
    /**
     * Type implementation reference.
     */
    public final CompositionDriver<OriginRecord, DataBvrCaclulationInfo<OriginRecord>, DataBvtCaclulationInfo<OriginRecord>> recordCompositionDriver
        = new AbstractDataCompositionDriver<OriginRecord, DataBvrCaclulationInfo<OriginRecord>, DataBvtCaclulationInfo<OriginRecord>>() {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasActiveBVR(DataBvrCaclulationInfo<OriginRecord> info) {
            return super.composeDefaultBVR(info) != null;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public OriginRecord toBVR(DataBvrCaclulationInfo<OriginRecord> info) {
            return super.composeDefaultBVR(info);
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public DataRecord toBVT(DataBvtCaclulationInfo<OriginRecord> info) {

            DataRecord result = super.composeDefaultBVT(info);
            if (result != null) {
                measuredAttributeValueConverter.enrichMeasuredAttributesByBase(result);
            }

            return result;
        }
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
            return "Records data calculation type.";
        }
    };
    /**
     * Constructor.
     */
    private RecordComposerComponent() {
        super();
    }
    /**
     * Checks versions list being active.
     * @param versions the list to check
     * @return true, if active, false otherwise
     */
    public boolean isActive(List<CalculableHolder<OriginRecord>> versions) {

        if (CollectionUtils.isEmpty(versions)) {
            return false;
        }

        DataBvrCaclulationInfo<OriginRecord> info = DataBvrCaclulationInfo.<OriginRecord>builder()
                .bvrMap(metaModelService.getReversedSourceSystems())
                .versions(versions)
                .build();

        return recordCompositionDriver.hasActiveBVR(info);
    }

    /**
     * Runs driver's BVR.
     * @param versions calculables
     * @param includeInactive honor inactivity
     * @param includeWinners include winners info
     * @return selected
     */
    public OriginRecord toBVR(List<CalculableHolder<OriginRecord>> versions, boolean includeInactive, boolean includeWinners) {

        if (CollectionUtils.isEmpty(versions)) {
            return null;
        }

        DataBvrCaclulationInfo<OriginRecord> info = DataBvrCaclulationInfo.<OriginRecord>builder()
                .bvrMap(metaModelService.getReversedSourceSystems())
                .versions(versions)
                .includeInactive(includeInactive)
                .build();

        return recordCompositionDriver.toBVR(info);
    }
    /**
     * Runs driver's BVT.
     * @param versions calculables
     * @param includeInactive honor inactivity
     * @param includeWinners include winners info
     * @return calculated
     */
    public DataRecord toBVT(List<CalculableHolder<OriginRecord>> versions, boolean includeInactive, boolean includeWinners) {

        if (CollectionUtils.isEmpty(versions)) {
            return null;
        }

        EntityModelElement element = ensureBvtMapElement(versions);
        DataBvtCaclulationInfo<OriginRecord> info = DataBvtCaclulationInfo.<OriginRecord>builder()
                .bvtMap(element.getBvt().getBvtMap())
                .attrsMap(element.getAttributes())
                .versions(versions)
                .includeInactive(includeInactive)
                .includeWinners(includeWinners)
                .build();

        return recordCompositionDriver.toBVT(info);
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
    public void toEtalon(RecordKeys keys, TimeInterval<OriginRecord> interval, Date updateDate, String updatedBy) {

        Date from = interval.getValidFrom();
        Date to = interval.getValidTo();

        List<CalculableHolder<OriginRecord>> recordCalculables = interval.toList();
        interval.setActive(isActive(recordCalculables));
        interval.setPending(isPending(recordCalculables));
        interval.setCalculationResult(toEtalon(keys, recordCalculables, from, to, true, false, updateDate, updatedBy));
    }
    /**
     * Full etalon calculation.
     * @param keys
     * @param interval
     */
    public void toEtalon(RecordKeys keys, TimeInterval<OriginRecord> interval) {

        Date from = interval.getValidFrom();
        Date to = interval.getValidTo();

        List<CalculableHolder<OriginRecord>> recordCalculables = interval.toList();
        interval.setActive(isActive(recordCalculables));
        interval.setPending(isPending(recordCalculables));
        interval.setCalculationResult(toEtalon(keys, recordCalculables, from, to, true, false));
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
    private EtalonRecord toEtalon(RecordKeys keys, List<CalculableHolder<OriginRecord>> versions,
            Date from, Date to, boolean includeInactive, boolean includeWinners, Date updateDate, String updatedBy) {

        DataRecord bvtResult = toBVT(versions, includeInactive, includeWinners);
        OriginRecord bvrResult = toBVR(versions, includeInactive, includeWinners);

        EtalonRecordInfoSection is = new EtalonRecordInfoSection()
                .withPeriodId(PeriodIdUtils.ensureDateValue(to))
                .withValidFrom(from)
                .withValidTo(to)
                .withCreateDate(keys.getCreateDate())
                .withUpdateDate(updateDate)
                .withCreatedBy(keys.getCreatedBy())
                .withUpdatedBy(updatedBy)
                .withEntityName(keys.getEntityName())
                .withStatus(keys.getEtalonKey().getStatus())
                .withApproval(keys.getEtalonKey().getState())
                .withOperationType(bvrResult == null ? null : bvrResult.getInfoSection().getOperationType())
                .withEtalonKey(keys.getEtalonKey());

        return new EtalonRecordImpl()
                .withInfoSection(is)
                .withDataRecord(bvtResult);
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
    public EtalonRecord toEtalon(RecordKeys keys, List<CalculableHolder<OriginRecord>> versions,
            Date from, Date to, boolean includeInactive, boolean includeWinners) {
        return toEtalon(keys, versions, from, to, includeInactive, includeWinners, keys.getUpdateDate(), keys.getUpdatedBy());
    }

    /**
     * Converts multiple timelines vistory records to timeline input.
     * @param k the keys
     * @return po the timeline input
     * @return timeline
     */
    public Timeline<OriginRecord> toRecordTimeline(RecordKeys k, Collection<RecordVistoryPO> pos) {

        List<CalculableHolder<OriginRecord>> calculables = Collections.emptyList();
        if (CollectionUtils.isNotEmpty(pos) && Objects.nonNull(k)) {

            calculables = new ArrayList<>(pos.size());
            for (RecordVistoryPO v : pos) {

                if (v != null) {

                    RecordOriginKey selected = k.findByOriginId(v.getOriginId());
                    if (Objects.isNull(selected)) {
                        continue;
                    }

                    OriginRecordImpl origin = new OriginRecordImpl()
                            .withDataRecord(v.getData())
                            .withInfoSection(new OriginRecordInfoSection()
                                    .withValidFrom(v.getValidFrom())
                                    .withValidTo(v.getValidTo())
                                    .withCreateDate(selected.getCreateDate())
                                    .withUpdateDate(v.getCreateDate())
                                    .withCreatedBy(selected.getCreatedBy())
                                    .withUpdatedBy(v.getCreatedBy())
                                    .withRevision(v.getRevision())
                                    .withStatus(v.getStatus())
                                    .withApproval(v.getApproval())
                                    .withOperationType(v.getOperationType())
                                    .withShift(v.getShift())
                                    .withMajor(v.getMajor())
                                    .withMinor(v.getMinor())
                                    .withOriginKey(selected));

                    // Possibly fix stalled records
                    transformerChain.getTransformerChain().transform(origin);
                    calculables.add(new DataRecordHolder(origin));
                }
            }
        }

        return new RecordTimeline(k, calculables);
    }

    /**
     * Converts PO to keys object.
     * @param po the key PO
     * @param op test predicate for the main origin
     * @return key object
     */
    public RecordKeys toRecordKeys(RecordKeysPO po, BiPredicate<RecordKeysPO, RecordOriginKeyPO> op) {

        // Key functions return a record with all fields nullified or unset on no keys
        // Check this and return null as appropriate.
        if (po == null || po.getId() == null || op == null) {
            return null;
        }

        RecordEtalonKey etalonKey = RecordEtalonKey.builder()
                .id(po.getId())
                .lsn(po.getLsn())
                .status(po.getStatus())
                .state(po.getState())
                .build();

        RecordOriginKey originKey = null;
        List<RecordOriginKey> supplementaryKeys = new ArrayList<>(po.getOriginKeys().size());
        for (int i = 0; i < po.getOriginKeys().size(); i++) {

            RecordOriginKeyPO okpo = po.getOriginKeys().get(i);
            RecordOriginKey ok = toOriginKey(po, okpo);
            if (Objects.isNull(ok)) {
                continue;
            }

            if (originKey == null && op.test(po, okpo)) {
                originKey = ok;
            }

            supplementaryKeys.add(ok);
        }

        if (originKey == null) {
            throw new PlatformFailureException("No valid origin key found for record key!",
                    DataExceptionIds.EX_DATA_RECORD_INVALID_KEYS);
        }

        return RecordKeys.builder()
                     .etalonKey(etalonKey)
                     .originKey(originKey)
                     .supplementaryKeys(supplementaryKeys)
                     .entityName(po.getName())
                     .published(po.isApproved())
                     .shard(po.getShard())
                     .node(StorageUtils.node(po.getShard()))
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
    public RecordOriginKey toOriginKey(RecordKeysPO record, RecordOriginKeyPO origin) {

        if (origin == null || Objects.isNull(origin.getId())) {
            return null;
        }

        return RecordOriginKey.builder()
                    .id(origin.getId().toString())
                    .initialOwner(origin.getInitialOwner())
                    .externalId(origin.getExternalId())
                    .entityName(record.getName())
                    .sourceSystem(origin.getSourceSystem())
                    .enrichment(BooleanUtils.toBoolean(origin.isEnrichment()))
                    .revision(origin.getRevision())
                    .status(origin.getStatus())
                    .createDate(origin.getCreateDate())
                    .createdBy(origin.getCreatedBy())
                    .updateDate(origin.getUpdateDate())
                    .updatedBy(origin.getUpdatedBy())
                    .build();
    }
}

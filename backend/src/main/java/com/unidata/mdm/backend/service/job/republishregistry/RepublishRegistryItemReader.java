/**
 *
 */

package com.unidata.mdm.backend.service.job.republishregistry;

import static org.apache.commons.lang3.tuple.Pair.of;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.unidata.mdm.backend.common.dto.TimeIntervalDTO;
import com.unidata.mdm.backend.common.dto.TimelineDTO;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.dao.OriginsVistoryDao;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.driver.EtalonComposer;
import com.unidata.mdm.backend.service.data.etalon.EtalonRecordsComponent;
import com.unidata.mdm.backend.service.data.util.DataRecordUtils;
import com.unidata.mdm.backend.service.job.reindex.ReindexJobException;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class RepublishRegistryItemReader implements ItemReader<List<Pair<RecordKeys,EtalonRecord>>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepublishRegistryItemReader.class);

    private String resourceName;

    private boolean complete;

    @Value("#{stepExecutionContext[ids]}")
    private List<String> ids;

    @Value("#{stepExecutionContext[allPeriods]}")
    private Boolean allPeriods;

    @Value("#{stepExecutionContext[entityName]}")
    private String entityName;

    @Value("#{stepExecutionContext[asOf]}")
    private Date asOf;

    @Autowired
    private EtalonRecordsComponent etalonRecordsComponent;

    @Autowired
    private CommonRecordsComponent commonRecordsComponent;

    @Autowired
    private OriginsVistoryDao originsVistoryDao;

    @Autowired
    private EtalonComposer etalonComposer;

    @Override
    public List<Pair<RecordKeys,EtalonRecord>> read() throws ReindexJobException {
        if (complete) {
            LOGGER.info("No data available [resourceName={}]", resourceName);
            return null;
        }

        LOGGER.info("Read data [resourceName={}, startId={}, endId={}]", resourceName, ids.get(0), ids.get(ids.size() - 1));
        final List<Pair<RecordKeys,EtalonRecord>> active = new ArrayList<>();
        for (final String id : ids) {
            try {
                if (allPeriods) {
                    final TimelineDTO timelineDTO = loadEtalonTimeline(id);
                    if (timelineDTO == null) {
                        //or try to load etalon
                        continue;
                    }
                    timelineDTO.getIntervals().stream()
                            .filter(Objects::nonNull)
                            .filter(TimeIntervalDTO::isActive)
                            .map(interval -> interval.getValidFrom() != null ? interval.getValidFrom() : interval.getValidTo())
                            .map(intervalAsOf -> etalonRecordsComponent.loadEtalonData(id, intervalAsOf, null, null, null, false, false))
                            .filter(Objects::nonNull)
                            .map(etalon-> of(commonRecordsComponent.identify(etalon.getInfoSection().getEtalonKey()),etalon))
                            .collect(Collectors.toCollection(() -> active));
                } else {
                    final EtalonRecord etalon = etalonRecordsComponent.loadEtalonData(id, asOf, null, null, null, false, false);
                    if (etalon != null) {
                        final RecordKeys keys = commonRecordsComponent.identify(etalon.getInfoSection().getEtalonKey());
                        active.add(of(keys,etalon));
                    }
                }
            } catch (final Exception exc) {
                LOGGER.warn("Caught exception {}", exc);
                throw new ReindexJobException(exc);
            }
        }

        complete = true;
        return active;
    }

    private TimelineDTO loadEtalonTimeline(final String etalonId) {
        return DataRecordUtils
            .buildTimeline(originsVistoryDao.loadContributingRecordsTimeline(etalonId, entityName, false),
                etalonId, etalonComposer);
    }


    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}

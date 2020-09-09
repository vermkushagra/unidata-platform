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

import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.TimeInterval;
import com.unidata.mdm.backend.common.types.Timeline;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.etalon.EtalonRecordsComponent;
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

    @Override
    public List<Pair<RecordKeys,EtalonRecord>> read() throws ReindexJobException {
        if (complete) {
            LOGGER.info("No data available [resourceName={}]", resourceName);
            return null;
        }

        LOGGER.info("Read data [resourceName={}, startId={}, endId={}]", resourceName, ids.get(0), ids.get(ids.size() - 1));
        final List<Pair<RecordKeys, EtalonRecord>> active = new ArrayList<>();
        for (final String id : ids) {
            try {
                if (allPeriods) {
                    final Timeline<OriginRecord> timelineDTO = loadEtalonTimeline(id);
                    if (timelineDTO == null) {
                        //or try to load etalon
                        continue;
                    }
                    timelineDTO.stream()
                            .filter(Objects::nonNull)
                            .filter(TimeInterval::isActive)
                            .map(interval -> Pair.<RecordKeys, EtalonRecord>of(timelineDTO.getKeys(), interval.getCalculationResult()))
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

    private Timeline<OriginRecord> loadEtalonTimeline(final String etalonId) {

        GetRequestContext gCtx = GetRequestContext.builder()
                .etalonKey(etalonId)
                .tasks(false)
                .includeDrafts(false)
                .fetchTimelineData(true)
                .build();

        return commonRecordsComponent.loadTimeline(gCtx);
    }


    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}

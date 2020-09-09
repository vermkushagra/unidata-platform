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

package com.unidata.mdm.backend.service.job.samplerestart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class SampleRestartItemReader implements ItemReader<SampleRestartItemSubmission>, ItemStream {
    private static final Logger logger = LoggerFactory.getLogger(SampleRestartItemReader.class);

    private static final String CURRENT_COUNT_ATTR = "current.count";

    private Integer from;
    private Integer to;
    private Integer currentCount;
    private String firstParameter;

    public void setTo(Integer to) {
        this.to = to;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public void setFirstParameter(String firstParameter) {
        this.firstParameter = firstParameter;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        boolean receivedFromCtx = false;

        if(executionContext.containsKey(CURRENT_COUNT_ATTR)){
            currentCount = executionContext.getInt(CURRENT_COUNT_ATTR);
            receivedFromCtx = true;
        } else {
            currentCount = from;
        }

        logger.debug("Open reader [receivedFromCtx={}, currentCount={}]", receivedFromCtx, currentCount);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.putInt(CURRENT_COUNT_ATTR, currentCount);
    }

    @Override
    public void close() throws ItemStreamException {
        // No-op.
    }

    @Override
    public SampleRestartItemSubmission read() throws Exception {
        if (from == null || to == null) {
            logger.error("Failed to get valid initializations");

            return null;
        }

        if (currentCount == null) {
            currentCount = from;
        }

        logger.info("Reader [currentCount={}, from={}, to={}, firstParameter={}]", currentCount, from, to,
            firstParameter);

        SampleRestartItemSubmission result = null;

        if (currentCount <= to) {
            result = new SampleRestartItemSubmission("" + currentCount);
            currentCount++;
        }

        return result;
    }
}

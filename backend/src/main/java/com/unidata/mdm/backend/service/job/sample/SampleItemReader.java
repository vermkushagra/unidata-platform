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
 * Date: 19.02.2016
 */

package com.unidata.mdm.backend.service.job.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class SampleItemReader implements ItemReader<SampleItemSubmission> {
    private static final Logger logger = LoggerFactory.getLogger(SampleItemReader.class);

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
    public SampleItemSubmission read() throws Exception, UnexpectedInputException,
        ParseException, NonTransientResourceException {
        if (from == null || to == null) {
            logger.error("Failed to get valid initializations");

            return null;
        }

        if (currentCount == null) {
            currentCount = from;
        }

        logger.info("Reader [currentCount={}, from={}, to={}, firstParameter={}]", currentCount, from, to,
            firstParameter);

        SampleItemSubmission result = null;

        if (currentCount <= to) {
            result = new SampleItemSubmission("" + currentCount);
            currentCount++;
        }

        return result;
    }
}

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

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class SampleItemWriter implements ItemWriter<SampleProcessedItem> {
    private static final Logger logger = LoggerFactory.getLogger(SampleItemWriter.class);

    /**
     *
     * @param items
     * @throws Exception
     */
    @Override
    public void write(List<? extends SampleProcessedItem> items) throws Exception {
        StringBuilder builder = new StringBuilder("Write items [");

        builder.append("size=").append(items.size()).append(", values=[");

        String joined = items.stream().map(SampleProcessedItem::getValue).collect(Collectors.joining(", "));

        builder.append(joined).append("]]");

        logger.info(builder.toString());
    }
}

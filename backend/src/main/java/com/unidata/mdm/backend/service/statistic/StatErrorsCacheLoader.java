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

package com.unidata.mdm.backend.service.statistic;

import static com.unidata.mdm.backend.common.context.SearchRequestContext.forEtalonData;
import static com.unidata.mdm.backend.common.search.FormField.FormType.POSITIVE;
import static com.unidata.mdm.backend.common.search.fields.RecordHeaderField.FIELD_FROM;
import static com.unidata.mdm.backend.common.search.fields.RecordHeaderField.FIELD_TO;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.unidata.mdm.backend.common.context.AggregationRequestContext;
import com.unidata.mdm.backend.common.context.NestedAggregationRequestContext;
import com.unidata.mdm.backend.common.context.TermsAggregationRequestContext;
import com.unidata.mdm.backend.common.dto.AggregationResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.fields.DQHeaderField;
import com.unidata.mdm.backend.common.search.fields.RecordHeaderField;
import com.unidata.mdm.meta.SimpleDataType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.cache.CacheLoader;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.statistic.ErrorsStatDTO;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.types.SeverityType;

/**
 * The Class StatErrorsCacheLoader.
 */
public class StatErrorsCacheLoader extends CacheLoader<ErrorsStatDTO, ErrorsStatDTO> {
    /**
     * ALL entities.
     */
    private static final String ALL = "ALL";

    private static final String SEVERITY = "severity";

    private static final String CATEGORY = "category";

    private static final Integer MAX_ERRORS_AGGREGATION_SIZE = 10;

    private SearchService searchService;

    /**
     * Instantiates a new stat errors cache loader.
     *
     * @param searchService the search service
     */
    public StatErrorsCacheLoader(SearchService searchService) {
        this.searchService = searchService;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.google.common.cache.CacheLoader#load(java.lang.Object)
     */
    @Override
    public ErrorsStatDTO load(ErrorsStatDTO key)  {
        ErrorsStatDTO value = new ErrorsStatDTO();
        value.setEntityName(key.getEntityName());
        Map<SeverityType, Integer> data = new HashMap<>();
        value.setData(data);
        Date now = new Date();
        FormField fromFormField = FormField.range(SimpleDataType.TIMESTAMP, FIELD_FROM.getField(), POSITIVE, null, now);
        FormField toFromField = FormField.range(SimpleDataType.TIMESTAMP, FIELD_TO.getField(), POSITIVE, now, null);
        FormField published = FormField.strictValue(SimpleDataType.BOOLEAN, RecordHeaderField.FIELD_PUBLISHED.getField(), true);
        FormField active = FormField.strictValue(SimpleDataType.BOOLEAN, RecordHeaderField.FIELD_DELETED.getField(), false);
        FormFieldsGroup time = FormFieldsGroup.createAndGroup(fromFormField, toFromField, published, active);

        AggregationRequestContext top = NestedAggregationRequestContext.builder()
                .name(key.getEntityName())
                .path(DQHeaderField.getParentField())
                .subAggregation(TermsAggregationRequestContext.builder()
                        .name(SEVERITY)
                        .size(MAX_ERRORS_AGGREGATION_SIZE)
                        .path(DQHeaderField.SEVERITY.getField())
                        .subAggregation(TermsAggregationRequestContext.builder()
                                .name(CATEGORY)
                                .path(DQHeaderField.CATEGORY.getField())
                                .size(MAX_ERRORS_AGGREGATION_SIZE)
                                .build())
                        .build())
                .build();

        SearchRequestContext requestContext = SearchRequestContext.forEtalonData(key.getEntityName())
                .form(time)
                .count(0)
                .onlyQuery(true)
                .skipEtalonId(true)
                .aggregations(Collections.singletonList(top))
                .build();

        SearchResultDTO result = searchService.search(requestContext);
        AggregationResultDTO aggregationResultDTO = CollectionUtils.isNotEmpty(result.getAggregates())
                ? result.getAggregates().get(0)
                : new AggregationResultDTO(key.getEntityName(), AggregationRequestContext.AggregationType.NESTED, -1, false);

        if (aggregationResultDTO.getDocumentsCount() == 0) {
            return value;
        }
        AggregationResultDTO severity = aggregationResultDTO.getSubAggregations().get(SEVERITY).get(SEVERITY);

        for (String severityType : severity.getCountMap().keySet()) {
            Long count = severity.getCountMap().get(severityType);
            if (count == null) {
                count = 0l;
            }
            data.put(SeverityType.fromValue(severityType), count.intValue());
			value.setTotal(value.getTotal() + count.intValue());
        }

        return value;
    }

}
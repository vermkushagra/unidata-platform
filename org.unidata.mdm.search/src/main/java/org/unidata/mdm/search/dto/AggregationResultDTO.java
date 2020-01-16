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

package org.unidata.mdm.search.dto;

import org.apache.commons.collections4.MapUtils;
import org.unidata.mdm.search.context.AggregationSearchContext.AggregationType;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Aggregation result
 */
public class AggregationResultDTO {
    /**
     * aggregation name
     */
    @Nonnull
    private final String aggregationName;
    /**
     * Type of the aggregation.
     */
    private final AggregationType aggregationType;
    /**
     * Total documents count.
     */
    private long documentsCount;
    /**
     * Map of top level aggregation result
     */
    private Map<String, Long> countMap = Collections.emptyMap();

    /**
     * Map of sub aggregation, keys equal with keys from count map.
     */
    private Map<String, Map<String, AggregationResultDTO>> subAggregations = Collections.emptyMap();
    /**
     * Constructor.
     * This is the sole constructor for the type, but it is used very differently for two main aggregation kinds.
     * It initializes two maps - one for counting statistics, one for sub aggregations.
     * The keys for countMap are understoof very distinct for two aggregation kinds:
     * countMap [
     *  key - term (the name of the term from 'path'}) in case of finite aggregation | aggName (the name of sub aggregation) in case of bucketing aggregation
     *  value - count (returned by aggregation (cardinality or sum for instance) | docCount (the document count in all buckets)
     * ]
     *
     * subAggregationsMap [
     *  key -name of the aggregation
     *  value - map of [
     *    key - bucket key
     *    value - AggregationResultDTO sub aggregation instance
     *  ]
     * ]
     * @param aggregationName the aggregation name
     * @param aggregationType the aggregation type
     * @param numberOfBuckets number of buckets (subaggregations, if any), 1 for terminating aggregations
     * @param hasSubAggregations has sub aggregations or not
     */
    public AggregationResultDTO(@Nonnull String aggregationName, AggregationType aggregationType, int numberOfBuckets, boolean hasSubAggregations) {

        this.aggregationName = aggregationName;
        this.aggregationType = aggregationType;

        if (numberOfBuckets > 0) {
            countMap = new HashMap<>(numberOfBuckets, 1);
        }

        if (hasSubAggregations && numberOfBuckets > 0) {
            subAggregations = new HashMap<>(numberOfBuckets, 1);
        }
    }

    /**
     * @param value - value
     * @param count -  count of values
     * @return true if count was added and it was the first value with that name;
     */
    public boolean add(@Nonnull String value, @Nonnull Long count) {
        return countMap.put(value, count) == null;
    }

    /**
     * @param aggregationName - value
     * @param subAggregation - sub aggregation
     * @return true if subAggregation was added and it was the first aggregation with that name;
     */
    public boolean add(@Nonnull String aggregationName, @Nonnull AggregationResultDTO subAggregation) {

        if (!subAggregations.containsKey(aggregationName)) {
            subAggregations.put(aggregationName, new HashMap<>());
        }

        Map<String, AggregationResultDTO> subMap = subAggregations.get(aggregationName);
        return subMap.put(subAggregation.getAggregationName(), subAggregation) == null;
    }

    /**
     * @return aggregation name
     */
    @Nonnull
    public String getAggregationName() {
        return aggregationName;
    }

    /**
     * @return the aggregationType
     */
    public AggregationType getAggregationType() {
        return aggregationType;
    }

    /**
     * @return the documentsCount
     */
    public long getDocumentsCount() {
        return documentsCount;
    }

    /**
     * @return map with
     */
    @Nonnull
    public Map<String, Long> getCountMap() {
        return Collections.unmodifiableMap(countMap);
    }

    /**
     * @param documentsCount the documentsCount to set
     */
    public void setDocumentsCount(long documentsCount) {
        this.documentsCount = documentsCount;
    }

    /**
     * @return
     */
    @Nonnull
    public Map<String, Map<String, AggregationResultDTO>> getSubAggregations() {
        return Collections.unmodifiableMap(subAggregations);
    }

    /**
     * Tells whether this result has sub aggregations.
     *
     * @return
     */
    public boolean hasSubAggregations() {
        return !subAggregations.isEmpty();
    }

    /**
     * Merge aggrigation
     * @param other
     */
    public void mergeAggrigation(AggregationResultDTO other) {

        if (other == null || MapUtils.isEmpty(other.countMap) || MapUtils.isEmpty(other.subAggregations)) {
            return;
        }

        if (countMap == null) {
            countMap = new HashMap<>(other.countMap);
        } else {
            other.countMap.forEach((otherKey, otherValue) -> {
                countMap.merge(otherKey, otherValue, (_thisValue, _otherValue) -> zeroIfNull(_thisValue) + zeroIfNull(_otherValue));
            });
        }

        if (subAggregations == null) {
            subAggregations = new HashMap<>(other.subAggregations);
        } else {
            other.subAggregations.forEach((otherKey, otherValue) -> {

                subAggregations.merge(otherKey, otherValue, (_thisValue, _otherValue) -> {
                    if (_thisValue == null) {
                        return _otherValue;
                    }
                    if (_otherValue == null) {
                        return _thisValue;
                    }
                    //for circular reference
                    if(_thisValue.equals(_otherValue)){
                        return _thisValue;
                    }
                    _otherValue.forEach((otherKey2, otherValue2) -> {
                        _thisValue.merge(otherKey2, otherValue2, (_thisValue2, _otherValue2) -> {
                            if (_thisValue2 == null) {
                                return _otherValue2;
                            }
                            if (_otherValue2 == null) {
                                return _thisValue2;
                            }
                            //for circular reference
                            if(_thisValue2.equals(_otherValue2)){
                                return _thisValue2;
                            }

                            _thisValue2.mergeAggrigation(_otherValue2);
                            return _thisValue2;
                        });
                    });

                    return _thisValue;
                });
            });
        }
    }

    private Long zeroIfNull(Long value) {
        return Optional.ofNullable(value).orElse(0L);
    }

}

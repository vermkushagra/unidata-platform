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

package com.unidata.mdm.cleanse.misc;

import static com.unidata.mdm.backend.common.search.fields.RecordHeaderField.FIELD_FROM;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT2;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionOutputParam;
import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchRequestOperator;
import com.unidata.mdm.backend.common.search.SortField;
import com.unidata.mdm.backend.common.search.SortField.SortOrder;
import com.unidata.mdm.backend.common.search.fields.RecordHeaderField;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.service.ServiceUtils;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.backend.util.ValidityPeriodUtils;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.PeriodBoundaryDef;
import com.unidata.mdm.meta.SimpleDataType;

/**
 * The Class CFDataConsistency.
 */
public class CFDataConsistency extends BasicCleanseFunctionAbstract implements CFSystemCleanseFunction {
    /**
     * Search service.
     */
    private SearchService searchService;
    /**
     * Model Service.
     */
    private MetaModelService modelService;
    /**
     * Instantiates a new cleanse function abstract.
     */
    public CFDataConsistency() {
        super(CFDataConsistency.class);
        this.searchService = ServiceUtils.getSearchService();
        this.modelService = ServiceUtils.getMetaModelService();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CleanseFunctionContext ctx) {

        CleanseFunctionInputParam param1 = ctx.getInputParamByPortName(INPUT1);
        CleanseFunctionInputParam param2 = ctx.getInputParamByPortName(INPUT2);

        String lookupEntityName = Objects.nonNull(param2) && !param2.isEmpty()
                ? param2.toSingletonValue()
                : null;

        // 1. Invalid input - lookup entity name is null
        if ((lookupEntityName == null || StringUtils.isBlank(lookupEntityName))) {
            throw new CleanseFunctionExecutionException(ctx.getCleanseFunctionName(),
                    MessageUtils.getMessage("app.cleanse.validation.invalid.parameters"));
        }

        // 2. Extract values, exit on empty
        Map<Object, List<Attribute>> check = extractAndMapAttributes(param1);
        if (check.isEmpty()) {
            ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT1, Boolean.TRUE));
            ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT2, StringUtils.EMPTY));
            return;
        }

        // 3. Fail on wrong configuration. Lookup entity not found by name.
        LookupEntityDef lookupEntityDef = modelService.getLookupEntityById(lookupEntityName);
        if (lookupEntityDef == null) {
            ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT1, Boolean.FALSE));
            ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT2,
                    createMessageString("app.cleanse.validation.lookupEntity.notExist", lookupEntityName)));
            return;
        }

        // 4. Collect current values and compare them with input. Report missing.
        Map<Object, List<Range<Date>>> current = collectCurrentState(lookupEntityDef, check);
        Pair<Set<Object>, Set<Attribute>> failed = collectMissingValues(check, current);

        // 5. Stop on missing values in links
        if (CollectionUtils.isNotEmpty(failed.getKey())) {
            ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT1, Boolean.FALSE));
            ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT2,
                    createMessageString("app.cleanse.validation.lookupEntityRecord.notExist", failed.getKey().toString())));
            ctx.failedValidations().addAll(createPathCollection(failed.getValue()));
            return;
        }

        // 6. Check ranges of referenced values
        String entityName = ctx.keys() != null
                ? ctx.keys().getEntityName()
                : ctx.getEntityName();

        Date from = ctx.getValidFrom() == null
                ? defaultStart(entityName)
                : ctx.getValidFrom();

        Date to = ctx.getValidTo() == null
                ? defaultEnd(entityName)
                : ctx.getValidTo();

        from = from == null ? SearchUtils.ES_MIN_DATE : from;
        to = to == null ? SearchUtils.ES_MAX_DATE : to;
        Range<Date> range = Range.between(from, to);

        failed = collectFailedRanges(check, current, range);

        boolean isOk = failed.getKey().isEmpty();
        ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT1, isOk));
        ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT2, isOk
                ? StringUtils.EMPTY
                : createMessageString("app.cleanse.validation.lookupEntityRecord.notOverlapped", failed.getKey().toString())));

        if (!isOk) {
            ctx.failedValidations().addAll(createPathCollection(failed.getValue()));
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isContextAware() {
        return true;
    }
    /**
     * Creates search request and executes it, returning preprocessed response.
     * @param lookupEntityDef lookup entity definition
     * @param values the values map
     * @return search result as multimap
     */
    private Map<Object, List<Range<Date>>> collectCurrentState(LookupEntityDef lookupEntityDef, Map<Object, List<Attribute>> values) {

        // 1. Request
        SearchRequestContext ctx = SearchRequestContext.forEtalonData(lookupEntityDef.getName())
                .form(FormFieldsGroup.createAndGroup(
                        FormField.strictValue(SimpleDataType.BOOLEAN, RecordHeaderField.FIELD_DELETED.getField(), Boolean.FALSE),
                        FormField.strictValue(SimpleDataType.BOOLEAN, RecordHeaderField.FIELD_PUBLISHED.getField(), Boolean.TRUE),
                        FormField.strictValues(
                                lookupEntityDef.getCodeAttribute().getSimpleDataType(),
                                lookupEntityDef.getCodeAttribute().getName(),
                                values.keySet())))
                .returnFields(Arrays.asList(
                        RecordHeaderField.FIELD_TO.getField(),
                        RecordHeaderField.FIELD_FROM.getField(),
                        lookupEntityDef.getCodeAttribute().getName()))
                .operator(SearchRequestOperator.OP_OR)
                .addSorting(Collections.singleton(new SortField(FIELD_FROM.getField(), SortOrder.ASC, false)))
                .page(0)
                .count(Integer.MAX_VALUE)
                .totalCount(true)
                .onlyQuery(true)
                .build();

        // 2. Execute
        SearchResultDTO searchResult = searchService.search(ctx);

        // 3. Post-process
        if (CollectionUtils.isEmpty(searchResult.getHits())) {
            return Collections.emptyMap();
        }

        Map<Object, List<Range<Date>>> ranges = new HashMap<>();
        for (SearchResultHitDTO hit : searchResult.getHits()) {

            Range<Date> range = getDateRange(hit);
            if (Objects.isNull(range)) {
                continue;
            }

            List<Object> codeValues = hit.getFieldValues(lookupEntityDef.getCodeAttribute().getName());
            if (CollectionUtils.isNotEmpty(codeValues)) {
                codeValues.forEach(codeValue -> ranges.computeIfAbsent(codeValue instanceof Number
                            ? ((Number) codeValue).longValue()
                            : codeValue.toString(), key -> new ArrayList<>())
                        .add(range));
            }
        }

        for (Iterator<Entry<Object, List<Range<Date>>>> vi = ranges.entrySet().iterator(); vi.hasNext(); ) {
            Entry<Object, List<Range<Date>>> val = vi.next();
            List<Range<Date>> merged = mergeRanges(val.getValue());
            val.getValue().clear();
            val.getValue().addAll(merged);
        }

        return ranges;
    }
    /**
     * Gets the not presented values.
     *
     * @param check the values to check
     * @param current the current state
     * @return the not present values
     */
    private Pair<Set<Object>, Set<Attribute>> collectMissingValues(
            Map<Object, List<Attribute>> check, Map<Object, List<Range<Date>>> current) {

        Set<Object> missingValues = new HashSet<>();
        Set<Attribute> failedAttributes = new HashSet<>();
        for (Entry<Object, List<Attribute>> checkEntry : check.entrySet()) {
            if (!current.containsKey(checkEntry.getKey())) {
                missingValues.add(checkEntry.getKey());
                failedAttributes.addAll(checkEntry.getValue());
            }
        }

        return new ImmutablePair<>(missingValues, failedAttributes);
    }
    /**
     * Collects failed ranges.
     * @param check the values to check
     * @param current current values
     * @param range the record range
     * @return failed
     */
    @Nonnull
    private Pair<Set<Object>, Set<Attribute>> collectFailedRanges(
            Map<Object, List<Attribute>> check, Map<Object, List<Range<Date>>> current, Range<Date> range) {

        Set<Object> missingValues = new HashSet<>();
        Set<Attribute> failedAttributes = new HashSet<>();
        for (Entry<Object, List<Attribute>> checkEntry : check.entrySet()) {

            List<Range<Date>> ranges = current.get(checkEntry.getKey());
            boolean isCovered = false;
            for (Range<Date> test : ranges) {
                isCovered = test.containsRange(range);
                if (isCovered) {
                    break;
                }
            }

            if (!isCovered) {
                missingValues.add(checkEntry.getKey());
                failedAttributes.addAll(checkEntry.getValue());
            }
        }

        return new ImmutablePair<>(missingValues, failedAttributes);
    }
    /**
     * Collects paths of attributes.
     * @param attributes the attributes collection
     * @return paths collection
     */
    private Collection<Pair<String, Attribute>> createPathCollection(Collection<Attribute> attributes) {
        return CollectionUtils.isEmpty(attributes)
                ? Collections.emptyList()
                : attributes.stream()
                    .map(attr -> new ImmutablePair<>(attr.toLocalPath(), attr))
                    .collect(Collectors.toList());
    }
    /**
     * Creates output message string.
     * @param message the message code
     * @param args arguments
     * @return message
     */
    private String createMessageString(String message, Object... args) {
        return MessageUtils.getMessage(message, args);
    }
    /**
     * Collect ranges.
     *
     * @param searchResult - search result
     * @return multi map, where key it is a lookup record etalon key and value is collection of record ranges
     */
    @Nonnull
    private Multimap<String, Range<Date>> collectRanges(SearchResultDTO searchResult) {
        int approximateRangesSize = 3;
        int hits = searchResult.getHits().size();
        Multimap<String, Range<Date>> ranges = HashMultimap.create(hits, approximateRangesSize);
        for (SearchResultHitDTO hit : searchResult.getHits()) {
            Range<Date> range = getDateRange(hit);
            if (Objects.isNull(range)) {
                continue;
            }
            ranges.put(hit.getId(), range);
        }

        for (String etalonId : new ArrayList<>(ranges.keySet())) {
            List<Range<Date>> recordRanges = new ArrayList<>(ranges.removeAll(etalonId));
            ranges.putAll(etalonId, mergeRanges(recordRanges));
        }

        return ranges;
    }

    /**
     * Merge ranges.
     *
     * @param lookupRanges - unmerged ranges
     * @return collection of merged ranges
     */
     private List<Range<Date>> mergeRanges(List<Range<Date>> lookupRanges){
         // Guard against clear -> addAll
         if (lookupRanges.size() < 2) {
            return lookupRanges.isEmpty()
                    ? Collections.emptyList()
                    : Collections.singletonList(lookupRanges.get(0));
         }

         //merge ranges
         List<Range<Date>> mergedRanges = new ArrayList<>(lookupRanges.size());
         lookupRanges.sort((o1, o2) -> {

             if (o1.getComparator() == null && o2.getMinimum() == null) {
                 return 0;
             } else if (o1.getMinimum() == null) {
                 return -1;
             } else if (o2.getMinimum() == null) {
                 return +1;
             }

             return o1.getMinimum().compareTo(o2.getMinimum());
        });

        Iterator<Range<Date>> iterator = lookupRanges.iterator();
        Range<Date> initialRange = iterator.next();
        while (iterator.hasNext()) {
            Range<Date> nextRange = iterator.next();
            Range<Date> mergedRange = getMergedRange(initialRange, nextRange);
            if (mergedRange == null) {
                mergedRanges.add(initialRange);
                initialRange = nextRange;
            } else {
                initialRange = mergedRange;
            }
        }

        mergedRanges.add(initialRange);
        return mergedRanges;
    }

    /**
     * Gets the merged range.
     *
     * @param first the first
     * @param second the second
     * @return the merged range
     */
    @Nullable
    private Range<Date> getMergedRange(@Nonnull Range<Date> first, @Nonnull Range<Date> second) {
        long to = first.getMaximum().getTime();
        long from = second.getMinimum().getTime();
        boolean isRangeOverlapped = from - to <= TimeUnit.MILLISECONDS.toMillis(1);
        if (isRangeOverlapped) {
            return Range.between(first.getMinimum(), second.getMaximum());
        } else {
            return null;
        }
    }

    /**
     * Gets the date range.
     *
     * @param hit the hit
     * @return the date range
     */
    @Nullable
    private Range<Date> getDateRange(SearchResultHitDTO hit) {

        SearchResultHitFieldDTO to = hit.getFieldValue(RecordHeaderField.FIELD_TO.getField());
        SearchResultHitFieldDTO from = hit.getFieldValue(RecordHeaderField.FIELD_FROM.getField());

        try {

            Date validTo = to == null || to.isNullField() ? SearchUtils.ES_MAX_DATE : new DateTime(to.getFirstValue().toString()).toDate();
            Date validFrom = from == null || from.isNullField() ? SearchUtils.ES_MIN_DATE : new DateTime(from.getFirstValue().toString()).toDate();
            return Range.between(validFrom, validTo);

        } catch (Exception e) {
            return null;
        }
    }
    /**
     * Default start.
     *
     * @param entityName the entity name
     * @return the date
     */
    private Date defaultStart(String entityName) {
        return modelService.getLookupEntityById(entityName) == null
                ? defaultStart(modelService.getEntityByIdNoDeps(entityName).getValidityPeriod())
                : defaultStart(modelService.getLookupEntityById(entityName).getValidityPeriod());
    }

    /**
     * Default start.
     *
     * @param boundary the boundary
     * @return the date
     */
    private Date defaultStart(PeriodBoundaryDef boundary) {
        if (boundary == null || boundary.getStart() == null) {
            return ValidityPeriodUtils.getGlobalValidityPeriodStart();
        }
        return boundary.getStart().toGregorianCalendar().getTime();
    }

    /**
     * Default end.
     *
     * @param entityName the entity name
     * @return the date
     */
    private Date defaultEnd(String entityName) {
        return modelService.getLookupEntityById(entityName) == null
                ? defaultEnd(modelService.getEntityByIdNoDeps(entityName).getValidityPeriod())
                : defaultEnd(modelService.getLookupEntityById(entityName).getValidityPeriod());
    }
    /**
     * Default end.
     *
     * @param boundary the boundary
     * @return the date
     */
    private Date defaultEnd(PeriodBoundaryDef boundary) {
        if (boundary == null || boundary.getEnd() == null) {
            return ValidityPeriodUtils.getGlobalValidityPeriodEnd();
        }
        return boundary.getEnd().toGregorianCalendar().getTime();
    }
}

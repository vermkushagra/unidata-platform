package com.unidata.mdm.cleanse.misc;

import static com.unidata.mdm.backend.common.search.FormField.strictValue;
import static com.unidata.mdm.backend.common.search.FormFieldsGroup.createAndGroup;
import static com.unidata.mdm.backend.common.search.SortField.SortOrder.ASC;
import static com.unidata.mdm.backend.service.search.util.RecordHeaderField.FIELD_DELETED;
import static com.unidata.mdm.backend.service.search.util.RecordHeaderField.FIELD_FROM;
import static com.unidata.mdm.backend.service.search.util.RecordHeaderField.FIELD_PUBLISHED;
import static com.unidata.mdm.backend.service.search.util.RecordHeaderField.FIELD_TO;
import static com.unidata.mdm.backend.service.search.util.SearchUtils.ES_MAX_DATE;
import static com.unidata.mdm.backend.service.search.util.SearchUtils.ES_MIN_DATE;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT2;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT2;
import static com.unidata.mdm.meta.SimpleDataType.BOOLEAN;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchRequestOperator;
import com.unidata.mdm.backend.common.search.SearchRequestType;
import com.unidata.mdm.backend.common.search.SortField;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.backend.service.cleanse.CFAppContext;
import com.unidata.mdm.backend.service.cleanse.DataQualityServiceExt;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.backend.util.ValidityPeriodUtils;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.PeriodBoundaryDef;

/**
 * The Class CFDataConsistency.
 */
public class CFDataConsistency extends BasicCleanseFunctionAbstract {
    /**
     * Search service.
     */
    private SearchService searchService;

    /** Model Service. */
    private MetaModelServiceExt modelService;
    /**
     * Instantiates a new cleanse function abstract.
     */
    public CFDataConsistency() {
        super(CFDataConsistency.class);
        this.searchService = CFAppContext.getBean(SearchService.class);
        this.modelService = CFAppContext.getBean(MetaModelServiceExt.class);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract#execute(java.util.Map, java.util.Map)
     */
    @Override
    public void execute(Map<String, Object> input, Map<String, Object> result) throws CleanseFunctionExecutionException {

        StringSimpleAttributeImpl outputMessage = new StringSimpleAttributeImpl(OUTPUT2, StringUtils.EMPTY);
        BooleanSimpleAttributeImpl cfResult = new BooleanSimpleAttributeImpl(OUTPUT1, false);

        Object refObj = getValueByPort(INPUT1, input);
        Object lookupEntityNameObj = getValueByPort(INPUT2, input);
        if (refObj == null || StringUtils.isEmpty(refObj.toString())) {
            cfResult.setValue(true);
            result.put(OUTPUT1, cfResult);
            result.put(OUTPUT2, outputMessage);
            return;
        }

        String lookupEntityName = lookupEntityNameObj.toString();
        LookupEntityDef lookupEntityDef = modelService.getLookupEntityById(lookupEntityName);

        if (lookupEntityDef == null) {
            result.put(OUTPUT1, cfResult);
            outputMessage.setValue(MessageUtils.getMessage("app.cleanse.validation.lookupEntity.notExist", lookupEntityName));
            result.put(OUTPUT2, outputMessage);
            return;
        }

        boolean isArray = refObj instanceof Object[];
        List<String> links = isArray ?
                //we can do it because links it is only int or string!
                Arrays.stream((Object[]) refObj).filter(Objects::nonNull).map(Object::toString).collect(Collectors.toList()) :
                Collections.singletonList(refObj.toString());

        if (CollectionUtils.isEmpty(links)) {
            cfResult.setValue(true);
            outputMessage.setValue(StringUtils.EMPTY);
            return;
        }

        Set<String> uniqueLinks = new HashSet<>(links);
        String lookupField = lookupEntityDef.getCodeAttribute().getName();
        FormField notDeleted = strictValue(BOOLEAN, FIELD_DELETED.getField(), FALSE);
        FormField published = strictValue(BOOLEAN, FIELD_PUBLISHED.getField(), TRUE);
        FormFieldsGroup group = createAndGroup(notDeleted, published);
        SearchRequestContext ctx = SearchRequestContext.forEtalonData(lookupEntityDef.getName())
                                                       .search(SearchRequestType.TERM)
                                                       .operator(SearchRequestOperator.OP_OR)
                                                       .searchFields(singletonList(lookupField))
                                                       .values(links)
                                                       .form(group)
                                                       .returnFields(asList(FIELD_TO.getField(), FIELD_FROM.getField(),lookupField))
                                                       .addSorting(singleton(new SortField(FIELD_FROM.getField(), ASC, false)))
                                                       .page(0)
                                                       .count(Integer.MAX_VALUE)
                                                       .totalCount(true)
                                                       .onlyQuery(true)
                                                       .build();

        SearchResultDTO searchResult = searchService.search(ctx);
        Set<String> notPresentedValues = getNotPresentedValues(uniqueLinks, lookupField, searchResult);
        boolean isPresent = notPresentedValues.isEmpty();

        if (isPresent) {
            Date from = input.get(DataQualityServiceExt.$FROM) == null
                    ? defaultStart((String) input.get(DataQualityServiceExt.$ENTITY))
                    : (Date) input.get(DataQualityServiceExt.$FROM);
            Date to = input.get(DataQualityServiceExt.$TO) == null
                    ? defaultEnd((String) input.get(DataQualityServiceExt.$ENTITY))
                    : (Date) input.get(DataQualityServiceExt.$TO);
            from = from == null ? ES_MIN_DATE : from;
            to = to == null ? ES_MAX_DATE : to;
            Range<Date> recordRange = Range.between(from, to);
            Multimap<String, Range<Date>> collectedRanges = collectRanges(searchResult);
            List<Object> failed = collectedRanges.asMap()
                                                 .entrySet()
                                                 .stream()
                                                 .filter(ran -> !ran.getValue()
                                                                    .stream()
                                                                    .anyMatch(r -> r.containsRange(recordRange)))
                                                 .map(Map.Entry::getKey)
                                                 .map(etalon -> getRefByEtalonId(etalon, lookupField, searchResult))
                                                 .filter(Objects::nonNull)
                                                 .collect(Collectors.toList());
            boolean isRangesMatch = failed.isEmpty();
            outputMessage.setValue(isRangesMatch
                    ? StringUtils.EMPTY
                    : MessageUtils.getMessage("app.cleanse.validation.lookupEntityRecord.notOverlapped", failed.toString()));
            cfResult.setValue(isRangesMatch);
        } else {
            outputMessage.setValue(MessageUtils.getMessage("app.cleanse.validation.lookupEntityRecord.notExist", notPresentedValues.toString()));
            cfResult.setValue(false);
        }

        result.put(OUTPUT1, cfResult);
        result.put(OUTPUT2, outputMessage);
    }

    /**
     * Gets the not presented values.
     *
     * @param refs the refs
     * @param lookupField the lookup field
     * @param searchResult the search result
     * @return the not presented values
     */
    private Set<String> getNotPresentedValues(Set<String> refs, String lookupField, SearchResultDTO searchResult) {
        searchResult.getHits()
                    .stream()
                    .map(hit -> hit.getFieldValue(lookupField))
                    .filter(Objects::nonNull)
                    .map(SearchResultHitFieldDTO::getValues)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .sequential()
                    .forEach(refs::remove);
        return refs;
    }

    /**
     * Gets the ref by etalon id.
     *
     * @param etalonId the etalon id
     * @param lookupField the lookup field
     * @param searchResult the search result
     * @return the ref by etalon id
     */
    private Object getRefByEtalonId(String etalonId, String lookupField, SearchResultDTO searchResult) {
        return searchResult.getHits()
                           .stream()
                           .filter(hiy -> hiy.getId().equals(etalonId))
                           .map(hit -> hit.getFieldValue(lookupField))
                           .filter(Objects::nonNull)
                           .map(SearchResultHitFieldDTO::getFirstValue)
                           .filter(Objects::nonNull)
                           .findAny()
                           .orElse(null);
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
     private Collection<Range<Date>> mergeRanges(List<Range<Date>> lookupRanges){
        if (lookupRanges.size() < 2) {
            return lookupRanges;
        }
        //merge ranges
        List<Range<Date>> mergedRanges = new ArrayList<>(lookupRanges.size());
        lookupRanges.sort(new Comparator<Range<Date>>() {

            @Override
            public int compare(Range<Date> o1, Range<Date> o2) {
                if (o1.getComparator() == null && o2.getMinimum() == null) {
                    return 0;
                } else if (o1.getMinimum() == null) {
                    return -1;
                } else if (o2.getMinimum() == null) {
                    return +1;
                }
                return o1.getMinimum().compareTo(o2.getMinimum());
            }

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
        SearchResultHitFieldDTO to = hit.getFieldValue(FIELD_TO.getField());
        SearchResultHitFieldDTO from = hit.getFieldValue(FIELD_FROM.getField());
        try {
            Date validTo =to==null || to.isNullField() ? ES_MAX_DATE : new DateTime(to.getFirstValue().toString()).toDate();
            Date validFrom =from ==null || from.isNullField() ? ES_MIN_DATE : new DateTime(from.getFirstValue().toString()).toDate();
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

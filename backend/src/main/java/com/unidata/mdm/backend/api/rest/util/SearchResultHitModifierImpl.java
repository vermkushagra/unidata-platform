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

package com.unidata.mdm.backend.api.rest.util;

import static com.unidata.mdm.backend.common.search.fields.RecordHeaderField.FIELD_ETALON_ID;
import static java.util.Collections.singletonList;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.common.search.FacetName;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.NestedSearchRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SearchApplicationException;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.fields.ClassifierDataHeaderField;
import com.unidata.mdm.backend.common.search.fields.MatchingHeaderField;
import com.unidata.mdm.backend.common.search.fields.RecordHeaderField;
import com.unidata.mdm.backend.common.search.fields.RelationHeaderField;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.configuration.application.ConfigurationUpdatesConsumer;
import com.unidata.mdm.backend.configuration.application.UnidataConfigurationProperty;
import com.unidata.mdm.backend.service.classifier.cache.CachedClassifier;
import com.unidata.mdm.backend.service.classifier.cache.CachedClassifierNodeLinkableAttribute;
import com.unidata.mdm.backend.service.classifier.cache.ClassifiersMetaModelCacheComponent;
import com.unidata.mdm.backend.service.measurement.MetaMeasurementService;
import com.unidata.mdm.backend.service.measurement.data.MeasurementUnit;
import com.unidata.mdm.backend.service.measurement.data.MeasurementValue;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.model.util.wrappers.EnumerationWrapper;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.SimpleAttributeDef;
import com.unidata.mdm.meta.SimpleDataType;

import reactor.core.publisher.Flux;

@Component
public class SearchResultHitModifierImpl implements SearchResultHitModifier, ConfigurationUpdatesConsumer {

    public enum ProcessingElements {
        LOOKUP, MEASURED, ENUM, DATE
    }

    /**
     * This logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchResultHitModifier.class);

    private final AtomicReference<DateTimeFormatter> dateDisplayFormatter = new AtomicReference<>(
            DateTimeFormatter.ofPattern(UnidataConfigurationProperty.UNIDATA_SEARCH_DISPLAY_DATE_FORMAT.getDefaultValue().get().toString()));

    private final AtomicReference<DateTimeFormatter> timeDisplayFormatter = new AtomicReference<>(
            DateTimeFormatter.ofPattern(UnidataConfigurationProperty.UNIDATA_SEARCH_DISPLAY_TIME_FORMAT.getDefaultValue().get().toString()));

    private final AtomicReference<DateTimeFormatter> dateTimeDisplayFormatter = new AtomicReference<>(
            DateTimeFormatter.ofPattern(UnidataConfigurationProperty.UNIDATA_SEARCH_DISPLAY_TIMESTAMP_FORMAT.getDefaultValue().get().toString()));
    /**
     * Search service.
     */
    @Autowired
    private SearchService searchService;
    /**
     * Meta model service
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
    /**
     *
     */
    @Autowired
    private MetaMeasurementService measurementService;

    /**
     * Nodes cache.
     */
    @Autowired
    private ClassifiersMetaModelCacheComponent cacheComponent;

    /**
     * Method provide search with after processing which replace lookup entity code id to main displayable attrs sequence.
     *
     * @param cCtx the search context
     * @param searchResult result of search by ctx
     */
    @Override
    public void modifySearchResult(@Nonnull SearchResultDTO searchResult, @Nonnull ComplexSearchRequestContext cCtx) {

        boolean isHierarhical = cCtx.getType() == ComplexSearchRequestContext.Type.HIERARCHICAL;
        if (!isHierarhical) {
            return;
        }

        // 1. Process main result as usual
        modifySearchResult(searchResult, cCtx.getMainRequest());

        boolean hasSubReturnFields = cCtx.getSupplementary().stream()
                .anyMatch(ctx -> CollectionUtils.isNotEmpty(ctx.getReturnFields()));

        // 2. Process return fields of supplementaries
        if (CollectionUtils.isEmpty(searchResult.getHits())
                || CollectionUtils.isEmpty(cCtx.getSupplementary())
                || !hasSubReturnFields) {
            return;
        }

        Map<String, SearchResultHitDTO> idsToHits = searchResult.getHits().stream()
                .collect(Collectors.toMap(
                        hit -> (String) hit.getFieldFirstValue(RecordHeaderField.FIELD_ETALON_ID.getField()),
                        hit -> hit));

        Set<String> keys = idsToHits.keySet();
        for (SearchRequestContext sCtx : cCtx.getSupplementary()) {
            if (CollectionUtils.isEmpty(sCtx.getReturnFields())) {
                continue;
            }

            final String parentIdField;
            if (sCtx.getType() == EntitySearchType.CLASSIFIER) {
                parentIdField = ClassifierDataHeaderField.FIELD_ETALON_ID_RECORD.getField();
            } else if (sCtx.getType() == EntitySearchType.ETALON_RELATION) {
                parentIdField = RelationHeaderField.FIELD_FROM_ETALON_ID.getField();
            } else if (sCtx.getType() == EntitySearchType.MATCHING) {
                parentIdField = MatchingHeaderField.FIELD_ETALON_ID.getField();
            } else {
                final String message = "Illegal type supplied for post-processing [{}]";
                LOGGER.warn(message, sCtx.getType());
                throw new SearchApplicationException(message, ExceptionId.EX_SEARCH_ILLEGAL_CHILD_TYPE_FOR_POST_PROCESSING, sCtx.getType());
            }

            if (sCtx.getType() == EntitySearchType.CLASSIFIER) {
                List<String> returnFields = new ArrayList<>();
                returnFields.add(parentIdField);
                Map<String, String> nestedFieldsTransponition = new HashMap<>();
                String nestedPath = null;
                for (String returnField : sCtx.getReturnFields()) {
                    if (!returnField.contains(SearchUtils.DOLLAR)) {
                        String attrName = StringUtils.substringAfter(returnField, SearchUtils.DOT);
                        nestedFieldsTransponition.put(attrName, returnField);
                        if (nestedPath == null) {
                            nestedPath = StringUtils.substringBefore(returnField, SearchUtils.DOT)
                                    + SearchUtils.DOT + ClassifierDataHeaderField.FIELD_CLS_NESTED_ATTRS.getField();
                        }
                    } else {
                        System.out.println(cacheComponent.getNode("", ""));
                        returnFields.add(returnField);
                    }
                }

                List<String> nestedReturnFields = new ArrayList<>();
                String clsAttrNamePath = nestedPath + SearchUtils.DOT + ClassifierDataHeaderField.FIELD_CLS_ATTR_NAME.getField();
                nestedReturnFields.add(nestedPath + SearchUtils.DOT + ClassifierDataHeaderField.FIELD_CLS_ATTR_NAME.getField());
                nestedReturnFields.add(nestedPath + SearchUtils.DOT + ClassifierDataHeaderField.FIELD_CLS_ATTR_VALUE_PREFIX.getField() + "string");
                nestedReturnFields.add(nestedPath + SearchUtils.DOT + ClassifierDataHeaderField.FIELD_CLS_ATTR_VALUE_PREFIX.getField() + "double");
                nestedReturnFields.add(nestedPath + SearchUtils.DOT + ClassifierDataHeaderField.FIELD_CLS_ATTR_VALUE_PREFIX.getField() + "long");
                nestedReturnFields.add(nestedPath + SearchUtils.DOT + ClassifierDataHeaderField.FIELD_CLS_ATTR_VALUE_PREFIX.getField() + "date");
                nestedReturnFields.add(nestedPath + SearchUtils.DOT + ClassifierDataHeaderField.FIELD_CLS_ATTR_VALUE_PREFIX.getField() + "time");
                nestedReturnFields.add(nestedPath + SearchUtils.DOT + ClassifierDataHeaderField.FIELD_CLS_ATTR_VALUE_PREFIX.getField() + "boolean");

                SearchRequestContext returnFieldsCtx = SearchRequestContext.builder((EntitySearchType) sCtx.getType(), sCtx.getEntity())
                        .form(FormFieldsGroup
                                .createAndGroup()
                                .addFormField(FormField.strictValues(SimpleDataType.STRING, parentIdField, keys)))
                        .returnFields(returnFields)
                        .form(sCtx.getForm())
                        .count(keys.size())
                        .asOf(sCtx.getAsOf())
                        .onlyQuery(true)
                        .nestedSearch(NestedSearchRequestContext.builder(
                                SearchRequestContext.builder()
                                        .fetchAll(true)
                                        .nestedPath(nestedPath)
                                        .count(1000)
                                        .returnFields(nestedReturnFields)
                                        .build())
                                .nestedQueryName(nestedPath)
                                .nestedSearchType(NestedSearchRequestContext.NestedSearchType.NESTED_OBJECTS)
                                .build())
                        .source(false)
                        .page(0)
                        .build();
                SearchResultDTO returnResultDTO = searchService.search(returnFieldsCtx);
                for (SearchResultHitDTO hit : returnResultDTO.getHits()) {
                    String etalonId = hit.getFieldFirstValue(parentIdField);
                    SearchResultHitDTO mainHit = idsToHits.get(etalonId);
                    Map<String, SearchResultHitFieldDTO> fieldsToAdd = new HashMap<>();
                    if (Objects.nonNull(mainHit)) {
                        for (SearchResultHitDTO hitForClassifierField : hit.getInnerHits().get(nestedPath)) {
                            String originAttrName = nestedFieldsTransponition.get(hitForClassifierField.getPreview().get(clsAttrNamePath).getFirstValue());
                            SearchResultHitFieldDTO value = hitForClassifierField.getPreview().values().stream()
                                    .filter(i -> !clsAttrNamePath.equals(i.getField()) && i.getFirstValue() != null).findAny()
                                    .orElse(null);
                            if (originAttrName != null && value != null) {
                                fieldsToAdd.put(originAttrName, new SearchResultHitFieldDTO(originAttrName, value.getValues()));
                            }
                        }
                    }
                    mainHit.getPreview().putAll(enrichValues(fieldsToAdd));
                }
            } else {
                List<String> returnFields = new ArrayList<>(sCtx.getReturnFields().size() + 1);
                returnFields.addAll(sCtx.getReturnFields());
                returnFields.add(parentIdField);

                SearchRequestContext returnFieldsCtx = SearchRequestContext.builder((EntitySearchType) sCtx.getType(), sCtx.getEntity())
                        .form(FormFieldsGroup
                                .createAndGroup()
                                .addFormField(FormField.strictValues(SimpleDataType.STRING, parentIdField, keys)))
                        .returnFields(returnFields)
                        .form(sCtx.getForm())
                        .count(keys.size())
                        .asOf(sCtx.getAsOf())
                        .onlyQuery(true)
                        .source(sCtx.isSource())
                        .page(0)
                        .build();

                SearchResultDTO returnResultDTO = searchService.search(returnFieldsCtx);
                returnResultDTO.getHits().forEach(hit -> {

                    String etalonId = hit.getFieldFirstValue(parentIdField);
                    SearchResultHitDTO mainHit = idsToHits.get(etalonId);
                    if (Objects.nonNull(mainHit)) {
                        mainHit.getPreview().putAll(hit.getPreview().entrySet().stream()
                                .filter(entry -> sCtx.getReturnFields().contains(entry.getKey()))
                                .collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
                    }
                });
            }
        }
    }

    private Map<? extends String, ? extends SearchResultHitFieldDTO> enrichValues(
            final Map<String, SearchResultHitFieldDTO> fields
    ) {
        final Map<String, Map<String, Function<Collection<String>, List<String>>>> valueGeneratorsMap = fields.keySet().stream()
                .map(this::classifierAndAttrPair)
                .collect(Collectors.groupingBy(Pair::getKey)).entrySet().stream()
                .map(e ->
                        Pair.of(
                                e.getKey(),
                                e.getValue().stream()
                                        .map(Pair::getValue)
                                        .map(attrName -> Pair.of(attrName, buildValueGenerator(e.getKey(), attrName)))
                                        .filter(p -> p.getValue() != null)
                                        .collect(Collectors.toMap(Pair::getKey, Pair::getValue))
                        )
                )
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        fields.forEach((key, value) -> {
            final Pair<String, String> classifierAndAttrPair = classifierAndAttrPair(key);
            final String classifierName = classifierAndAttrPair.getLeft();
            if (valueGeneratorsMap.containsKey(classifierName)) {
                final Map<String, Function<Collection<String>, List<String>>> attrsMap = valueGeneratorsMap.get(classifierName);
                final String attrName = classifierAndAttrPair.getRight();
                if (attrsMap.containsKey(attrName)) {
                    final Function<Collection<String>, List<String>> valueGenerator = attrsMap.get(attrName);
                    fields.put(
                            key,
                            new SearchResultHitFieldDTO(
                                    value.getField(),
                                    valueGenerator.apply(
                                            value.getValues().stream()
                                                    .map(Object::toString)
                                                    .collect(Collectors.toList())
                                    ).stream()
                                            .map(v -> (Object) v)
                                            .collect(Collectors.toList())
                            )
                    );
                }
            }
        });
        return fields;
    }

    private Pair<String, String> classifierAndAttrPair(String s) {
        return Pair.of(
                StringUtils.substringBefore(s, SearchUtils.DOT),
                StringUtils.substringAfter(s, SearchUtils.DOT)
        );
    }

    private final Function<String, Function<Collection<String>, List<String>>> toValueGenerator =
            name -> (Function<Collection<String>, List<String>>) values -> {
                if (CollectionUtils.isEmpty(values)) {
                    return Collections.emptyList();
                }
                final LookupEntityDef lookupEntity = metaModelService.getLookupEntityById(name);
                if (lookupEntity == null) {
                    return new ArrayList<>(values);
                }
                final List<String> mainDisplayableAttrNames = ModelUtils.findMainDisplayableAttrNamesSorted(lookupEntity);
                if (CollectionUtils.isEmpty(mainDisplayableAttrNames)) {
                    return new ArrayList<>(values);
                }

                final String codeAttr = lookupEntity.getCodeAttribute().getName();
                mainDisplayableAttrNames.add(codeAttr);
                SearchRequestContext searchCtx = SearchRequestContext.forEtalonData(lookupEntity.getName())
                        .form(FormFieldsGroup
                                .createAndGroup()
                                .addFormField(
                                        FormField.strictValues(
                                                lookupEntity.getCodeAttribute().getSimpleDataType(),
                                                codeAttr,
                                                values
                                        )
                                )
                        )
                        .returnFields(mainDisplayableAttrNames)
                        .onlyQuery(true)
                        .count(values.size())
                        .page(0)
                        .build();

                final SearchResultDTO searchResultDTO = searchService.search(searchCtx);

                if (CollectionUtils.isEmpty(searchResultDTO.getHits())) {
                    return new ArrayList<>(values);
                }

                Map<String, AttributeInfoHolder> attributesMap = metaModelService.getAttributesInfoMap(name);
                return values.stream()
                        .map(v -> {
                            SearchResultHitDTO hit = findHit(
                                    searchResultDTO,
                                    v,
                                    codeAttr
                            );
                            return extractSearchResult(
                                    hit,
                                    searchCtx.getReturnFields().subList(0, searchCtx.getReturnFields().size() - 1),
                                    attributesMap.get(codeAttr)
                            );
                        })
                        .collect(Collectors.toList());
            };

    private Function<Collection<String>, List<String>> buildValueGenerator(String classifier, String attrName) {
        final CachedClassifier clsf = cacheComponent.getClassifier(classifier);
        final Optional<String> lookupName = clsf.getNodes().values().stream()
                .flatMap(n -> n.getAttributes().values().stream().flatMap(Collection::stream))
                .filter(a -> a.getName().equals(attrName))
                .filter(a -> a instanceof CachedClassifierNodeLinkableAttribute)
                .map(a -> (CachedClassifierNodeLinkableAttribute) a)
                .filter(CachedClassifierNodeLinkableAttribute::isLookupLink)
                .map(CachedClassifierNodeLinkableAttribute::getLookupName)
                .findFirst();
        return lookupName
                .map(toValueGenerator)
                .orElse(null);
    }

    /**
     * Extracts search result.
     * @param hit the related hit
     * @param fieldNames field name
     * @param attrHolder the link attribute metadata holder
     * @return result or null
     */
    protected String extractSearchResult(SearchResultHitDTO hit, List<String> fieldNames, AttributeInfoHolder attrHolder) {

        if (hit == null || hit.getPreview().isEmpty()) {
            return null;
        }

        Map<String, AttributeInfoHolder> attrMap = Collections.emptyMap();
        if (attrHolder.showFieldNamesInDisplay()) {
            attrMap = metaModelService.getAttributesInfoMap(attrHolder.getLookupLinkName());
        }

        StringBuilder b = new StringBuilder();
        for (String name : fieldNames) {
            SearchResultHitFieldDTO fieldValue = hit.getFieldValue(name);
            if(fieldValue != null && fieldValue.isNonNullField()){

                String converted = String.valueOf(fieldValue.isCollection()
                        ? fieldValue.getFirstValue() + " (" + String.join(", ", fieldValue.getValues().subList(1, fieldValue.getValues().size()).stream()
                        .map(Object::toString).collect(Collectors.toList())) + ")"
                        : fieldValue.getFirstValue());

                if (attrHolder.showFieldNamesInDisplay()) {
                    AttributeInfoHolder targetField = attrMap.get(name);
                    converted = targetField != null
                            ? targetField.getAttribute().getDisplayName() + ": " + converted
                            : converted;
                }

                boolean firstHit = b.length() == 0;
                b.append(firstHit ? converted : " ");
                b.append(firstHit ? "" : converted);
            }
        }

        return b.length() == 0 ? null : b.toString();
    }

    /**
     *
     * @param result - search result
     * @param value - search value
     * @param codeName - name of value in preview
     * @return hit if present
     */
    protected SearchResultHitDTO findHit(SearchResultDTO result, Object value, String codeName) {
        for (SearchResultHitDTO hit : result.getHits()) {
            SearchResultHitFieldDTO field = hit.getFieldValue(codeName);
            if (field == null) {
                continue;
            }
            if (field.getValues().stream().map(String::valueOf).anyMatch(str -> str.equals(value.toString()))) {
                return hit;
            }
        }
        return null;
    }

    /**
     * Method provide search with after processing which modify all processed elements to display view.
     *
     * @param ctx the search context
     * @param searchResult result of search by ctx
     */
    @Override
    public void modifySearchResult(@Nonnull SearchResultDTO searchResult, @Nonnull SearchRequestContext ctx) {
        innerModifySearchResult(searchResult, ctx, EnumSet.allOf(ProcessingElements.class), null, null, false);
    }

    /**
     * Method provide search with after processing which modify processed elements to display view.
     *
     * @param ctx the search context
     * @param searchResult result of search by ctx
     * @param enumSet elements to process
     */
    @Override
    public void modifySearchResult(@Nonnull SearchResultDTO searchResult, @Nonnull SearchRequestContext ctx,
                                   EnumSet<ProcessingElements> enumSet) {
        innerModifySearchResult(searchResult, ctx, enumSet, null, null, false);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void modifySearchResult(SearchResultDTO searchResult, SearchRequestContext ctx, String attributesSource, String fieldPrefix) {
        innerModifySearchResult(searchResult, ctx, EnumSet.allOf(ProcessingElements.class), attributesSource, fieldPrefix, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modifySearchResult(SearchResultDTO searchResult, SearchRequestContext ctx, String attributesSource,
            String fieldPrefix, Set<ProcessingElements> elements, boolean appendDisplayValues) {
        innerModifySearchResult(searchResult, ctx, elements, attributesSource, fieldPrefix, appendDisplayValues);
    }

    @Override
    public void subscribe(Flux<Map<String, Optional<? extends Serializable>>> updates) {
        final String displayDateFormatKey = UnidataConfigurationProperty.UNIDATA_SEARCH_DISPLAY_DATE_FORMAT.getKey();
        final String displayTimeFormatKey = UnidataConfigurationProperty.UNIDATA_SEARCH_DISPLAY_TIME_FORMAT.getKey();
        final String displayDateTimeFormatKey = UnidataConfigurationProperty.UNIDATA_SEARCH_DISPLAY_TIMESTAMP_FORMAT.getKey();

        updates
                .filter(values ->
                        values.containsKey(displayDateFormatKey) && values.get(displayDateFormatKey).isPresent()
                )
                .map(values -> DateTimeFormatter.ofPattern(values.get(displayDateFormatKey).get().toString()))
                .subscribe(dateDisplayFormatter::set);

        updates
                .filter(values ->
                        values.containsKey(displayTimeFormatKey) && values.get(displayTimeFormatKey).isPresent()
                )
                .map(values -> DateTimeFormatter.ofPattern(values.get(displayTimeFormatKey).get().toString()))
                .subscribe(timeDisplayFormatter::set);

        updates
                .filter(values ->
                        values.containsKey(displayDateTimeFormatKey) && values.get(displayDateTimeFormatKey).isPresent()
                )
                .map(values -> DateTimeFormatter.ofPattern(values.get(displayDateTimeFormatKey).get().toString()))
                .subscribe(dateTimeDisplayFormatter::set);

    }

    private void innerModifySearchResult(
            @Nonnull SearchResultDTO searchResult,
            @Nonnull SearchRequestContext ctx,
            Set<ProcessingElements> processingElements,
            String attributesSourceName,
            String fieldPrefix,
            boolean append) {

        MeasurementPoint.start();
        try {

            if (CollectionUtils.isEmpty(ctx.getReturnFields())) {
                return;
            }

            Map<String, AttributeInfoHolder> attributesMap = Objects.isNull(attributesSourceName)
                    ? metaModelService.getAttributesInfoMap(ctx.getEntity())
                    : metaModelService.getAttributesInfoMap(attributesSourceName);

            // Right is the displayableField, left is codeAttrName
            Map<LookupSearchInfo, SearchRequestContext> mainDisplayableAttrsRequest = new HashMap<>();
            for (String displayableField : ctx.getReturnFields()) {

                if (isSystemDateAttribute(displayableField)) {
                    for (SearchResultHitDTO hit : searchResult.getHits()) {

                        SearchResultHitFieldDTO field = hit.getFieldValue(displayableField);
                        if (field == null || field.isNullField()) {
                            continue;
                        }

                        if (append) {
                            field.getDisplayValues().add(SearchUtils.formatForUI(field.getFirstValue().toString()));
                        } else {
                            hit.getPreview().remove(field.getField());
                            hit.getPreview().put(displayableField,
                                    new SearchResultHitFieldDTO(displayableField,
                                            singletonList(SearchUtils.formatForUI(field.getFirstValue().toString()))));
                        }
                    }
                }

                AttributeInfoHolder attr = attributesMap.get(Objects.nonNull(fieldPrefix) && displayableField.startsWith(fieldPrefix)
                        ? displayableField.substring(fieldPrefix.length())
                        : displayableField);

                if (Objects.isNull(attr)) {
                    continue;
                }

                // process links
                if (attr.isLookupLink() && processingElements.contains(ProcessingElements.LOOKUP)) {
                    Map<LookupSearchInfo, SearchRequestContext> nestedCtx
                            = createRequestToLookupEntity(searchResult, displayableField, attr);
                    if (Objects.nonNull(nestedCtx)) {
                        mainDisplayableAttrsRequest.putAll(nestedCtx);
                    }
                }

                // process measured values!
                if (attr.isMeasured() && processingElements.contains(ProcessingElements.MEASURED)) {
                    SimpleAttributeDef simpleAttributeDef = attr.narrow();
                    if (simpleAttributeDef.getMeasureSettings() != null) {
                        String valueId = simpleAttributeDef.getMeasureSettings().getValueId();
                        addShortMeasurementUnitNameToHits(searchResult, displayableField, valueId, append);
                    }
                }

                // process enum values
                if (attr.isEnumValue() && processingElements.contains(ProcessingElements.ENUM)) {
                    for (SearchResultHitDTO hit : searchResult.getHits()) {

                        SearchResultHitFieldDTO field = hit.getFieldValue(displayableField);
                        if (field == null || field.isNullField()) {
                            continue;
                        }

                        String displayValue = getEnumDisplayValues(attr.getEnumName(), field.getValues());
                        if (append) {
                            field.getDisplayValues().add(displayValue);
                        } else {
                            hit.getPreview().remove(field.getField());
                            hit.getPreview().put(displayableField,
                                    new SearchResultHitFieldDTO(displayableField,
                                            StringUtils.isBlank(displayValue) ? null : singletonList(displayValue)));
                        }
                    }
                }

                // process date values
                if (attr.isDate() && processingElements.contains(ProcessingElements.DATE)) {
                    for (SearchResultHitDTO hit : searchResult.getHits()) {

                        SearchResultHitFieldDTO field = hit.getFieldValue(displayableField);
                        if (field == null || field.isNullField()) {
                            continue;
                        }

                        String displayValue = getDateDisplayValues(
                                ((SimpleAttributeDef) attr.getAttribute()).getSimpleDataType(), field.getFirstValue());

                        if (append) {
                            field.getDisplayValues().add(displayValue);
                        } else {
                            hit.getPreview().remove(field.getField());
                            hit.getPreview().put(displayableField,
                                    new SearchResultHitFieldDTO(displayableField,
                                            StringUtils.isBlank(displayValue) ? null : singletonList(displayValue)));
                        }
                    }
                }
            }

            if (MapUtils.isNotEmpty(mainDisplayableAttrsRequest)) {
                ComplexSearchRequestContext context = ComplexSearchRequestContext.multi(mainDisplayableAttrsRequest.values());
                Map<SearchRequestContext, SearchResultDTO> result = searchService.search(context);
                for (Entry<LookupSearchInfo, SearchRequestContext> entry : mainDisplayableAttrsRequest.entrySet()) {

                    SearchResultDTO nestedSearchResult = result.get(entry.getValue());
                    if (Objects.isNull(nestedSearchResult)) {
                        continue;
                    }

                    for (SearchResultHitDTO hit : searchResult.getHits()) {

                        SearchResultHitFieldDTO field = hit.getFieldValue(entry.getKey().displayAttributeName);
                        if (field == null || field.isNullField()) {
                            continue;
                        }


                        Date asDate = ctx.getAsOf() != null ? ctx.getAsOf() :
                                SearchUtils.getDateForDisplayAttributes(new Date(),
                                    SearchUtils.parse(hit.getFieldFirstValue(RecordHeaderField.FIELD_FROM.getField())),
                                    SearchUtils.parse(hit.getFieldFirstValue(RecordHeaderField.FIELD_TO.getField())));

                        Pair<String, String> displayValueCombo
                            = getLookupDisplayValues(nestedSearchResult, entry.getKey(), field.getValues(), asDate);
                        if (append) {
                            field.getDisplayValues().add(displayValueCombo.getValue());
                            field.setSystemId(displayValueCombo.getKey());
                        } else {

                            SearchResultHitFieldDTO newHit = new SearchResultHitFieldDTO(
                                    entry.getKey().displayAttributeName,
                                    StringUtils.isBlank(displayValueCombo.getValue()) ? null : singletonList(displayValueCombo.getValue()));
                            newHit.setSystemId(displayValueCombo.getKey());

                            hit.getPreview().remove(field.getField());
                            hit.getPreview().put(entry.getKey().displayAttributeName, newHit);
                        }
                    }
                }
            }
        } finally {
            MeasurementPoint.stop();
        }
    }

    private void addShortMeasurementUnitNameToHits(@Nonnull SearchResultDTO searchResult,
                                                   @Nonnull String displayableField, @Nonnull String valueId, boolean append) {

        MeasurementValue measurementValue = measurementService.getValueById(valueId);
        MeasurementUnit measurementUnit = measurementValue == null ? null : measurementValue.getBaseUnit();
        String shortName = measurementUnit == null ? null : measurementUnit.getShortName();

        if (shortName == null) {
            return;
        }
        for (SearchResultHitDTO hit : searchResult.getHits()) {

            SearchResultHitFieldDTO oldField = hit.getFieldValue(displayableField);
            if (oldField == null || oldField.isNullField()) {
                continue;
            }

            if (append) {
                List<String> withUnits = oldField.getValues()
                        .stream()
                        .filter(Objects::nonNull)
                        .map(value -> value.toString() + " " + shortName)
                        .collect(Collectors.toList());

                oldField.getDisplayValues().addAll(withUnits);
            } else {
                List<Object> withUnits = oldField.getValues()
                        .stream()
                        .filter(Objects::nonNull)
                        .map(value -> value.toString() + " " + shortName)
                        .collect(Collectors.toList());

                SearchResultHitFieldDTO newField = new SearchResultHitFieldDTO(displayableField, withUnits);
                hit.getPreview().remove(oldField.getField());
                hit.getPreview().put(newField.getField(), newField);
            }
        }
    }

    private Map<LookupSearchInfo, SearchRequestContext> createRequestToLookupEntity(
            final SearchResultDTO searchResult,
            final String displayableField,
            final AttributeInfoHolder attr
    ) {

        String lookupEntityId = attr.getLookupLinkName();

        LookupEntityDef lookupEntityDef = metaModelService.getLookupEntityById(lookupEntityId);
        Map<String, AttributeInfoHolder> attributes = metaModelService.getAttributesInfoMap(lookupEntityId);

        List<String> displayableNames = CollectionUtils.isNotEmpty(attr.getLookupEntityDisplayAttributes())
                ? attr.getLookupEntityDisplayAttributes()
                : ModelUtils.findMainDisplayableAttrNamesSorted(lookupEntityDef);

        String codeAttrName = lookupEntityDef.getCodeAttribute().getName();

        boolean includeCodeAttribute = displayableNames.contains(codeAttrName);

        displayableNames.add(codeAttrName);
        displayableNames.add(RecordHeaderField.FIELD_FROM.getField());
        displayableNames.add(RecordHeaderField.FIELD_TO.getField());

        List<Object> ids = getCodeAttrIds(searchResult, displayableField);


        LookupSearchInfo lookupSearchInfo = new LookupSearchInfo(displayableField, codeAttrName, includeCodeAttribute,
                attr, attributes);

        return Collections.singletonMap(lookupSearchInfo, SearchRequestContext.forEtalonData(lookupEntityDef.getName())
                .form(FormFieldsGroup.createAndGroup()
                        .addFormField(FormField.strictValues(lookupEntityDef.getCodeAttribute().getSimpleDataType(), codeAttrName, ids)))
                .returnFields(displayableNames)
                .facets(Collections.singletonList(FacetName.FACET_UN_RANGED))
                .count(SearchRequestContext.MAX_PAGE_SIZE)
                .page(0)
                .build());
    }

    private List<Object> getCodeAttrIds(SearchResultDTO searchResult, String codeAttrName) {
        return searchResult.getHits()
                .stream()
                .map(SearchResultHitDTO::getPreview)
                .map(Map::values)
                .flatMap(Collection::stream)
                .filter(field -> field.getField().equals(codeAttrName))
                .filter(SearchResultHitFieldDTO::isNonNullField)
                .map(SearchResultHitFieldDTO::getValues)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map(Object::toString)
                .distinct()
                .collect(Collectors.toList());
    }

    @Nonnull
    private Pair<String, String> getLookupDisplayValues(SearchResultDTO nestedSearchResult,
                                                        LookupSearchInfo lookupSearchInfo,
                                                        List<Object> codeAttrs, Date asDate) {

        for (SearchResultHitDTO nestedHit : nestedSearchResult.getHits()) {

            SearchResultHitFieldDTO codeAttrValue = nestedHit.getFieldValue(lookupSearchInfo.codeAttributeName);
            if (codeAttrValue == null || codeAttrValue.isNullField()) {
                continue;
            }

            if (!codeAttrValue.getValues().containsAll(codeAttrs)) {
                continue;
            }


            if (!SearchUtils.dateInPeriod(asDate,
                    SearchUtils.parse(nestedHit.getFieldFirstValue(RecordHeaderField.FIELD_FROM.getField())),
                    SearchUtils.parse(nestedHit.getFieldFirstValue(RecordHeaderField.FIELD_TO.getField())))) {
                continue;
            }

            List<String> displayValues = new ArrayList<>();
            String codeAttributeValue = null;
            String etalonId = null;
            for (SearchResultHitFieldDTO hf : nestedHit.getPreview().values()) {

                if (CollectionUtils.isEmpty(hf.getValues())) {
                    continue;
                }

                if (hf.getField().equals(FIELD_ETALON_ID.getField())) {
                    etalonId = (String) hf.getFirstValue();
                    continue;
                }

                if (isSystemDateAttribute(hf.getField())) {
                    continue;
                }

                if (hf.getField().equals(lookupSearchInfo.codeAttributeName)) {
                    codeAttributeValue = String.valueOf(hf.getFirstValue());
                    if (!lookupSearchInfo.includeCodeAttribute) {
                        continue;
                    }

                    if (lookupSearchInfo.linkAttr.showFieldNamesInDisplay()) {
                        AttributeInfoHolder attr = lookupSearchInfo.targetAttrs.get(hf.getField());
                        codeAttributeValue = attr != null
                                ? attr.getAttribute().getDisplayName() + ": " + codeAttributeValue
                                : codeAttributeValue;
                    }
                }

                String converted = String.valueOf(hf.isCollection()
                        ? hf.getFirstValue() + " (" + String.join(", ", hf.getValues().subList(1, hf.getValues().size()).stream()
                        .map(Object::toString).collect(Collectors.toList())) + ")"
                        : hf.getFirstValue());

                if (lookupSearchInfo.linkAttr.showFieldNamesInDisplay()) {
                    AttributeInfoHolder attr = lookupSearchInfo.targetAttrs.get(hf.getField());
                    converted = attr != null
                            ? attr.getAttribute().getDisplayName() + ": " + converted
                            : converted;
                }

                displayValues.add(converted);
            }

            if (displayValues.isEmpty() && codeAttributeValue != null) {
                displayValues.add(codeAttributeValue);
            }

            return Pair.of(etalonId, String.join(StringUtils.SPACE, displayValues));
        }

        return Pair.of(StringUtils.EMPTY, StringUtils.EMPTY);
    }

    @Nonnull
    private String getEnumDisplayValues(String enumName, List<Object> values) {

        if (CollectionUtils.isEmpty(values)) {
            return StringUtils.EMPTY;
        }

        EnumerationWrapper ew = metaModelService.getValueById(enumName, EnumerationWrapper.class);
        if (Objects.isNull(ew)) {
            return StringUtils.EMPTY;
        }

        List<String> displayValues = new ArrayList<>();
        for (Object v : values) {
            displayValues.add(ew.getEnumerationMap().get(v));
        }

        return String.join(StringUtils.SPACE, displayValues);
    }

    private boolean isSystemDateAttribute(String fieldName) {
        return RecordHeaderField.FIELD_FROM.getField().equals(fieldName)
                || RecordHeaderField.FIELD_TO.getField().equals(fieldName)
                || RecordHeaderField.FIELD_CREATED_AT.getField().equals(fieldName)
                || RecordHeaderField.FIELD_UPDATED_AT.getField().equals(fieldName);
    }


    private String getDateDisplayValues(SimpleDataType dataType, Object value) {
        String result;
        switch (dataType) {
            case DATE:
                result = convertDate(value.toString(), DateTimeFormatter.ISO_LOCAL_DATE, dateDisplayFormatter.get());
                break;
            case TIME:
                result = convertDate(value.toString(), DateTimeFormatter.ISO_LOCAL_TIME, timeDisplayFormatter.get());
                break;
            case TIMESTAMP:
                result = convertDate(value.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME, dateTimeDisplayFormatter.get());
                break;
            default:
                result = value.toString();
        }
        return result;
    }

    private static String convertDate(String d, DateTimeFormatter fromFormatter, DateTimeFormatter toFormatter) {
        return toFormatter.format(fromFormatter.parse(d));
    }

    private class LookupSearchInfo {

        private String displayAttributeName;

        private String codeAttributeName;

        private boolean includeCodeAttribute;

        private AttributeInfoHolder linkAttr;

        private Map<String, AttributeInfoHolder> targetAttrs;

        LookupSearchInfo(String displayAttributeName, String codeAttributeName, boolean includeCodeAttribute,
                         AttributeInfoHolder linkAttr,
                         Map<String, AttributeInfoHolder> targetAttrs) {
            this.displayAttributeName = displayAttributeName;
            this.codeAttributeName = codeAttributeName;
            this.includeCodeAttribute = includeCodeAttribute;
            this.linkAttr = linkAttr;
            this.targetAttrs = targetAttrs;
        }
    }
}

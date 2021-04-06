package com.unidata.mdm.backend.api.rest.util;

import static com.unidata.mdm.backend.service.search.util.RecordHeaderField.FIELD_ETALON_ID;
import static java.util.Collections.singletonList;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.base.Optional;
import com.unidata.mdm.backend.configuration.application.ConfigurationUpdatesConsumer;
import com.unidata.mdm.backend.configuration.application.UnidataConfigurationProperty;
import com.unidata.mdm.meta.SimpleDataType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SearchApplicationException;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.search.SearchRequestType;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.service.measurement.MetaMeasurementService;
import com.unidata.mdm.backend.service.measurement.data.MeasurementUnit;
import com.unidata.mdm.backend.service.measurement.data.MeasurementValue;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.model.util.wrappers.EnumerationWrapper;
import com.unidata.mdm.backend.service.search.util.ClassifierDataHeaderField;
import com.unidata.mdm.backend.service.search.util.MatchingHeaderField;
import com.unidata.mdm.backend.service.search.util.RecordHeaderField;
import com.unidata.mdm.backend.service.search.util.RelationHeaderField;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.SimpleAttributeDef;
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
     * Method provide search with after processing which replace lookup entity code id to main displayable attrs sequence.
     *
     * @param cCtx          the search context
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

            List<String> returnFields = new ArrayList<>(sCtx.getReturnFields().size() + 1);
            returnFields.addAll(sCtx.getReturnFields());
            returnFields.add(parentIdField);

            SearchRequestContext returnFieldsCtx = SearchRequestContext.builder((EntitySearchType) sCtx.getType(), sCtx.getEntity())
                    .values(new ArrayList<>(keys))
                    .form(sCtx.getForm())
                    .search(SearchRequestType.TERM)
                    .searchFields(singletonList(parentIdField))
                    .returnFields(returnFields)
                    .count(keys.size())
                    .asOf(sCtx.getAsOf())
                    .onlyQuery(true)
                    .source(false)
                    .page(0)
                    .build();

            SearchResultDTO returnResultDTO = searchService.search(returnFieldsCtx);
            returnResultDTO.getHits().stream()
                .forEach(hit -> {

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
    /**
     * Method provide search with after processing which modify all processed elements to display view.
     *
     * @param ctx          the search context
     * @param searchResult result of search by ctx
     */
    @Override
    public void modifySearchResult(@Nonnull SearchResultDTO searchResult, @Nonnull SearchRequestContext ctx) {
        innerModifySearchResult(searchResult, ctx, EnumSet.allOf(ProcessingElements.class));
    }
    /**
     * Method provide search with after processing which modify processed elements to display view.
     *
     * @param ctx          the search context
     * @param searchResult result of search by ctx
     * @param enumSet elements to process
     */
    @Override
    public void modifySearchResult(@Nonnull SearchResultDTO searchResult, @Nonnull SearchRequestContext ctx,
                                   EnumSet<ProcessingElements> enumSet) {
        innerModifySearchResult(searchResult, ctx, enumSet);
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

    private void innerModifySearchResult(@Nonnull SearchResultDTO searchResult, @Nonnull SearchRequestContext ctx,
                                        EnumSet<ProcessingElements> processingElements) {

        Map<String, AttributeInfoHolder> attributesMap = metaModelService.getAttributesInfoMap(ctx.getEntity());
        // Right is the displayableField, left is codeAttrName
        Map<LookupSearchInfo, SearchRequestContext> mainDisplayableAttrsRequest = new HashMap<>();
        for (String displayableField : ctx.getReturnFields()) {

            if(isSystemDateAttribute(displayableField)){
                for (SearchResultHitDTO hit : searchResult.getHits()) {

                    SearchResultHitFieldDTO field = hit.getFieldValue(displayableField);
                    if (field == null || field.isNullField()) {
                        continue;
                    }

                    hit.getPreview().remove(field.getField());

                    hit.getPreview().put(displayableField,
                            new SearchResultHitFieldDTO(displayableField, singletonList(SearchUtils.formatForUI(field.getFirstValue().toString()))));
                }
            }

            AttributeInfoHolder attr = attributesMap.get(displayableField);

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
                    addShortMeasurementUnitNameToHits(searchResult, displayableField, valueId);
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
                    hit.getPreview().remove(field.getField());
                    hit.getPreview().put(displayableField,
                            new SearchResultHitFieldDTO(displayableField,
                                    StringUtils.isBlank(displayValue) ? null : singletonList(displayValue)));
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
                            ((SimpleAttributeDef)attr.getAttribute()).getSimpleDataType(), field.getFirstValue());

                    hit.getPreview().remove(field.getField());
                    hit.getPreview().put(displayableField,
                            new SearchResultHitFieldDTO(displayableField,
                                    StringUtils.isBlank(displayValue) ? null : singletonList(displayValue)));
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

                    String displayValue = getLookupDisplayValues(nestedSearchResult, entry.getKey(), field.getValues());
                    hit.getPreview().remove(field.getField());
                    hit.getPreview().put(entry.getKey().displayAttributeName,
                            new SearchResultHitFieldDTO(entry.getKey().displayAttributeName,
                                    StringUtils.isBlank(displayValue) ? null : singletonList(displayValue)));
                }
            }
        }
    }

    private void addShortMeasurementUnitNameToHits(@Nonnull SearchResultDTO searchResult,
            @Nonnull String displayableField, @Nonnull String valueId) {

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

    private Map<LookupSearchInfo, SearchRequestContext>
        createRequestToLookupEntity(SearchResultDTO searchResult, String displayableField, AttributeInfoHolder attr) {

        String lookupEntityId = attr.getLookupLinkName();

        LookupEntityDef lookupEntityDef = metaModelService.getLookupEntityById(lookupEntityId);
        Map<String, AttributeInfoHolder> attributes = metaModelService.getAttributesInfoMap(lookupEntityId);

        List<String> displayableNames = CollectionUtils.isNotEmpty(attr.getLookupEntityDisplayAttributes())
                ? attr.getLookupEntityDisplayAttributes()
                : ModelUtils.findMainDisplayableAttrNamesSorted(lookupEntityDef);

        String codeAttrName = lookupEntityDef.getCodeAttribute().getName();

        boolean includeCodeAttribute = displayableNames.contains(codeAttrName);

        displayableNames.add(codeAttrName);

        List<Object> ids = getCodeAttrIds(searchResult, displayableField);

        LookupSearchInfo lookupSearchInfo = new LookupSearchInfo(displayableField, codeAttrName, includeCodeAttribute,
                attr, attributes);

        return Collections.singletonMap(lookupSearchInfo, SearchRequestContext.forEtalonData(lookupEntityDef.getName())
                  .values(ids)
                  .search(SearchRequestType.TERM)
                  .searchFields(singletonList(codeAttrName))
                  .returnFields(displayableNames)
                  .count(ids.size())
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
    private String getLookupDisplayValues(SearchResultDTO nestedSearchResult, LookupSearchInfo lookupSearchInfo, List<Object> codeAttrs) {

        for (SearchResultHitDTO nestedHit : nestedSearchResult.getHits()) {

            SearchResultHitFieldDTO codeAttrValue = nestedHit.getFieldValue(lookupSearchInfo.codeAttributeName);
            if (codeAttrValue == null || codeAttrValue.isNullField()) {
                continue;
            }

            if (!codeAttrValue.getValues().containsAll(codeAttrs)) {
                continue;
            }

            List<String> displayValues = new ArrayList<>();
            String codeAttributeValue = null;
            for (SearchResultHitFieldDTO hf : nestedHit.getPreview().values()) {

                if (hf.getField().equals(FIELD_ETALON_ID.getField()) || CollectionUtils.isEmpty(hf.getValues())) {
                    continue;
                }

                if(hf.getField().equals(lookupSearchInfo.codeAttributeName)){
                    codeAttributeValue = String.valueOf(hf.getFirstValue());
                    if(!lookupSearchInfo.includeCodeAttribute){
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

            return String.join(StringUtils.SPACE, displayValues);
        }

        return StringUtils.EMPTY;
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

    private boolean isSystemDateAttribute(String fieldName){
        return RecordHeaderField.FIELD_FROM.getField().equals(fieldName)
                || RecordHeaderField.FIELD_TO.getField().equals(fieldName)
                || RecordHeaderField.FIELD_CREATED_AT.getField().equals(fieldName)
                || RecordHeaderField.FIELD_UPDATED_AT.getField().equals(fieldName);
    }


    private String getDateDisplayValues(SimpleDataType dataType, Object value){
        String result;
        switch (dataType){
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

    private static String convertDate(String d,DateTimeFormatter fromFormatter ,DateTimeFormatter toFormatter) {
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

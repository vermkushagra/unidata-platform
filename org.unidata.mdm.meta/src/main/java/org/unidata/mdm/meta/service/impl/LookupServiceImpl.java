package org.unidata.mdm.meta.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.meta.service.LookupService;
import org.unidata.mdm.meta.service.MetaMeasurementService;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.type.search.EntityIndexType;
import org.unidata.mdm.meta.type.search.RecordHeaderField;
import org.unidata.mdm.search.context.SearchRequestContext;
import org.unidata.mdm.search.dto.SearchResultDTO;
import org.unidata.mdm.search.dto.SearchResultHitDTO;
import org.unidata.mdm.search.dto.SearchResultHitFieldDTO;
import org.unidata.mdm.search.service.SearchService;
import org.unidata.mdm.search.type.form.FormField;
import org.unidata.mdm.search.type.form.FormFieldsGroup;
import org.unidata.mdm.search.util.SearchUtils;
import org.unidata.mdm.system.util.TextUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @author Dmitry Kopin on 31.05.2019.
 */
@Component
public class LookupServiceImpl implements LookupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LookupServiceImpl.class);

    @Autowired
    SearchService searchService;

    @Autowired
    private MetaModelService metaModelService;

    @Autowired
    private MetaMeasurementService measurementService;

    private String displayValuesNotFoundMessage = null;

    public LookupServiceImpl() {
        super();
    }

    private LoadingCache<LookupKey, SearchResultDTO> lookupSearchCache = CacheBuilder.newBuilder()
            .expireAfterWrite(3, TimeUnit.SECONDS)
            .build(new LookupSearchCacheLoader());

    @Override
    public Pair<String, String> getLookupDisplayNameById(String lookupName, Object codeAttrValue, Date validFrom, Date validTo, List<String> toBuildAttrs, boolean useAttributeNameForDisplay) {

        if (displayValuesNotFoundMessage == null) {
            displayValuesNotFoundMessage = TextUtils.getText("app.error.no.display.value");
        }
        List<String> displayAttrs = CollectionUtils.isNotEmpty(toBuildAttrs) ? toBuildAttrs : metaModelService.findMainDisplayableAttrNamesSorted(lookupName);
        Map<String, AttributeModelElement> attrsMap = metaModelService.getAttributesInfoMap(lookupName);
        AttributeModelElement codeAttr = attrsMap.values().stream()
                .filter(AttributeModelElement::isCode)
                .findFirst()
                .orElse(null);

        if (Objects.isNull(codeAttr)) {
            return null;
        }

        try {

            SearchResultDTO searchResult = lookupSearchCache.get(new LookupKey(lookupName, codeAttr, codeAttrValue, displayAttrs));

            List<String> arrtValues = new ArrayList<>();
            SearchResultHitDTO hit = searchResult.getHits().stream()
                    .filter(r -> {
                        Date lookupValidFrom = SearchUtils.parse(r.getFieldFirstValue(RecordHeaderField.FIELD_FROM.getName()));
                        Date lookupValidTo = SearchUtils.parse(r.getFieldFirstValue(RecordHeaderField.FIELD_TO.getName()));
                        return ((validTo == null || lookupValidFrom == null || !lookupValidFrom.after(validTo))
                                && ((validFrom == null) || lookupValidTo == null || !lookupValidTo.before(validFrom)));
                    })
                    .findFirst()
                    .orElse(null);

            if (hit == null) {
                return Pair.of(null, displayValuesNotFoundMessage);
            }

            for (String attr : displayAttrs) {
                SearchResultHitFieldDTO hf = hit.getFieldValue(attr);

                // UN-7814
                if (hf != null && !hf.isEmpty()) {

                    AttributeModelElement attrHolder = attrsMap.get(hf.getField());
                    String converted;
                    if (attrHolder != null && attrHolder.isArray()) {
                        converted = "[" + hf.getValues().subList(0, hf.getValues().size()).stream()
                                .filter(Objects::nonNull)
                                .map(Object::toString)
                                .collect(Collectors.joining(", ")) + "]";
                    } else {
                        converted = String.valueOf(hf.isCollection()
                                ? hf.getFirstValue() + " (" + String.join(", ", hf.getValues().subList(1, hf.getValues().size()).stream()
                                .filter(Objects::nonNull)
                                .map(Object::toString)
                                .collect(Collectors.toList())) + ")"
                                : hf.getFirstValue());
                        if (attrHolder.isMeasured()) {
                            converted += " " + measurementService.getUnitById(
                                    attrHolder.getMeasured().getValueId(),
                                    attrHolder.getMeasured().getDefaultUnitId())
                            .getShortName();
                        }

                    }

                    if (useAttributeNameForDisplay) {
                        converted = attrHolder != null
                                ? attrHolder.getDisplayName() + ": " + converted
                                : converted;
                    }

                    arrtValues.add(converted);
                }
            }

            return Pair.of(hit.getFieldFirstValue(RecordHeaderField.FIELD_ETALON_ID.getName()), String.join(StringUtils.SPACE, arrtValues));
        } catch (Exception e) {
            LOGGER.error("Can't get lookup display name", e);
            return Pair.of(null, displayValuesNotFoundMessage);
        }
    }

    private class LookupSearchCacheLoader extends CacheLoader<LookupKey, SearchResultDTO> {

        /**
         * Load.
         *
         * @param ctx
         * @return search result
         * @throws Exception the exception
         */
        /*
         * (non-Javadoc)
         *
         * @see com.google.common.cache.CacheLoader#load(java.lang.Object)
         */
        @Override
        public SearchResultDTO load(LookupKey lookupKey) {

            FormFieldsGroup restrictions = FormFieldsGroup.createAndGroup(
                    FormField.strictValue(lookupKey.codeAttr.getValueType().toSearchType(), lookupKey.codeAttr.getPath(), lookupKey.codeValue),
                    FormField.booleanValue(RecordHeaderField.FIELD_PUBLISHED.getName(), true));

            SearchRequestContext ctx = SearchRequestContext.builder(EntityIndexType.RECORD, lookupKey.entityName)
                    .form(restrictions)
                    .returnFields(ListUtils.union(lookupKey.returnFields, Arrays.asList(
                            RecordHeaderField.FIELD_FROM.getName(),
                            RecordHeaderField.FIELD_TO.getName(),
                            RecordHeaderField.FIELD_ETALON_ID.getName(),
                            lookupKey.codeAttr.getPath())))
                    .onlyQuery(true)
                    .count(100)
                    .build();

            return searchService.search(ctx);
        }
    }

    private class LookupKey {
        private String entityName;
        private AttributeModelElement codeAttr;
        private Object codeValue;
        private List<String> returnFields;

        public LookupKey(String entityName, AttributeModelElement codeAttr, Object codeValue, List<String> returnFields) {
            this.entityName = entityName;
            this.codeAttr = codeAttr;
            this.codeValue = codeValue;
            this.returnFields = returnFields;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((entityName == null) ? 0 : entityName.hashCode());
            result = prime * result + ((codeAttr == null) ? 0 : codeAttr.hashCode());
            result = prime * result + ((codeValue == null) ? 0 : codeValue.hashCode());
            result = prime * result + ((returnFields == null) ? 0 : returnFields.hashCode());
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            LookupKey other = (LookupKey) obj;
            if (entityName == null) {
                if (other.entityName != null) {
                    return false;
                }
            } else if (!entityName.equals(other.entityName)) {
                return false;
            }

            if (codeAttr == null) {
                if (other.codeAttr != null) {
                    return false;
                }
            } else if (!codeAttr.equals(other.codeAttr)) {
                return false;
            }

            if (codeValue == null) {
                if (other.codeValue != null) {
                    return false;
                }
            } else if (!codeValue.equals(other.codeValue)) {
                return false;
            }
            if (returnFields == null) {
                if (other.returnFields != null) {
                    return false;
                }
            } else if (!returnFields.equals(other.returnFields)) {
                return false;
            }

            return true;
        }
    }
}

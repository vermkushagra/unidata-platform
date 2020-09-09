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

package com.unidata.mdm.backend.service.data.listener.classifier;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.GetClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.dto.GetClassifierDTO;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeArrayAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeSimpleAttrDTO;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchRequestOperator;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.ArrayValue;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.CodeLinkValue;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.service.classifier.ClsfService;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.data.listener.record.AbstractDataRecordAttributesProcessingExecutor;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.meta.LookupEntityDef;

public class ClassifierGetFillValuesAfterExecutor  extends AbstractDataRecordAttributesProcessingExecutor
        implements DataRecordAfterExecutor<GetClassifierDataRequestContext> {

    private ClsfService clsfService;

    private MetaModelServiceExt metaModelService;

    private SearchService searchService;

    @Autowired
    public void setClsfService(ClsfService clsfService) {
        this.clsfService = clsfService;
    }

    @Autowired
    public void setMetaModelService(MetaModelServiceExt metaModelService) {
        this.metaModelService = metaModelService;
    }

    @Autowired
    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public boolean execute(GetClassifierDataRequestContext ctx) {

        GetClassifierDTO classifierData = ctx.getFromStorage(StorageId.CLASSIFIER_DATA);

        // WF may create null response
        // for still invisible classifications
        if (Objects.isNull(classifierData)) {
            return true;
        }

        ClsfNodeDTO node = clsfService.getNodeWithAttrs(
                classifierData.getClassifierKeys().getNodeId(),
                classifierData.getClassifierKeys().getName(),
                true
        );
        Map<String, Attribute> attrs = classifierData.getEtalon().getAllAttributes().stream()
                .collect(Collectors.toMap(Attribute::getName, Function.identity()));
        if (MapUtils.isNotEmpty(attrs)) {
            final Optional<ClsfNodeSimpleAttrDTO> simpleAttr = node.getNodeSimpleAttrs().stream()
                    .filter(n -> attrs.containsKey(n.getAttrName()))
                    .findFirst();
            if (simpleAttr.isPresent() && StringUtils.isNotBlank(simpleAttr.get().getLookupEntityType())) {
                fillAttributesDisplayValues(
                        attrs.get(simpleAttr.get().getAttrName()),
                        simpleAttr.get().getLookupEntityType()
                );
            }
            final Optional<ClsfNodeArrayAttrDTO> arrayAttr = node.getNodeArrayAttrs().stream()
                    .filter(n -> attrs.containsKey(n.getAttrName()))
                    .findFirst();
            if (arrayAttr.isPresent() && StringUtils.isNotBlank(arrayAttr.get().getLookupEntityType())) {
                fillAttributesDisplayValues(
                        attrs.get(arrayAttr.get().getAttrName()),
                        arrayAttr.get().getLookupEntityType()
                );
            }
        }
        return true;
    }

    private void fillAttributesDisplayValues(final Attribute attribute, final String lookupEntityName) {
        final LookupEntityDef lookupEntity = metaModelService.getLookupEntityById(lookupEntityName);
        final List<String> mainDisplayableAttrNames = ModelUtils.findMainDisplayableAttrNamesSorted(lookupEntity);
        if (CollectionUtils.isEmpty(mainDisplayableAttrNames)) {
            return;
        }
        final String codeAttributeName = lookupEntity.getCodeAttribute().getName();
        mainDisplayableAttrNames.add(codeAttributeName);

        List<Object> searchValues;
        if (attribute.getAttributeType() == Attribute.AttributeType.ARRAY) {
            searchValues = attribute.isEmpty()
                    ? Collections.emptyList()
                    : ((ArrayAttribute<?>) attribute).getValue().stream()
                    .map(ArrayValue::getValue)
                    .collect(Collectors.toList());
        }
        else {
            searchValues = ((SimpleAttribute<?>) attribute).getValue() == null
                    ? Collections.emptyList()
                    : Collections.singletonList(((SimpleAttribute<?>) attribute).getValue());
        }

        if (CollectionUtils.isEmpty(searchValues)) {
            return;
        }

        SearchRequestContext searchCtx = SearchRequestContext.forEtalonData(lookupEntity.getName())
                .operator(SearchRequestOperator.OP_OR)
                .form(FormFieldsGroup
                        .createAndGroup()
                        .addFormField(FormField.strictValues(
                                lookupEntity.getCodeAttribute().getSimpleDataType(),
                                codeAttributeName,
                                searchValues)))
                .returnFields(mainDisplayableAttrNames)
                .onlyQuery(true)
                .count(searchValues.size())
                .page(0)
                .build();

        final SearchResultDTO searchResultDTO = searchService.search(searchCtx);

        if (CollectionUtils.isEmpty(searchResultDTO.getHits())) {
            return;
        }

        Map<String, AttributeInfoHolder> attributesMap = metaModelService.getAttributesInfoMap(lookupEntityName);
        if (attribute.getAttributeType() == Attribute.AttributeType.ARRAY) {
            for (ArrayValue<?> arrayValue : ((ArrayAttribute<?>) attribute).getValue()) {
                SearchResultHitDTO hit = findHit(
                        searchResultDTO,
                        arrayValue.getValue(),
                        searchCtx.getReturnFields().get(searchCtx.getReturnFields().size() - 1)
                );
                String displayValue = extractSearchResult(
                        hit,
                        searchCtx.getReturnFields().subList(0, searchCtx.getReturnFields().size()-1),
                        attributesMap.get(codeAttributeName)
                );

                if (arrayValue instanceof CodeLinkValue) {
                    CodeLinkValue clv = (CodeLinkValue) arrayValue;
                    clv.setLinkEtalonId(hit.getId());
                }

                arrayValue.setDisplayValue(displayValue);
            }
        } else {
            SimpleAttribute<?> simpleAttribute = (SimpleAttribute<?>) attribute;
            SearchResultHitDTO hit = findHit(searchResultDTO, simpleAttribute.getValue(), searchCtx.getReturnFields().get(searchCtx.getReturnFields().size() - 1));
            String displayValue = extractSearchResult(
                    hit,
                    searchCtx.getReturnFields().subList(0, searchCtx.getReturnFields().size() - 1),
                    attributesMap.get(codeAttributeName)
            );
            simpleAttribute.setDisplayValue(displayValue);
            ((CodeLinkValue) attribute).setLinkEtalonId(displayValue != null ? hit.getId() : null);
        }
    }
}

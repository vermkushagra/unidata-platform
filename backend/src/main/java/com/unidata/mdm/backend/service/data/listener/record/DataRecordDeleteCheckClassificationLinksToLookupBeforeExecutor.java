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

package com.unidata.mdm.backend.service.data.listener.record;

import static com.unidata.mdm.backend.common.search.FormField.strictString;
import static com.unidata.mdm.backend.common.search.FormField.strictValue;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.NestedSearchRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.fields.ClassifierDataHeaderField;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.service.classifier.ClsfService;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.search.impl.AdminAgentComponent;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.SimpleDataType;

/**
 * @author Mikhail Mikhailov
 *         'Delete' pre-check validator.
 */
public class DataRecordDeleteCheckClassificationLinksToLookupBeforeExecutor
    implements DataRecordBeforeExecutor<DeleteRequestContext>, AbstractDataRecordDeleteCommonExecutor<DeleteRequestContext> {


    private MetaModelService metaModelService;

    private ClsfService clsfService;

    private SearchService searchService;

    @Autowired
    public void setMetaModelService(MetaModelService metaModelService) {
        this.metaModelService = metaModelService;
    }

    @Autowired
    public void setClsfService(ClsfService clsfService) {
        this.clsfService = clsfService;
    }

    @Autowired
    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Constructor.
     */
    public DataRecordDeleteCheckClassificationLinksToLookupBeforeExecutor() {
        super();
    }
    /**
     * Execute.
     */
    @Override
    public boolean execute(DeleteRequestContext ctx) {

        final EtalonRecord etalonRecord = getCurrentEtalonRecord(ctx);
        if(etalonRecord == null) {
            return true;
        }

        final String lookupEntityName = etalonRecord.getInfoSection().getEntityName();
        final LookupEntityDef lookupEntity = metaModelService.getLookupEntityById(lookupEntityName);
        if (lookupEntity != null) {
            final CodeAttribute<?> attribute = (CodeAttribute<?>) etalonRecord.getAttribute(lookupEntity.getCodeAttribute().getName());
            if (attribute.getValue() == null) {
                return true;
            }
            if (clsfService.containsCodeAttrsValue(lookupEntity, String.valueOf(attribute.getValue()))) {
                throw new BusinessException(
                        "Lookup entity has links from classier data.",
                        ExceptionId.EX_LOOKUP_ENTITY_HAS_CLASSIFIER_DATA,
                        lookupEntity.getName()
                );
            }

            final List<ClsfNodeDTO> nodes = clsfService.findNodesWithLookupAttributes(lookupEntity);
            if (CollectionUtils.isEmpty(nodes)) {
                return true;
            }

            if (lookupEntityValueUsedInData(lookupEntityName, attribute.getValue(), nodes)) {
                throw new BusinessException(
                        "Lookup entity has links from classier data.",
                        ExceptionId.EX_LOOKUP_ENTITY_HAS_CLASSIFIER_DATA,
                        lookupEntity.getName()
                );
            }
        }
        return true;
    }

    private boolean lookupEntityValueUsedInData(
            final String lookupEntityName,
            final Object value,
            final Collection<ClsfNodeDTO> nodes
    ) {
        final Map<String, List<ClsfNodeDTO>> nodesMap = nodes.stream()
                .collect(Collectors.groupingBy(ClsfNodeDTO::getClsfName));
        final List<Pair<String, List<Pair<FormField, NestedSearchRequestContext>>>> requests = nodesMap.entrySet().stream()
                .map(e -> {
                    final String clsfName = e.getKey();

                    final String classifierNodeField = classifierNodeField(clsfName);

                    final List<Pair<FormField, NestedSearchRequestContext>> subRequests =
                            classifierSubrequests(lookupEntityName, value, e, clsfName, classifierNodeField);

                    return Pair.of(clsfName, subRequests);
                })
                .collect(Collectors.toList());
        for (EntityDef entityDef : metaModelService.getEntitiesList()) {
            final HashSet<String> classifiers = new HashSet<>(entityDef.getClassifiers());
            final List<SearchRequestContext> subRequests = requests.stream()
                    .filter(r -> classifiers.contains(r.getKey()))
                    .flatMap(r -> r.getValue().stream())
                    .map(r -> SearchRequestContext.forEtalonClassifier(entityDef.getName())
                            .form(FormFieldsGroup.createAndGroup(r.getLeft()))
                            .nestedSearch(r.getRight())
                            .countOnly(true)
                            .build()
                    )
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(subRequests)) {
                final ComplexSearchRequestContext complexSearchRequestContext =
                        ComplexSearchRequestContext.multi(subRequests);

                final Map<SearchRequestContext, SearchResultDTO> search = searchService.search(complexSearchRequestContext);
                for (SearchResultDTO searchResultDTO : search.values()) {
                    if (searchResultDTO.getTotalCount() > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String classifierNodeField(final String clsfName) {
        return String.join(
                SearchUtils.DOT,
                clsfName,
                ClassifierDataHeaderField.FIELD_NODES.getField(),
                ClassifierDataHeaderField.FIELD_NODE_ID.getField()
        );
    }

    private List<Pair<FormField, NestedSearchRequestContext>> classifierSubrequests(
            final String lookupEntityName,
            final Object value,
            final Map.Entry<String, List<ClsfNodeDTO>> entry,
            final String clsfName,
            final String classifierNodeField
    ) {
        return entry.getValue().stream().map(n -> {
            final FormField nodeIdFormField = strictString(classifierNodeField, n.getNodeId());


            final FormFieldsGroup orGroup = FormFieldsGroup.createOrGroup();
            n.getAllNodeAttrs().stream()
                    .filter(a -> lookupEntityName.equals(a.getLookupEntityType()))
                    .map(a -> {
                        final String classifierAttrNameField = String.join(
                                ".",
                                clsfName,
                                ClassifierDataHeaderField.FIELD_CLS_NESTED_ATTRS.getField(),
                                ClassifierDataHeaderField.FIELD_CLS_ATTR_NAME.getField()
                        );

                        final FormField attrNameFormField = strictString(classifierAttrNameField, a.getAttrName());

                        String valueFieldName = "";
                        switch (a.getLookupEntityCodeAttributeType()) {
                            case INTEGER:
                                valueFieldName = AdminAgentComponent.INTEGER_VALUE_FIELD;
                                break;
                            case STRING:
                                valueFieldName = AdminAgentComponent.STRING_VALUE_FIELD;
                                break;
                        }

                        final String classifierAttrValueField = String.join(
                                ".",
                                clsfName,
                                ClassifierDataHeaderField.FIELD_CLS_NESTED_ATTRS.getField(),
                                valueFieldName
                        );

                        final FormField valueFormField = strictValue(
                                SimpleDataType.valueOf(a.getLookupEntityCodeAttributeType().name()),
                                classifierAttrValueField,
                                value
                        );
                        return FormFieldsGroup.createAndGroup(attrNameFormField, valueFormField);
                    }).forEach(orGroup::addChildGroup);

            final NestedSearchRequestContext nestedSearchRequestContext = NestedSearchRequestContext.builder(
                    SearchRequestContext.builder()
                            .nestedPath(
                                    String.join(
                                            SearchUtils.DOT,
                                            clsfName,
                                            ClassifierDataHeaderField.FIELD_CLS_NESTED_ATTRS.getField()
                                    )
                            )
                            .form(orGroup)
                            .count(50000)
                            .source(false)
                            .build()
            )
                    .nestedQueryName(clsfName)
                    .nestedSearchType(NestedSearchRequestContext.NestedSearchType.NESTED_OBJECTS)
                    .build();

            return Pair.of(nodeIdFormField, nestedSearchRequestContext);
        }).collect(Collectors.toList());
    }
}

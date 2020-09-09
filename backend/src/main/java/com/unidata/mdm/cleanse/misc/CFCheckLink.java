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

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT2;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT2;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionOutputParam;
import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchRequestOperator;
import com.unidata.mdm.backend.common.search.fields.RecordHeaderField;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.service.ServiceUtils;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.SimpleDataType;

public class CFCheckLink extends BasicCleanseFunctionAbstract implements CFSystemCleanseFunction {
    /**
     * Search service.
     */
    private SearchService searchService;
    /**
     * Model Service
     */
    private MetaModelService modelService;
    /**
     * Instantiates a new cleanse function abstract.
     */
    public CFCheckLink() {
        super(CFCheckLink.class);
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

        // 1. Invalid input - lookup entity name is null
        String lookupEntityName = Objects.nonNull(param2) && !param2.isEmpty()
                ? param2.toSingletonValue()
                : null;

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
            throw new CleanseFunctionExecutionException(ctx.getCleanseFunctionName(),
                    MessageUtils.getMessage("app.cleanse.validation.lookupEntity.notExist"));
        }

        // 4. Collect current values and compare them with input. Report missing.
        Set<Object> current = collectCurrentState(lookupEntityDef, check);
        Pair<Set<Object>, Set<Attribute>> failed = collectMissingValues(check, current);

        // 5. Stop on missing values in links
        if (CollectionUtils.isNotEmpty(failed.getKey())) {
            ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT1, Boolean.FALSE));
            ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT2,
                    MessageUtils.getMessage("app.cleanse.validation.lookupEntityRecord.notExist", failed.getKey().toString())));
            ctx.failedValidations().addAll(createPathCollection(failed.getValue()));
            return;
        }

        ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT1, Boolean.TRUE));
        ctx.putOutputParam(CleanseFunctionOutputParam.of(OUTPUT2, StringUtils.EMPTY));
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
    private Set<Object> collectCurrentState(LookupEntityDef lookupEntityDef, Map<Object, List<Attribute>> values) {

        // 1. Request
        SearchRequestContext ctx = SearchRequestContext.forEtalonData(lookupEntityDef.getName())
                .form(FormFieldsGroup.createAndGroup(
                        FormField.strictValue(SimpleDataType.BOOLEAN, RecordHeaderField.FIELD_DELETED.getField(), Boolean.FALSE),
                        FormField.strictValue(SimpleDataType.BOOLEAN, RecordHeaderField.FIELD_PUBLISHED.getField(), Boolean.TRUE),
                        FormField.strictValues(
                                lookupEntityDef.getCodeAttribute().getSimpleDataType(),
                                lookupEntityDef.getCodeAttribute().getName(),
                                values.keySet())))
                .returnFields(lookupEntityDef.getCodeAttribute().getName())
                .operator(SearchRequestOperator.OP_OR)
                .page(0)
                .count(Integer.MAX_VALUE)
                .totalCount(true)
                .onlyQuery(true)
                .build();

        // 2. Execute
        SearchResultDTO searchResult = searchService.search(ctx);

        // 3. Post-process
        if (CollectionUtils.isEmpty(searchResult.getHits())) {
            return Collections.emptySet();
        }

        Set<Object> current = new HashSet<>();
        for (SearchResultHitDTO hit : searchResult.getHits()) {

            List<Object> codeValues = hit.getFieldValues(lookupEntityDef.getCodeAttribute().getName());
            if (CollectionUtils.isNotEmpty(codeValues)) {
                codeValues.forEach(codeValue -> current.add(codeValue instanceof Number
                            ? ((Number) codeValue).longValue()
                            : codeValue.toString()));
            }
        }

        return current;
    }
    /**
     * Gets the not presented values.
     *
     * @param check the values to check
     * @param current the current state
     * @return the not present values
     */
    private Pair<Set<Object>, Set<Attribute>> collectMissingValues(
            Map<Object, List<Attribute>> check, Set<Object> current) {

        Set<Object> missingValues = new HashSet<>();
        Set<Attribute> failedAttributes = new HashSet<>();
        for (Entry<Object, List<Attribute>> checkEntry : check.entrySet()) {
            if (!current.contains(checkEntry.getKey())) {
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
}

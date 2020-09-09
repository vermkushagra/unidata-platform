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

package com.unidata.mdm.backend.service.matching.data;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.ValidationResult;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.service.matching.algorithms.AlgorithmType;

public class MatchingRule {
    /**
     * Business limitation of rule name.
     */
    private static final int RULE_NAME_MAX_LENGTH = 255;
    private Integer id;
    private String name;
    private String description;
    private String entityName;
    private boolean withPreprocessing = false;
    private boolean autoMerge = false;
    private boolean active;
    private Collection<MatchingAlgorithm> matchingAlgorithms;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Nonnull
    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public boolean isAutoMerge() {
        return autoMerge;
    }

    public void setAutoMerge(boolean autoMerge) {
        this.autoMerge = autoMerge;
    }


    public Collection<MatchingAlgorithm> getMatchingAlgorithms() {
        return Objects.isNull(matchingAlgorithms) ? Collections.emptyList() : matchingAlgorithms;
    }

    public void setMatchingAlgorithms(Collection<MatchingAlgorithm> matchingAlgorithms) {
        this.matchingAlgorithms = matchingAlgorithms;
    }

    /**
     * Return unique value for cluster which help to recognize it.
     * Return null if rule is not exact.
     *
     * @param etalonRecord - etalon record
     * @return cluster id
     */
    @Nullable
    @Deprecated
    public String getClusterIdentifier(@Nonnull DataRecord etalonRecord) {
        Hasher hasher = Hashing.murmur3_128(getId()).newHasher();
        getMatchingAlgorithms().forEach(algo -> hasher.putObject(etalonRecord, algo.getFunnel()));
        return BaseEncoding.base16().encode(hasher.hash().asBytes());
    }

    @Nullable
    public List<Object> constuct(@Nonnull DataRecord record) {

        List<Object> result = new ArrayList<>(getMatchingAlgorithms().size() + 1);
        getMatchingAlgorithms().forEach(a -> result.addAll(a.construct(record)));
        if (CollectionUtils.isNotEmpty(result)) {

            boolean hasNMNAlgorithm = matchingAlgorithms.stream()
                        .anyMatch(al -> al.getAlgorithmType() == AlgorithmType.EXACT_NULL_MATCH_NOTHING ||
                                        al.getAlgorithmType() == AlgorithmType.EXACT_EXCLUDED_VALUE_MATCH_NOTHING);
            // Null match nothing is active
            // Null match nothing is therefore a property of a rule and not of the algorithm
            if (hasNMNAlgorithm && getMatchingAlgorithms().size() > result.size()) {
                return Collections.emptyList();
            }

            result.add(0, id);
        }

        return result;
    }

    public FormFieldsGroup getFormFieldGroup(@Nonnull DataRecord etalonRecord, Map<String, AttributeInfoHolder> attrs){
        FormFieldsGroup result = FormFieldsGroup.createAndGroup();
        getMatchingAlgorithms().forEach(a -> result.addChildGroup(a.getFormFieldGroup(etalonRecord, attrs)));
        result.getChildGroups().removeIf(Objects::isNull);
        if(CollectionUtils.isEmpty(result.getChildGroups()) && CollectionUtils.isEmpty(result.getFormFields())){
            return null;
        }
        return result;
    }


    /**
     * Throw an Exception in case when something wrong with inner state of rule
     */
    public Collection<ValidationResult> checkCompleteness() {
        Collection<ValidationResult> validationErrors = new ArrayList<>(5);
        String messageEntityName = isBlank(getEntityName()) ? "Не указанно" : getEntityName();
        String messageName = isBlank(getName()) ? "Не указанно" : getName();
        if (isEmpty(getMatchingAlgorithms())) {
            ValidationResult validation = new ValidationResult(
                    "Matching rule [{}] in entity [{}] should contains some algorithms",
                    ExceptionId.EX_MATCHING_NEED_ALGORITHMS.getCode(), messageName, messageEntityName);
            validationErrors.add(validation);
        } else {
            getMatchingAlgorithms().stream()
                                   .map(MatchingAlgorithm::checkCompleteness)
                                   .filter(cl -> !cl.isEmpty())
                                   .map(cl -> new ValidationResult(
                                           "Matching rule [{}] in entity [{}] contains incorrect algorithms", cl,
                                           ExceptionId.EX_MATCHING_RULE_INCORRECT_INNER_ALGOS.getCode(), messageName,
                                           messageEntityName))
                                   .collect(Collectors.toCollection(() -> validationErrors));
        }

        if (isBlank(getName())) {
            ValidationResult validation = new ValidationResult("Matching rule [{}] in entity [{}] can not be define without a name",
                    ExceptionId.EX_MATCHING_RULE_INCORRECT_BLANK_NAME.getCode(), messageName, messageEntityName);
            validationErrors.add(validation);
        } else if(getName().length()> RULE_NAME_MAX_LENGTH){
            ValidationResult validation = new ValidationResult("Matching rule [{}] in entity [{}] can not be define with name which length more then [{}]",
                    ExceptionId.EX_MATCHING_RULE_INCORRECT_LONG_NAME.getCode(), messageName, messageEntityName, RULE_NAME_MAX_LENGTH);
            validationErrors.add(validation);
        }

        if (isBlank(getEntityName())) {
            ValidationResult validation = new ValidationResult("Matching rule [{}] in entity [{}] can not be define without an entity name",
                    ExceptionId.EX_MATCHING_RULE_INCORRECT_BLANK_ENTITY.getCode(), messageName, messageEntityName);
            validationErrors.add(validation);
        }
        return validationErrors;
    }

    public boolean isWithPreprocessing() {
        return withPreprocessing;
    }

    public void setWithPreprocessing(boolean withPreprocessing) {
        this.withPreprocessing = withPreprocessing;
    }
}

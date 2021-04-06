package com.unidata.mdm.backend.service.matching.data;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

        if (getMatchingAlgorithms().stream().allMatch(al -> !al.isExactMatching())) {
            ValidationResult validation = new ValidationResult("Matching rule [{}] in entity [{}] can not contain only not exact rules",
                    ExceptionId.EX_MATCHING_ONLY_NOT_EXACT_ALGOS.getCode(), messageName, messageEntityName);
            validationErrors.add(validation);
        }
        return validationErrors;
    }

}

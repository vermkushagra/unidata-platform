package com.unidata.mdm.backend.service.matching.data;

import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_MATCHING_ALGO_INCORRECT_INNER_FIELDS;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.hash.Funnel;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.ValidationResult;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.service.matching.algorithms.Algorithm;
import com.unidata.mdm.backend.service.matching.algorithms.AlgorithmType;
/**
 * Matching algorithm - thin wrapper around matching worker code.
 *
 * @author Mikhail Mikhailov
 */
public class MatchingAlgorithm {
    /**
     * The id.
     */
    private Integer id;
    /**
     * Name.
     */
    private String name;
    /**
     * Description.
     */
    private String description;
    /**
     * Fields under control.
     */
    private Collection<MatchingField> matchingFields;
    /**
     * Algorithm's implementation.
     */
    private Algorithm algorithmImpl;
    /**
     * Fetch function.
     */
    private Function<Integer, Algorithm> lazyAlgorithmFetch;

    private Funnel<DataRecord> etalonRecordFunnel;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Collection<MatchingField> getMatchingFields() {
        return matchingFields == null ? Collections.emptyList() : matchingFields;
    }

    public void setMatchingFields(Collection<MatchingField> matchingFields) {
        if (CollectionUtils.isNotEmpty(matchingFields)) {
            this.matchingFields = matchingFields.stream()
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    /**
     * @param lazyAlgorithmFetch - function with fetch linked algorithm
     * @return self
     */
    public MatchingAlgorithm addFetchFunction(Function<Integer, Algorithm> lazyAlgorithmFetch) {
        this.lazyAlgorithmFetch = lazyAlgorithmFetch;
        return this;
    }

    /**
     * Gets the uderlaying algorithm.
     * @return alogorithm instance
     */
    private Algorithm getAlgorithmImpl() {

        if (Objects.isNull(algorithmImpl)) {
            algorithmImpl = lazyAlgorithmFetch.apply(getId());
        }

        return algorithmImpl;
    }

    public AlgorithmType getAlgorithmType() {
        return getAlgorithmImpl().getType();
    }

    /**
     * @return true if algorithm is exact, otherwise false.
     */
    public boolean isExactMatching() {
        return getAlgorithmImpl().isExact();
    }

    /**
     * @return funnel for hashing etalon record
     */
    public Funnel<DataRecord> getFunnel() {
        if (etalonRecordFunnel != null) {
            return etalonRecordFunnel;
        }
        etalonRecordFunnel = (etalonRecord, into) -> {
            Collection<Integer> fieldsForId = getAlgorithmImpl().getFieldsForIdentify();
            getMatchingFields().stream().sequential()
                    .filter(field -> fieldsForId.contains(field.getId()))
                    .sorted()
                    .forEach(field -> field.hashField(etalonRecord, into));
        };
        return etalonRecordFunnel;
    }

    public List<Object> construct(@Nonnull DataRecord record) {

        Collection<Integer> fieldIds = getAlgorithmImpl().getFieldsForIdentify();
        List<Object> result = new ArrayList<>(fieldIds.size());

        for (MatchingField mf : getMatchingFields()) {
            if (!fieldIds.contains(mf.getId())) {
                continue;
            }

            Collection<Integer> supplementaryIds = getAlgorithmImpl().getSupplementaryFields(mf.getId());
            Object val;
            if(CollectionUtils.isNotEmpty(supplementaryIds)){
                // todo refactoring this
                MatchingField supplementaryField = getMatchingFields()
                        .stream()
                        .filter(matchingField -> supplementaryIds.contains(matchingField.getId()))
                        .findFirst()
                        .orElse(null);
                if(supplementaryField != null){
                    val = getAlgorithmImpl().construct(mf.extractAttribute(record), supplementaryField.getAttrName());
                } else {
                    val = getAlgorithmImpl().construct(mf.extractAttribute(record));
                }

            } else {
                val = getAlgorithmImpl().construct(mf.extractAttribute(record));
            }

            if (Objects.nonNull(val)) {
                result.add(val);
            }
        }

        return result;
    }
    /**
     * @param etalonRecord - matching record
     * @return null in case when algorithm match all records.
     */
    @Nullable
    public FormFieldsGroup getFormFieldGroup(@Nonnull DataRecord etalonRecord) {
        Map<Integer, SimpleAttribute<?>> attributeMap = new HashMap<>();
        getMatchingFields().stream()
                .map(field -> Pair.of(field.getId(), field.extractAttribute(etalonRecord)))
                .filter(pair -> pair.getValue() != null)
                .collect(toMap(Pair::getKey, Pair::getValue, (a, b) -> b, () -> attributeMap));

        return getAlgorithmImpl().getFormFieldGroup(attributeMap);
    }

    /**
     * State validator method.
     * @return collection of validation errors
     */
    public Collection<ValidationResult> checkCompleteness() {
        Collection<ValidationResult> validationErrors = new ArrayList<>(4);
        String algoName = isBlank(this.getName()) ? "Неизвестно" : getName();
        if (isEmpty(getMatchingFields())) {
            ValidationResult validation = new ValidationResult("Algorithm [{}] should contains some fields",
                    ExceptionId.EX_MATCHING_ALGO_INCORRECT_FIELDS_EMPTY.getCode(), algoName);
            validationErrors.add(validation);
        } else {
            getMatchingFields().stream()
                               .map(MatchingField::checkCompleteness)
                               .filter(cl -> !cl.isEmpty())
                               .map(cl -> new ValidationResult("Algorithm [{}] contains incorrect fields", cl,
                                       EX_MATCHING_ALGO_INCORRECT_INNER_FIELDS.getCode(), algoName))
                               .collect(Collectors.toCollection(() -> validationErrors));
        }
        if (isBlank(this.getName())) {
            ValidationResult validation = new ValidationResult("Algorithm [{}] should contains name",
                    ExceptionId.EX_MATCHING_ALGO_INCORRECT_NAME_ABSENT.getCode(), algoName);
            validationErrors.add(validation);
        }
        if (isNull(this.getId())) {
            ValidationResult validation = new ValidationResult("Algorithm [{}] should contains id",
                    ExceptionId.EX_MATCHING_ALGO_INCORRECT_ID_EMPTY.getCode(), algoName);
            validationErrors.add(validation);
        }
        return validationErrors;
    }
}

package org.unidata.mdm.search.service.impl;

import static java.util.Collections.singletonList;
import static org.unidata.mdm.search.type.form.FormField.FilteringType.NEGATIVE;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.InnerHitBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.search.MatchQuery;
import org.elasticsearch.join.query.HasChildQueryBuilder;
import org.elasticsearch.join.query.HasParentQueryBuilder;
import org.elasticsearch.join.query.JoinQueryBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ReverseNestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.support.IncludeExclude;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.unidata.mdm.search.context.AggregationSearchContext;
import org.unidata.mdm.search.context.CardinalityAggregationRequestContext;
import org.unidata.mdm.search.context.ComplexSearchRequestContext;
import org.unidata.mdm.search.context.FilterAggregationRequestContext;
import org.unidata.mdm.search.context.NestedAggregationRequestContext;
import org.unidata.mdm.search.context.NestedSearchRequestContext;
import org.unidata.mdm.search.context.NestedSearchRequestContext.NestedSearchType;
import org.unidata.mdm.search.context.ReverseNestedAggregationRequestContext;
import org.unidata.mdm.search.context.SearchRequestContext;
import org.unidata.mdm.search.context.TermsAggregationRequestContext;
import org.unidata.mdm.search.context.ValueCountAggregationRequestContext;
import org.unidata.mdm.search.exception.SearchApplicationException;
import org.unidata.mdm.search.exception.SearchExceptionIds;
import org.unidata.mdm.search.type.FieldType;
import org.unidata.mdm.search.type.HierarchicalIndexType;
import org.unidata.mdm.search.type.form.FormField;
import org.unidata.mdm.search.type.form.FormFieldsGroup;
import org.unidata.mdm.search.type.search.SearchRequestType;
import org.unidata.mdm.search.util.SearchUtils;
import org.unidata.mdm.system.util.ConvertUtils;

/**
 * @author Mikhail Mikhailov
 *         Common methods for search agents.
 */
public class BaseAgentComponent extends ElasticBaseComponent {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseAgentComponent.class);

    /**
     * default min score for search
     */
    @Value("${unidata.search.fuzziness:1}")
    private String defaultFuzziness;

    /**
     * default min score for search
     */
    @Value("${unidata.search.fuzziness.prefix.length:4}")
    private Integer fuzzinessPrefixLength;
    /**
     * @param formFields
     * @return bool filter
     */
    protected QueryBuilder createFormFilter(Collection<FormFieldsGroup> formFields) {

        BoolQueryBuilder topFilter = QueryBuilders.boolQuery();
        for (FormFieldsGroup groupFormFields : formFields) {
            BoolQueryBuilder groupBoolFilter = QueryBuilders.boolQuery();
            if (groupFormFields.getGroupType() == FormFieldsGroup.GroupType.OR) {
                groupFormFields.getFormFields().stream()
                        .map(this::getFilter)
                        .filter(Objects::nonNull)
                        .forEach(groupBoolFilter::should);
            } else {
                groupFormFields.getFormFields().stream()
                        .map(this::getFilter)
                        .filter(Objects::nonNull)
                        .forEach(groupBoolFilter::must);
            }
            if (groupBoolFilter.hasClauses()) {
                topFilter.must(groupBoolFilter);
            }
            if (CollectionUtils.isNotEmpty(groupFormFields.getChildGroups())) {
                if (groupFormFields.getGroupType() == FormFieldsGroup.GroupType.OR) {
                    groupFormFields.getChildGroups().stream()
                            .filter(Objects::nonNull)
                            .forEach(childGroup ->
                            topFilter.should(createInnerFormFilter(childGroup)));
                } else {
                    groupFormFields.getChildGroups().stream()
                            .filter(Objects::nonNull)
                            .forEach(childGroup ->
                            topFilter.must(createInnerFormFilter(childGroup)));
                }
            }
        }


        if (topFilter.hasClauses()) {
            return topFilter;
        } else {
            return QueryBuilders.matchAllQuery();
        }
    }

    private QueryBuilder createInnerFormFilter(FormFieldsGroup groupFilter) {

        BoolQueryBuilder topFilter = QueryBuilders.boolQuery();
        if (groupFilter.getGroupType() == FormFieldsGroup.GroupType.OR) {
            groupFilter.getFormFields().stream()
                    .map(this::getFilter)
                    .filter(Objects::nonNull)
                    .forEach(topFilter::should);
        } else {
            groupFilter.getFormFields().stream()
                    .map(this::getFilter)
                    .filter(Objects::nonNull)
                    .forEach(topFilter::must);
        }

        if (CollectionUtils.isNotEmpty(groupFilter.getChildGroups())) {
            if (groupFilter.getGroupType() == FormFieldsGroup.GroupType.OR) {
                groupFilter.getChildGroups().forEach(
                        childGroup -> topFilter.should(createInnerFormFilter(childGroup)));
            } else {
                groupFilter.getChildGroups().forEach(
                        childGroup -> topFilter.must(createInnerFormFilter(childGroup)));
            }
        }

        if (topFilter.hasClauses()) {
            return topFilter;
        } else {
            return QueryBuilders.matchAllQuery();
        }
    }

    /**
     * @param formField
     * @return specified filter
     */
    @Nullable
    private QueryBuilder getFilter(@Nonnull FormField formField) {

        String path = formField.getPath();
        QueryBuilder filterBuilder = null;

        switch (formField.getSearchType()) {
            case EXIST:
                filterBuilder = QueryBuilders.existsQuery(path);
                break;
            case START_WITH:
                filterBuilder = createStartWithQuery(formField, false);
                break;
            case LIKE:
                filterBuilder = createWildcardQuery(path, formField.getSingleValue().toString());
                break;
            case RANGE:
                filterBuilder = createRangeFilter(formField);
                break;
            case FUZZY:
                filterBuilder = createFuzzyQueryFromFormField(formField);
                break;
            case LEVENSHTEIN:
                filterBuilder = createLevenshteinQueryFromFormField(formField);
                break;
            case EXACT:
                filterBuilder = createTermQuery(formField);
                break;
            case MORPHOLOGICAL:
                filterBuilder = createMorphologicallyQueryFromFormField(formField);
                break;
            case NONE_MATCH:
                filterBuilder = QueryBuilders.boolQuery().mustNot(QueryBuilders.matchAllQuery());
                break;
            case DEFAULT:
                filterBuilder = createTermQuery(formField);
                break;
        }

        //change with UN-5115 (remove exist restriction)
        if (filterBuilder != null && formField.getFormType() == NEGATIVE) {
            filterBuilder = QueryBuilders.boolQuery().mustNot(filterBuilder);
        }

        //we support nested object just for $dq_errors.
        return filterBuilder;
    }

    /**
     * @param formField field
     * @return term filter by field
     */
    private QueryBuilder createTermQuery(FormField formField) {
        String fieldName;
        if (formField.getType() == FieldType.STRING) {
            fieldName = stringNonAnalyzedField(formField.getPath());
        } else {
            fieldName = formField.getPath();
        }
        if (formField.isNull()) {
            return QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(fieldName));
        } else if (formField.isMultiValues()) {
            return QueryBuilders.termsQuery(fieldName, formField.getValues());
        } else {
            return QueryBuilders.termQuery(fieldName, formField.getSingleValue());
        }

    }

    /**
     * @param initialFieldName
     * @return correct field name
     */
    protected String stringNonAnalyzedField(String initialFieldName) {
        return initialFieldName.contains(SearchUtils.DOLLAR) || initialFieldName.equals(SearchUtils.ID_FIELD) ?
                initialFieldName :
                initialFieldName + SearchUtils.DOT + SearchUtils.NAN_FIELD;
    }

    /**
     * @param initialFieldName
     * @return correct field name
     */
    private String stringMorphologicallyAnalyzedField(String initialFieldName) {
        return initialFieldName.contains(SearchUtils.DOLLAR) ?
                initialFieldName :
                initialFieldName + SearchUtils.DOT + SearchUtils.MORPH_FIELD;
    }

    /**
     * @param formField
     * @return range query
     */
    private QueryBuilder createRangeFilter(FormField formField) {

        Object left = formField.getRange().getLeftBoundary();
        Object right = formField.getRange().getRightBoundary();

        if (left == null && right == null) {
            return null;
        }

        RangeQueryBuilder rangeFilter = QueryBuilders.rangeQuery(formField.getPath());
        if (left != null) {
            Long timeMillis = toTimeMillis(left);
            rangeFilter.gte(timeMillis != null ? timeMillis : left);
        }

        if (right != null) {
            Long timeMillis = toTimeMillis(right);
            rangeFilter.lte(timeMillis != null ? timeMillis : right);
        }
        return rangeFilter;
    }

    private QueryBuilder createMatchQueryFromFormField(FormField formField) {
        return createMatchQuery(formField.getSingleValue() == null ? null : formField.getSingleValue().toString(),
                singletonList(formField.getPath()), null, false);
    }

    private QueryBuilder createMorphologicallyQueryFromFormField(FormField formField) {
        return createFuzzyQuery(formField.getSingleValue() == null ? null : formField.getSingleValue().toString(),
                stringMorphologicallyAnalyzedField(formField.getPath()), true);
    }

    private QueryBuilder createFuzzyQueryFromFormField(FormField formField) {
        return createFuzzyQuery(formField.getSingleValue() == null ? null : formField.getSingleValue().toString(),
                formField.getPath(), false);
    }

    private QueryBuilder createLevenshteinQueryFromFormField(FormField formField) {

        String path = formField.getType() == FieldType.STRING
                ? stringNonAnalyzedField(formField.getPath())
                : formField.getPath();

        return createLevenshteinQuery(formField.getSingleValue() == null ? null : formField.getSingleValue().toString(),
                path);
    }


    /**
     * Modifies field name so, that it is suitable for search with term queries.
     *
     * @param entityName the entity name
     * @param fieldName  the field name
     * @return possibly adjusted field name
     */
    protected String ensureStringFieldForTermQueries(String entityName, String fieldName) {

        // FIXME: @Modules. MMS is not visible from here. Generate NAN for all fields.
        return fieldName + SearchUtils.DOT + SearchUtils.NAN_FIELD;
        /*
        AttributedModelElement attributesWrapper = modelService.getValueById(entityName, LookupInfoHolder.class);
        if (attributesWrapper == null) {
            attributesWrapper = modelService.getValueById(entityName, EntityInfoHolder.class);
        }

        // Lookup or ordinary field link
        if (attributesWrapper != null) {

            AttributeModelElement attributeInfoHolder = attributesWrapper.getAttributes().get(fieldName);
            if (attributeInfoHolder != null) {
                boolean isString = attributeInfoHolder.getValueType() == AttributeValueType.STRING;
                if (isString) {
                    return fieldName + SearchUtils.DOT + SearchUtils.NAN_FIELD;
                }
            }
        }

        return fieldName;
        */
    }

    /**
     * Split fields list to sting and not string fields
     *
     * @param entityName  the entity name
     * @param fieldNames  the field names
     * @param scoreFields score values for fields
     * @return pair first list is string fields, second not string fields
     */
    protected Pair<Map<String, Float>, Map<String, Float>> splitStringAttributes(String entityName,
                                                                                 List<String> fieldNames,
                                                                                 Map<String, Float> scoreFields) {

        // FIXME: @Modules. MMS is not visible from here.
        Map<String, Float> stringAttrs = new HashMap<>();
        for (String fieldName : fieldNames) {
            stringAttrs.put(fieldName, scoreFields.getOrDefault(fieldName, 1f));
        }

        return Pair.of(stringAttrs, Collections.emptyMap());
        /*
        AttributedModelElement attributesWrapper = modelService.getValueById(entityName, LookupInfoHolder.class);
        if (attributesWrapper == null) {
            attributesWrapper = modelService.getValueById(entityName, EntityInfoHolder.class);
        }

        // Lookup or ordinary field link
        if (attributesWrapper != null) {

            Map<String, Float> stringAttrs = new HashMap<>();
            Map<String, Float> notStringAttrs = new HashMap<>();

            for (String fieldName : fieldNames) {
                AttributeModelElement attributeInfoHolder = attributesWrapper.getAttributes().get(fieldName);
                if (attributeInfoHolder != null) {
                    boolean isString = attributeInfoHolder.getValueType() == AttributeValueType.STRING;

                    if (isString) {
                        stringAttrs.put(fieldName, scoreFields.getOrDefault(fieldName, 1f));
                    } else {
                        notStringAttrs.put(fieldName, scoreFields.getOrDefault(fieldName, 1f));
                    }
                }
            }
            return Pair.of(stringAttrs, notStringAttrs);
        }
        return null;
        */
    }

    /**
     * Creates query builder from context.
     *
     * @param ctx the context
     * @return query builder
     */
    @Nonnull
    protected QueryBuilder createGeneralQueryFromContext(final SearchRequestContext ctx) {

        if (ctx.isFetchAll()) {
            return QueryBuilders.matchAllQuery();
        }

        if (ctx.isSayt()) {
            return createSaytQueryFromContext(ctx);
        }

        boolean isSimple = ctx.isSimple();
        boolean isForm = ctx.isForm();
        boolean isEmpty = ctx.isEmpty();

        if (isEmpty) {
            return QueryBuilders.boolQuery().mustNot(QueryBuilders.matchAllQuery());
        }

        QueryBuilder formFilter = null;
        QueryBuilder simpleResult = null;

        if (isForm) {
            formFilter = createFormFilter(ctx.getForm());
        }

        if (isSimple) {
            simpleResult = createSimpleQuery(ctx);
        }

        if (CollectionUtils.isNotEmpty(ctx.getNestedSearch())) {

            BoolQueryBuilder joinChildrenFilter = QueryBuilders.boolQuery();
            for (NestedSearchRequestContext entry : ctx.getNestedSearch()) {
                processNestedContext(entry, joinChildrenFilter);
            }

            if (formFilter != null) {
                joinChildrenFilter.must(formFilter);
            }

            formFilter = joinChildrenFilter;
        }

        BoolQueryBuilder queryResult;
        if (simpleResult != null) {
            queryResult = QueryBuilders.boolQuery()
                    .must(simpleResult);
        } else {
            queryResult = QueryBuilders
                    .boolQuery()
                    .must(QueryBuilders.matchAllQuery());
        }

        if (formFilter != null) {
            if (ctx.isScoreEnabled()) {
                queryResult.must(formFilter);
            } else {
                queryResult.filter(formFilter);
            }
        }

        return queryResult;
    }

    private void processNestedContext(NestedSearchRequestContext nsCtx, BoolQueryBuilder joiner) {

        SearchRequestContext nestedCtx = nsCtx.getNestedSearch();
        // For nested object query
        // nested -> must not ->  term
        // not work.
        // Instead we need use another order
        // must not -> nested - term
        // for all negative fields and group fields

        List<FormFieldsGroup> specialGroupFields = null;
        if (nsCtx.getNestedSearchType() == NestedSearchType.NESTED_OBJECTS
                && nestedCtx.getForm() != null
                && nestedCtx.getForm().size() == 1) {
            FormFieldsGroup forCopy = nestedCtx.getForm().get(0);
            final FormFieldsGroup specialGroup = forCopy.getGroupType() == FormFieldsGroup.GroupType.AND
                    ? FormFieldsGroup.createAndGroup()
                    : FormFieldsGroup.createOrGroup();
            if (forCopy.getFormFields() != null && forCopy.getFormFields().stream()
                    .anyMatch(formField -> formField.getFormType() == FormField.FilteringType.NEGATIVE)) {
                forCopy.getFormFields().stream()
                        .filter(formField -> formField.getFormType() == FormField.FilteringType.NEGATIVE)
                        .forEach(formField -> specialGroup.addFormField(FormField.copyInvertedField(formField)));
                if (specialGroupFields == null) {
                    specialGroupFields = new ArrayList<>();
                }
                specialGroupFields.add(specialGroup);
                forCopy.getFormFields().removeIf(formField -> formField.getFormType() == FormField.FilteringType.NEGATIVE);
            }
            if (forCopy.getChildGroups() != null) {
                for (FormFieldsGroup childGroup : forCopy.getChildGroups()) {
                    if (childGroup.getFormFields() != null && childGroup.getFormFields().stream()
                            .anyMatch(formField -> formField.getFormType() == FormField.FilteringType.NEGATIVE)) {
                        if (specialGroupFields == null) {
                              specialGroupFields = new ArrayList<>();
                        }
                        FormFieldsGroup copyGroup = childGroup.getGroupType() == FormFieldsGroup.GroupType.AND
                                ? FormFieldsGroup.createOrGroup()
                                : FormFieldsGroup.createAndGroup();
                        for (FormField field : childGroup.getFormFields()) {
                            copyGroup.addFormField(FormField.copyInvertedField(field));
                        }
                        specialGroupFields.add(copyGroup);
                    }
                }
                forCopy.getChildGroups().removeIf(childGroup -> childGroup.getFormFields() != null && childGroup.getFormFields().stream()
                        .anyMatch(formField -> formField.getFormType() == FormField.FilteringType.NEGATIVE));
            }


            if (CollectionUtils.isEmpty(forCopy.getFormFields()) && CollectionUtils.isEmpty(forCopy.getChildGroups())) {
                nestedCtx.getForm().remove(forCopy);
            }
        }

        QueryBuilder innerQuery = createGeneralQueryFromContext(nestedCtx);

        if (nsCtx.getNestedSearchType() == NestedSearchType.HAS_CHILD) {

            HasChildQueryBuilder hasChild
                = JoinQueryBuilders.hasChildQuery(
                        nestedCtx.getType().getName(), innerQuery, ScoreMode.None);

            if (nsCtx.getMinDocCount() != null) {
                hasChild.minMaxChildren(nsCtx.getMinDocCount(), Integer.MAX_VALUE);
            }

            if (CollectionUtils.isNotEmpty(nestedCtx.getReturnFields())) {
                hasChild.innerHit(createInnerHitBuilder(nsCtx));
            }
            if (nsCtx.isPositive()) {
                joiner.must(hasChild);
            } else {
                joiner.mustNot(hasChild);
            }
        }   if (nsCtx.getNestedSearchType() == NestedSearchType.HAS_PARENT) {

            HasParentQueryBuilder hasParent
                    = JoinQueryBuilders.hasParentQuery(
                    nestedCtx.getType().getName(), innerQuery, false);

            if (CollectionUtils.isNotEmpty(nestedCtx.getReturnFields())) {
                hasParent.innerHit(createInnerHitBuilder(nsCtx));
            }
            if (nsCtx.isPositive()) {
                joiner.must(hasParent);
            } else {
                joiner.mustNot(hasParent);
            }
        } else if (nsCtx.getNestedSearchType() == NestedSearchType.NESTED_OBJECTS) {

            NestedQueryBuilder nqb = QueryBuilders.nestedQuery(nestedCtx.getNestedPath(), innerQuery, ScoreMode.None);
            if (CollectionUtils.isNotEmpty(nestedCtx.getReturnFields())) {
                nqb.innerHit(createInnerHitBuilder(nsCtx));
            }

            if (specialGroupFields != null) {
                QueryBuilder specialBuilder = createFormFilter(specialGroupFields);

                joiner.must(QueryBuilders.boolQuery()
                        .should(nqb)
                        .should(QueryBuilders.boolQuery().mustNot(
                                QueryBuilders.nestedQuery(nestedCtx.getNestedPath(), specialBuilder, ScoreMode.None))));

            } else {
                if (nsCtx.isPositive()) {
                    joiner.must(nqb);
                } else {
                    joiner.mustNot(nqb);
                }
            }
        }
    }

    private InnerHitBuilder createInnerHitBuilder(NestedSearchRequestContext nsCtx) {

        SearchRequestContext nestedCtx = nsCtx.getNestedSearch();
        FetchSourceContext fetchSourceContext
            = new FetchSourceContext(
                true,
                nestedCtx.getReturnFields() == null
                        ? null
                        : nestedCtx.getReturnFields().toArray(new String[nestedCtx.getReturnFields().size()]),
                null);

        InnerHitBuilder innerHitBuilder = new InnerHitBuilder();
        innerHitBuilder.setName(nsCtx.getNestedQueryName());
        innerHitBuilder.setFetchSourceContext(fetchSourceContext);
        innerHitBuilder.setSize(nestedCtx.getCount());

        return innerHitBuilder;
    }

    /**
     * Creates a simple query.
     *
     * @param ctx the context
     * @return {@link QueryBuilder}
     */
    protected QueryBuilder createSimpleQuery(SearchRequestContext ctx) {
        SearchRequestType requestType = ctx.getSearch() == null
                ? SearchRequestType.MATCH
                : ctx.getSearch();

        QueryBuilder queryBuilder = null;
        switch (requestType) {
            case MATCH:
                queryBuilder = createMatchQuery(ctx);
                break;
            case QSTRING:
                queryBuilder = createQStringQuery(ctx);
                break;
            case FUZZY:
                queryBuilder = createFuzzyQuery(ctx);
                break;
            case TERM:
                // term query need for backward compatibility
                queryBuilder = createTermQuery(ctx);
                break;
            default:
                break;
        }

        return queryBuilder;
    }


    /**
     * Creates a wildcard query.
     *
     * @param field search field
     * @param text  text
     * @return query builder
     */
    private QueryBuilder createWildcardQuery(String field, String text) {
        text = text.replace("*", "\\*").replace("?", "\\?");
        if (text == null) {
            return QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(field));
        }

        boolean hasMoreThenOneTerm = text.contains(StringUtils.SPACE);
        if (hasMoreThenOneTerm) {
            List<String> terms = Arrays.asList(StringUtils.split(text, StringUtils.SPACE));
            BoolQueryBuilder q = QueryBuilders.boolQuery();
            terms.forEach(s -> q.must(QueryBuilders.wildcardQuery(field, "*" + s.toLowerCase() + "*")));
            return q;
        } else {
            // UN-5293
            return QueryBuilders.wildcardQuery(field, "*" + text.toLowerCase() + "*");
        }
    }

    /**
     * Creates a query string query.
     *
     * @param ctx the context
     * @return query builder
     */
    protected QueryBuilder createQStringQuery(final SearchRequestContext ctx) {

        QueryStringQueryBuilder builder = QueryBuilders
                .queryStringQuery(ctx.getText())
                .lenient(true);

        for (String field : ctx.getSearchFields()) {
            builder.field(field);
        }

        builder.phraseSlop(SearchUtils.DEFAULT_SLOP_VALUE);
        return builder;
    }

    /**
     * Creates a match query.
     *
     * @param ctx the context
     * @return query builder
     */
    protected QueryBuilder createMatchQuery(final SearchRequestContext ctx) {
        Map<String, Float> searchFields = ctx.getScoreFields();
        return createMatchQuery(ctx.getText(), ctx.getSearchFields(), searchFields, false);
    }


    protected QueryBuilder createSaytQueryFromContext(final SearchRequestContext ctx) {
        if (ctx.getSearchFields().size() == 1) {
            return QueryBuilders
                    .matchQuery(ctx.getSearchFields().get(0),
                            ctx.getText())
                    .maxExpansions(SearchUtils.DEFAULT_MAX_EXPANSIONS_VALUE)
                    .slop(SearchUtils.DEFAULT_SLOP_VALUE)
                    .operator(Operator.AND)
                    .analyzer(SearchUtils.STANDARD_STRING_ANALYZER_NAME)
                    .zeroTermsQuery(MatchQuery.ZeroTermsQuery.ALL)
                    .lenient(true);
        } else {
            return QueryBuilders
                    .multiMatchQuery(
                            ctx.getText(),
                            ctx.getSearchFields().toArray(
                                    new String[ctx.getSearchFields().size()]))
                    .maxExpansions(SearchUtils.DEFAULT_MAX_EXPANSIONS_VALUE)
                    .slop(SearchUtils.DEFAULT_SLOP_VALUE)
                    .operator(Operator.AND)
                    .analyzer(SearchUtils.STANDARD_STRING_ANALYZER_NAME)
                    .zeroTermsQuery(MatchQuery.ZeroTermsQuery.ALL)
                    .lenient(true);
        }
    }

    /**
     * Creates a match query.
     *
     * @param text                  - text
     * @param fields                - fields
     * @param searchMorphologically search using morphological analyzer
     * @return query builder
     */
    private QueryBuilder createMatchQuery(String text,
                                          Collection<String> fields,
                                          Map<String, Float> scoreFields,
                                          boolean searchMorphologically) {
        // todo think about match with text == null
        Operator operator = Operator.AND;
        if (fields.size() == 1) {
            MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(fields.iterator().next(), text)
                    .maxExpansions(SearchUtils.DEFAULT_MAX_EXPANSIONS_VALUE)
                    .slop(SearchUtils.DEFAULT_SLOP_VALUE)
                    .operator(operator)
                    .analyzer(searchMorphologically
                            ? SearchUtils.MORPH_STRING_ANALYZER_NAME
                            : SearchUtils.STANDARD_STRING_ANALYZER_NAME)
                    .lenient(true);
            if (MapUtils.isNotEmpty(scoreFields)) {
                matchQueryBuilder.boost(scoreFields.get(fields.iterator().next()));
            }
            return matchQueryBuilder;
        } else {
            boolean hasMoreThenOneTerm = text.contains(StringUtils.SPACE);
            MultiMatchQueryBuilder.Type type = hasMoreThenOneTerm ?
                    MultiMatchQueryBuilder.Type.CROSS_FIELDS :
                    MultiMatchQueryBuilder.Type.BEST_FIELDS;
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(text, fields.toArray(new String[fields.size()]))
                    .maxExpansions(SearchUtils.DEFAULT_MAX_EXPANSIONS_VALUE)
                    .slop(SearchUtils.DEFAULT_SLOP_VALUE)
                    .operator(operator)
                    .analyzer(searchMorphologically
                            ? SearchUtils.MORPH_STRING_ANALYZER_NAME
                            : SearchUtils.STANDARD_STRING_ANALYZER_NAME)
                    .type(type)
                    .lenient(true);
            if (MapUtils.isNotEmpty(scoreFields)) {
                multiMatchQueryBuilder.fields(scoreFields);
            }
            return multiMatchQueryBuilder;
        }
    }

    /**
     * Creates a match query.
     *
     * @param searchMorphologically search using morphological analyzer
     * @return query builder
     */
    private QueryBuilder createStartWithQuery(FormField formField, boolean searchMorphologically) {
        String text = formField.getSingleValue() == null ? null : formField.getSingleValue().toString();
        String field = formField.getPath();
        Operator operator = Operator.AND;
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(field, text)
                .maxExpansions(SearchUtils.DEFAULT_MAX_EXPANSIONS_VALUE)
                .slop(SearchUtils.DEFAULT_SLOP_VALUE)
                .operator(operator)
                .analyzer(searchMorphologically
                        ? SearchUtils.MORPH_STRING_ANALYZER_NAME
                        : SearchUtils.STANDARD_STRING_ANALYZER_NAME)
                .lenient(true);
        PrefixQueryBuilder prefixQueryBuilder = QueryBuilders.prefixQuery(stringNonAnalyzedField(field), text);
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(stringNonAnalyzedField(field), text);
        return QueryBuilders.boolQuery()
                .should(matchQueryBuilder)
                .should(prefixQueryBuilder)
                .should(termQueryBuilder)
                .minimumShouldMatch(1);
    }

    /**
     * Creates a term query.
     *
     * @param ctx the context
     * @return query builder
     */
    protected QueryBuilder createTermQuery(final SearchRequestContext ctx) {

        if (CollectionUtils.isEmpty(ctx.getSearchFields())
                || StringUtils.equals(ctx.getSearchFields().get(0), SearchUtils.ALL_FIELD)) {
            final String message = "Invalid fields supplied for term query. Fields = {}";
            LOGGER.warn(message, ctx.getSearchFields());
            throw new SearchApplicationException(message, SearchExceptionIds.EX_SEARCH_INVALID_TERM_FIELDS, ctx.getSearchFields());
        }

        QueryBuilder fb;
        if (ctx.getSearchFields().size() == 1) {

            if (CollectionUtils.isEmpty(ctx.getValues())) {
                fb = QueryBuilders.termQuery(
                        ensureStringFieldForTermQueries(ctx.getEntity(), ctx.getSearchFields().get(0)),
                        ctx.getText());
            } else {
                fb = QueryBuilders.termsQuery(
                        ensureStringFieldForTermQueries(ctx.getEntity(), ctx.getSearchFields().get(0)),
                        ctx.getValues());
            }

        } else {

            BoolQueryBuilder bfb = QueryBuilders.boolQuery();
            for (String fieldName : ctx.getSearchFields()) {
                if (CollectionUtils.isEmpty(ctx.getValues())) {
                    bfb.should(QueryBuilders.termQuery(
                            ensureStringFieldForTermQueries(ctx.getEntity(), fieldName), ctx.getText()));
                } else {
                    bfb.should(QueryBuilders.termsQuery(
                            ensureStringFieldForTermQueries(ctx.getEntity(), fieldName), ctx.getValues()));
                }
            }

            fb = bfb;
        }

        return QueryBuilders.constantScoreQuery(QueryBuilders
                .boolQuery()
                .must(QueryBuilders.matchAllQuery())
                .filter(fb));
    }


    /**
     * Creates a fuzzy search query.
     *
     * @param ctx the context
     * @return query builder
     */
    protected QueryBuilder createFuzzyQuery(final SearchRequestContext ctx) {
        Map<String, Float> scoreFields = ctx.getScoreFields();
        Pair<Map<String, Float>, Map<String, Float>> parsedFields = splitStringAttributes(ctx.getEntity(), ctx.getSearchFields(), scoreFields);
        QueryBuilder fuzzyQuery = null;
        QueryBuilder matchQuery = null;
        QueryBuilder qb = null;
        if (parsedFields != null) {
            if (MapUtils.isNotEmpty(parsedFields.getLeft())) {
                fuzzyQuery = createFuzzyQuery(ctx.getText(), parsedFields.getLeft().keySet(), parsedFields.getLeft());
            }
            if (MapUtils.isNotEmpty(parsedFields.getRight())) {
                matchQuery = createMatchQuery(ctx.getText(), parsedFields.getRight().keySet(), parsedFields.getRight(), false);
            }

            if (fuzzyQuery == null) {
                if (matchQuery == null) {
                    qb = QueryBuilders.matchAllQuery();
                } else {
                    qb = matchQuery;
                }
            } else {
                if (matchQuery == null) {
                    qb = fuzzyQuery;
                } else {
                    BoolQueryBuilder bfb = QueryBuilders.boolQuery();
                    bfb.should(matchQuery);
                    bfb.should(fuzzyQuery);
                    qb = bfb;
                }
            }
        }

        if (qb == null) {
            qb = QueryBuilders.matchAllQuery();
        }

        if (MapUtils.isEmpty(scoreFields)) {
            return QueryBuilders.constantScoreQuery(qb);
        } else {
            ctx.rescore(true);
            return qb;
        }

    }

    private QueryBuilder createFuzzyQuery(String text, String field, boolean searchMorphologically) {
        if (text == null) {
            return QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(field));
        }
        return QueryBuilders.matchQuery(field, text)
                .maxExpansions(SearchUtils.DEFAULT_MAX_EXPANSIONS_VALUE)
                .operator(Operator.AND)
                .analyzer(searchMorphologically
                        ? SearchUtils.MORPH_STRING_ANALYZER_NAME
                        : SearchUtils.STANDARD_STRING_ANALYZER_NAME)
                .prefixLength(fuzzinessPrefixLength)
                .fuzziness(Fuzziness.build(defaultFuzziness))
                .lenient(true);
    }

    private QueryBuilder createLevenshteinQuery(String text, String field) {
        if (text == null) {
            return QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(field));
        }
        return QueryBuilders.matchQuery(field, text)
                .maxExpansions(SearchUtils.DEFAULT_MAX_EXPANSIONS_VALUE)
                .operator(Operator.AND)
                .fuzziness(Fuzziness.build(defaultFuzziness))
                .lenient(true);
    }

    /**
     * Creates a fuzzy search query.
     *
     * @param text   - text
     * @param fields - fields
     * @return query builder
     */
    private QueryBuilder createFuzzyQuery(String text, Collection<String> fields, Map<String, Float> scoreFields) {
        if (fields.size() == 1) {
            boolean hasMoreThenOneTerm = text.contains(StringUtils.SPACE);
            Operator operator = hasMoreThenOneTerm ?
                    Operator.OR :
                    Operator.AND;
            MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(fields.iterator().next(), text)
                    .maxExpansions(SearchUtils.DEFAULT_MAX_EXPANSIONS_VALUE)
                    .operator(operator)
                    .analyzer(SearchUtils.STANDARD_STRING_ANALYZER_NAME)
                    .prefixLength(fuzzinessPrefixLength)
                    .fuzziness(Fuzziness.build(defaultFuzziness))
                    .lenient(true);

            if (MapUtils.isNotEmpty(scoreFields)) {
                matchQueryBuilder.boost(scoreFields.get(fields.iterator().next()));
            }
            return matchQueryBuilder;
        } else {
            boolean hasMoreThenOneTerm = text.contains(StringUtils.SPACE);
            Operator operator = hasMoreThenOneTerm ?
                    Operator.OR :
                    Operator.AND;
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(text, fields.toArray(new String[fields.size()]))
                    .maxExpansions(SearchUtils.DEFAULT_MAX_EXPANSIONS_VALUE)
                    .slop(SearchUtils.DEFAULT_SLOP_VALUE)
                    .operator(operator)
                    .analyzer(SearchUtils.STANDARD_STRING_ANALYZER_NAME)
                    .prefixLength(fuzzinessPrefixLength)
                    .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                    .fuzziness(Fuzziness.build(defaultFuzziness))
                    .lenient(true);

            if (MapUtils.isNotEmpty(scoreFields)) {
                multiMatchQueryBuilder.fields(scoreFields);
            }
            return multiMatchQueryBuilder;
        }
    }

    /**
     * @param searchRequest - complex search request
     * @return map, where key it is a simple search request and value it is a query
     */
    @Nonnull
    protected Map<SearchRequestContext, QueryBuilder> createQueryForComplex(final ComplexSearchRequestContext searchRequest) {

        if (searchRequest.getType() == ComplexSearchRequestContext.Type.HIERARCHICAL
                && searchRequest.getMainRequest() != null) {

            SearchRequestContext main = searchRequest.getMainRequest();
            QueryBuilder baseQuery = createGeneralQueryFromContext(main);
            HierarchicalIndexType fromType = (HierarchicalIndexType) main.getType();
            if (!searchRequest.getSupplementary().isEmpty()) {
                BoolQueryBuilder joinChildrenFilter = QueryBuilders.boolQuery();
                for (SearchRequestContext related : searchRequest.getSupplementary()) {
                    HierarchicalIndexType toType = (HierarchicalIndexType) related.getType();
                    QueryBuilder toRequest = createGeneralQueryFromContext(related);
                    QueryBuilder hierarchicalFilter = buildHierarchicalFilter(fromType, toType, toRequest, main);

                    if (related.isMust()) {
                        joinChildrenFilter.must(hierarchicalFilter);
                    } else {
                        joinChildrenFilter.should(hierarchicalFilter);
                    }
                }

                baseQuery = QueryBuilders
                        .boolQuery()
                        .must(baseQuery)
                        .filter(joinChildrenFilter);
            }

            return Collections.singletonMap(main, baseQuery);

        } else if (searchRequest.getType() == ComplexSearchRequestContext.Type.MULTI
                && !searchRequest.getSupplementary().isEmpty()) {
            Map<SearchRequestContext, QueryBuilder> result = new HashMap<>(searchRequest.getSupplementary().size(), 1);
            for (SearchRequestContext ctx : searchRequest.getSupplementary()) {
                result.put(ctx, createGeneralQueryFromContext(ctx));
            }
            return result;
        }
        return Collections.emptyMap();
    }

    /**
     * Creates aggregations.
     *
     * @param ctx the context to process
     * @return aggregation builder
     */
    protected AbstractAggregationBuilder<?> createAggregation(AggregationSearchContext ctx) {

        switch (ctx.getAggregationType()) {
            case CARDINALITY:

                CardinalityAggregationRequestContext caCtx = ctx.narrow();
                return AggregationBuilders
                        .cardinality(caCtx.getName())
                        .field(caCtx.getPath());
            case VALUE_COUNT:

                ValueCountAggregationRequestContext vaCtx = ctx.narrow();
                return AggregationBuilders
                        .count(vaCtx.getName())
                        .field(vaCtx.getPath());
            case FILTER:

                FilterAggregationRequestContext faCtx = ctx.narrow();
                FilterAggregationBuilder filterBuilder = AggregationBuilders
                        .filter(faCtx.getName(), createFormFilter(faCtx.getFields()));

                for (AggregationSearchContext inner : faCtx.aggregations()) {
                    filterBuilder.subAggregation(createAggregation(inner));
                }

                return filterBuilder;
            case NESTED:

                NestedAggregationRequestContext naCtx = ctx.narrow();
                NestedAggregationBuilder nestedBuilder = AggregationBuilders
                        .nested(naCtx.getName(), naCtx.getPath());

                for (AggregationSearchContext inner : naCtx.aggregations()) {
                    nestedBuilder.subAggregation(createAggregation(inner));
                }

                return nestedBuilder;
            case REVERSE_NESTED:

                ReverseNestedAggregationRequestContext rnaCtx = ctx.narrow();
                ReverseNestedAggregationBuilder reverseNestedBuilder = AggregationBuilders
                        .reverseNested(rnaCtx.getName())
                        .path(rnaCtx.getPath());

                for (AggregationSearchContext inner : rnaCtx.aggregations()) {
                    reverseNestedBuilder.subAggregation(createAggregation(inner));
                }

                return reverseNestedBuilder;
            case TERM:

                TermsAggregationRequestContext taCtx = ctx.narrow();
                TermsAggregationBuilder termsBuilder = AggregationBuilders.terms(taCtx.getName())
                        .field(taCtx.getPath())
                        .minDocCount(taCtx.getMinCount())
                        .size(taCtx.getSize());
                IncludeExclude include = null;
                if (taCtx.getExcludeValues() instanceof String[]) {
                    include = new IncludeExclude(null, (String[]) taCtx.getExcludeValues());
                } else if (taCtx.getExcludeValues() instanceof Long[]) {
                    include = new IncludeExclude(null, Arrays.stream(taCtx.getExcludeValues())
                            .filter(Objects::nonNull)
                            .mapToLong(l -> (Long) l)
                            .toArray());
                } else if (taCtx.getExcludeValues() instanceof Double[]) {
                    include = new IncludeExclude(null, Arrays.stream(taCtx.getExcludeValues())
                            .filter(Objects::nonNull)
                            .mapToDouble(l -> (Double) l)
                            .toArray());
                }

                IncludeExclude exclude = null;
                if (taCtx.getIncludeValues() instanceof String[]) {
                    exclude = new IncludeExclude((String[]) taCtx.getIncludeValues(), null);
                } else if (taCtx.getIncludeValues() instanceof Long[]) {
                    exclude = new IncludeExclude(Arrays.stream(taCtx.getIncludeValues())
                            .filter(Objects::nonNull)
                            .mapToLong(l -> (Long) l)
                            .toArray(), null);
                } else if (taCtx.getIncludeValues() instanceof Double[]) {
                    exclude = new IncludeExclude(Arrays.stream(taCtx.getIncludeValues())
                            .filter(Objects::nonNull)
                            .mapToDouble(l -> (Double) l)
                            .toArray(), null);
                }
                if (include != null || exclude != null) {
                    termsBuilder.includeExclude(IncludeExclude.merge(include, exclude));
                }

                for (AggregationSearchContext inner : taCtx.aggregations()) {
                    termsBuilder.subAggregation(createAggregation(inner));
                }

                return termsBuilder;
        }

        return null;
    }

    /**
     * @param fromType  - from type
     * @param toType    - to type
     * @param toRequest - to request
     * @return hierarchical filter, only for one layer directed graph
     */
    @Nonnull
    private QueryBuilder buildHierarchicalFilter(HierarchicalIndexType fromType, HierarchicalIndexType toType,
                                                 QueryBuilder toRequest, SearchRequestContext main) {
        //Examples: relation -> data || data-> classifier || classifier -> origin etc...
        if (!toType.isTopType() && !fromType.isTopType()) {
            HierarchicalIndexType top = fromType.getTopType();

            HasChildQueryBuilder child = JoinQueryBuilders.hasChildQuery(toType.getName(), toRequest, ScoreMode.None);
            return JoinQueryBuilders.hasParentQuery(top.getName(), child, true);
        }

        if (fromType.equals(toType)) {
            return QueryBuilders.boolQuery().must(toRequest);
        }

        if (toType.isTopType()) {
            return JoinQueryBuilders.hasParentQuery(toType.getName(), toRequest, true);
        }

        if (fromType.isTopType()) {
            return JoinQueryBuilders.hasChildQuery(toType.getName(), toRequest, ScoreMode.None);
        }
        throw new RuntimeException("Fail fast exception : unreachable state  was reached (rewrite code!)");
    }

    /**
     * Date object to TimeMillis.
     *
     * @param o
     *            object
     * @return Long timeMillis
     */
    private Long toTimeMillis(Object o) {

        if (o == null) {
            return null;
        } else if (o instanceof LocalDate) {
            return ConvertUtils.localDate2Date((LocalDate) o).getTime();
        } else if (o instanceof LocalDateTime) {
            return ConvertUtils.localDateTime2Date((LocalDateTime) o).getTime();
        } else if (o instanceof LocalTime) {
            return ConvertUtils.localTime2Date((LocalTime) o).getTime();
        } else if (o instanceof Calendar) {
            return ((Calendar) o).getTimeInMillis();
        } else if (o instanceof Date) {
            return ((Date) o).getTime();
        }

        LOGGER.warn("Cannot convert value of type [{}] to TimeMillis.", o.getClass().getName());
        return null;
    }
}

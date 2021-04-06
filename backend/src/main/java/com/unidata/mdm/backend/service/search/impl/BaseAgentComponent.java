/**
 *
 */
package com.unidata.mdm.backend.service.search.impl;

import static com.unidata.mdm.backend.common.search.FormField.FormType.NEGATIVE;
import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.InnerHitBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.join.query.HasChildQueryBuilder;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.unidata.mdm.backend.common.context.AggregationRequestContext;
import com.unidata.mdm.backend.common.context.CardinalityAggregationRequestContext;
import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.FilterAggregationRequestContext;
import com.unidata.mdm.backend.common.context.NestedAggregationRequestContext;
import com.unidata.mdm.backend.common.context.ReverseNestedAggregationRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.TermsAggregationRequestContext;
import com.unidata.mdm.backend.common.context.ValueCountAggregationRequestContext;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SearchApplicationException;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormField.SearchType;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchRequestType;
import com.unidata.mdm.backend.common.search.types.HierarchicalSearchType;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.wrappers.AttributesWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;
import com.unidata.mdm.backend.service.search.util.DqHeaderField;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.meta.ArrayAttributeDef;
import com.unidata.mdm.meta.ArrayValueType;
import com.unidata.mdm.meta.CodeAttributeDef;
import com.unidata.mdm.meta.SimpleAttributeDef;
import com.unidata.mdm.meta.SimpleDataType;

/**
 * @author Mikhail Mikhailov
 *         Common methods for search agents.
 */
public class BaseAgentComponent extends ElasticBaseComponent {

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

    @Autowired
    private MetaModelServiceExt modelService;
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseAgentComponent.class);

    /**
     * Constructor.
     */
    public BaseAgentComponent() {
        super();
    }

    /**
     * @param formFields
     * @return bool filter
     */
    private QueryBuilder createFormFilter(Collection<FormFieldsGroup> formFields) {

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
                    groupFormFields.getChildGroups().forEach(childGroup ->
                            topFilter.should(createInnerFormFilter(childGroup)));
                } else {
                    groupFormFields.getChildGroups().forEach(childGroup ->
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

        //todo replace to switch
        String path = formField.getPath();
        if (formField.isNull()) {
            switch (formField.getFormType()) {
                case NEGATIVE:
                    return QueryBuilders.existsQuery(path);
                case POSITIVE:
                    return QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(path));
            }
        }

        QueryBuilder filterBuilder = null;
        if (FormField.SearchType.FUZZY.equals(formField.getSearchType())) {
            QueryBuilder fuzzyQuery = createFuzzyQueryFromFormField(formField);
            filterBuilder = QueryBuilders.boolQuery().must(fuzzyQuery);
        } else {
            if (formField.isRange()) {
                filterBuilder = createRangeFilter(formField);
            } else if (formField.isSingle()) {
                if (formField.getType() == SimpleDataType.STRING && formField.isLike()) {
                    QueryBuilder wildcardQuery = createWildcardQuery(path, formField.getSingleValue().toString());
                    filterBuilder = QueryBuilders.boolQuery().must(wildcardQuery);
                } else if (formField.getType() == SimpleDataType.STRING && !formField.isStrict()) {
                    QueryBuilder matchQuery = createMatchQueryFromFormField(formField);
                    filterBuilder = QueryBuilders.boolQuery().must(matchQuery);
                } else {
                    filterBuilder = createTermQuery(formField);
                }
            }

            //change with UN-5115 (remove exist restriction)
            if (filterBuilder != null && formField.getFormType() == NEGATIVE) {
                filterBuilder = QueryBuilders.boolQuery().mustNot(filterBuilder);
            }
        }

        //we support nested object just for $dq_errors.
        return filterBuilder != null && formField.getPath().startsWith(DqHeaderField.getParentField()) && formField.getPath().contains(".") ?
                QueryBuilders.nestedQuery(formField.getPath().split("\\.")[0], filterBuilder, ScoreMode.None) :
                filterBuilder;
    }

    /**
     * @param formField
     * @return term filter
     */
    private QueryBuilder createTermQuery(FormField formField) {
        if (formField.getType() == SimpleDataType.STRING
                || formField.getType() == SimpleDataType.CLOB
                || formField.getType() == SimpleDataType.BLOB) {
            String fieldName = stringNonAnalyzedField(formField.getPath());
            return QueryBuilders.termQuery(fieldName, formField.getSingleValue());
        } else {
            return QueryBuilders.termQuery(formField.getPath(), formField.getSingleValue());
        }
    }

    /**
     * @param initialFieldName
     * @return correct field name
     */
    private String stringNonAnalyzedField(String initialFieldName) {
        return initialFieldName.contains(SearchUtils.DOLLAR) ?
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
            rangeFilter.gte(left instanceof Date ? ((Date) left).getTime() : left);
        }

        if (right != null) {
            rangeFilter.lte(right instanceof Date ? ((Date) right).getTime() : right);
        }
        return rangeFilter;
    }

    private QueryBuilder createMatchQueryFromFormField(FormField formField) {

        if (formField.getType() == SimpleDataType.STRING && formField.getSearchType() == SearchType.MORPHOLOGICAL) {
            return createFuzzyQuery(formField.getSingleValue().toString(),
                    Collections.singletonList(stringMorphologicallyAnalyzedField(formField.getPath())), true);
        }

        return createMatchQuery(formField.getSingleValue().toString(), singletonList(formField.getPath()), false);
    }

    private QueryBuilder createFuzzyQueryFromFormField(FormField formField) {
        return createFuzzyQuery(formField.getSingleValue().toString(), singletonList(formField.getPath()), false);
    }

    /**
     * Modifies field name so, that it is suitable for search with term queries.
     *
     * @param entityName the entity name
     * @param fieldName  the field name
     * @return possibly adjusted field name
     */
    protected String ensureStringFieldForMorphQueries(String entityName, String fieldName) {

        AttributesWrapper attributesWrapper = modelService.getValueById(entityName, LookupEntityWrapper.class);
        if (attributesWrapper == null) {
            attributesWrapper = modelService.getValueById(entityName, EntityWrapper.class);
        }

        if (attributesWrapper != null) {

            AttributeInfoHolder attributeInfoHolder = attributesWrapper.getAttributes().get(fieldName);
            if (attributeInfoHolder != null) {

                boolean morphSearchable = false;
                if (attributeInfoHolder.isArray()) {
                    ArrayAttributeDef attrDef = attributeInfoHolder.narrow();
                    morphSearchable = attrDef.getArrayValueType() == ArrayValueType.STRING && attrDef.isSearchMorphologically();
                } else if (attributeInfoHolder.isSimple()) {
                    SimpleAttributeDef attrDef = attributeInfoHolder.narrow();
                    morphSearchable = attrDef.getSimpleDataType() == SimpleDataType.STRING && attrDef.isSearchMorphologically();
                }

                if (morphSearchable) {
                    return fieldName + SearchUtils.DOT + SearchUtils.MORPH_FIELD;
                }
            }
        }

        return fieldName;
    }

    /**
     * Modifies field name so, that it is suitable for search with term queries.
     *
     * @param entityName the entity name
     * @param fieldName  the field name
     * @return possibly adjusted field name
     */
    protected String ensureStringFieldForTermQueries(String entityName, String fieldName) {

        AttributesWrapper attributesWrapper = modelService.getValueById(entityName, LookupEntityWrapper.class);
        if (attributesWrapper == null) {
            attributesWrapper = modelService.getValueById(entityName, EntityWrapper.class);
        }

        // Lookup or ordinary field link
        if (attributesWrapper != null) {

            AttributeInfoHolder attributeInfoHolder = attributesWrapper.getAttributes().get(fieldName);
            if (attributeInfoHolder != null) {
                boolean isString = attributeInfoHolder.isLookupLink()
                        ? attributeInfoHolder.isSimple()
                        ? ((SimpleAttributeDef) attributeInfoHolder.getAttribute()).getLookupEntityCodeAttributeType() == SimpleDataType.STRING
                        : ((ArrayAttributeDef) attributeInfoHolder.getAttribute()).getLookupEntityCodeAttributeType() == ArrayValueType.STRING
                        : attributeInfoHolder.isSimple()
                        ? ((SimpleAttributeDef) attributeInfoHolder.getAttribute()).getSimpleDataType() == SimpleDataType.STRING
                        : attributeInfoHolder.isCode()
                        ? ((CodeAttributeDef) attributeInfoHolder.getAttribute()).getSimpleDataType() == SimpleDataType.STRING
                        : ((ArrayAttributeDef) attributeInfoHolder.getAttribute()).getArrayValueType() == ArrayValueType.STRING;

                if (isString) {
                    return fieldName + SearchUtils.DOT + SearchUtils.NAN_FIELD;
                }
            }
        }

        return fieldName;
    }

    /**
     * Split fields list to sting and not string fields
     *
     * @param entityName the entity name
     * @param fieldNames the field name
     * @return pair first list is string filds, second not string fields
     */
    protected Pair<List<String>, List<String>> splitStringAttributes(String entityName, List<String> fieldNames) {

        AttributesWrapper attributesWrapper = modelService.getValueById(entityName, LookupEntityWrapper.class);
        if (attributesWrapper == null) {
            attributesWrapper = modelService.getValueById(entityName, EntityWrapper.class);
        }

        // Lookup or ordinary field link
        if (attributesWrapper != null) {

            List<String> stringAttrs = new ArrayList<>();
            List<String> notStringAttrs = new ArrayList<>();

            for (String fieldName : fieldNames) {
                AttributeInfoHolder attributeInfoHolder = attributesWrapper.getAttributes().get(fieldName);
                if (attributeInfoHolder != null) {
                    boolean isString = attributeInfoHolder.isLookupLink()
                            ? attributeInfoHolder.isSimple()
                            ? ((SimpleAttributeDef) attributeInfoHolder.getAttribute()).getLookupEntityCodeAttributeType() == SimpleDataType.STRING
                            : ((ArrayAttributeDef) attributeInfoHolder.getAttribute()).getLookupEntityCodeAttributeType() == ArrayValueType.STRING
                            : attributeInfoHolder.isSimple()
                            ? ((SimpleAttributeDef) attributeInfoHolder.getAttribute()).getSimpleDataType() == SimpleDataType.STRING
                            : attributeInfoHolder.isCode()
                            ? ((CodeAttributeDef) attributeInfoHolder.getAttribute()).getSimpleDataType() == SimpleDataType.STRING
                            : ((ArrayAttributeDef) attributeInfoHolder.getAttribute()).getArrayValueType() == ArrayValueType.STRING;

                    if (isString) {
                        stringAttrs.add(fieldName);
                    } else {
                        notStringAttrs.add(fieldName);
                    }
                }
            }
            return Pair.of(stringAttrs, notStringAttrs);
        }
        return null;
    }

    /**
     * Checks whether a nested path set and decorates  query with nested, if needed.
     *
     * @param q
     * @param ctx
     * @return
     */
    private QueryBuilder ensureNestedPath(QueryBuilder q, SearchRequestContext ctx) {

        if (ctx.isNested()) {
            return QueryBuilders.nestedQuery(ctx.getNestedPath(), q, ScoreMode.None);
        }

        return q;
    }

    /**
     * Creates query builder from context.
     *
     * @param ctx the context
     * @return query builder
     */
    @Nonnull
    protected QueryBuilder createGeneralQueryFromContext(final SearchRequestContext ctx) {

        MeasurementPoint.start();
        try {

            if (ctx.isFetchAll()) {
                return ensureNestedPath(QueryBuilders.matchAllQuery(), ctx);
            }

            QueryBuilder formFilter = null;
            QueryBuilder simpleResult = null;

            boolean isSimple = ctx.isSimple();
            boolean isForm = ctx.isForm();
            boolean isCombo = ctx.isCombo();
            boolean isEmpty = ctx.isEmpty();
            if (isEmpty) {
                return ensureNestedPath(QueryBuilders.boolQuery().mustNot(QueryBuilders.matchAllQuery()), ctx);
            }
            if (isForm) {
                formFilter = createFormFilter(ctx.getForm());
            }

            if (isSimple) {
                //todo replace this to filter!
                simpleResult = createSimpleQuery(ctx);
            }

            if (isCombo) {
                return ensureNestedPath(QueryBuilders.boolQuery().must(simpleResult).filter(formFilter), ctx);
            } else {
                return ensureNestedPath(isSimple
                                ? simpleResult
                                : QueryBuilders
                                .boolQuery()
                                .must(QueryBuilders.matchAllQuery())
                                .filter(formFilter),
                        ctx);
            }
        } finally {
            MeasurementPoint.stop();
        }
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
            case TERM:
                queryBuilder = createTermQuery(ctx);
                break;
            case QSTRING:
                queryBuilder = createQStringQuery(ctx);
                break;
            case FUZZY:
                queryBuilder = createFuzzyQuery(ctx);
                break;
            default:
                break;
        }

        return queryBuilder;
    }

    /**
     * Creates a wildcard query.
     *
     * @param ctx the context
     * @return query builder
     */
    protected QueryBuilder createWildcardQuery(final SearchRequestContext ctx) {
        //todo rewrite to bool query in case if we need search by all search fields.
        return createWildcardQuery(ctx.getSearchFields().get(0), ctx.getText());
    }

    /**
     * Creates a wildcard query.
     *
     * @param field search field
     * @param text  text
     * @return query builder
     */
    private QueryBuilder createWildcardQuery(String field, String text) {
        String nonAnalyzedField = stringNonAnalyzedField(field);
        return QueryBuilders.wildcardQuery(nonAnalyzedField, text);
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
        return createMatchQuery(ctx.getText(), ctx.getSearchFields(), false);
    }

    /**
     * Creates a match query.
     *
     * @param text                  - text
     * @param fields                - fields
     * @param searchMorphologically search using morphological analyzer
     * @return query builder
     */
    private QueryBuilder createMatchQuery(String text, List<String> fields, boolean searchMorphologically) {

        if (fields.size() == 1) {
            return QueryBuilders.matchQuery(fields.get(0), text)
                    .maxExpansions(SearchUtils.DEFAULT_MAX_EXPANSIONS_VALUE)
                    .slop(SearchUtils.DEFAULT_SLOP_VALUE)
                    .operator(Operator.AND)
                    .analyzer(searchMorphologically
                            ? SearchUtils.MORPH_STRING_ANALYZER_NAME
                            : SearchUtils.STANDARD_STRING_ANALYZER_NAME)
                    .lenient(true);
        } else {
            boolean hasMoreThenOneTerm = text.contains(StringUtils.SPACE);
            Operator operator = hasMoreThenOneTerm ?
                    Operator.OR :
                    Operator.AND;
            MultiMatchQueryBuilder.Type type = hasMoreThenOneTerm ?
                    MultiMatchQueryBuilder.Type.CROSS_FIELDS :
                    MultiMatchQueryBuilder.Type.BEST_FIELDS;
            return QueryBuilders.multiMatchQuery(text, fields.toArray(new String[fields.size()]))
                    .maxExpansions(SearchUtils.DEFAULT_MAX_EXPANSIONS_VALUE)
                    .slop(SearchUtils.DEFAULT_SLOP_VALUE)
                    .operator(operator)
                    .analyzer(searchMorphologically
                            ? SearchUtils.MORPH_STRING_ANALYZER_NAME
                            : SearchUtils.STANDARD_STRING_ANALYZER_NAME)
                    .type(type)
                    .lenient(true);
        }
    }


    /**
     * Creates a fuzzy search query.
     *
     * @param ctx the context
     * @return query builder
     */
    protected QueryBuilder createFuzzyQuery(final SearchRequestContext ctx) {
        Pair<List<String>, List<String>> parsedFields = splitStringAttributes(ctx.getEntity(), ctx.getSearchFields());
        QueryBuilder fuzzyQuery = null;
        QueryBuilder matchQuery = null;
        if (parsedFields != null) {
            if (CollectionUtils.isNotEmpty(parsedFields.getLeft())) {
                fuzzyQuery = createFuzzyQuery(ctx.getText(), parsedFields.getLeft(), false);
            }
            if (CollectionUtils.isNotEmpty(parsedFields.getRight())) {
                matchQuery = createMatchQuery(ctx.getText(), parsedFields.getRight(), false);
            }
            if (fuzzyQuery == null) {
                if (matchQuery == null) {
                    return QueryBuilders.matchAllQuery();
                } else {
                    return matchQuery;
                }
            } else {
                if (matchQuery == null) {
                    return fuzzyQuery;
                } else {
                    BoolQueryBuilder bfb = QueryBuilders.boolQuery();
                    bfb.should(matchQuery);
                    bfb.should(fuzzyQuery);
                    return bfb;
                }
            }
        }
        return QueryBuilders.matchAllQuery();
    }

    /**
     * Creates a fuzzy search query.
     *
     * @param text                  - text
     * @param fields                - fields
     * @param searchMorphologically search using morphological analyzer
     * @return query builder
     */
    private QueryBuilder createFuzzyQuery(String text, List<String> fields, boolean searchMorphologically) {

        if (fields.size() == 1) {
            boolean hasMoreThenOneTerm = text.contains(StringUtils.SPACE);
            Operator operator = hasMoreThenOneTerm ?
                    Operator.OR :
                    Operator.AND;
            return QueryBuilders.matchQuery(fields.get(0), text)
                    .maxExpansions(SearchUtils.DEFAULT_MAX_EXPANSIONS_VALUE)
                    .operator(operator)
                    .analyzer(searchMorphologically
                            ? SearchUtils.MORPH_STRING_ANALYZER_NAME
                            : SearchUtils.STANDARD_STRING_ANALYZER_NAME)
                    .prefixLength(fuzzinessPrefixLength)
                    .fuzziness(Fuzziness.build(defaultFuzziness))
                    .lenient(true);

        } else {
            boolean hasMoreThenOneTerm = text.contains(StringUtils.SPACE);
            Operator operator = hasMoreThenOneTerm ?
                    Operator.OR :
                    Operator.AND;
            return QueryBuilders.multiMatchQuery(text, fields.toArray(new String[fields.size()]))
                    .maxExpansions(SearchUtils.DEFAULT_MAX_EXPANSIONS_VALUE)
                    .slop(SearchUtils.DEFAULT_SLOP_VALUE)
                    .operator(operator)
                    .analyzer(searchMorphologically
                            ? SearchUtils.MORPH_STRING_ANALYZER_NAME
                            : SearchUtils.STANDARD_STRING_ANALYZER_NAME)
                    .prefixLength(fuzzinessPrefixLength)
                    .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                    .fuzziness(Fuzziness.build(defaultFuzziness))
                    .lenient(true);

        }
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
            throw new SearchApplicationException(message, ExceptionId.EX_SEARCH_INVALID_TERM_FIELDS, ctx.getSearchFields());
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

        return QueryBuilders
                .boolQuery()
                .must(QueryBuilders.matchAllQuery())
                .filter(fb)
                /*qb */;
    }

    /**
     * Creates a prefix query.
     *
     * @param ctx the context
     * @return query builder
     */
    protected QueryBuilder createPrefixQuery(final SearchRequestContext ctx) {
        return QueryBuilders.prefixQuery(ctx.getSearchFields().get(0), ctx.getText());
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
            HierarchicalSearchType fromType = (HierarchicalSearchType) main.getType();
            if (!searchRequest.getSupplementary().isEmpty()) {

                BoolQueryBuilder joinChildrenFilter = QueryBuilders.boolQuery();
                for (SearchRequestContext related : searchRequest.getSupplementary()) {
                    HierarchicalSearchType toType = (HierarchicalSearchType) related.getType();
                    QueryBuilder toRequest = createGeneralQueryFromContext(related);
                    QueryBuilder hierarchicalFilter = buildHierarchicalFilter(
                            fromType,
                            toType,
                            toRequest,
                            searchRequest.getMinChildCount(),
                            main.getInnerHits());
                    joinChildrenFilter.must(hierarchicalFilter);
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
    protected AbstractAggregationBuilder<?> createAggregation(AggregationRequestContext ctx) {

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

                for (AggregationRequestContext inner : faCtx.aggregations()) {
                    filterBuilder.subAggregation(createAggregation(inner));
                }

                return filterBuilder;
            case NESTED:

                NestedAggregationRequestContext naCtx = ctx.narrow();
                NestedAggregationBuilder nestedBuilder = AggregationBuilders
                        .nested(naCtx.getName(), naCtx.getPath());

                for (AggregationRequestContext inner : naCtx.aggregations()) {
                    nestedBuilder.subAggregation(createAggregation(inner));
                }

                return nestedBuilder;
            case REVERSE_NESTED:

                ReverseNestedAggregationRequestContext rnaCtx = ctx.narrow();
                ReverseNestedAggregationBuilder reverseNestedBuilder = AggregationBuilders
                        .reverseNested(rnaCtx.getName())
                        .path(rnaCtx.getPath());

                for (AggregationRequestContext inner : rnaCtx.aggregations()) {
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

                for (AggregationRequestContext inner : taCtx.aggregations()) {
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
    private QueryBuilder buildHierarchicalFilter(HierarchicalSearchType fromType, HierarchicalSearchType toType,
                                                 QueryBuilder toRequest, Integer minDocCount, Pair<String, List<String>> innerHits) {
        //Examples: relation -> data || data-> classifier || classifier -> origin etc...
        if (!toType.isTopType() && !fromType.isTopType()) {
            HierarchicalSearchType top = fromType.getTopType();

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
            HasChildQueryBuilder child = JoinQueryBuilders.hasChildQuery(toType.getName(), toRequest, ScoreMode.None);
            if (minDocCount != null) {
                child.minMaxChildren(minDocCount, Integer.MAX_VALUE);
            }

            if (innerHits != null) {
                InnerHitBuilder innerHitBuilder = new InnerHitBuilder();
                innerHitBuilder.setName(innerHits.getKey());
                FetchSourceContext fetchSourceContext = new FetchSourceContext(true, innerHits.getValue().toArray(new String[0]), null);
                innerHitBuilder.setFetchSourceContext(fetchSourceContext);
                // todo change in 4.8
                innerHitBuilder.setSize(300);
                child.innerHit(innerHitBuilder);
            }
            return child;
        }
        throw new RuntimeException("Fail fast exception : unreachable state  was reached (rewrite code!)");
    }
}

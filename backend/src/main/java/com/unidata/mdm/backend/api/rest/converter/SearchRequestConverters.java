package com.unidata.mdm.backend.api.rest.converter;

import static com.unidata.mdm.backend.api.rest.converter.RestSearchDtoConverter.convertSortFields;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unidata.mdm.backend.api.rest.dto.search.SearchComboRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchComplexRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchFormRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchRequestDataType;
import com.unidata.mdm.backend.api.rest.dto.search.SearchRequestRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchSimpleRO;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;

/**
 * Converter for external search request.
 */
public class SearchRequestConverters {

    /**
     * @param request - external request
     * @return search request ctx
     */
    public static SearchRequestContext from(SearchComboRO request) {
        List<FormFieldsGroup> formFields = isEmpty(request.getFormFields())?  null : RestSearchDtoConverter.convert(request.getFormFields());
        return getBuilder(request).asOf(request.getAsOf())
                                  .search(request.getQtype())
                                  .operator(request.getOperator())
                                  .returnFields(request.getReturnFields())
                                  .addSorting(convertSortFields(request))
                                  .form(formFields)
                                  .searchFields(request.getSearchFields())
                                  .text(request.getText())
                                  .facetsAsStrings(request.getFacets())
                                  .count(request.getCount())
                                  .page(request.getPage() > 0 ? request.getPage() - 1 : request.getPage())
                                  .source(request.isSource())
                                  .totalCount(request.isTotalCount())
                                  .countOnly(request.isCountOnly())
                                  .fetchAll(request.isFetchAll())
                                  .onlyQuery(request.getDataType() == SearchRequestDataType.CLASSIFIER ? true : false)
                                  .runExits(true)
                                  .build();
    }

    /**
     * @param request - external request
     * @return search request ctx
     */
    public static SearchRequestContext from(SearchSimpleRO request) {
        return getBuilder(request).asOf(request.getAsOf())
                                  .search(request.getQtype())
                                  .operator(request.getOperator())
                                  .returnFields(request.getReturnFields())
                                  .searchFields(request.getSearchFields())
                                  .facetsAsStrings(request.getFacets())
                                  .addSorting(RestSearchDtoConverter.convertSortFields(request))
                                  .text(request.getText(), request.isSayt())
                                  .count(request.getCount())
                                  .page(request.getPage() > 0 ? request.getPage() - 1 : request.getPage())
                                  .source(request.isSource())
                                  .totalCount(request.isTotalCount())
                                  .countOnly(request.isCountOnly())
                                  .fetchAll(request.isFetchAll())
                                  .onlyQuery(request.getDataType() == SearchRequestDataType.CLASSIFIER ? true : false)
                                  .runExits(true)
                                  .build();
    }

    /**
     * @param request - external request
     * @return search request ctx
     */
    public static SearchRequestContext from(SearchFormRO request) {
        List<FormFieldsGroup> formFields = isEmpty(request.getFormFields())?  null : RestSearchDtoConverter.convert(request.getFormFields());
        return getBuilder(request).asOf(request.getAsOf())
                                  .search(request.getQtype())
                                  .operator(request.getOperator())
                                  .returnFields(request.getReturnFields())
                                  .addSorting(RestSearchDtoConverter.convertSortFields(request))
                                  .form(formFields)
                                  .facetsAsStrings(request.getFacets())
                                  .count(request.getCount())
                                  .page(request.getPage() > 0 ? request.getPage() - 1 : request.getPage())
                                  .source(request.isSource())
                                  .totalCount(request.isTotalCount())
                                  .countOnly(request.isCountOnly())
                                  .fetchAll(request.isFetchAll())
                                  .onlyQuery(request.getDataType() == SearchRequestDataType.CLASSIFIER ? true : false)
                                  .runExits(true)
                                  .build();
    }

    /**
     * @param request - external request
     * @return search request ctx
     */
    public static SearchRequestContext from(SearchComplexRO request) {
        List<FormFieldsGroup> formFields = isEmpty(request.getFormFields())?  null : RestSearchDtoConverter.convert(request.getFormFields());
        return getBuilder(request).asOf(request.getAsOf())
                                  .search(request.getQtype())
                                  .operator(request.getOperator())
                                  .returnFields(request.getReturnFields())
                                  .searchFields(request.getSearchFields())
                                  .text(request.getText())
                                  .addSorting(RestSearchDtoConverter.convertSortFields(request))
                                  .form(formFields)
                                  .facetsAsStrings(request.getFacets())
                                  .count(request.getCount())
                                  .page(request.getPage() > 0 ? request.getPage() - 1 : request.getPage())
                                  .source(request.isSource())
                                  .totalCount(request.isTotalCount())
                                  .countOnly(request.isCountOnly())
                                  .fetchAll(request.isFetchAll())
                                  .onlyQuery(request.getDataType() == SearchRequestDataType.CLASSIFIER ? true : false)
                                  .runExits(true)
                                  .build();
    }

    /**
     * @param source
     * @return builder for search
     */
    @Nonnull
    private static SearchRequestContext.SearchRequestContextBuilder getBuilder(@Nonnull SearchRequestRO source) {
        SearchRequestDataType type = source.getDataType();
        switch (type) {
        case ETALON:
            return SearchRequestContext.forEtalon(EntitySearchType.ETALON, source.getEntity());
        case ETALON_DATA:
            return SearchRequestContext.forEtalon(EntitySearchType.ETALON_DATA, source.getEntity());
        case ETALON_REL:
            return SearchRequestContext.forEtalon(EntitySearchType.ETALON_RELATION, source.getEntity());
        case CLASSIFIER:
            return SearchRequestContext.builder(EntitySearchType.CLASSIFIER, source.getEntity());
        default:
            throw new RuntimeException();
        }
    }

    /**
     * @param source - abstract external request
     * @return search request ctx
     */
    @Nullable
    public static SearchRequestContext from(@Nullable SearchRequestRO source) {
        if (source == null) {
            return null;
        }
        if (SearchSimpleRO.class.isInstance(source)) {
            SearchSimpleRO request = (SearchSimpleRO) source;
            return from(request);
        } else if (SearchFormRO.class.isInstance(source)) {
            SearchFormRO request = (SearchFormRO) source;
            return from(request);
        } else if (SearchComboRO.class.isInstance(source)) {
            SearchComboRO request = (SearchComboRO) source;
            return from(request);
        } else if (SearchComplexRO.class.isInstance(source)){
            SearchComplexRO request = (SearchComplexRO) source;
            return from(request);
        }
        return null;
    }
}

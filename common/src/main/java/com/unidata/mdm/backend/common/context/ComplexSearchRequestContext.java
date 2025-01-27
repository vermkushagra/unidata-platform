package com.unidata.mdm.backend.common.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections.CollectionUtils;

import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Need for complex search over parent child relation, child - child relation.
 */
public class ComplexSearchRequestContext {

    /**
     * Main request
     */
    private SearchRequestContext mainRequest;
    /**
     * Supplementary requests
     */
    private Collection<SearchRequestContext> supplementary;

    private Integer minChildCount;


    /**
     * private constructor
     */
    private ComplexSearchRequestContext() {
    }

    /**
     * @param main          - main search request which results will be filtered by supplementary requests
     * @param supplementary - supplementary requests will be used for filtering results of main request.
     * @return complex search context for hierarchical search
     */
    @Nonnull
    public static ComplexSearchRequestContext hierarchical(@Nonnull SearchRequestContext main,
            SearchRequestContext... supplementary) {
        if (supplementary == null || supplementary.length == 0) {
            ComplexSearchRequestContext context = new ComplexSearchRequestContext();
            context.mainRequest = main;
            context.supplementary = Collections.emptyList();
            return context;
        }
        String entity = main.getEntity();
        boolean isTheSameEntities = Arrays.stream(supplementary)
                                          .allMatch(ctx -> Objects.equals(ctx.getEntity(), entity));
        if (!isTheSameEntities) {
            throw new BusinessException("Platform doesn't support in related request cross indexing",
                    ExceptionId.EX_SEARCH_COMPLEX_RELATED_REQUEST_INCORRECT);
        }

        boolean isRelatedTypes = Arrays.stream(supplementary)
                                       .allMatch(ctx -> ctx.getType().isRelatedWith(main.getType()));
        if (!isRelatedTypes) {
            throw new BusinessException("Platform doesn't support in related request unrelated types",
                    ExceptionId.EX_SEARCH_COMPLEX_RELATED_REQUEST_INCORRECT);
        }

        ComplexSearchRequestContext context = new ComplexSearchRequestContext();
        context.mainRequest = main;
        context.supplementary = Arrays.stream(supplementary).collect(Collectors.toList());
        return context;
    }

    /**
     * @param crossRequests - collection of search indexes.
     * @return complex request for searching over a few indexs.
     */
    @Nonnull
    public static ComplexSearchRequestContext multi(@Nonnull Collection<SearchRequestContext> crossRequests) {
        ComplexSearchRequestContext context = new ComplexSearchRequestContext();
        context.supplementary = crossRequests;
        return context;
    }

    /**
     * @param crossRequests - collection of search indexes.
     * @return complex request for searching over a few indexs.
     */
    @Nonnull
    public static ComplexSearchRequestContext multi(@Nonnull SearchRequestContext requared,
            SearchRequestContext... crossRequests) {
        ComplexSearchRequestContext context = new ComplexSearchRequestContext();
        context.supplementary = Arrays.stream(crossRequests).collect(Collectors.toList());
        context.supplementary.add(requared);
        return context;
    }

    /**
     * @return set of unique entity names for requests
     */
    public Set<String> getEntityNames() {
        if (getType() == Type.HIERARCHICAL) {
            return Collections.singleton(mainRequest.getEntity());
        } else {
            return supplementary.stream().map(SearchRequestContext::getEntity).collect(Collectors.toSet());
        }
    }

    /**
     * @return collection of search requests.
     */
    public Collection<SearchRequestContext> getAllInnerContexts() {
        if (getType() == Type.MULTI) {
            return supplementary;
        } else {
            List<SearchRequestContext> result = new ArrayList<>(supplementary);
            result.add(mainRequest);
            return result;
        }
    }

    /**
     * @return true if context doesn't contain any inner contexts.
     */
    public boolean isEmpty() {
        if (getType() == Type.HIERARCHICAL) {
            return mainRequest == null;
        } else {
            return CollectionUtils.isEmpty(getSupplementary());
        }
    }

    /**
     * @return type of complex search request
     */
    public Type getType() {
        return mainRequest == null ? Type.MULTI : Type.HIERARCHICAL;
    }

    /**
     * @return main request
     */
    @Nullable
    public SearchRequestContext getMainRequest() {
        return mainRequest;
    }

    /**
     * @return supplementary requests
     */
    @Nonnull
    public Collection<SearchRequestContext> getSupplementary() {
        return supplementary;
    }

    public Integer getMinChildCount() {
        return minChildCount;
    }

    public void setMinChildCount(Integer minChildCount) {
        this.minChildCount = minChildCount;
    }


    /**
     * type of complex request
     */
    public enum Type {
        MULTI, HIERARCHICAL;
    }
}

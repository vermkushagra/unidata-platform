package com.unidata.mdm.backend.api.rest.util;

import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;

import javax.annotation.Nonnull;
import java.util.EnumSet;

/**
 * @author Dmitry Kopin on 30.10.2017.
 */
public interface SearchResultHitModifier {

    /**
     * Method provide search with after processing which replace lookup entity code id to main displayable attrs sequence.
     *
     * @param cCtx          the search context
     * @param searchResult result of search by ctx
     */
    void modifySearchResult(@Nonnull SearchResultDTO searchResult, @Nonnull ComplexSearchRequestContext cCtx);

    /**
     * Method provide search with after processing which modify all processed elements to display view.
     *
     * @param ctx          the search context
     * @param searchResult result of search by ctx
     */
    void modifySearchResult(@Nonnull SearchResultDTO searchResult, @Nonnull SearchRequestContext ctx);

    /**
     * Method provide search with after processing which modify processed elements to display view.
     *
     * @param ctx          the search context
     * @param searchResult result of search by ctx
     * @param enumSet elements to process
     */
    void modifySearchResult(@Nonnull SearchResultDTO searchResult, @Nonnull SearchRequestContext ctx,
                                   EnumSet<SearchResultHitModifierImpl.ProcessingElements> enumSet);


}

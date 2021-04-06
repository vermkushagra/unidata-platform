package com.unidata.mdm.backend.common.integration.exits;

import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;

/**
 * @author Dmitry Kopin
 * Search listener interface.
 */
public interface SearchListener {
    /**
     * Method is called before search operation,
     * @param ctx search request context
     * @return {@link ExitResult} with information about execution user exit
     */
     default ExitResult beforeSearch(SearchRequestContext ctx){
         return null;
     }
    /**
     * Method is called after search operation and after build search result
     * @param ctx search request context
     * @param searchResult search result
     * @return {@link ExitResult} with information about execution user exit
     */
    default ExitResult afterSearch(SearchRequestContext ctx, SearchResultDTO searchResult){
        return null;
    }
}

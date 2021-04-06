/**
 *
 */
package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.ErrorInfo;
import com.unidata.mdm.backend.api.rest.dto.search.SearchResultHitFieldRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchResultHitRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchResultRO;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.common.types.RecordStatus;
import org.apache.commons.collections.CollectionUtils;

/**
 * @author Mikhail Mikhailov
 */
public class SearchResultToRestSearchResultConverter {

    /**
     * Constructor.
     */
    private SearchResultToRestSearchResultConverter() {
        super();
    }

    /**
     * Converts search result to REST search result.
     *
     * @param result the result from the search service.
     * @return REST object
     */
    public static SearchResultRO convert(SearchResultDTO result, boolean isDeleted) {

        if (result == null) {
            return null;
        }

        SearchResultRO ro = new SearchResultRO();

        ro.setFields(result.getFields());
        ro.setTotalCount(result.getTotalCount());
        ro.setMaxScore(result.getMaxScore());

        if (CollectionUtils.isNotEmpty(result.getErrors())) {
            List<ErrorInfo> errorInfos = new ArrayList<>();
            result.getErrors().forEach(error -> errorInfos.add(ErrorInfoToRestErrorInfoConverter.convert(error)));
            ro.setErrors(errorInfos);
            ro.setSuccess(true);
        }

        convertSearchHits(result.getHits(), ro.getHits(), isDeleted);

        return ro;
    }

    /**
     * Hits -> hits conversion method.
     *
     * @param source    the source
     * @param target    the target
     * @param isDeleted
     */
    private static void convertSearchHits(List<SearchResultHitDTO> source, List<SearchResultHitRO> target, boolean isDeleted) {
        for (SearchResultHitDTO hit : source) {
            SearchResultHitRO hitRO = new SearchResultHitRO(hit.getInternalId());
            for (SearchResultHitFieldDTO field : hit.getPreview().values()) {
                hitRO.getPreview()
                        .add(new SearchResultHitFieldRO(field.getField(), field.getFirstValue(), field.getValues()));
            }
            hitRO.setSource(hit.getSource());
            hitRO.setScore(hit.getScore());
            hitRO.setStatus(isDeleted ? RecordStatus.INACTIVE.name() : RecordStatus.ACTIVE.name());
            target.add(hitRO);
        }
    }
}

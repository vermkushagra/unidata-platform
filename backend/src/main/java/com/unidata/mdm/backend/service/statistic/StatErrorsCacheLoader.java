package com.unidata.mdm.backend.service.statistic;

import static com.unidata.mdm.backend.common.context.SearchRequestContext.forEtalonData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.cache.CacheLoader;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.statistic.ErrorsStatDTO;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.types.SeverityType;

/**
 * The Class StatErrorsCacheLoader.
 */
public class StatErrorsCacheLoader extends CacheLoader<ErrorsStatDTO, ErrorsStatDTO> {
    /**
     * ALL entities.
     */
    private static final String ALL = "ALL";

    private SearchService searchService;

    /**
     * Instantiates a new stat errors cache loader.
     *
     * @param searchService the search service
     */
    public StatErrorsCacheLoader(SearchService searchService) {
        this.searchService = searchService;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.google.common.cache.CacheLoader#load(java.lang.Object)
     */
    @Override
    public ErrorsStatDTO load(ErrorsStatDTO key)  {
        ErrorsStatDTO value = new ErrorsStatDTO();
        value.setEntityName(key.getEntityName());
        Map<SeverityType, Integer> data = new HashMap<>();
        for (SeverityType severityType : SeverityType.values()) {
            if (!StringUtils.equals(key.getEntityName(), ALL)) {
                SearchRequestContext ctx = forEtalonData(key.getEntityName()).asOf(new Date()).build();
                int count = (int) searchService.countErrorsBySeverity(severityType.name(), ctx);
				data.put(severityType, count);
				value.setTotal(value.getTotal() + count);
			}
        }
        value.setData(data);
        return value;
    }

}
package com.unidata.mdm.backend.service.statistic;

import com.unidata.mdm.backend.api.rest.dto.table.SearchableTable;
import com.unidata.mdm.backend.common.service.StatService;
import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;

import java.util.Date;

public interface StatServiceExt extends StatService, AfterContextRefresh {

    /**
     * Persist current statistic for all entities.
     */
    void persistStatistic(Date fromDate, Date toDate) ;

    /**
     * Sets the cache ttl.
     *
     * @param cacheTTL
     *            the new cache ttl
     */
    void setCacheTTL(int cacheTTL);

    SearchableTable getErrorStatisticAggregation(String entityName);
}
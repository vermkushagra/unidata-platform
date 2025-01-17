package com.unidata.mdm.backend.service.statistic;

import com.google.common.cache.CacheLoader;
import com.unidata.mdm.backend.common.context.StatisticRequestContext;
import com.unidata.mdm.backend.common.dto.statistic.StatisticDTO;
import com.unidata.mdm.backend.common.dto.statistic.StatisticResponseDTO;
import com.unidata.mdm.backend.common.dto.statistic.TimeSerieDTO;
import com.unidata.mdm.backend.common.statistic.GranularityType;
import org.apache.commons.lang.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * The Class StatisticCacheLoader.
 */
public class StatisticCacheLoader extends CacheLoader<StatisticRequestContext, StatisticResponseDTO> {
    private StatServiceExt statService;

    /**
     * Instantiates a new statistic cache loader.
     *
     * @param statService
     */
    public StatisticCacheLoader(StatServiceExt statService) {
        this.statService = statService;

    }

    /*
     * (non-Javadoc)
     *
     * @see com.google.common.cache.CacheLoader#load(java.lang.Object)
     */
    @Override
    public StatisticResponseDTO load(StatisticRequestContext request) {
        StatisticResponseDTO response = new StatisticResponseDTO();
        if(request.isForLastDate()){
            response.setStats(statService.gatherLastAvailableStatistic(request.getEntityName()));
        } else {
            response.setStats(statService.gatherHistoricalStatistic(request.getStartDate(), request.getEndDate(),
                    request.getEntityName()));
            filterByGranularity(request, response);
        }
        return response;
    }

    private StatisticResponseDTO filterByGranularity(final StatisticRequestContext request, final StatisticResponseDTO response) {
        Long temporalUnit = GranularityType.toTemporalUnit(request.getGranularity()).getDuration().getSeconds();
        for (StatisticDTO stat : response.getStats()) {
            List<TimeSerieDTO> filteredSeries = new ArrayList<>();
            Date leftSide = request.getStartDate();
            Date rightSide = DateUtils.addSeconds(leftSide, temporalUnit.intValue());
            Iterator<TimeSerieDTO> it = stat.getSeries().iterator();

            TimeSerieDTO timeSerie = it.next();
            do {
                // if it is last period
                if(DateUtils.addSeconds(rightSide, temporalUnit.intValue()).after(request.getEndDate())){
                    rightSide = request.getEndDate();
                }
                int filteredValue = 0;
                boolean notEmptyInterval = false;
                while (!timeSerie.getTime().before(leftSide) && timeSerie.getTime().before(rightSide)) {
                    if (stat.getType().isAggregate()) {
                        filteredValue += timeSerie.getValue();
                    } else {
                        filteredValue = timeSerie.getValue();
                    }
                    notEmptyInterval = true;

                    if(!it.hasNext()){
                        break;
                    }
                    timeSerie = it.next();
                }
                if(notEmptyInterval){
                    TimeSerieDTO filteredSerie = new TimeSerieDTO();
                    filteredSerie.setTime(rightSide);
                    filteredSerie.setValue(filteredValue);
                    filteredSeries.add(filteredSerie);
                }
                leftSide = rightSide;
                rightSide = DateUtils.addSeconds(leftSide, temporalUnit.intValue());
            }
            while (!rightSide.after(request.getEndDate()));
            stat.setSeries(filteredSeries);
        }
        return response;
    }
}

package com.unidata.mdm.backend.common.dto.statistic;

import java.util.List;

import com.unidata.mdm.backend.common.statistic.StatisticType;

/**
 * The Class Statistic.
 */
public class StatisticDTO {

    /** statistic type. */
    private StatisticType type;
    /**
     * entity name
     */
    private String entityName;

    /** time series. */
    private List<TimeSerieDTO> series;

    /**
     * Gets the type.
     *
     * @return the type
     */
    public StatisticType getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type
     *            the new type
     */
    public void setType(StatisticType type) {
        this.type = type;
    }

    /**
     * Gets the series.
     *
     * @return the series
     */
    public List<TimeSerieDTO> getSeries() {
        return series;
    }

    /**
     * Sets the series.
     *
     * @param series
     *            the new series
     */
    public void setSeries(List<TimeSerieDTO> series) {
        this.series = series;
    }

    /**
     * gets entity name
     * @return entity name
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * sets entity name
     * @param entityName entity name
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
}

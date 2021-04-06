package com.unidata.mdm.backend.common.dto.statistic;

import java.util.List;


/**
 * The Class StatisticResponse.
 */
public class StatisticResponseDTO {
    
    /** The stats. */
    private List<StatisticDTO> stats;

    /**
     * Gets the stats.
     *
     * @return the stats
     */
    public List<StatisticDTO> getStats() {
        return stats;
    }

    /**
     * Sets the stats.
     *
     * @param stats the new stats
     */
    public void setStats(List<StatisticDTO> stats) {
        this.stats = stats;
    }
}

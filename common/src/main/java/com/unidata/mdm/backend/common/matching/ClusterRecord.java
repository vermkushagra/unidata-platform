package com.unidata.mdm.backend.common.matching;

import java.util.Date;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.common.search.types.Aggregatable;
/**
 * Denotes a clustered record.
 */
public class ClusterRecord implements Aggregatable {
    /**
     * Etalon id of the record.
     */
    @Nonnull
    private final String etalonId;
    /**
     * Build date of the cluster.
     */
    @Nonnull
    private final Date matchingDate;
    /**
     * Matching rate. Always 100% for exact rules.
     */
    private final int matchingRate;

    public ClusterRecord(@Nonnull String etalonId, @Nonnull Date etalonDate, int matchingRate) {
        this.matchingRate = matchingRate;
        this.etalonId = etalonId;
        this.matchingDate = etalonDate;
    }

    @Nonnull
    public String getEtalonId() {
        return etalonId;
    }

    @Nonnull
    public Date getMatchingDate() {
        return matchingDate;
    }

    public int getMatchingRate() {
        return matchingRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClusterRecord)) return false;

        ClusterRecord that = (ClusterRecord) o;

        return etalonId.equals(that.etalonId);
    }

    @Override
    public int hashCode() {
        return etalonId.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean discard() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean stop() {
        return false;
    }
}

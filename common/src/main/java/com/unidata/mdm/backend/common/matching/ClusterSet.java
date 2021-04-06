package com.unidata.mdm.backend.common.matching;

import java.util.Collection;
import java.util.Date;

/**
 * @author Mikhail Mikhailov
 * Clusters for a period.
 */
public class ClusterSet {
    /**
     * Clusters collection.
     */
    private Collection<Cluster> clusters;
    /**
     * Cluster validity from boundary.
     */
    private Date from;
    /**
     * Cluster validity to boundary.
     */
    private Date to;
    /**
     * Constructor.
     */
    public ClusterSet() {
        super();
    }
    /**
     * Constructor.
     * @param clusters clusters
     * @param from period's from
     * @param to period's to
     */
    public ClusterSet(Collection<Cluster> clusters, Date from, Date to) {
        super();
        this.clusters = clusters;
        this.from = from;
        this.to = to;
    }
    /**
     * @return the from
     */
    public Date getFrom() {
        return from;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(Date from) {
        this.from = from;
    }

    /**
     * @return the to
     */
    public Date getTo() {
        return to;
    }

    /**
     * @param to the to to set
     */
    public void setTo(Date to) {
        this.to = to;
    }

    /**
     * @return the clusters
     */
    public Collection<Cluster> getClusters() {
        return clusters;
    }

    /**
     * @param clusters the clusters to set
     */
    public void setClusters(Collection<Cluster> clusters) {
        this.clusters = clusters;
    }
}

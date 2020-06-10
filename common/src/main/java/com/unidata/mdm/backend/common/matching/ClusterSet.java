/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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

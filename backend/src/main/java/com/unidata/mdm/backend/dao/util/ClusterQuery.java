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

package com.unidata.mdm.backend.dao.util;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClusterQuery {

    private static final Comparator<Conditions> COMPARATOR = Comparator.comparingInt(Conditions::getOrder);

    private String entityName;

    private Integer ruleId;

    private Collection<String> etalonIds;

    private Long clusterId;

    private String clusterIdentifier;

    private Collection<Conditions> conditions = new TreeSet<>(COMPARATOR);

    private Integer limit;

    private Integer offset;

    private Date matchingDate;

    private ClusterQuery() {
    }

    public Collection<Conditions> getConditions() {
        return conditions;
    }

    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("etalonIds", etalonIds.stream().map(UUID::fromString).collect(Collectors.toList()));
        map.put("clusterId", clusterId);
        map.put("hash", clusterIdentifier);
        map.put("limit", limit);
        map.put("offset", offset);
        map.put("entityName", entityName);
        map.put("ruleId", ruleId);
        map.put("order", "cl.id");
        map.put("matchingDate", matchingDate);
        return map;
    }

    public enum Conditions {
        CORRUPTED(0), ETALON_ID(1), CLUSTER_ID(2), HASH(3), RULE(4), ENTITY(5), ORDER(6), MATCHING_DATE(7), LIMIT(8), OFFSET(9), ;

        private int order;

        Conditions(int order) {
            this.order = order;
        }

        public int getOrder() {
            return order;
        }
    }

    public static ClusterQueryBuilder builder() {
        return new ClusterQueryBuilder();
    }

    public static final class ClusterQueryBuilder {
        private String entityName;
        private Integer ruleId;
        private Collection<String> etalonIds;
        private Long clusterId;
        private String clusterIdentifier;
        private Integer limit;
        private Integer offset;
        private Date matchingDate;
        private Collection<Conditions> conditions = new ArrayList<>();

        private ClusterQueryBuilder() {
        }

        public ClusterQueryBuilder withEntityName(String entityName) {
            this.entityName = entityName;
            conditions.add(Conditions.ENTITY);
            return this;
        }

        public ClusterQueryBuilder withRuleId(Integer ruleId) {
            this.ruleId = ruleId;
            if (ruleId != null) {
                conditions.add(Conditions.RULE);
            }
            return this;
        }

        public ClusterQueryBuilder withEtalonIds(Collection<String> etalonIds) {
            this.etalonIds = etalonIds;
            conditions.add(Conditions.ETALON_ID);
            return this;
        }

        public ClusterQueryBuilder withClusterId(Long clusterId) {
            this.clusterId = clusterId;
            conditions.add(Conditions.CLUSTER_ID);
            return this;
        }

        public ClusterQueryBuilder withClusterIdentifier(String clusterIdentifier) {
            this.clusterIdentifier = clusterIdentifier;
            if (clusterIdentifier != null) {
                conditions.add(Conditions.HASH);
            }
            return this;
        }

        public ClusterQueryBuilder withLimit(Integer limit) {
            this.limit = limit;
            conditions.add(Conditions.LIMIT);
            return this;
        }

        public ClusterQueryBuilder withOffset(Integer offset) {
            this.offset = offset;
            conditions.add(Conditions.OFFSET);
            return this;
        }

        public ClusterQueryBuilder ordered() {
            conditions.add(Conditions.ORDER);
            return this;
        }

        public ClusterQueryBuilder corrupted() {
            conditions.add(Conditions.CORRUPTED);
            return this;
        }

        public ClusterQueryBuilder withMatchingDate(Date matchingDate) {
            this.matchingDate = matchingDate;
            if (matchingDate != null) {
                conditions.add(Conditions.MATCHING_DATE);
            }
            return this;
        }


        public ClusterQuery build() {
            ClusterQuery clusterQuery = new ClusterQuery();
            clusterQuery.etalonIds = this.etalonIds == null ? Collections.emptyList() : this.etalonIds;
            clusterQuery.clusterIdentifier = this.clusterIdentifier;
            clusterQuery.entityName = this.entityName;
            clusterQuery.clusterId = this.clusterId;
            clusterQuery.ruleId = this.ruleId;
            clusterQuery.limit = this.limit;
            clusterQuery.offset = this.offset;
            clusterQuery.matchingDate = this.matchingDate;
            clusterQuery.conditions.addAll(this.conditions);
            return clusterQuery;
        }
    }
}

package com.unidata.mdm.backend.dao.util;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClusterQuery {

    private static final Comparator<Conditions> COMPARATOR = (o1, o2) -> Integer.compare(o1.getOrder(), o2.getOrder());

    private String entityName;

    private String storage;

    private Integer groupId;

    private Integer ruleId;

    private Collection<String> etalonIds;

    private Long clusterId;

    private String clusterIdentifier;

    private Collection<Conditions> conditions = new TreeSet<>(COMPARATOR);

    private Integer limit;

    private Integer offset;

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
        map.put("storage", storage);
        map.put("ruleId", ruleId);
        map.put("groupId", groupId);
        map.put("order", "cl.id");
        return map;
    }

    public enum Conditions {
        CORRUPTED(0), ETALON_ID(1), CLUSTER_ID(2), HASH(3), RULE(4), GROUP(5), GROUP_NULL(5), ENTITY(6), ORDER(7), LIMIT(8), OFFSET(9);

        private int order;

        Conditions(int order) {
            this.order = order;
        }

        public int getOrder() {
            return order;
        }
    }

    public static final class ClusterQueryBuilder {
        private String entityName;
        private String storage;
        private Integer groupId;
        private Integer ruleId;
        private Collection<String> etalonIds;
        private Long clusterId;
        private String clusterIdentifier;
        private Integer limit;
        private Integer offset;
        private Collection<Conditions> conditions = new ArrayList<>();

        private ClusterQueryBuilder() {
        }

        public static ClusterQueryBuilder query() {
            return new ClusterQueryBuilder();
        }

        public ClusterQueryBuilder withEntityName(String entityName, String storage) {
            this.storage = storage;
            this.entityName = entityName;
            conditions.add(Conditions.ENTITY);
            return this;
        }

        public ClusterQueryBuilder withGroupId(Integer groupId) {
            this.groupId = groupId;
            conditions.add(this.groupId == null ? Conditions.GROUP_NULL : Conditions.GROUP);
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

        public ClusterQuery build() {
            ClusterQuery clusterQuery = new ClusterQuery();
            clusterQuery.etalonIds = this.etalonIds == null ? Collections.emptyList() : this.etalonIds;
            clusterQuery.clusterIdentifier = this.clusterIdentifier;
            clusterQuery.entityName = this.entityName;
            clusterQuery.groupId = this.groupId;
            clusterQuery.clusterId = this.clusterId;
            clusterQuery.ruleId = this.ruleId;
            clusterQuery.limit = this.limit;
            clusterQuery.offset = this.offset;
            clusterQuery.storage = this.storage;
            if (this.conditions.contains(Conditions.GROUP_NULL) && !this.conditions.contains(Conditions.RULE)) {
                this.conditions.remove(Conditions.GROUP_NULL);
            }
            clusterQuery.conditions.addAll(this.conditions);
            return clusterQuery;
        }
    }
}

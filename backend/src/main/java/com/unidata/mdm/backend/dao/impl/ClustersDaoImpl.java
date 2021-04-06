package com.unidata.mdm.backend.dao.impl;

import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_MATCHING_CLUSTER_ALREADY_MODIFIED;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.dao.ClustersDao;
import com.unidata.mdm.backend.dao.util.ClusterQuery;
import com.unidata.mdm.backend.po.matching.ClusterPO;
import com.unidata.mdm.backend.po.matching.ClusterRecordPO;

@Repository
public class ClustersDaoImpl extends AbstractDaoImpl implements ClustersDao {

    private static final Integer INITIAL_VERSION = 1;

    private final String SELECT;

    private final String SELECT_IDS;

    private final String SELECT_COUNT;

    private final String SELECT_RECORDS_COUNT;

    private final String INSERT_CLUSTER;

    private final String UPDATE_CLUSTER;

    private final String INSERT_RECORDS;

    //removing clusters
    private final String DELETE_CLUSTERS;

    //removing etalon from matched records
    private final String DELETE_RECORDS_FROM_CLUSTER_BY_ETALONS;
    private final String DELETE_RECORDS_FROM_CLUSTER_BY_CLUSTER;

    //block list!
    private final String INSERT_INTO_BLOCK_LIST;
    private final String SELECT_BLOCKED_RECORDS;
    private final String DELETE_BLOCKED_RECORDS;

    /**
     * Result set extractor, for cluster
     */
    private static final ResultSetExtractor<Collection<ClusterPO>> RECORD_CLUSTER_EXTRACTOR = rs -> {
        Map<Long, ClusterPO> clusterMap = new HashMap<>();
        while (rs.next()) {
            Long clusterId = rs.getLong("cluster_id");
            if (rs.wasNull()) {
                continue;
            }
            ClusterPO cluster = clusterMap.get(clusterId);
            if (cluster == null) {
                cluster = new ClusterPO();
                clusterMap.put(clusterId, cluster);
                cluster.setClusterId(clusterId);
                cluster.setClusterRecordPOs(new HashMap<>());
                cluster.setMatchingDate(rs.getTimestamp("matching_date"));
                cluster.setStorage(rs.getString("storage_fkey"));
                cluster.setVersion(rs.getInt("version"));
                cluster.setRuleId(rs.getInt("rule_id"));
                cluster.setClusterHash(rs.getString("cluster_identifier"));
                cluster.setEntityName(rs.getString("entity_name"));
                cluster.setGroupId(rs.getInt("group_id"));
            }
            String etalonId = rs.getString("etalon_id");
            Date date = rs.getTimestamp("etalon_date");
            int matchingRate = rs.getInt("matching_rate");
            ClusterRecordPO clusterRecord = new ClusterRecordPO();
            clusterRecord.setEtalonId(etalonId);
            clusterRecord.setEtalonDate(date);
            clusterRecord.setMatchingRate(matchingRate);
            clusterRecord.setClusterId(clusterId);
            cluster.getClusterRecordPOs().put(clusterRecord.getEtalonId(), clusterRecord);
        }
        return new ArrayList<>(clusterMap.values());
    };

    private static final ResultSetExtractor<Multimap<String, String>> BLOCKED_RECORDS_EXTRACTOR = rs -> {
        Multimap<String, String> result = HashMultimap.create();
        while (rs.next()) {
            String blocked = rs.getString("blocked_etalon_id");
            String blockedFor = rs.getString("blocked_for_etalon_id");
            result.put(blocked, blockedFor);
        }
        return result;
    };

    /**
     * Constructor.
     *
     * @param dataSource
     */
    @Autowired
    public ClustersDaoImpl(DataSource dataSource, @Qualifier("cluster-sql") Properties sql) {
        super(dataSource);
        SELECT = sql.getProperty("SELECT");
        SELECT_IDS = sql.getProperty("SELECT_IDS");
        SELECT_COUNT = sql.getProperty("SELECT_COUNT");
        SELECT_RECORDS_COUNT = sql.getProperty("SELECT_RECORDS_COUNT");
        INSERT_CLUSTER = sql.getProperty("INSERT_CLUSTER");
        UPDATE_CLUSTER = sql.getProperty("UPDATE_CLUSTER");
        INSERT_RECORDS = sql.getProperty("INSERT_RECORDS");
        DELETE_CLUSTERS = sql.getProperty("DELETE_CLUSTERS");
        DELETE_RECORDS_FROM_CLUSTER_BY_ETALONS = sql.getProperty("DELETE_RECORDS_FROM_CLUSTER_BY_ETALONS");
        DELETE_RECORDS_FROM_CLUSTER_BY_CLUSTER = sql.getProperty("DELETE_RECORDS_FROM_CLUSTER_BY_CLUSTER");
        INSERT_INTO_BLOCK_LIST = sql.getProperty("INSERT_INTO_BLOCK_LIST");
        SELECT_BLOCKED_RECORDS = sql.getProperty("SELECT_BLOCKED_RECORDS");
        DELETE_BLOCKED_RECORDS = sql.getProperty("DELETE_BLOCKED_RECORDS");
    }

    @Override
    public void insertCluster(@Nonnull ClusterPO cluster) {
        cluster.setVersion(INITIAL_VERSION);
        MapSqlParameterSource map = getClusterMap(cluster);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedJdbcTemplate.update(INSERT_CLUSTER, map, keyHolder, new String[]{"id"});
        Long clusterId = keyHolder.getKey().longValue();
        cluster.setClusterId(clusterId);
        cluster.getClusterRecordPOs().values().forEach(rec -> rec.setClusterId(clusterId));
        insertRecords(cluster.getClusterRecordPOs().values());
    }

    @Override
    public void updateCluster(@Nonnull ClusterPO existedCluster, @Nonnull ClusterPO newCluster) {
        calculateDiff(existedCluster, newCluster);
        if (existedCluster.getClusterRecordPOs().isEmpty() && newCluster.getClusterRecordPOs().isEmpty()) {
            //the same cluster records
            return;
        }
        newCluster.setVersion(existedCluster.getVersion());
        MapSqlParameterSource map = getClusterMap(newCluster);
        map.addValue("id", existedCluster.getClusterId());
        int row = namedJdbcTemplate.update(UPDATE_CLUSTER, map);
        if (row == 0) {
            throw new DataProcessingException("Some one already modify cluster", EX_MATCHING_CLUSTER_ALREADY_MODIFIED);
        }
        if (!existedCluster.getClusterRecordPOs().isEmpty()) {
            //remove old
            map.addValue("ids", existedCluster.getClusterRecordPOs().keySet());
            namedJdbcTemplate.update(DELETE_RECORDS_FROM_CLUSTER_BY_CLUSTER, map);
        }
        if (!newCluster.getClusterRecordPOs().isEmpty()) {
            //insert new
            insertRecords(newCluster.getClusterRecordPOs().values());
        }
    }

    private void calculateDiff(@Nonnull ClusterPO existedCluster, @Nonnull ClusterPO newCluster) {
        //todo copy of map, in case when we will need to work with cluster after upsert
        Map<String, ClusterRecordPO> existedRecords = existedCluster.getClusterRecordPOs();
        Map<String, ClusterRecordPO> newRecords = newCluster.getClusterRecordPOs();
        for (Iterator<String> it = existedRecords.keySet().iterator(); it.hasNext(); ) {
            String etalonId = it.next();
            ClusterRecordPO newRecord = newRecords.remove(etalonId);
            if (newRecord != null) {
                it.remove();
            }
        }
    }

    private void insertRecords(@Nonnull Collection<ClusterRecordPO> records) {
        SqlParameterSource[] batchArgs = records.stream()
                .map(record -> new MapSqlParameterSource()
                        .addValue("etalonId", record.getEtalonId())
                        .addValue("clusterId", record.getClusterId())
                        .addValue("matchingRate", record.getMatchingRate())
                        .addValue("etalonDate", new Timestamp(record.getEtalonDate().getTime())))
                .toArray(SqlParameterSource[]::new);
        namedJdbcTemplate.batchUpdate(INSERT_RECORDS, batchArgs);
    }

    private MapSqlParameterSource getClusterMap(@Nonnull ClusterPO cluster) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("storage", cluster.getStorage());
        map.addValue("entityName", cluster.getEntityName());
        map.addValue("groupId", cluster.getGroupId());
        map.addValue("ruleId", cluster.getRuleId());
        map.addValue("hash", cluster.getClusterHash());
        map.addValue("version", cluster.getVersion());
        map.addValue("date", new Timestamp(cluster.getMatchingDate().getTime()));
        return map;
    }

    @Nonnull
    @Override
    public Collection<Long> getClusterIds(@Nonnull ClusterQuery clusterQuery) {
        String query = getQuery(SELECT_IDS, clusterQuery, this::getClusterQueryBuilder);
        return namedJdbcTemplate.queryForList(query, clusterQuery.getMap(), Long.class);
    }

    @Nonnull
    @Override
    public Collection<ClusterPO> getClusters(@Nonnull ClusterQuery clusterQuery) {
        String query = getQuery(SELECT, clusterQuery, this::getClusterQueryBuilder);
        return namedJdbcTemplate.query(query, clusterQuery.getMap(), RECORD_CLUSTER_EXTRACTOR);
    }

    @Nonnull
    @Override
    public Long getCount(@Nonnull ClusterQuery clusterQuery) {
        String query = getQuery(SELECT_COUNT, clusterQuery, this::getClusterQueryBuilder);
        return namedJdbcTemplate.queryForObject(query, new MapSqlParameterSource(clusterQuery.getMap()) {
            @Override
            public Object getValue(String paramName) {
                Object val = super.getValue(paramName);
                if ("etalonIds".equals(paramName) && val instanceof Collection) {
                    return ((Collection<?>) val).stream().filter(Objects::nonNull)
                        .map(Object::toString)
                        .map(UUID::fromString)
                        .collect(Collectors.toList());
                }

                return val;
            }

        }, Long.class);
    }

    @Override
    public void removeClusters(@Nonnull ClusterQuery clusterQuery) {
        String query = getQuery(DELETE_CLUSTERS, clusterQuery, this::getClusterQueryBuilder);
        namedJdbcTemplate.update(query, clusterQuery.getMap());
    }

    private String getQuery(@Nonnull String baseQuery, @Nonnull ClusterQuery clusterQuery,
            @Nonnull Function<ClusterQuery.Conditions, String> queryBuilder) {
        String conditions = clusterQuery.getConditions()
                                        .stream()
                                        .sequential()
                                        .map(queryBuilder)
                                        .collect(Collectors.joining(" "));
        return String.format(baseQuery, conditions);
    }

    private String getClusterQueryBuilder(ClusterQuery.Conditions condition) {
        switch (condition) {
            case CLUSTER_ID:
                return "and cl.id=:clusterId";
            case ENTITY:
                return "and cl.entity_name=:entityName and cl.storage_fkey=:storage";
            case RULE:
                return "and cl.rule_id=:ruleId";
            case GROUP:
                return "and cl.group_id=:groupId";
            case GROUP_NULL:
                return "and cl.group_id IS NULL";
            case HASH:
                return "and cl.cluster_identifier=:hash";
            case LIMIT:
                return "LIMIT :limit";
            case OFFSET:
                return "OFFSET :offset";
            case ORDER:
                return "order by :order";
            case ETALON_ID:
                return "and cl.id in (select cluster_id from matched_records where etalon_id in (:etalonIds))";
            case CORRUPTED:
                return "and (select count(cluster_id) from matched_records where cl.id=matched_records.cluster_id)<2";
            default:
                return "";
        }
    }

    private String getBlockListQueryBuilder(ClusterQuery.Conditions condition) {
        switch (condition) {
            case ENTITY:
                return "and bmr.entity_name=:entityName and bmr.storage_fkey=:storage";
            case RULE:
                return "and bmr.rule_id=:ruleId";
            case GROUP:
                return "and bmr.group_id=:groupId";
            case GROUP_NULL:
                return "and bmr.group_id IS NULL";
            case HASH:
                return "and bmr.cluster_identifier=:hash";
            case LIMIT:
                return "LIMIT :limit";
            case OFFSET:
                return "OFFSET :offset";
            case ORDER:
                return "order by :order";
            case ETALON_ID:
                return "and (bmr.blocked_etalon_id in (:etalonIds) or bmr.blocked_for_etalon_id in (:etalonIds))";
            default:
                return "";
        }
    }

    @Override
    public int removeRecordsFromClusters(@Nonnull Collection<String> etalonIds) {

        if (etalonIds.isEmpty()) {
            return 0;
        }

        Map<String, ?> params = Collections.singletonMap("ids", etalonIds);
        // Only one parameter "ids" is expected.
        return namedJdbcTemplate.update(DELETE_RECORDS_FROM_CLUSTER_BY_ETALONS, new MapSqlParameterSource(params) {
            @Override
            public Object getValue(String paramName) {
                Object val = super.getValue(paramName);
                if ("ids".equals(paramName)) {
                    return etalonIds.stream()
                            .map(UUID::fromString)
                            .collect(Collectors.toList());
                }

                return val;
            }
        });
    }

    @Override
    public void removeRecordsFromCluster(@Nonnull Collection<String> etalonIds, @Nonnull Long clusterId) {
        if (etalonIds.isEmpty()) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ids", etalonIds);
        map.put("id", clusterId);
        namedJdbcTemplate.update(DELETE_RECORDS_FROM_CLUSTER_BY_CLUSTER, new MapSqlParameterSource(map) {
            @Override
            public Object getValue(String paramName) {
                Object val = super.getValue(paramName);
                if ("ids".equals(paramName) && val instanceof Collection) {
                    return ((Collection<?>) val).stream().filter(Objects::nonNull)
                            .map(Object::toString)
                            .map(UUID::fromString)
                            .collect(Collectors.toList());
                }

                return val;
            }
        });
    }

    @Override
    public Long getUniqueRecordsCount(@Nonnull ClusterQuery clusterQuery) {
        String query = getQuery(SELECT_RECORDS_COUNT, clusterQuery, this::getClusterQueryBuilder);
        return namedJdbcTemplate.queryForObject(query, clusterQuery.getMap(), Long.class);
    }

    @Override
    public void addToBlockList(@Nonnull final Multimap<String, String> etalonIds, @Nonnull final ClusterPO cluster) {
        SqlParameterSource[] bulk = etalonIds.entries().stream()
                .map(pair -> getClusterMap(cluster)
                        .addValue("blockedId", pair.getKey())
                        .addValue("blockedForId", pair.getValue()))
                .toArray(SqlParameterSource[]::new);
        namedJdbcTemplate.batchUpdate(INSERT_INTO_BLOCK_LIST, bulk);
    }

    @Override
    public Multimap<String, String> getBlockedPairs(@Nonnull ClusterQuery clusterQuery) {
        String query = getQuery(SELECT_BLOCKED_RECORDS, clusterQuery, this::getBlockListQueryBuilder);
        return namedJdbcTemplate.query(query, clusterQuery.getMap(), BLOCKED_RECORDS_EXTRACTOR);
    }

    @Override
    public void removeFromBlockList(@Nonnull ClusterQuery clusterQuery, @Nonnull Collection<ClusterMetaData> clusterMetas) {
        String query = getQuery(DELETE_BLOCKED_RECORDS, clusterQuery, this::getBlockListQueryBuilder);
        Map<String, Object> map = clusterQuery.getMap();
        int i = 0;
        StringBuilder additionQuery = new StringBuilder();
        for (ClusterMetaData meta : clusterMetas) {
            if (i != 0) {
                additionQuery.append(" or ");
            }
            map.put("groupId" + i, meta.getGroupId());
            map.put("ruleId" + i, meta.getRuleId());
            map.put("hash" + i, null);
            additionQuery.append("(")
                    .append("bmr.rule_id=:ruleId").append(i)
                    .append(" and ")
                    .append("bmr.group_id=:groupId").append(i)
                    .append(" and ")
                    .append("bmr.cluster_identifier<>:hash").append(i)
                    .append(")");
            i++;
        }
        if (i != 0) {
            query += " and (";
            additionQuery.append(")");
            query += additionQuery.toString();
        }

        namedJdbcTemplate.update(query, new MapSqlParameterSource(map) {
            @Override
            public Object getValue(String paramName) {
                Object val = super.getValue(paramName);
                if ("etalonIds".equals(paramName) && val instanceof Collection) {
                    return ((Collection<?>) val).stream().filter(Objects::nonNull)
                        .map(Object::toString)
                        .map(UUID::fromString)
                        .collect(Collectors.toList());
                }

                return val;
            }

        });
    }

    @Override
    public void removeFromBlockList(@Nonnull ClusterQuery clusterQuery) {
        String query = getQuery(DELETE_BLOCKED_RECORDS, clusterQuery, this::getBlockListQueryBuilder);
        namedJdbcTemplate.update(query, new MapSqlParameterSource(clusterQuery.getMap()) {
            @Override
            public Object getValue(String paramName) {
                Object val = super.getValue(paramName);
                if ("etalonIds".equals(paramName) && val instanceof Collection) {
                    return ((Collection<?>) val).stream().filter(Objects::nonNull)
                        .map(Object::toString)
                        .map(UUID::fromString)
                        .collect(Collectors.toList());
                }

                return val;
            }

        });
    }
}

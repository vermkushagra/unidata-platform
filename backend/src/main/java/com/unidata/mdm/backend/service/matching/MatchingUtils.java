package com.unidata.mdm.backend.service.matching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.common.matching.ClusterSet;

/**
 * @author Mikhail Mikhailov
 * Some matching utilities.
 */
public class MatchingUtils {
    /**
     * Constructor.
     */
    private MatchingUtils() {
        super();
    }
    /**
     * Returns cluster id string.
     * @param metadata cluster metadata
     * @return string
     */
    public static String clusterIdString(@Nonnull ClusterMetaData metadata) {
        return new StringBuilder()
                .append(metadata.getStorage())
                .append("_")
                .append(metadata.getGroupId())
                .append("_")
                .append(metadata.getRuleId())
                .toString();
    }
    /**
     * Returns cluster set view, suitable for indexing.
     * @param set the set
     * @return view
     */
    public static Map<Integer, Pair<Collection<Integer>, Collection<Object>>> toIndexable(@Nonnull ClusterSet set) {

        Map<Integer, Pair<Collection<Integer>, Collection<Object>>> result = new HashMap<>();
        for (Cluster cluster : set.getClusters()) {

            ClusterMetaData md = cluster.getMetaData();
            Pair<Collection<Integer>, Collection<Object>> ruleValues = result.get(md.getGroupId());
            if (Objects.isNull(ruleValues)) {

                ruleValues = new ImmutablePair<>(new HashSet<>(), new ArrayList<>());
                result.put(md.getGroupId(), ruleValues);
            }

            ruleValues.getKey().add(md.getRuleId());
            ruleValues.getValue().addAll(cluster.getData());
        }

        return result;
    }
}

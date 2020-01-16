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

package org.unidata.mdm.data.convert;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.po.storage.DataClusterPO;
import org.unidata.mdm.data.po.storage.DataNodePO;
import org.unidata.mdm.data.type.storage.DataCluster;
import org.unidata.mdm.data.type.storage.DataNode;
import org.unidata.mdm.data.type.storage.DataShard;
import org.unidata.mdm.data.type.storage.PoolSetting;
import org.unidata.mdm.system.exception.PlatformFailureException;

/**
 * Converts serialized cluster metadata to middle tier form and vice versa.
 *
 * @author maria.chistyakova
 * @since 28.10.2019
 */
public class DataClusterConverter {

    private DataClusterConverter() {
        super();
    }

    public static DataCluster of(DataClusterPO po) {

        if (Objects.isNull(po)) {
            return null;
        }


        DataNode[] nodes = new DataNode[po.getNodes().size()];
        DataShard[] shards = new DataShard[po.getNumberOfShards()];

        // Nodes are zero based, ordered by id
        for (DataNodePO node : po.getNodes()) {

            DataNode converted = of(node);
            if (Objects.isNull(converted)) {
                continue;
            }

            nodes[node.getId()] = converted;
        }

        // Shards are zero based, simply assigned by modulo for now
        for (int i = 0; i < po.getNumberOfShards(); i++) {
            shards[i] = DataShard.builder()
                    .number(i)
                    .primary(nodes[(i + 1) % nodes.length])
                    .build();
        }

        return DataCluster.builder()
                .id(po.getId())
                .nodes(nodes)
                .shards(shards)
                .distributionFactor(po.getDistributionFactor())
                .name(po.getName())
                .hasData(po.hasData())
                .initialized(po.isInitialized())
                .version(po.getVersion())
                .build();
    }

    public static DataNode of(DataNodePO po) {

        if (Objects.isNull(po)) {
            return null;
        }

        return DataNode.builder()
                .number(po.getId())
                .name(po.getName())
                .host(po.getHost())
                .port(po.getPort())
                .database(po.getDatabase())
                .user(po.getUser())
                .password(po.getPassword())
                .settings(po.getSettings())
                .createDate(po.getCreateDate())
                .updateDate(po.getUpdateDate())
                .build();
    }

    public static DataClusterPO of(Integer shardNumber, String[] nodeIds, String unidataNodeId) {

        if (ArrayUtils.isEmpty(nodeIds) || shardNumber <= 0) {
            throw new PlatformFailureException("Invalid storage initialization spec.",
                    DataExceptionIds.EX_DATA_STORAGE_INIT_FAILED);
        }

        DataClusterPO po = new DataClusterPO();
        List<DataNodePO> nodes = new ArrayList<>();
        for (String spec : nodeIds) {
            nodes.add(of(spec, unidataNodeId));
        }

        po.setName("Default Unidata storage cluster.");
        po.setNodes(nodes);
        po.setNumberOfShards(shardNumber);
        po.setDistributionFactor(shardNumber / nodes.size());
        po.setHasData(false);
        po.setInitialized(false);

        return po;
    }

    // This all is temporary stuff
    // Will be done via metadata
    public static DataNodePO of(String spec, String nodeId) {

        String[] parts = StringUtils.split(spec, ':');
        if (parts == null || parts.length < 4) {
            throw new PlatformFailureException("Node line supplied in wrong format '{}'.",
                    DataExceptionIds.EX_DATA_STORAGE_INIT_NODE_FORMAT, spec);
        }

        // 1. Node number
        int number = Integer.parseInt(parts[0]);

        // 2. Node name
        String name = parts[1];
        if (!StringUtils.contains(parts[2], "@")) {
            throw new PlatformFailureException("Supplied Node line contains no credentials '{}'.",
                    DataExceptionIds.EX_DATA_STORAGE_INIT_NODE_NO_CREDENTIALS, spec);
        }

        // 3. DB user.
        String user = StringUtils.substringBefore(parts[2], "@");
        // 4. DB password
        String password = StringUtils.substringAfter(parts[2], "@");
        // 5. DB password
        String database = "unidata";
        // 6. DB host
        String host = null;
        if (StringUtils.contains(parts[3], "@")) {
            database = StringUtils.substringBefore(parts[3], "@");
            host = StringUtils.substringAfter(parts[3], "@");
        } else {
            host = parts[3];
        }

        // 7. DB Port
        int port = 5432;
        if (parts.length > 4) {
            port = Integer.parseInt(parts[4]);
        }

        Map<PoolSetting, String> settings = new EnumMap<>(PoolSetting.class);
        for (PoolSetting s : PoolSetting.values()) {
            settings.put(s, s.getDefaultValue());
        }

        settings.put(PoolSetting.POOL_UNIQUE_NAME, "UDST-" + nodeId + "-" + number);

        DataNodePO po = new DataNodePO();
        po.setId(number);
        po.setName(name);
        po.setHost(host);
        po.setPort(port);
        po.setDatabase(database);
        po.setUser(user);
        po.setPassword(password);
        po.setSettings(settings);

        return po;
    }
}

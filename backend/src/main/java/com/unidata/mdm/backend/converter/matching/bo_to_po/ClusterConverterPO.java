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

package com.unidata.mdm.backend.converter.matching.bo_to_po;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.matching.ClusterRecord;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.po.matching.ClusterPO;
import com.unidata.mdm.backend.po.matching.ClusterRecordPO;

@ConverterQualifier
@Component
public class ClusterConverterPO implements Converter<Cluster, ClusterPO> {

    @Autowired
    private Converter<ClusterRecord, ClusterRecordPO> converter;

    @Override
    public ClusterPO convert(Cluster source) {
        ClusterPO clusterPO = new ClusterPO();
        clusterPO.setClusterId(source.getClusterId());
        clusterPO.setClusterOwnerRecord(source.getClusterOwnerRecord());
        clusterPO.setEntityName(source.getMetaData().getEntityName());
        clusterPO.setRuleId(source.getMetaData().getRuleId());
        clusterPO.setMatchingDate(source.getMatchingDate());
        clusterPO.setStorage(source.getMetaData().getStorage());
        Map<String, ClusterRecordPO> records = source.getClusterRecords()
                                                     .stream()
                                                     .map(converter::convert)
                                                     .collect(Collectors.toMap(ClusterRecordPO::getEtalonId, r -> r));
        clusterPO.setClusterRecordPOs(records);
        if (clusterPO.getClusterId() != null) {
            clusterPO.getClusterRecordPOs().values().forEach(re -> re.setClusterId(clusterPO.getClusterId()));
        }
        return clusterPO;
    }

}

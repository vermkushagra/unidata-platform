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

package com.unidata.mdm.backend.converter.matching.po_to_bo;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.common.matching.ClusterRecord;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.po.matching.ClusterPO;
import com.unidata.mdm.backend.po.matching.ClusterRecordPO;

@ConverterQualifier
@Component
public class ClusterConverter implements Converter<ClusterPO, Cluster> {

    @Autowired
    private Converter<ClusterRecordPO, ClusterRecord> converter;

    @Override
    public Cluster convert(ClusterPO source) {
        Cluster cluster = new Cluster(source.getMatchingDate());
        cluster.setClusterId(source.getClusterId());
        cluster.setClusterOwnerRecord(source.getClusterOwnerRecord());
        Set<ClusterRecord> records = source.getClusterRecordPOs()
                                           .values()
                                           .stream()
                                           .map(converter::convert)
                                           .collect(Collectors.toSet());
        cluster.setClusterRecords(records);
        ClusterMetaData clusterMetaData = ClusterMetaData.builder()
                .entityName(source.getEntityName())
                .ruleId(source.getRuleId())
                .build();
        cluster.setMetaData(clusterMetaData);
        return cluster;
    }

}

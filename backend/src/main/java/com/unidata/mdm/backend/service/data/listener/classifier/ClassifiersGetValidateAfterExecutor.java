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

package com.unidata.mdm.backend.service.data.listener.classifier;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.GetClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.dto.GetClassifierDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.EtalonClassifier;
import com.unidata.mdm.backend.service.classifier.ClsfService;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;

public class ClassifiersGetValidateAfterExecutor implements DataRecordAfterExecutor<GetClassifiersDataRequestContext> {

    @Autowired
    private ClsfService clsfService;

    @Override
    public boolean execute(GetClassifiersDataRequestContext ctx) {

        final Map<String, List<GetClassifierDTO>> data = ctx.getFromStorage(StorageId.CLASSIFIERS_DATA);
        if (MapUtils.isEmpty(data)) {
            return true;
        }

        data.values().forEach(l ->
                l.forEach(clsf -> {

                    final EtalonClassifier etalon = clsf.getEtalon();
                    final ClsfNodeDTO node = clsfService.getNodeWithAttrs(
                            etalon.getInfoSection().getNodeId(),
                            etalon.getInfoSection().getClassifierName(),
                            true);

                    final Set<String> attrs = Objects.isNull(node) || CollectionUtils.isEmpty(node.getAllNodeAttrs())
                            ? Collections.emptySet()
                            : node.getAllNodeAttrs().stream().map(ClsfNodeAttrDTO::getAttrName).collect(Collectors.toSet());

                    etalon.getSimpleAttributes().stream()
                            .map(Attribute::getName)
                            .filter(a -> !attrs.contains(a))
                            .forEach(etalon::removeAttribute);
                })
        );

        return true;
    }
}

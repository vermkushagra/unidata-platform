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

package com.unidata.mdm.backend.api.rest.converter;

import com.unidata.mdm.backend.api.rest.dto.data.FullRecordRO;
import com.unidata.mdm.backend.api.rest.dto.data.RelationDeleteWrapperRO;
import com.unidata.mdm.backend.common.ConvertUtils;
import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import org.apache.commons.lang3.StringUtils;


/**
 * @author Dmitry Kopin on 20.06.2018.
 */
public class FullRecordConverter {
    public static DeleteRelationRequestContext convert(RelationDeleteWrapperRO deleteWrapper, FullRecordRO fullRecordRO) {
        return DeleteRelationRequestContext.builder()
                .relationName(deleteWrapper.getRelName())
                .entityName(fullRecordRO.getDataRecord().getEntityName())
                .relationEtalonKey(deleteWrapper.getEtalonRelationId())
                .inactivateEtalon(StringUtils.isNoneBlank(deleteWrapper.getEtalonRelationId()))
                .relationOriginKey(deleteWrapper.getOriginRelationId())
                .inactivateOrigin(StringUtils.isNoneBlank(deleteWrapper.getOriginRelationId()))
                .inactivatePeriod(deleteWrapper.getValidFrom() != null && deleteWrapper.getValidTo() != null)
                .validFrom(ConvertUtils.localDateTime2Date(deleteWrapper.getValidFrom()))
                .validTo(ConvertUtils.localDateTime2Date(deleteWrapper.getValidTo()))
                .wipe(deleteWrapper.isWipe())
                .build();
    }
}

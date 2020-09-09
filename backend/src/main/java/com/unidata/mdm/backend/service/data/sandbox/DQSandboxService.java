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

package com.unidata.mdm.backend.service.data.sandbox;

import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.context.RunDQRulesContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.EtalonRecord;

public interface DQSandboxService {
    EtalonRecord upsert(EtalonRecord etalonRecord);

    EtalonRecord findRecordById(long recordId);

    void deleteRecords(List<Long> recordsIds, String entityName);

    SearchResultDTO searchRecords(SearchRequestContext searchRequestContext);

    Map<EtalonRecord, List<DataQualityError>> runDataQualityRules(RunDQRulesContext runDQRulesContext);
}

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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.MapUtils;
import org.unidata.mdm.core.util.PeriodIdUtils;
import org.unidata.mdm.data.type.data.EtalonRecord;
import org.unidata.mdm.data.type.data.EtalonRecordInfoSection;
import org.unidata.mdm.meta.type.search.EntityIndexType;
import org.unidata.mdm.meta.type.search.RecordIndexId;
import org.unidata.mdm.search.type.indexing.Indexing;
import org.unidata.mdm.search.type.indexing.IndexingField;

/**
 * @author Mikhail Mikhailov on Oct 10, 2019
 */
public final class RecordIndexingConverter extends AbstractIndexingConverter {
    /**
     * Constructor.
     */
    private RecordIndexingConverter() {
        super();
    }

    public static List<Indexing> convert(Map<EtalonRecord, Collection<IndexingField>> records) {

        if (MapUtils.isEmpty(records)) {
            return Collections.emptyList();
        }

        List<Indexing> result = new ArrayList<>(records.size());
        for (Entry<EtalonRecord, Collection<IndexingField>> e : records.entrySet()) {

            EtalonRecord etalonRecord = e.getKey();
            EtalonRecordInfoSection infoSection = etalonRecord.getInfoSection();

            List<IndexingField> fields = new ArrayList<>(e.getValue().size() + etalonRecord.getSize());

            // 1. Header
            fields.addAll(e.getValue());

            // 2. Data
            fields.addAll(AbstractIndexingConverter.buildRecord(etalonRecord));

            // 3. Add
            result.add(new Indexing(EntityIndexType.RECORD,
                    RecordIndexId.of(
                            infoSection.getEntityName(),
                            infoSection.getEtalonKey().getId(),
                            PeriodIdUtils.ensureDateValue(infoSection.getValidTo())))
                        .withFields(fields));
        }

        return result;
    }
}

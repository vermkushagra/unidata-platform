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

package com.unidata.mdm.backend.dao;

import java.util.List;

import com.unidata.mdm.backend.po.SandboxRecordPO;

public interface DQSandboxDao {
    long save(SandboxRecordPO sandboxRecord);

    SandboxRecordPO findRecordById(long recordId);

    void deleteByIds(List<Long> recordsIds);

    void deleteByEntityName(String entityName);

    List<SandboxRecordPO> find(String entity, int page, int count);

    List<SandboxRecordPO> findByRecordsIds(List<Long> collect);

    long count(final String entityName);
}

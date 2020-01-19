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

package org.unidata.mdm.data.dao;

import java.util.UUID;

import org.unidata.mdm.core.type.keys.ExternalId;
import org.unidata.mdm.data.dao.impl.BaseStorageDAOImpl.DataNodeEntry;
import org.unidata.mdm.system.dao.BaseDao;

/**
 * @author Mikhail Mikhailov
 * Data storage DAO base class.
 */
public interface BaseStorageDAO extends BaseDao {
    /**
     * Selects connection entry by node number directly.
     * @param node the node number
     * @return connection entry
     */
    DataNodeEntry nodeSelect(int node);
    /**
     * Selects connection entry by shard number.
     * @param shard the shard number
     * @return connection entry
     */
    DataNodeEntry shardSelect(int shard);
    /**
     * Selects default connection entry.
     * @return connection entry
     */
    DataNodeEntry defaultSelect();
    /**
     * Selects connection entry by etalon id.
     * @param uuid the record (record/clsf/rel) UUID id
     * @return connection entry
     */
    DataNodeEntry keySelect(String uuid);
    /**
     * Selects connection entry by etalon id.
     * @param uuid the record (record/clsf/rel) UUID id
     * @return connection entry
     */
    DataNodeEntry keySelect(UUID uuid);
    /**
     * Selects connection entry by record external id.
     * @param id ext id
     * @param name entity name
     * @param system source system
     * @return connection entry
     */
    DataNodeEntry keySelect(String id, String name, String system);
    /**
     * Selects connection entry by record external id.
     * @param id ext id
     * @return connection entry
     */
    DataNodeEntry keySelect(ExternalId id);
}

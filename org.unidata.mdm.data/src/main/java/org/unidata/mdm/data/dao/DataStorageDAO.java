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

import java.util.List;

import org.unidata.mdm.data.po.storage.DataClusterPO;
import org.unidata.mdm.data.po.storage.DataNodePO;

/**
 * @author Mikhail Mikhailov
 *
 */
public interface DataStorageDAO extends BaseStorageDAO {
    /**
     * Loads cluster metadata.
     * @return cluster info
     */
    DataClusterPO load();
    /**
     * Loads cluster metadata and initializes data cluster support from it.
     * @return processed cluster info
     */
    DataClusterPO loadAndInit();
    /**
     * Stops and cleans the underlaying DSs.
     */
    void shutdown();
    /**
     * Saves cluster info.
     * Current implementation resets/closes underlaying data sources.
     * Thus, this method <b>MUST NOT</b> be called often (in fact, it should be called once at cluster initialization).
     * This is subject to change after a full blown configurator is ready.
     * @param info cluster metadata
     */
    void save(DataClusterPO info);
    /**
     * Saves a collection of nodes.
     * @param nodes the nodes to save
     */
    void save(List<DataNodePO> nodes);
}

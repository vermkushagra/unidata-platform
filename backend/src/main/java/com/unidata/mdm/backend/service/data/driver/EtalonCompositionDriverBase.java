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

package com.unidata.mdm.backend.service.data.driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.data.CalculableHolder;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;

/**
 * @author Mikhail Mikhailov
 * Base ECD class.
 */
public abstract class EtalonCompositionDriverBase<T> implements EtalonCompositionDriver<T> {
    /**
     * Meta model service.
     */
    @Autowired
    protected MetaModelServiceExt metaModelService;
    /**
     * Constructor.
     */
    public EtalonCompositionDriverBase() {
        super();
    }

    /**
     * Collects versions by their source systems.
     * @param versions the versions
     * @return map
     */
    protected Map<String, List<CalculableHolder<T>>>
        collectVersionsBySourceSystem(List<CalculableHolder<T>> versions) {

        Map<String, List<CalculableHolder<T>>> versionsBySourceSystem = new HashMap<>();
        for (int i = 0; i < versions.size(); i++) {

            CalculableHolder<T> current = versions.get(i);
            if (!versionsBySourceSystem.containsKey(current.getSourceSystem())) {
                versionsBySourceSystem.put(current.getSourceSystem(), new ArrayList<>());
            }

            versionsBySourceSystem.get(current.getSourceSystem()).add(current);
        }

        return versionsBySourceSystem;
    }

    /**
     * Filters versions by status, last update date and source system, returning versions map.
     * @param versions the versions to filter
     * @return filtered versions map
     */
    protected Map<String, CalculableHolder<T>>
        filterVersionsBySourceSystem(List<CalculableHolder<T>> versions) {

        Map<String, CalculableHolder<T>> versionsBySourceSystem = new HashMap<>();

        // 1. Collect origins by source system, filtering out old and inactive versions
        for (int i = 0; i < versions.size(); i++) {

            CalculableHolder<T> current = versions.get(i);
            String sourceSystem = current.getSourceSystem();

            CalculableHolder<T> other = versionsBySourceSystem.get(sourceSystem);
            // 1.1 Check if the source system contains a version already
            if (other != null) {

                // 1.1.1 Replace, if the current LUD is after the previous one
                boolean replace = current.getLastUpdate().after(other.getLastUpdate());
                if (replace) {
                    versionsBySourceSystem.put(sourceSystem, current);
                }
            } else {
                versionsBySourceSystem.put(sourceSystem, current);
            }
        }

        return versionsBySourceSystem;
    }

    /**
     * Gets ordered BVR map for the supplied type name.
     * @return map
     */
    protected Map<String, Integer> getOrderedBVRMap() {
        return metaModelService.getReversedSourceSystems();
    }

    /**
     * Compose default BVR.
     * @param versions the versions
     * @param includeInactive include incative or not
     * @return selected version
     */
    protected T composeDefaultBVR(List<CalculableHolder<T>> versions, boolean includeInactive) {

        // 1. Get versions by source systems
        Map<String, CalculableHolder<T>> versionsMap = filterVersionsBySourceSystem(versions);

        // 2. Add support for different source systems of the same weight. The younger one will be selected.
        Map<Integer, List<CalculableHolder<T>>> mergeSet = new LinkedHashMap<>(versions.size());
        if (!versionsMap.isEmpty()) {
            Map<String, Integer> sortedSourceSystems = getOrderedBVRMap();
            for (Entry<String, Integer> entry : sortedSourceSystems.entrySet()) {

                // 2.1 Skip merged
                CalculableHolder<T> c = versionsMap.get(entry.getKey());
                if (c == null || c.getStatus() == RecordStatus.MERGED) {
                    continue;
                }

                // 2.2 Put to merge set
                if (!mergeSet.containsKey(entry.getValue())) {
                    mergeSet.put(entry.getValue(), new ArrayList<>());
                }

                mergeSet.get(entry.getValue()).add(c);
            }
        }

        // 3. Get result
        return selectVersionFromMergeSet(mergeSet, includeInactive);
    }

    /**
     *
     * @param mergeSet
     * @param IncludeInactive
     * @return
     */
    protected T selectVersionFromMergeSet(Map<Integer, List<CalculableHolder<T>>> mergeSet, boolean includeInactive) {

        T result  = null;
        for (Entry<Integer, List<CalculableHolder<T>>> entry : mergeSet.entrySet()) {

            CalculableHolder<T> c = getLatest(entry.getValue());
            if (c.getStatus() == RecordStatus.INACTIVE) {
                result = includeInactive ? c.getValue() : null;
                break;
            }

            result = c.getValue();
            break;
        }

        return result;
    }

    /**
     * Gets the youngest record available.
     * @param list the list
     * @return record
     */
    protected CalculableHolder<T> getLatest(List<CalculableHolder<T>> list) {

        CalculableHolder<T> result = null;
        for (CalculableHolder<T> c : list) {
            if (result != null) {
                // Ugly stuff, but helps to overcome wrong Date <-> Timestamp method covariant selection
                result = c.getLastUpdate().getTime() > result.getLastUpdate().getTime() ? c : result;
            } else {
                result = c;
            }
        }

        return result;
    }
}

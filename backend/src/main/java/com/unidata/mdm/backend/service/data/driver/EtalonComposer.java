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

import static java.util.stream.Collectors.toMap;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.data.CalculableHolder;
import com.unidata.mdm.backend.common.data.CalculableType;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.service.measurement.MeasuredAttributeValueConverter;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.wrappers.BVTMapWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;

/**
 * TODO The interface is crap! Refactor it!
 * @author Mikhail Mikhailov Composer entry point.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class EtalonComposer {
    /**
     * Driver singletons map.
     */
    private Map<CalculableType, EtalonCompositionDriver> drivers;

    /**
     * Meta model service
     */
    @Autowired
    private MetaModelServiceExt modelService;

    @Autowired
    private MeasuredAttributeValueConverter measuredAttributeConverter;

    /**
     * Constructor.
     */

    public EtalonComposer(Map<CalculableType, EtalonCompositionDriver> drivers) {
        super();
        this.drivers = drivers;
    }

    /**
     * Tells whether the given contributors set denotes an active interval
     * (validity range).
     *
     * @param driverType
     *            the driver type
     * @param contributors
     *            contributors set
     * @return true, if active, false otherwise
     */

    public <T> boolean hasActive(EtalonCompositionDriverType driverType, List<CalculableHolder<T>> contributors) {

        MeasurementPoint.start();
        try {

            if (contributors == null || contributors.isEmpty()) {
                return false;
            }

            CalculableType calculableType = contributors.get(0).getCalculableType();
            EtalonCompositionDriver<T> driver = drivers.get(calculableType);

            if (driver != null) {
                switch (driverType) {
                case BVR:
                    return driver.hasActiveBVR(contributors);
                case BVT:
                    return driver.hasActiveBVT(contributors);
                default:
                    break;
                }
            }

            return false;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Composes an etalon from a list of calculable version holders.
     *
     * @param driverType
     *            the driver type to use
     * @param versions
     *            the version list to use
     * @param includeInactive
     *            include inactive versions into calculation or not
     * @return new etalon or null
     */
    public <T> T compose(EtalonCompositionDriverType driverType, List<CalculableHolder<T>> versions,
            boolean includeInactive, boolean includeWinners) {

        MeasurementPoint.start();
        try {
            if (versions == null || versions.isEmpty()) {
                return null;
            }

            CalculableType calculableType = versions.get(0).getCalculableType();
            EtalonCompositionDriver<T> driver = drivers.get(calculableType);

            if (driver == null) {
                return null;
            }

            T result = null;

            switch (driverType) {
                case BVR:
                    result = driver.composeBVR(versions, includeInactive, includeWinners);
                    break;
                case BVT:
                    result = driver.composeBVT(versions, includeInactive, includeWinners);
                    break;
            }

            if (calculableType == CalculableType.RECORD && result != null) {
                measuredAttributeConverter.enrichMeasuredAttributesByBase((DataRecord) result);
            }

            return result;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * @param versions
     *            - mapped origins which include
     * @return map where key is attribute name and value is etalon id
     */
    public Map<String, String> getAttributeWinnersMap(Map<String, List<CalculableHolder<OriginRecord>>> versions,
            String entityName) {

        BVTMapWrapper bvtMapWrapper = modelService.isEntity(entityName)
                ? modelService.getValueById(entityName, EntityWrapper.class)
                : modelService.getValueById(entityName, LookupEntityWrapper.class);

        Map<String, ComparableAttributeWrapper> attributesMap = new HashMap<>();
        for (Map.Entry<String, List<CalculableHolder<OriginRecord>>> etalonVersions : versions.entrySet()) {

            String etalonId = etalonVersions.getKey();
            List<CalculableHolder<OriginRecord>> origins = etalonVersions.getValue();
            for (CalculableHolder<OriginRecord> origin : origins) {
                Date lud = origin.getLastUpdate();
                for (Attribute simpleAttribute : origin.getValue().getAllAttributes()) {

                    String attrName = simpleAttribute.getName();
                    Map<String, Integer> sourceSystem = bvtMapWrapper.getBvtMap().get(attrName);
                    if (sourceSystem == null) {
                        continue;
                    }

                    Integer weight = sourceSystem.get(origin.getSourceSystem());
                    ComparableAttributeWrapper comparableAttributeWrapper = new ComparableAttributeWrapper(weight, lud,
                            etalonId, simpleAttribute);
                     ComparableAttributeWrapper winner = determineWinner(attributesMap.get(attrName),
                            comparableAttributeWrapper);
                    attributesMap.put(attrName, winner);
                }
                /*
                for (SimpleAttribute simpleAttribute : origin.getValue().getSimpleAttributes()) {

                    String attrName = simpleAttribute.getName();
                    Map<String, Integer> sourceSystem = bvtMapWrapper.getBvtMap().get(attrName);
                    if (sourceSystem == null) {
                        continue;
                    }

                    Integer weight = sourceSystem.get(origin.getSourceSystem());
                    ComparableAttributeWrapper comparableAttributeWrapper = new ComparableAttributeWrapper(weight, lud,
                            etalonId, simpleAttribute);
                    ComparableAttributeWrapper winner = determineWinner(attributesMap.get(attrName),
                            comparableAttributeWrapper);
                    attributesMap.put(attrName, winner);
                }

                for (ComplexAttribute complexAttribute : origin.getValue().getComplexAttributes()) {

                    String attrName = complexAttribute.getName();
                    Map<String, Integer> sourceSystem = bvtMapWrapper.getBvtMap().get(attrName);
                    if (sourceSystem == null) {
                        continue;
                    }

                    Integer weight = sourceSystem.get(origin.getSourceSystem());
                    ComparableAttributeWrapper comparableAttributeWrapper = new ComparableAttributeWrapper(weight, lud,
                            etalonId, complexAttribute);
                    attributesMap.put(attrName,
                            determineWinner(attributesMap.get(attrName), comparableAttributeWrapper));
                }
                */
            }
        }

        return attributesMap.values().stream()
                .collect(toMap(wrapper -> wrapper.abstractAttribute.getName(), wrapper -> wrapper.etalonId));
    }

    private ComparableAttributeWrapper determineWinner(ComparableAttributeWrapper prev,
            ComparableAttributeWrapper current) {
        if (Objects.isNull(prev) || current.compareTo(prev) > 0) {
            return current;
        } else {
            return prev;
        }
    }

    private class ComparableAttributeWrapper implements Comparable<ComparableAttributeWrapper> {
        int weight;
        Date date;
        String etalonId;
        Attribute abstractAttribute;

        ComparableAttributeWrapper(int weight, Date date, String etalonId, Attribute abstractAttribute) {
            this.weight = weight;
            this.date = date;
            this.etalonId = etalonId;
            this.abstractAttribute = abstractAttribute;
        }

        @Override
        public int compareTo(ComparableAttributeWrapper o) {
            int result = Integer.compare(this.weight, o.weight);
            if (result == 0) {
                if (Objects.isNull(o.date)) {
                    return 1;
                }
                if (Objects.isNull(this.date)) {
                    return -1;
                }
                return this.date.compareTo(o.date);
            }
            return result;
        }
    }
}

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

package com.unidata.mdm.backend.service.measurement;

import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_MEASUREMENT_BASE_IS_NOT_DEFINE;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_MEASUREMENT_CONVERSION_FUNCTION_INCORRECT;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_MEASUREMENT_FUNCTION_SHOULD_BE_STANDARD;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_MEASUREMENT_ID_INCORRECT_FOR_PATTERN;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_MEASUREMENT_MERGE_IMPOSSIBLE_UNIT_WAS_CHANGED;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_MEASUREMENT_MERGE_IMPOSSIBLE_UNIT_WAS_REMOVED;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_MEASUREMENT_SOMEONE_ALREADY_REMOVE_VALUE;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_MEASUREMENT_UNIT_FUNCTION_IS_NOT_DEFINE;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_MEASUREMENT_UNIT_ID_IS_NOT_DEFINE;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_MEASUREMENT_UNIT_NAME_IS_NOT_DEFINE;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_MEASUREMENT_UNIT_SHORT_NAME_IS_NOT_DEFINE;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_MEASUREMENT_UNIT_VALUE_ID_IS_NOT_DEFINE;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_MEASUREMENT_VALUE_ID_IS_NOT_DEFINE;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_MEASUREMENT_VALUE_NAME_IS_NOT_DEFINE;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_MEASUREMENT_VALUE_SHORT_NAME_IS_NOT_DEFINE;
import static com.unidata.mdm.backend.service.measurement.MeasurementConverter.convert;
import static java.util.Collections.emptySet;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.tuple.Pair.of;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unidata.mdm.backend.service.data.relations.RelationsServiceComponent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.dao.MeasurementDao;
import com.unidata.mdm.backend.po.measurement.MeasurementValuePO;
import com.unidata.mdm.backend.service.measurement.data.MeasurementUnit;
import com.unidata.mdm.backend.service.measurement.data.MeasurementValue;
import com.unidata.mdm.backend.service.registration.RegistrationService;
import com.unidata.mdm.backend.service.registration.keys.MeasurementValueRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.UniqueRegistryKey;

/**
 * Measurement service, responsible for managing measurement values
 */
@Service
public class MeasurementServiceImpl implements MetaMeasurementService {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MeasurementServiceImpl.class);
    /**
     * Measurement dao
     */
    @Autowired
    private MeasurementDao measurementDao;

    /**
     * Registration service
     */
    @Autowired
    private RegistrationService registrationService;

    /**
     * Measurement conversion service
     */
    @Autowired
    private MeasurementConversionService measurementConversionService;

    /**
     * Values cache
     */
    private IMap<String, MeasurementValue> cachedValues;

    @Nonnull
    @Override
    public Collection<MeasurementValue> getAllValues() {
        return cachedValues.values();
    }

    @Nullable
    @Override
    public MeasurementValue getValueById(@Nonnull String valueId) {
        return cachedValues.get(valueId);
    }

    @Nullable
    @Override
    public MeasurementUnit getUnitById(@Nonnull String valueId, @Nonnull String unitId) {
        MeasurementValue value = getValueById(valueId);
        return value == null ? null : value.getUnitById(unitId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveValue(@Nonnull MeasurementValue measurementValue) {
        validateValue(measurementValue);
        String valueId = measurementValue.getId();
        MeasurementValue prevValue = getValueById(valueId);
        if (prevValue != null) {
            update(prevValue, measurementValue);
        } else {
            save(measurementValue);
        }
    }

    private void update(@Nonnull MeasurementValue prevValue, @Nonnull MeasurementValue measurementValue) {
        measurementDao.update(convert(measurementValue));
        Collection<MeasurementUnit> newUnits = new ArrayList<>(measurementValue.getMeasurementUnits());
        if(!StringUtils.equals(prevValue.getShortName(), measurementValue.getShortName())) {
            throw new BusinessException("Unable to merge different measurement units.",
            		ExceptionId.EX_MEASUREMENT_MERGE_IMPOSSIBLE_DIFFERENT_UNITS, measurementValue.getShortName(), prevValue.getShortName(), measurementValue.getId() );
        }
        for (MeasurementUnit prevUnit : prevValue.getMeasurementUnits()) {
            MeasurementUnit newUnit = measurementValue.getUnitById(prevUnit.getId());

            if (newUnit == null) {
                throw new BusinessException("Units can't be removed", EX_MEASUREMENT_MERGE_IMPOSSIBLE_UNIT_WAS_REMOVED,
                        prevUnit.getName());
            }
            boolean functionChanged = !newUnit.getConvectionFunction().equals(prevUnit.getConvectionFunction());
            boolean baseChanged = newUnit.isBase() != prevUnit.isBase();
            if (functionChanged || baseChanged) {
                throw new BusinessException("Conversion function was changed or base unit",
                        EX_MEASUREMENT_MERGE_IMPOSSIBLE_UNIT_WAS_CHANGED, prevUnit.getName());
            }
            newUnits.remove(newUnit);
        }
        registerConversionFunctions(newUnits);
        cachedValues.put(measurementValue.getId(), measurementValue);
    }

    private void save(MeasurementValue measurementValue) {
        measurementDao.save(convert(measurementValue));
        registerConversionFunctions(measurementValue.getMeasurementUnits());
        //we skip unit keys, because we can remove only whole value with all units.
        registrationService.registry(new MeasurementValueRegistryKey(measurementValue.getId()), emptySet(), emptySet());
        cachedValues.put(measurementValue.getId(), measurementValue);
    }

    private void registerConversionFunctions(@Nonnull Collection<MeasurementUnit> newUnits) {
        for (MeasurementUnit unit : newUnits) {
            try {
                measurementConversionService.registerMeasurementUnit(unit);
            } catch (Exception e) {
                LOGGER.error("Problem with register measurement function", e);
                newUnits.forEach(measurementConversionService::removeMeasurementUnit);
                throw new BusinessException("unit " + unit.getName() + " has incorrect conversion function",
                        EX_MEASUREMENT_CONVERSION_FUNCTION_INCORRECT, unit.getConvectionFunction(), unit.getName());
            }
        }
    }

    private void unregisterConversionFunctions(@Nonnull String valueId) {

        MeasurementValue value = cachedValues.get(valueId);
        if (value != null) {
            for (MeasurementUnit unit : value.getMeasurementUnits()) {
                try {
                    measurementConversionService.removeMeasurementUnit(unit);
                } catch (Exception e) {
                    LOGGER.error("Problem with register measurement function", e);
                    value.getMeasurementUnits().forEach(measurementConversionService::registerMeasurementUnit);
                    throw new BusinessException("unit " + unit.getName() + " has incorrect conversion function",
                            EX_MEASUREMENT_CONVERSION_FUNCTION_INCORRECT, unit.getConvectionFunction(), unit.getName());
                }
            }

        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeValue(@Nonnull String measureValueId) {
        Set<UniqueRegistryKey> refToKeys = registrationService.getReferencesTo(new MeasurementValueRegistryKey(measureValueId));
        // check reference
        if (CollectionUtils.isNotEmpty(refToKeys)) {
            throw new BusinessException("Delete unavailable", ExceptionId.EX_MEASUREMENT_REMOVING_FORBIDDEN,
                    refToKeys);
        }
        boolean result = measurementDao.removeValues(Collections.singletonList(measureValueId));
        if (!result) {
            throw new BusinessException("value is already removed", EX_MEASUREMENT_BASE_IS_NOT_DEFINE, measureValueId);
        }
        MeasurementValue value = getValueById(measureValueId);
        if (value == null) {
            throw new BusinessException("value is already removed", EX_MEASUREMENT_BASE_IS_NOT_DEFINE, measureValueId);
        }
        registrationService.remove(new MeasurementValueRegistryKey(measureValueId));
        unregisterConversionFunctions(measureValueId);
        cachedValues.remove(measureValueId);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean batchRemove(@Nonnull Collection<String> measureValueIds, boolean skipRefs, boolean override) {
        Collection<Pair<MeasurementValueRegistryKey, Set<UniqueRegistryKey>>> refToKeys = null;
        refToKeys = measureValueIds.stream().map(MeasurementValueRegistryKey::new)
                .map(key -> of(key, registrationService.getReferencesTo(key)))
                .filter(refs -> !refs.getValue().isEmpty()).collect(Collectors.toList());
        // check reference
        if (!refToKeys.isEmpty() && !override) {
            throw new BusinessException("Delete unavailable", ExceptionId.EX_MEASUREMENT_REMOVING_FORBIDDEN,
                    refToKeys);
        }

        boolean removed = measurementDao.removeValues(measureValueIds);
        if (!removed) {
            throw new BusinessException("value is already removed", EX_MEASUREMENT_SOMEONE_ALREADY_REMOVE_VALUE);
        }
        Collection<MeasurementValue> values = measureValueIds.stream()
                .map(this::getValueById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (measureValueIds.size() != values.size()) {
            throw new BusinessException("value is already removed", EX_MEASUREMENT_SOMEONE_ALREADY_REMOVE_VALUE);
        }
        if (!skipRefs) {
            Set<UniqueRegistryKey> keys = measureValueIds.stream()
                    .map(MeasurementValueRegistryKey::new)
                    .collect(Collectors.toSet());
            registrationService.batchRemove(keys);
            values.forEach(val -> unregisterConversionFunctions(val.getId()));
            values.forEach(val -> cachedValues.remove(val.getId()));
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveValues(List<MeasurementValue> values) {
        if (CollectionUtils.isEmpty(values)) {
            return;
        }
        Set<String> valueIds = new HashSet<>();
        for (MeasurementValue value : values) {
            if (valueIds.contains(value.getId())) {
                throw new BusinessException("Base unit defined more than once", ExceptionId.EX_MEASUREMENT_DUPL_ID,
                        value.getId());
            }
            valueIds.add(value.getId());
            saveValue(value);
        }
    }

    @Override
    public void validateValue(MeasurementValue measurementValue) {
        String id = measurementValue.getId();
        if (isBlank(id) || id.length() > BASE_STRING_LENGTH) {
            throw new BusinessException("Value id is not define or too long", EX_MEASUREMENT_VALUE_ID_IS_NOT_DEFINE,
                    measurementValue.getId());
        }
        if (!ID_PATTERN.matcher(id).matches()) {
            throw new BusinessException("Value id is incorrect, because contains not permitted symbols",
                    EX_MEASUREMENT_ID_INCORRECT_FOR_PATTERN, measurementValue.getId());
        }
        String name = measurementValue.getName();
        if (isBlank(name) || name.length() > BASE_STRING_LENGTH) {
            throw new BusinessException("Value name is not define or too long", EX_MEASUREMENT_VALUE_NAME_IS_NOT_DEFINE,
                    measurementValue.getName());
        }
        String shortName = measurementValue.getShortName();
        if (isBlank(shortName) || shortName.length() > SHORT_STRING_LENGTH) {
            throw new BusinessException("Value short name is not define or too long", EX_MEASUREMENT_VALUE_SHORT_NAME_IS_NOT_DEFINE,
                    measurementValue.getShortName());
        }
        measurementValue.getMeasurementUnits().forEach(this::validateUnit);

        long count = measurementValue.getMeasurementUnits().stream().filter(MeasurementUnit::isBase).count();
        if (count == 0) {
            throw new BusinessException("Base unit not defined", ExceptionId.EX_MEASUREMENT_BASE_IS_NOT_DEFINE,
                    measurementValue.getId());
        }
        if (count > 1) {
            throw new BusinessException("Base unit defined more than once", ExceptionId.EX_MEASUREMENT_BASE_UNIT_DUPL,
                    measurementValue.getId());
        }

        int idsCount = measurementValue.getMeasurementUnits()
                .stream()
                .map(MeasurementUnit::getId)
                .collect(Collectors.toSet())
                .size();
        int unitsCount = measurementValue.getMeasurementUnits().size();
        if (idsCount != unitsCount) {
            throw new BusinessException("Some ids are duplicated", EX_MEASUREMENT_VALUE_SHORT_NAME_IS_NOT_DEFINE);
        }
        String baseConversionFunction = measurementValue.getBaseUnit().getConvectionFunction();
        if (!BASE_CONVERSION.equals(baseConversionFunction)) {
            throw new BusinessException("Base conversion function is incorrect",
                    EX_MEASUREMENT_FUNCTION_SHOULD_BE_STANDARD, baseConversionFunction, BASE_CONVERSION);
        }
    }

    private void validateUnit(MeasurementUnit measurementUnit) {
        String id = measurementUnit.getId();
        if (isBlank(id) || id.length() > BASE_STRING_LENGTH) {
            throw new BusinessException("Unit id is not define or too long", EX_MEASUREMENT_UNIT_ID_IS_NOT_DEFINE,
                    measurementUnit.toString());
        }
        if (!ID_PATTERN.matcher(id).matches()) {
            throw new BusinessException("Unit id is incorrect, because contains not permitted symbols",
                    EX_MEASUREMENT_ID_INCORRECT_FOR_PATTERN, measurementUnit.getId());
        }
        String name = measurementUnit.getName();
        if (isBlank(name) || name.length() > LONG_STRING_LENGTH) {
            throw new BusinessException("Unit name is not define or too long", EX_MEASUREMENT_UNIT_NAME_IS_NOT_DEFINE,
                    measurementUnit.toString());
        }
        String shortName = measurementUnit.getShortName();
        if (isBlank(shortName) || shortName.length() > SHORT_STRING_LENGTH) {
            throw new BusinessException("Unit short name is not define or too long", EX_MEASUREMENT_UNIT_SHORT_NAME_IS_NOT_DEFINE,
                    measurementUnit.toString());
        }
        String function = measurementUnit.getConvectionFunction();
        if (isBlank(function) || function.length() > MAX_STRING_LENGTH) {
            throw new BusinessException("Unit function is not define or too long", EX_MEASUREMENT_UNIT_FUNCTION_IS_NOT_DEFINE,
                    measurementUnit.toString());
        }
        if (isBlank(measurementUnit.getValueId())) {
            throw new BusinessException("Value id is not define in unit", EX_MEASUREMENT_UNIT_VALUE_ID_IS_NOT_DEFINE,
                    measurementUnit.toString());
        }
    }

    private void addToCache(MeasurementValue value) {
        boolean exist = cachedValues.containsKey(value.getId());
        if (!exist) {
            registerConversionFunctions(value.getMeasurementUnits());
            cachedValues.put(value.getId(), value);
        }
    }

    @Autowired
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        cachedValues = hazelcastInstance.getMap("values");
    }

    @Override
    public void afterContextRefresh() {
        if (cachedValues != null && cachedValues.size() != 0) {
            cachedValues.values().forEach(measurementValue -> unregisterConversionFunctions(measurementValue.getId()));
            cachedValues.evictAll();
        }
        Map<String, MeasurementValuePO> valuesMap = measurementDao.getAllValues();
        Collection<MeasurementValue> values = valuesMap.values()
                .stream()
                .map(MeasurementConverter::convert)
                .collect(Collectors.toList());
        values.forEach(val -> registrationService.registry(new MeasurementValueRegistryKey(val.getId()), emptySet(),
                emptySet()));
        values.forEach(this::addToCache);
    }
}

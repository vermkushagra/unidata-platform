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

package org.unidata.mdm.data.service.impl;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.unidata.mdm.core.type.calculables.Calculable;
import org.unidata.mdm.core.type.calculables.CalculableHolder;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.model.EntityModelElement;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.system.exception.PlatformFailureException;

/**
 * @author Mikhail Mikhailov
 * Basic composition stuff.
 */
public abstract class AbstractComposer {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractComposer.class);
    /**
     * MMS instance.
     */
    @Autowired
    protected MetaModelService metaModelService;
    /**
     * Constructor.
     */
    protected AbstractComposer() {
        super();
    }
    /**
     * Checks versions list for containing a pending version.
     * @param versions the list to check
     * @return true, if contains, false otherwise
     */
    public <T extends Calculable> boolean isPending(List<CalculableHolder<T>> versions) {
        return CollectionUtils.isNotEmpty(versions) && versions.stream().anyMatch(ch -> ch.getApproval() == ApprovalState.PENDING);
    }
    /**
     * Selects BVT map.
     * @param versions the caluclables
     */
    protected <X extends Calculable> EntityModelElement ensureBvtMapElement(List<CalculableHolder<X>> versions) {

        EntityModelElement element = metaModelService.getEntityModelElementById(versions.get(0).getTypeName());
        if (Objects.isNull(element)) {
            final String message = "Meta model type element with id '{}' not found for BVT calculation.";
            LOGGER.warn(message, versions.get(0).getTypeName());
            throw new PlatformFailureException(message,
                    DataExceptionIds.EX_DATA_NO_ENTITY_ELEMENT_FOR_BVT_CALCULATION,
                    versions.get(0).getTypeName());
        }

        if (!element.isBvtCapable()) {
            final String message = "Meta model type element with id '{}' is not BVT capable.";
            LOGGER.warn(message, versions.get(0).getTypeName());
            throw new PlatformFailureException(message,
                    DataExceptionIds.EX_DATA_ENTITY_ELEMENT_NOT_BVT_CAPABLE,
                    versions.get(0).getTypeName());
        }

        return element;
    }
}

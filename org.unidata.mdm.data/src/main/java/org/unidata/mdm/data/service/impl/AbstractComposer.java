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

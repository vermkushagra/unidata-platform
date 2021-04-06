package com.unidata.mdm.backend.service.registration.handlers;

import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.service.registration.keys.UniqueRegistryKey;

public abstract class ForbidAbstractDeleteHandler<D extends UniqueRegistryKey, L extends UniqueRegistryKey> implements DeleteHandler<D, L> {
    @Override
    public void onDelete(D removingKey, L linkingKey) {
        //temporary disabled
//        throw new BusinessException("Element can't be removed", ExceptionId.EX_SYSTEM_REMOVING_FORBID_HAS_LINKS,
//                (getRemovedEntityType().getDescription() + ":" + removingKey.toString()),
//                (getLinkedEntityType().getDescription()) + ":" + linkingKey.toString());
    }
}

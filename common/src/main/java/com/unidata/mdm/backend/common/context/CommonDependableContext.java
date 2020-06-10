package com.unidata.mdm.backend.common.context;

import java.util.ArrayList;
import java.util.List;

public abstract class CommonDependableContext extends CommonSendableContext implements RecordIdentityContext {

    @SuppressWarnings("unchecked")
    public CommonDependableContext(final CommonDependableContext parentContext) {
        if (parentContext != null) {
            final List<CommonSendableContext> contextsList = parentContext.getFromStorage(StorageId.DEPENDED_CONTEXTS);
            if (contextsList == null) {
                parentContext.putToStorage(StorageId.DEPENDED_CONTEXTS, new ArrayList<>());
            }
            ((List<CommonSendableContext>) parentContext.getFromStorage(StorageId.DEPENDED_CONTEXTS)).add(this);
        }
    }
}

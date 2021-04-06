package com.unidata.mdm.backend.service.bulk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.unidata.mdm.backend.common.bulk.BulkOperationConfiguration;
import com.unidata.mdm.backend.common.types.BulkOperationType;
import org.apache.commons.collections4.CollectionUtils;

public class RemoveRelationsFromConfiguration extends BulkOperationConfiguration {

    private final List<String> relationsNames = new ArrayList<>();

    public RemoveRelationsFromConfiguration(final Collection<String> relationsNames) {
        super(BulkOperationType.REMOVE_RELATIONS_FROM);
        if (CollectionUtils.isNotEmpty(relationsNames)) {
            this.relationsNames.addAll(relationsNames);
        }
    }

    public List<String> getRelationsNames() {
        return Collections.unmodifiableList(relationsNames);
    }
}

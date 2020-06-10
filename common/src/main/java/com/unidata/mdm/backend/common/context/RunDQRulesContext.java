package com.unidata.mdm.backend.common.context;

import java.util.List;

public class RunDQRulesContext {
    final List<String> etalonRecordsIds;
    final String entityName;
    final boolean sandbox;
    final List<String> rules;

    public RunDQRulesContext(
            final List<String> etalonRecordsIds,
            final String entityName,
            final boolean sandbox,
            final List<String> rules
    ) {
        this.etalonRecordsIds = etalonRecordsIds;
        this.entityName = entityName;
        this.sandbox = sandbox;
        this.rules = rules;
    }

    public List<String> getEtalonRecordsIds() {
        return etalonRecordsIds;
    }

    public String getEntityName() {
        return entityName;
    }

    public boolean isSandbox() {
        return sandbox;
    }

    public List<String> getRules() {
        return rules;
    }
}

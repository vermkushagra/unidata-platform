package com.unidata.mdm.backend.service.security.impl;

import java.util.Map.Entry;

import com.hazelcast.core.ReadOnly;
import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;
import com.unidata.mdm.backend.common.security.SecurityToken;

/**
 * @author Mikhail Mikhailov
 * Bogus entry processor, dedicated to timestamp renewal after near cache reads.
 */
public class TokenTimestampRefresher implements EntryProcessor<String, SecurityToken>, ReadOnly {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 4779553343762697067L;
    /**
     * Bogus backup processor, to keep backups in sync with master partition.
     */
    private final EntryBackupProcessor<String, SecurityToken> backupProcessor =
            new EntryBackupProcessor<String, SecurityToken>() {
                /**
                 * SVUID.
                 */
                private static final long serialVersionUID = 1081818211998591708L;
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void processBackup(Entry<String, SecurityToken> entry) {
                    // NOP
                }
            };
    /**
     * {@inheritDoc}
     */
    @Override
    public Object process(Entry<String, SecurityToken> entry) {
        // We're done with this
        return entry.getKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntryBackupProcessor<String, SecurityToken> getBackupProcessor() {
        return backupProcessor;
    }
}

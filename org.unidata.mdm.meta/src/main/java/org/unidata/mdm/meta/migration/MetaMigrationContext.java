package org.unidata.mdm.meta.migration;

import nl.myndocs.database.migrator.processor.MigrationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author maria.chistyakova
 * @since 14.10.2019
 */
@Component
public class MetaMigrationContext extends MigrationContext {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * @return the applicationContext
     */
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

}

package org.unidata.mdm.system.migration;

import nl.myndocs.database.migrator.processor.MigrationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author maria.chistyakova
 * @since 14.10.2019
 */
@Component
public class SpringContextAwareMigrationContext extends MigrationContext {

        @Autowired
        private final ApplicationContext applicationContext;
        /**
         * Constructor.
         * @param applicationContext current context
         */
        SpringContextAwareMigrationContext(ApplicationContext applicationContext) {
            super();
            this.applicationContext = applicationContext;
        }
        /**
         * @return the applicationContext
         */
        public ApplicationContext getApplicationContext() {
            return applicationContext;
        }

}

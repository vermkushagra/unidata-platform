package org.unidata.mdm.core.module;

import static org.unidata.mdm.system.exception.SystemExceptionIds.EX_MODULE_CANNOT_BE_INSTALLED;
import static org.unidata.mdm.system.exception.SystemExceptionIds.EX_MODULE_CANNOT_BE_UNINSTALLED;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.unidata.mdm.core.configuration.CoreConfiguration;
import org.unidata.mdm.core.configuration.CoreConfigurationProperty;
import org.unidata.mdm.core.exception.CoreExceptionIds;
import org.unidata.mdm.core.migrations.CoreSchemaMigrations;
import org.unidata.mdm.core.migrations.UninstallCoreSchemaMigrations;
import org.unidata.mdm.core.type.search.AuditHeaderField;
import org.unidata.mdm.core.type.search.AuditIndexType;
import org.unidata.mdm.core.util.CoreServiceUtils;
import org.unidata.mdm.core.util.JsonUtils;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.search.context.MappingRequestContext;
import org.unidata.mdm.search.service.SearchService;
import org.unidata.mdm.search.type.mapping.Mapping;
import org.unidata.mdm.search.type.mapping.impl.BooleanMappingField;
import org.unidata.mdm.search.type.mapping.impl.StringMappingField;
import org.unidata.mdm.system.exception.PlatformFailureException;
import org.unidata.mdm.system.migration.SpringContextAwareMigrationContext;
import org.unidata.mdm.system.type.configuration.ApplicationConfigurationProperty;
import org.unidata.mdm.system.type.module.Dependency;
import org.unidata.mdm.system.type.module.Module;
import org.unidata.mdm.system.util.DataSourceUtils;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;

import nl.myndocs.database.migrator.database.Selector;
import nl.myndocs.database.migrator.database.query.Database;
import nl.myndocs.database.migrator.processor.Migrator;

public class CoreModule implements Module {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreModule.class);
    /**
     * This module id.
     */
    public static final String MODULE_ID = "org.unidata.mdm.core";

    private static final List<Dependency> DEPENDENCIES = Arrays.asList(
            new Dependency("org.unidata.mdm.system", "5.2"),
            new Dependency("org.unidata.mdm.search", "5.2")
    );


    // todo move to StorageUtils.DATA_STORAGE_SCHEMA_NAME
    private String mdmCoreSecuritySchemaName = "org_unidata_mdm_core";

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Autowired
    private SearchService searchService;

    @Autowired
    private DataSource coreDataSource;

    @Autowired
    private CoreConfiguration coreConfiguration;

    /**
     * Lock name.
     */
    private static final String CREATE_INDEX_LOCK_NAME = "createAuditIndexLock";

    @Override
    public String getId() {
        return MODULE_ID;
    }

    @Override
    public String getVersion() {
        return "5.2";
    }

    @Override
    public String getName() {
        return "Unidata core";
    }

    @Override
    public String getDescription() {
        return "Unidata core module";
    }

    @Override
    public Collection<Dependency> getDependencies() {
        return DEPENDENCIES;
    }

    @Override
    public ApplicationConfigurationProperty[] configurationProperties() {
        return CoreConfigurationProperty.values();
    }

    private Migrator migrator;

    /**
     * The mapping.
     */
    private static final Mapping AUDIT_INDEX_MAPPING = new Mapping(AuditIndexType.AUDIT)
            .withFields(
                    new StringMappingField(AuditHeaderField.ACTION.getName())
                            .withNonAnalyzable(true)
                            .withIndexType(AuditIndexType.AUDIT),
                    new StringMappingField(AuditHeaderField.CLIENT_IP.getName())
                            .withNonAnalyzable(true)
                            .withIndexType(AuditIndexType.AUDIT),
                    new StringMappingField(AuditHeaderField.DATE.getName())
                            .withIndexType(AuditIndexType.AUDIT),
                    new StringMappingField(AuditHeaderField.DETAILS.getName())
                            .withIndexType(AuditIndexType.AUDIT),
                    new StringMappingField(AuditHeaderField.ENDPOINT.getName())
                            .withNonAnalyzable(true)
                            .withIndexType(AuditIndexType.AUDIT),
                    new StringMappingField(AuditHeaderField.ENTITY.getName())
                            .withNonAnalyzable(true)
                            .withIndexType(AuditIndexType.AUDIT),
                    new StringMappingField(AuditHeaderField.ETALON_ID.getName())
                            .withNonAnalyzable(true)
                            .withIndexType(AuditIndexType.AUDIT),
                    new StringMappingField(AuditHeaderField.EXTERNAL_ID.getName())
                            .withNonAnalyzable(true)
                            .withIndexType(AuditIndexType.AUDIT),
                    new StringMappingField(AuditHeaderField.OPERATION_ID.getName())
                            .withNonAnalyzable(true)
                            .withIndexType(AuditIndexType.AUDIT),
                    new StringMappingField(AuditHeaderField.ORIGIN_ID.getName())
                            .withNonAnalyzable(true)
                            .withIndexType(AuditIndexType.AUDIT),
                    new StringMappingField(AuditHeaderField.SERVER_IP.getName())
                            .withNonAnalyzable(true)
                            .withIndexType(AuditIndexType.AUDIT),
                    new StringMappingField(AuditHeaderField.SOURCE_SYSTEM.getName())
                            .withNonAnalyzable(true)
                            .withIndexType(AuditIndexType.AUDIT),
                    new StringMappingField(AuditHeaderField.SUB_SYSTEM.getName())
                            .withNonAnalyzable(true)
                            .withIndexType(AuditIndexType.AUDIT),
                    new BooleanMappingField(AuditHeaderField.SUCCESS.getName())
                            .withIndexType(AuditIndexType.AUDIT),
                    new StringMappingField(AuditHeaderField.TASK_ID.getName())
                            .withNonAnalyzable(true)
                            .withIndexType(AuditIndexType.AUDIT),
                    new StringMappingField(AuditHeaderField.USER.getName())
                            .withNonAnalyzable(true)
                            .withIndexType(AuditIndexType.AUDIT)
            );


    @Override
    public void install() {
        LOGGER.info("Install");

        try {
            getMigrator().migrate(
                    coreConfiguration.getBeanByClass(SpringContextAwareMigrationContext.class),
                    CoreSchemaMigrations.migrations()
            );
        } catch (SQLException e) {
            throw new PlatformFailureException(
                    "cannot install core module",
                    e,
                    EX_MODULE_CANNOT_BE_INSTALLED
            );
        }
    }


    @Override
    public void uninstall() {
        LOGGER.info("Uninstall");
        try {
            getMigrator().migrate(coreConfiguration.getBeanByClass(SpringContextAwareMigrationContext.class), UninstallCoreSchemaMigrations.migrations());
        } catch (SQLException e) {
            throw new PlatformFailureException(
                    "cannot uninstall core module",
                    e,
                    EX_MODULE_CANNOT_BE_UNINSTALLED
            );
        }
    }

    @Override
    public void start() {
        LOGGER.info("Starting...");

        SecurityUtils.init();
        CoreServiceUtils.init();
        JsonUtils.init();

        // ???
        // * TODO: Temporary! Move the content to Audit and kill this service after Audit cleanup.
        final ILock createIndexLock = hazelcastInstance.getLock(CREATE_INDEX_LOCK_NAME);
        try {
            if (createIndexLock.tryLock(1, TimeUnit.SECONDS)) {
                try {

                    MappingRequestContext mCtx = MappingRequestContext.builder()
                            .entity(AuditIndexType.INDEX_NAME)
                            .storageId(SecurityUtils.getCurrentUserStorageId())
                            .mapping(AUDIT_INDEX_MAPPING)
                            .build();

                    searchService.process(mCtx);
                } finally {
                    createIndexLock.unlock();
                }
            } else {
                final String message = "Cannot aquire audit index create lock.";
                LOGGER.error(message);
                throw new PlatformFailureException(message, CoreExceptionIds.EX_SYSTEM_INDEX_LOCK_TIME_OUT);
            }
        } catch (InterruptedException e) {
            final String message = "Cannot aquire audit index create lock.";
            LOGGER.error(message);
            throw new PlatformFailureException(message, e, CoreExceptionIds.EX_SYSTEM_INDEX_LOCK_TIME_OUT);
        }

        LOGGER.info("Started");
    }

    @Override
    public void stop() {
        LOGGER.info("Stopping...");
        Hazelcast.shutdownAll();
        DataSourceUtils.shutdown(coreDataSource);
        LOGGER.info("Stopped.");
    }


    private Migrator getMigrator() throws SQLException {
        if (migrator != null) {
            return migrator;
        }

        Connection connection = coreDataSource.getConnection();

        Database database = new Selector()
                .loadFromConnection(connection, mdmCoreSecuritySchemaName);

        return new Migrator(database, "change_log");
    }
}

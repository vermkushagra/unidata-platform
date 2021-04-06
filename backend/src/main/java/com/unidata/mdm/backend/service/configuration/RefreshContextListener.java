/**
 *
 */
package com.unidata.mdm.backend.service.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.common.configuration.ConfigurationConstants;
import com.unidata.mdm.backend.common.configuration.PlatformConfiguration;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext.UpdateModelRequestContextBuilder;
import com.unidata.mdm.backend.common.service.CleanseFunctionService;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.common.service.RoleService;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.service.SecurityService;
import com.unidata.mdm.backend.common.service.ServiceUtils;
import com.unidata.mdm.backend.dao.util.DatabaseVendor;
import com.unidata.mdm.backend.db.update.FlywayUpdater;
import com.unidata.mdm.backend.service.classifier.ClsfService;
import com.unidata.mdm.backend.service.data.util.DataRecordUtils;
import com.unidata.mdm.backend.service.data.util.DataUtils;
import com.unidata.mdm.backend.service.matching.MatchingMetaFacadeService;
import com.unidata.mdm.backend.service.matching.data.MatchingUserSettings;
import com.unidata.mdm.backend.service.measurement.MeasurementValueXmlConverter;
import com.unidata.mdm.backend.service.measurement.MetaMeasurementService;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.IdUtils;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.backend.util.ValidityPeriodUtils;
import com.unidata.mdm.classifier.FullClassifierDef;
import com.unidata.mdm.match.MatchingSettingsDef;
import com.unidata.mdm.meta.MeasurementValueDef;
import com.unidata.mdm.meta.MeasurementValues;
import com.unidata.mdm.meta.Model;

/**
 * @author Mikhail Mikhailov
 * Listens for app context events and runs code in orchestrated fashion.
 */
public class RefreshContextListener implements ApplicationListener<ContextRefreshedEvent>, InitializingBean {

    /**
     * Prevent from being called more then once.
     */
    private static final AtomicInteger INIT_ONCE = new AtomicInteger(0);
    /**
     * Prevent from being called more then once.
     */
    private static final AtomicInteger DB_ONCE = new AtomicInteger(0);
    /**
     * Clean DB property.
     */
    @Value("${" + ConfigurationConstants.DB_CLEAN_PROPERTY + ":false}")
    private boolean cleanDb;
    /**
     * Migrate DB property.
     */
    @Value("${" + ConfigurationConstants.DB_MIGRATE_PROPERTY + ":false}")
    private boolean migrateDb;
    /**
     * Internal smoke test property.
     */
    @Value("${" + ConfigurationConstants.SMOKE_STAND_FLAG_PROPERTY + ":false}")
    private boolean isSmokeStand;
    /**
     * Internal smoke model property.
     */
    @Value("${" + ConfigurationConstants.SMOKE_STAND_MODEL_PROPERTY + ":@null}")
    private String modelPath;
    /**
     * Internal smoke classifiers property.
     */
    @Value("${" + ConfigurationConstants.SMOKE_STAND_CLASSIFIERS_PROPERTY + ":@null}")
    private String classifiersPath;
    /**
     * Internal smoke measure units property.
     */
    @Value("${" + ConfigurationConstants.SMOKE_STAND_MEASURE_UNITS_PROPERTY + ":@null}")
    private String measureunitsPath;
    /**
     * Internal smoke match rules property.
     */
    @Value("${" + ConfigurationConstants.SMOKE_STAND_MATCH_RULES_PROPERTY + ":@null}")
    private String matchRulesPath;
    /**
     * MMS instance.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
    /**
     * Classifier service.
     */
    @Autowired
    private ClsfService classifierService;
    /**
     * Measured values service.
     */
    @Autowired
    private MetaMeasurementService metaMeasurementService;
    /**
     * Matching rules facade.
     */
    @Autowired
    private MatchingMetaFacadeService matchingMetaFacadeService;
    /**
     * Matching rules converter.
     */
    @Autowired
    private Converter<MatchingSettingsDef, MatchingUserSettings> matchingRulesConverter;
    /**
     * Logger.
     */
    private static final Logger LOGGER
        = LoggerFactory.getLogger(RefreshContextListener.class);

    /**
     * After context refresh beans.
     */
    private List<String> contextRefreshClassNames = new ArrayList<>();

    /**
     * The data sources to process.
     */
    private DataSource dataSource;

    /**
     * Schema names.
     */
    private List<String> schemaNames;

    /**
     * Constructor.
     */
    public RefreshContextListener() {
        super();
    }

    /**
     * Handles context refresh.
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (INIT_ONCE.incrementAndGet() > 1) {
            LOGGER.info("Subsequent context refresh call for a child context. Skip.");
            return;
        }

        ApplicationContext ctx = event.getApplicationContext();

        // 1. "Known" utility class initializers
        MessageUtils.init(ctx);
        IdUtils.init(ctx);
        SecurityUtils.init(ctx);
        DataUtils.init(ctx);

        // 2. Configured classes
        if (!CollectionUtils.isEmpty(contextRefreshClassNames)) {

            for (String beanName : contextRefreshClassNames) {

                try {
                    Class<?> klass = ctx.getClassLoader().loadClass(beanName);

                    if (!AfterContextRefresh.class.isAssignableFrom(klass)) {
                        LOGGER.warn("Class '{}' is probably not an AfterContextRefresh instance! Skipping.",
                                beanName);
                        continue;
                    }
                    try{
                        AfterContextRefresh bean = (AfterContextRefresh) ctx.getBean(klass);
                        bean.afterContextRefresh();
                    } catch (NoSuchBeanDefinitionException e){
                        LOGGER.info("Singleton of type '{}' was not initialized by the context (null). Skipping.", beanName);
                    }

                    LOGGER.info("Singleton of type '{}' initialized after context refresh.", beanName);
                } catch (Exception exc) {
                    LOGGER.error("After context refresh: failed to initialize '{}' instance.",
                            beanName, exc);
                }
            }
        }

        // 3 Utilities, requiring initialized services from above
        ServiceUtils.init(
                ctx.getBean(PlatformConfiguration.class),
                ctx.getBean(DataRecordsService.class),
                ctx.getBean(MetaModelService.class),
                ctx.getBean(CleanseFunctionService.class),
                ctx.getBean(SearchService.class),
                ctx.getBean(SecurityService.class),
                ctx.getBean(RoleService.class)
        );

        DataRecordUtils.init(ctx);
        ValidityPeriodUtils.init(ctx);

        // 4. Install smoke bits
        if (isSmokeStand) {

            // 4.1 Classifiers
            if (StringUtils.isNoneBlank(classifiersPath)) {
                try {

                    Resource[] models = ctx.getResources(classifiersPath);
                    for (int i = 0; Objects.nonNull(models) && i < models.length; i++) {
                        Resource r = models[i];
                        FullClassifierDef classifier = JaxbUtils.createClassifierFromInputStream(r.getInputStream());
                        if (Objects.isNull(classifier)) {
                            continue;
                        }

                        classifierService.addFullFilledClassifierByIds(classifier);
                    }
                } catch (IOException | JAXBException e) {
                    LOGGER.warn("Cannot load classifiers. Exception caught!", e);
                }
            }

            // 4.2 Measure units
            if (StringUtils.isNoneBlank(measureunitsPath)) {
                try {

                    Resource[] models = ctx.getResources(measureunitsPath);
                    for (int i = 0; Objects.nonNull(models) && i < models.length; i++) {
                        Resource r = models[i];
                        MeasurementValues values = JaxbUtils.createMeasurementValuesFromInputStream(r.getInputStream());
                        if (Objects.isNull(values)) {
                            continue;
                        }

                        for (MeasurementValueDef value : values.getValue()) {
                            metaMeasurementService.saveValue(MeasurementValueXmlConverter.convert(value));
                        }
                    }
                } catch (IOException | JAXBException e) {
                    LOGGER.warn("Cannot load measure units. Exception caught!", e);
                }
            }

            // 4.3 Model
            if (StringUtils.isNoneBlank(modelPath)) {
                try {
                    Resource[] models = ctx.getResources(modelPath);
                    for (int i = 0; Objects.nonNull(models) && i < models.length; i++) {
                        Resource r = models[i];
                        Model model = JaxbUtils.createModelFromInputStream(r.getInputStream());
                        boolean isRecreate = true;

                        UpdateModelRequestContext uMCtx = new UpdateModelRequestContextBuilder()
                                .enumerationsUpdate(model.getEnumerations())
                                .sourceSystemsUpdate(model.getSourceSystems())
                                .nestedEntityUpdate(model.getNestedEntities())
                                .lookupEntityUpdate(model.getLookupEntities())
                                .entitiesGroupsUpdate(model.getEntitiesGroup())
                                .entityUpdate(model.getEntities())
                                .relationsUpdate(model.getRelations())
                                .cleanseFunctionsUpdate(model.getCleanseFunctions() == null ? null : model.getCleanseFunctions().getGroup())
                                .storageId(model.getStorageId())
                                .isForceRecreate(isRecreate ? UpdateModelRequestContext.UpsertType.FULLY_NEW : UpdateModelRequestContext.UpsertType.ADDITION)
                                .build();

                        uMCtx.putToStorage(StorageId.DEFAULT_CLASSIFIERS, model.getDefaultClassifiers());
                        metaModelService.upsertModel(uMCtx);

                    }
                } catch (IOException e) {
                    LOGGER.warn("Cannot load model. Exception caught!", e);
                }
            }

            // 4.4 Match rules
            if (StringUtils.isNoneBlank(matchRulesPath)) {
                try {

                    Resource[] models = ctx.getResources(matchRulesPath);
                    for (int i = 0; Objects.nonNull(models) && i < models.length; i++) {
                        Resource r = models[i];
                        MatchingSettingsDef values = JaxbUtils.createMatchingUserSettingsFromInputStream(r.getInputStream());
                        if (Objects.isNull(values)) {
                            continue;
                        }

                        MatchingUserSettings matchingUserSettings = matchingRulesConverter.convert(values);
                        matchingMetaFacadeService.saveUserSettings(matchingUserSettings);
                    }
                } catch (IOException | JAXBException e) {
                    LOGGER.warn("Cannot load match rules. Exception caught!", e);
                }
            }
        }

        DatabaseVendor.registerSupportedDrivers();
    }

    /**
     * @param afterContextRefreshBeans the afterContextRefreshBeans to set
     */
    public void setContextRefreshClassNames(List<String> afterContextRefreshBeans) {
        this.contextRefreshClassNames = afterContextRefreshBeans;
    }

    /**
     * @param dataSource the dataSources to set
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @param schemaNames the schemaNames to set
     */
    public void setSchemaNames(List<String> schemaNames) {
        this.schemaNames = schemaNames;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if (DB_ONCE.incrementAndGet() > 1) {
            LOGGER.info("Subsequent context create call for a child context. Skip.");
            return;
        }

        // 1. Migration support. Must be first
        if ((migrateDb || cleanDb) && !Objects.isNull(dataSource)) {

            LOGGER.info("Executing DB clean and/or migrate.");
            FlywayUpdater.migrate(dataSource, "classpath:db.migration",
                CollectionUtils.isEmpty(schemaNames)
                    ? new String[]{ "public" }
                    : schemaNames.toArray(new String[schemaNames.size()]),
                cleanDb,
                migrateDb);
        }

    }
}

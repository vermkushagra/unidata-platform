package org.unidata.mdm.data.dao.cluster;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jdbc.pool.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unidata.mdm.data.po.storage.DataNodePO;
import org.unidata.mdm.data.type.storage.DataNode;
import org.unidata.mdm.data.type.storage.PoolSetting;

import bitronix.tm.resource.jdbc.PoolingDataSource;

/**
 * @author Mikhail Mikhailov
 * Just random storage utilities.
 */
public final class ClusterUtils {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterUtils.class);
    /**
     * Apache DS factory.
     */
    private static final DataSourceFactory DATA_SOURCE_FACTORY = new DataSourceFactory();
    /**
     * Constructor.
     */
    private ClusterUtils() {
        super();
    }
    /**
     * Apache.
     * @param node
     * @param schemaName
     * @return
     */
    public static DataSource newPoolingNonXADataSource(DataNode node, String schemaName) throws Exception {

        Map<PoolSetting, String> values = node.getSettings();
        String additionalProperties = values.get(PoolSetting.POOL_DRIVER_PROPERTIES);

        String url = new StringBuilder("jdbc:postgresql://")
                .append(node.getHost())
                .append(":")
                .append(node.getPort())
                .append("/")
                .append(node.getDatabase())
                .append("?currentSchema=")
                .append(schemaName)
                .append("&user=").append(node.getUser())
                .append("&password=").append(node.getPassword())
                .append("&ApplicationName=").append("UnidataStorageDS[").append(node.getNumber()).append("]")
                .append(StringUtils.isNotBlank(additionalProperties) ? additionalProperties : StringUtils.EMPTY)
                .toString();

        Properties properties = new Properties();
        properties.setProperty("url", url);
        properties.setProperty("driverClassName", "org.postgresql.Driver");
        properties.setProperty("password", node.getPassword());
        properties.setProperty("username", node.getUser());

        translateBt2Apache(values, properties);

        return DATA_SOURCE_FACTORY.createDataSource(properties);
    }
    /*
    public static DataSource newPoolingDataSource(Properties properties) throws Exception {
        return DATA_SOURCE_FACTORY.createDataSource(properties, null, false);
    }
    */
    private static void translateBt2Apache(Map<PoolSetting, String> values, Properties properties) {

        /*
         * POOL_MIN_POOL_SIZE("minPoolSize", "10"),
        POOL_MAX_POOL_SIZE("maxPoolSize", "30"),
        POOL_MAX_IDLE_TIME("maxIdleTime", "60"),
        POOL_MIN_LIFE_TIME("maxLifeTime", "0"),
        POOL_AUTOMATIC_ENLISTING_ENABLED("automaticEnlistingEnabled", Boolean.TRUE.toString()),
        POOL_USE_TM_JOIN("useTmJoin", Boolean.TRUE.toString()),
        POOL_ACQUIRE_INCREMENT("acquireIncrement", "1"),
        POOL_ACQUISITION_TIMEOUT("acquisitionTimeout", "30"),
        POOL_ACQUISITION_INTERVAL("acquisitionInterval", "1"),
        POOL_CONNECTION_TEST_QUERY("testQuery", "select 1"),
        POOL_CONNECTION_TEST_TIMEOUT("connectionTestTimeout", "1"),
         */

        properties.setProperty("minIdle", values.get(PoolSetting.POOL_MIN_POOL_SIZE));
        properties.setProperty("maxActive", values.get(PoolSetting.POOL_MAX_POOL_SIZE));
        properties.setProperty("validationQuery", values.get(PoolSetting.POOL_CONNECTION_TEST_QUERY));
    }

    /**
     * Bitronix
     */
    public static DataSource newPoolingXADataSource(DataNodePO node, String schemaName) {

        Map<PoolSetting, String> values = node.getSettings();
        String additionalProperties = values.get(PoolSetting.POOL_DRIVER_PROPERTIES);

        String url = new StringBuilder("jdbc:postgresql://")
                .append(node.getHost())
                .append(":")
                .append(node.getPort())
                .append("/")
                .append(node.getDatabase())
                .append("?currentSchema=")
                .append(schemaName)
                .append("&user=").append(node.getUser())
                .append("&password=").append(node.getPassword())
                .append("&ApplicationName=").append("UnidataStorageDS[").append(node.getId()).append("]")
                .append(StringUtils.isNotBlank(additionalProperties) ? additionalProperties : StringUtils.EMPTY)
                .toString();


        PoolingDataSource ds = new PoolingDataSource();
        for (Entry<PoolSetting, String> setting : values.entrySet()) {

            if (setting.getValue() == null
             || setting.getKey() == PoolSetting.POOL_DRIVER_PROPERTIES) {
                continue;
            }

            try {

                Class<?> targetClazz = PropertyUtils.getPropertyType(ds, setting.getKey().getPropName());
                if (targetClazz != null) {

                    if (targetClazz == String.class) {
                        PropertyUtils.setProperty(ds, setting.getKey().getPropName(), setting.getValue());
                    } else {

                        Object targetValue = ConvertUtils.convert(setting.getValue(), targetClazz);
                        if (targetValue != setting.getValue()) {
                            PropertyUtils.setProperty(ds, setting.getKey().getPropName(), targetValue);
                        }
                    }
                }
            } catch (Exception e) {
                // NOPE
            }
        }

        Properties properties = new Properties();
        properties.setProperty("url", url);

        ds.setDriverProperties(properties);

        LOGGER.info("Initializing storage XA PoolingDataSource with id '{}'.", ds.getUniqueName());

        // Join XA TX manager.
        ds.init();
        return ds;
    }

    public static void shutdown(DataSource ds) {

        if (Objects.isNull(ds)) {
            return;
        }

        try {

            if (ds instanceof PoolingDataSource) {
                ((PoolingDataSource) ds).close();
            } else if (ds instanceof org.apache.tomcat.jdbc.pool.DataSource) {
                ((org.apache.tomcat.jdbc.pool.DataSource) ds).close(true);
            }

        } catch (Exception e) {
            LOGGER.warn("Exception caught, while closing pooling data source!", e);
        }
    }
}

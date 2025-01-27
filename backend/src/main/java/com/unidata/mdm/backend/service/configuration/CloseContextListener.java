package com.unidata.mdm.backend.service.configuration;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.util.CollectionUtils;

/**
 * @author Mikhail Mikhailov
 *
 */
public class CloseContextListener implements ApplicationListener<ContextClosedEvent> {

    /**
     * Prevent from being called more then once.
     */
    private static final AtomicInteger CLOSE_ONCE = new AtomicInteger(0);

    /**
     * Logger.
     */
    private static final Logger LOGGER
        = LoggerFactory.getLogger(CloseContextListener.class);

    /**
     * The data sources to process.
     */
    private List<DataSource> dataSources;

    /**
     * Constructor.
     */
    public CloseContextListener() {
        super();
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {

        if (CLOSE_ONCE.incrementAndGet() > 1) {
            LOGGER.info("Subsequent context close call for a child context. Skip.");
            return;
        }

        for (int i = 0; !CollectionUtils.isEmpty(dataSources) && i < dataSources.size(); i++) {

            DataSource ds = dataSources.get(i);
            if (ds instanceof org.apache.tomcat.jdbc.pool.DataSource) {
                ((org.apache.tomcat.jdbc.pool.DataSource) ds).close();
            } else if (ds instanceof org.postgresql.ds.PGPoolingDataSource) {
                ((org.postgresql.ds.PGPoolingDataSource) ds).close();
            } else if (ds instanceof org.apache.commons.dbcp.BasicDataSource){
                try {
                    ((com.unidata.mdm.backend.jdbc.datasource.HsqldbDataSource) ds).close();

                } catch (SQLException exc){
                    LOGGER.error("Can't close hsqldb data source", exc);
                }
            }
        }
    }

    /**
     * @return the dataSources
     */
    public List<DataSource> getDataSources() {
        return dataSources;
    }

    /**
     * @param dataSources the dataSources to set
     */
    public void setDataSources(List<DataSource> dataSources) {
        this.dataSources = dataSources;
    }
}

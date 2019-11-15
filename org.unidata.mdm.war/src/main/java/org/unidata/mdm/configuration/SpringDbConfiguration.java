package org.unidata.mdm.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class SpringDbConfiguration {

    @Bean
    public DataSource unidataDataSource() {
        JndiDataSourceLookup jndiDataSourceLookup = new JndiDataSourceLookup();
        jndiDataSourceLookup.setResourceRef(true);
        return jndiDataSourceLookup.getDataSource("jdbc/UniDataDataSource");
    }

    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("unidataDataSource") final DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}

package org.unidata.mdm.configuration;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDbConfiguration {
	/*
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
    */
}

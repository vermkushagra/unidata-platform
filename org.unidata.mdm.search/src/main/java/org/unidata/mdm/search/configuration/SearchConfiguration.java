package org.unidata.mdm.search.configuration;

import java.nio.charset.StandardCharsets;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.unidata.mdm.search.util.SearchUtils;

/**
 * @author Mikhail Mikhailov
 * Root spring context link.
 */
@Configuration
public class SearchConfiguration {

    @Bean
    public Client esClient(
            @Value("${unidata.search.cluster.name}") final String searchCluster,
            @Value("${unidata.search.nodes.addresses}") final String searchNodes
    ) {
        return SearchUtils.initializeSearchClient(searchCluster, searchNodes);
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();

        source.setDefaultEncoding(StandardCharsets.UTF_8.name());
        source.addBasenames("classpath:messages");

        return source;
    }
}

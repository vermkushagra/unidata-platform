package org.unidata.mdm.search.configuration;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
}

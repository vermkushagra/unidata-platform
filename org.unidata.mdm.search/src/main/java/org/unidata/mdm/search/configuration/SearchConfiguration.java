/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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

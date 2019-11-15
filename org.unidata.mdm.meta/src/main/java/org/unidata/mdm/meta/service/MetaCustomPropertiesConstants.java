package org.unidata.mdm.meta.service;

/**
 * @author Dmitry Kopin on 18.10.2018.
 */
public class MetaCustomPropertiesConstants {

    private MetaCustomPropertiesConstants() {
        super();
    }

    public static final String SEARCH_SHARDS_NUMBER = "unidata_search_shards_number";

    public static final String SEARCH_REPLICAS_NUMBER = "unidata_search_replicas_number";

    public static final String SKIP_RELATION_VALIDATION = "unidata_skip_relation_validation";

    public static final String CUSTOM_TOKENIZE_PROPERTY = "tokenize_on_chars";
}


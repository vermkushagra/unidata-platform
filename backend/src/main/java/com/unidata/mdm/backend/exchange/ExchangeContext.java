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

/**
 *
 */
package com.unidata.mdm.backend.exchange;

import static com.unidata.mdm.backend.jdbc.DataSourceUtil.initPooledDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;

import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Client;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mchange.v2.c3p0.PooledDataSource;
import com.unidata.mdm.backend.common.context.CommonRequestContext;
import com.unidata.mdm.backend.common.search.SearchRequestType;
import com.unidata.mdm.backend.exchange.def.ExchangeDefinition;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.backend.util.IdUtils;
import com.unidata.mdm.meta.Model;

/**
 * @author Mikhail Mikhailov
 * Configuration.
 */
@SuppressWarnings("serial")
public class ExchangeContext extends CommonRequestContext implements AutoCloseable {
    /**
     * Metadata package.
     */
    public static final String METADATA_CONTEXT_PATH = "com.unidata.mdm.meta";
    /**
     * Initial Args
     */
    private String[] initialArgs;
    /**
     * Classes used for migration.
     */
    private List<String> migrationClasses;
    /**
     * Supplied actions.
     */
    private Action[] actions;
    /**
     * Name of the entity(s).
     */
    private List<String> entityNames;
    /**
     * Storage id.
     */
    private String storageId;
    /**
     * ES cluster name.
     */
    private String searchCuster;
    /**
     * Name of the type on index.
     */
    private String searchType;
    /**
     * Number of shards to create.
     */
    private int numberOfShards = -1;
    /**
     * Number of replicas to create.
     */
    private int numberOfReplicas = -1;
    /**
     * The search value.
     */
    private String searchValue;
    /**
     * Search fields.
     */
    private List<String> searchFields;
    /**
     * List of addresses possibly with ports to try.
     */
    private String searchAddresses;
    /**
     * Elasticsearch client transport instance.
     */
    private Client searchClient;
    /**
     * Force create index.
     */
    private boolean searchForceCreate;
    /**
     * Bulk input data file.
     */
    private String bulkInputFile;
    /**
     * Bulk output data file.
     */
    private String bulkOutputFile;
    /**
     * Model read from file.
     */
    private Model model;
    /**
     * Input format.
     */
    private List<InputFormat> inputFormats;
    /**
     * File name / may be URI later.
     */
    private String importSystem;
    /**
     * Exchange definition (mapping and transformation rules).
     */
    private ExchangeDefinition exchangeDefinition;
    /**
     * Start offset.
     */
    private long startOffset = -1L;
    /**
     * Bulk size.
     */
    private long blockSize = -1L;
    /**
     * Thread pool size.
     */
    private int poolSize = 6;
    /**
     * Unidata DB datasource.
     */
    private DataSource unidataDataSource;
    /**
     * Unidata DB datasource.
     */
    private DataSource landingDataSource;
    /**
     * Update mappings during import or not.
     */
    private ImportMetaMode importMetaMode;
    /**
     * Drop mappings (before create).
     */
    private boolean dropMappings;
    /**
     * Clean types before re-index.
     */
    private boolean cleanTypes;
    /**
     * User requested to print help and exit.
     */
    private boolean helpRequest;
    /**
     *
     */
    private String operationId;
    /**
     * @author Mikhail Mikhailov
     * Recognized actions.
     */
    public static enum Action {
        /**
         * Creates index.
         */
        CREATE_INDEX,
        /**
         * Drops index.
         */
        DROP_INDEX,
        /**
         * Index search by supplied params.
         */
        SEARCH,
        /**
         * Import data.
         */
        IMPORT_DATA,
        /**
         * Import model.
         */
        IMPORT_MODEL,
        /**
         * Model migration
         */
        MIGRATION;

        /**
         * Extracts actions from the parameter value.
         * @param param the value
         * @return actions array or null
         */
        public static Action[] fromString(String param) {
            if (param != null) {
                String[] values = param.split(SearchUtils.COMMA_SEPARATOR);
                if (values.length > 0) {
                    List<Action> actions = new ArrayList<Action>();
                    for (String value : values) {
                        for (Action a : Action.values()) {
                            if (a.name().equalsIgnoreCase(value.trim())) {
                                actions.add(a);
                                break;
                            }
                        }
                    }
                    return actions.size() > 0 ? actions.toArray(new Action[actions.size()]) : null;
                }
            }
            return null;
        }
    }

    /**
     * Supported input formats.
     * @author Mikhail Mikhailov
     */
    public static enum InputFormat {
        CSV,
        DB;

        /**
         * Gets input format from string.
         * @param param format as string
         * @return input format or null
         */
        public static List<InputFormat> fromString(String param) {
            if (param != null) {
                String[] values = param.split(SearchUtils.COMMA_SEPARATOR);
                if (values.length > 0) {
                    List<InputFormat> formats = new ArrayList<InputFormat>();
                    for (String value : values) {
                        for (InputFormat f : InputFormat.values()) {
                            if (f.name().equalsIgnoreCase(value.trim())) {
                                formats.add(f);
                                break;
                            }
                        }
                    }
                    return formats;
                }
            }
            return null;
        }
    }

    /**
     * @author Mikhail Mikhailov
     * Known search configuration params.
     */
    public static enum ConfigurationParams {

        /**
         * Actions, separated  by comma.
         */
        PARAM_ACTION("action", " - The action to perform. May be chained with comma (,)."),
        /**
         * Storage id to use.
         */
        PARAM_STORAGE_ID("storage-id", " - The storage id to use."),
        /**
         * ES index name.
         */
        PARAM_ENTITY("entity", " - Name(s, comma separated) of the entity type(s)."),
        /**
         * ES searchCuster name.
         */
        PARAM_SEARCH_CLUSTER("search-cluster", " - Elasticsearch cluster name (defined in " + SearchUtils.ES_CLUSTER_NAME_SETTING + ")."),
        /**
         * ES searchNode host.
         */
        PARAM_SEARCH_HOST("search-host", " - Elasticsearch host (http://<host>:<port>)"),
        /**
         * ES number of shards.
         */
        PARAM_SEARCH_NUMBER_OF_SHARDS("search-number-of-shards", " - Number of shards to create."),
        /**
         * ES number of replicas.
         */
        PARAM_SEARCH_NUMBER_OF_REPLICAS("search-number-of-replicas", " - Name of replicas to create."),
        /**
         * ES force create index.
         */
        PARAM_SEARCH_FORCE_CREATE("search-force-create", " - Force index drop before create, if already exists."),
        /**
         * Search type param.
         */
        PARAM_SEARCH_TYPE("search-type", " - Search type. One of " + SearchRequestType.values().toString() + "."),
        /**
         * Search fields.
         */
        PARAM_SEARCH_FIELDS("search-fields", " - Search fields in dot notation, divided by |."),
        /**
         * Search value.
         */
        PARAM_SEARCH_VALUE("search-value", " - The value to search."),
        /**
         * XML file with bulk data.
         */
        PARAM_BULK_INPUT_FILE("bulk-input-file", " - File name for bulk input (not used right now)."),
        /**
         * XML file to save bulk data (IMPORT_DATA action).
         */
        PARAM_BULK_OUTPUT_FILE("bulk-output-file", " - File name for bulk output (not used right now)."),
        /**
         * Model XML file.
         */
        PARAM_MODEL("model", " - File name of an xml file, containing model definition."),
        /**
         * Input format (.csv is the default).
         */
        PARAM_INPUT_FORMAT("input-format", " - Import format. One of " + InputFormat.values().toString() + "."),
        /**
         * Input source (file).
         */
        PARAM_IMPORT_SYSTEM("import-system", " - Import system name, such as 1C, SAP etc. Must exist in the model being imported."),
        /**
         * Import description.
         */
        PARAM_EXCHANGE_DEFINITION("exchange-def", " - Path to a .json file with exchange definition."),
        /**
         * UniData DB URL.
         */
        PARAM_DB_URL("db-url", " - JDBC database URL."),
        /**
         * UniData DB URL.
         */
        PARAM_BLOCK_SIZE("block-size", " - Block size (5000 records by default)."),
        /**
         * Start offset.
         */
        PARAM_START_OFFSET("start-offset", " - Start offset (0 by default)."),
        /**
         * Thread pool size.
         */
        PARAM_POOL_SIZE("pool-size", " - Start offset (0 by default)."),
        /**
         * UniData DB URL.
         */
        PARAM_DB_LANDING_URL("db-landing-url", " - JDBC landing database URL."),
        /**
         * Update mappings.
         */
        PARAM_IMPORT_META_MODE("model-import-mode", " - depends on this flag an utility will merge or drop mappings during import meta model. Available UPDATE, RECREATE."
                +"In "+ Action.IMPORT_MODEL + " actions."),
        /**
         * Drop mappings before create.
         */
        PARAM_DROP_MAPPINGS("drop-mappings", " - If set, will drop mappings for "
                + Action.IMPORT_DATA.toString() + " and " + Action.IMPORT_MODEL + " actions."),
        /**
         * Help.
         */
        PARAM_PRINT_HELP("help", " - Prints this help."),
        /**
         * Clean before reindex.
         */
        PARAM_REINDEX_CLEAN_BEFORE("clean-before-reindex", " - Clean content of a type before reindexing."),
        /**
         * Collection of classes which will be used for migrating
         */
        PARAM_MIGRATE_BY_CLASSES("m-classes", "Classes which will be used for migrating, (separated by ':')");

        /**
         * Constructor.
         * @param paramName param name
         * @param help help string
         */
        private ConfigurationParams(String paramName, String help) {
            this.paramName = paramName;
            this.help = help;
        }
        /**
         * Param name.
         */
        private final String paramName;
        /**
         * Help string.
         */
        private final String help;

        /**
         * @return the help
         */
        public String getHelpString() {
            return help;
        }

        /**
         * Gets param name.
         * @return param name
         */
        public String getParamName() {
            return paramName;
        }
    }

    /**
     * Ctor.
     */
    public ExchangeContext() {
        super();
    }

    /**
     * Load from properties.
     * @param properties the properties
     * @return configuration
     */
    public static ExchangeContext getContext(Properties properties)
            throws Exception {
        String[] args = new String[properties.size() * 2];
        int i = 0;
        for (Entry<Object, Object> e : properties.entrySet()) {
            args[i++] = "--" + e.getKey().toString();
            args[i++] = e.getValue().toString();
        }

        return getContext(args);
    }

    /**
     * Gets configuration from command line arguments.
     * @param argv arguments
     * @return configuration
     */
    public static ExchangeContext getContext(String[] argv)
            throws Exception {

        ExchangeContext configuration = new ExchangeContext();
        configuration.initialArgs = argv;
        for (int i = 0; i < argv.length; i++) {
            for (ConfigurationParams param : ConfigurationParams.values()) {
                if (StringUtils.equals(argv[i], "--" + param.getParamName())) {
                    switch (param) {
                        case PARAM_SEARCH_CLUSTER:
                            configuration.searchCuster = i + 1 < argv.length ? argv[++i] : null;
                            break;
                        case PARAM_SEARCH_HOST:
                            configuration.searchAddresses = i + 1 < argv.length ? argv[++i] : null;;
                            break;
                        case PARAM_BULK_INPUT_FILE:
                            configuration.bulkInputFile = i + 1 < argv.length ? argv[++i] : null;
                            break;
                        case PARAM_BULK_OUTPUT_FILE:
                            configuration.bulkOutputFile = i + 1 < argv.length ? argv[++i] : null;
                            break;
                        case PARAM_ACTION:
                            configuration.actions = Action.fromString(i + 1 < argv.length ? argv[++i] : null);
                            break;
                        case PARAM_STORAGE_ID:
                            configuration.storageId = i + 1 < argv.length ? argv[++i] : null;
                            break;
                        case PARAM_ENTITY:
                            List<String> entities = new ArrayList<>();
                            String entitiesString = i + 1 < argv.length ? argv[++i] : null;
                            if (entitiesString != null) {
                                String[] tokens = entitiesString.split(SearchUtils.COMMA_SEPARATOR);
                                Collections.addAll(entities, tokens);
                            }
                            configuration.entityNames = entities;
                            break;
                        case PARAM_SEARCH_TYPE:
                            configuration.searchType = i + 1 < argv.length ? argv[++i] : null;
                            break;
                        case PARAM_SEARCH_FIELDS:
                            List<String> fields = new ArrayList<>();
                            String fieldsString = i + 1 < argv.length ? argv[++i] : null;
                            if (fieldsString != null) {
                                String[] tokens = fieldsString.split(SearchUtils.PIPE_SEPARATOR);
                                Collections.addAll(fields, tokens);
                            }
                            configuration.searchFields = fields;
                            break;
                        case PARAM_SEARCH_VALUE:
                            configuration.searchValue = i + 1 < argv.length ? argv[++i] : null;
                            break;
                        case PARAM_SEARCH_FORCE_CREATE:
                            configuration.searchForceCreate = i + 1 < argv.length && Boolean.parseBoolean(argv[++i].trim());
                            break;
                        case PARAM_INPUT_FORMAT:
                            configuration.inputFormats = InputFormat.fromString(i + 1 < argv.length ? argv[++i] : null);
                            break;
                        case PARAM_IMPORT_SYSTEM:
                            configuration.importSystem = i + 1 < argv.length ? argv[++i] : null;
                            break;
                        case PARAM_MODEL:
                            configuration.model = readModelFromFile(i + 1 < argv.length ? argv[++i] : null);
                            break;
                        case PARAM_EXCHANGE_DEFINITION:
                            configuration.exchangeDefinition = readExchangeDefinitionFromFile(i + 1 < argv.length ? argv[++i] : null);
                            break;
                        case PARAM_DB_URL:
                            configuration.unidataDataSource = initPooledDataSource(i + 1 < argv.length ? argv[++i] : null);
                            break;
                        case PARAM_DB_LANDING_URL:
                            String landingJdbcUrl = i + 1 < argv.length ? argv[++i] : null;
                            configuration.landingDataSource = landingJdbcUrl == null ? null : initPooledDataSource(landingJdbcUrl);
                            break;
                        case PARAM_IMPORT_META_MODE:
                            configuration.importMetaMode = i + 1 < argv.length ? ImportMetaMode.valueOf(argv[++i].trim()) : ImportMetaMode.RECREATE;
                            break;
                        case PARAM_DROP_MAPPINGS:
                            configuration.dropMappings = i + 1 < argv.length ? Boolean.valueOf(argv[++i]) : Boolean.FALSE;
                            break;
                        case PARAM_BLOCK_SIZE:
                            configuration.blockSize = i + 1 < argv.length ? Long.valueOf(argv[++i]) : -1;
                            break;
                        case PARAM_START_OFFSET:
                            configuration.startOffset = i + 1 < argv.length ? Long.valueOf(argv[++i]) : -1;
                            break;
                        case PARAM_POOL_SIZE:
                            configuration.poolSize = i + 1 < argv.length ? Integer.valueOf(argv[++i]) : 6;
                            break;
                        case PARAM_PRINT_HELP:
                            configuration.helpRequest = true;
                            return configuration;
                        case PARAM_REINDEX_CLEAN_BEFORE:
                            configuration.cleanTypes = i + 1 < argv.length ? Boolean.valueOf(argv[++i]) : Boolean.FALSE;
                            break;
                        case PARAM_SEARCH_NUMBER_OF_REPLICAS:
                            configuration.numberOfReplicas = i + 1 < argv.length ? Integer.valueOf(argv[++i]) : 0;
                            break;
                        case PARAM_SEARCH_NUMBER_OF_SHARDS:
                            configuration.numberOfShards = i + 1 < argv.length ? Integer.valueOf(argv[++i]) : 1;
                            break;
                        case PARAM_MIGRATE_BY_CLASSES:
                            List<String> migrationClasses = new ArrayList<>();
                            String migrationClassesString = i + 1 < argv.length ? argv[++i] : null;
                            if (migrationClassesString != null) {
                                String[] classes = migrationClassesString.split(SearchUtils.COLON_SEPARATOR);
                                Collections.addAll(migrationClasses, classes);
                            }
                            configuration.migrationClasses = migrationClasses;
                            break;
                        default:
                            break;
                    }
                    break;
                }
            }
        }

        return configuration;
    }

    /**
     * Prints help.
     */
    public static void getParamsHelp(PrintStream out) {
        for (ConfigurationParams param : ConfigurationParams.values()) {
            out.printf("--%s %s%n", param.getParamName(), param.getHelpString());
        }
    }

    /**
     * @return the searchCuster
     */
    public String getSearchCuster() {
        return searchCuster;
    }

    /**
     * @return the addresses
     */
    public String getSearchAddresses() {
        return searchAddresses;
    }

    public Action[] getActions() {
        return actions;
    }


    /**
     * @return the storageId
     */
    public String getStorageId() {
        return storageId;
    }

    public String getBulkInputFile() {
        return bulkInputFile;
    }

    /**
     * @return the entityNames
     */
    public List<String> getEntityNames() {
        return entityNames;
    }

    /**
     * @return the type
     */
    public String getSearchType() {
        return searchType;
    }

    /**
     * @return the fields
     */
    public List<String> getSearchFields() {
        return searchFields;
    }

    /**
     * @return the searchValue
     */
    public String getSearchValue() {
        return searchValue;
    }

    /**
     * @return the searchClient
     */
    public Client getSearchClient() {
        if (searchClient == null) {

            if (this.getSearchCuster() == null
             || this.getSearchAddresses() == null) {
                throw new IllegalArgumentException("Invalid or unsufficient data for ElasticSearch client.");
            }

            searchClient = SearchUtils.initializeSearchClient(this.getSearchCuster(), this.getSearchAddresses());
        }
        return searchClient;
    }

    /**
     * @return the searchForceCreate
     */
    public boolean isSearchForceCreate() {
        return searchForceCreate;
    }

    /**
     * @return the bulkOutputFile
     */
    public String getBulkOutputFile() {
        return bulkOutputFile;
    }

    /**
     * @return the modelFile
     */
    public Model getModel() {
        return model;
    }

    /**
     * @return the inputFormats
     */
    public List<InputFormat> getInputFormats() {
        return inputFormats;
    }

    /**
     * @return the exchangeDefinition
     */
    public ExchangeDefinition getExchangeDefinition() {
        return exchangeDefinition;
    }

    /**
     * @return the importSystem
     */
    public String getImportSystem() {
        return importSystem;
    }

    /**
     * @return the unidataDataSource
     */
    public DataSource getUnidataDataSource() {
        return unidataDataSource;
    }

    /**
     * @return the importMetaMode
     */
    public ImportMetaMode getImportMetaMode() {
        return importMetaMode;
    }

    /**
     * @return the dropMappings
     */
    public boolean isDropMappings() {
        return dropMappings;
    }


    /**
     * @return the cleanTypes
     */
    public boolean isCleanTypes() {
        return cleanTypes;
    }

    /**
     * @return the helpRequest
     */
    public boolean isHelpRequest() {
        return helpRequest;
    }

    /**
     * @return initial params
     */
    public String[] getInitialArgs() {
        return initialArgs;
    }

    /**
     * @return collection of migration classes.
     */
    public List<String> getMigrationClasses() {
        return migrationClasses;
    }

    /**
     * Reads model from file.
     * @param fName
     * @return model
     * @throws JAXBException
     * @throws IOException
     */
    private static Model readModelFromFile(String fName)
            throws JAXBException, IOException {
        try (FileInputStream fis = new FileInputStream(fName)) {
            return (Model) JAXBContext
                    .newInstance(METADATA_CONTEXT_PATH)
                    .createUnmarshaller()
                    .unmarshal(fis);
        }
    }
    /**
     * Reads exchange definition from file.
     * @param fName
     * @return definition or null
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    private static ExchangeDefinition readExchangeDefinitionFromFile(String fName)
            throws JsonParseException, JsonMappingException, IOException {
        try (FileInputStream fis = new FileInputStream(fName)) {
            ObjectMapper om = new ObjectMapper();
            om.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
            om.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            return om.readValue(fis, ExchangeDefinition.class);
        }
    }

    /**
     * Closes resources which may have been allocated.
     */
    @Override
    public void close() throws Exception {
        if (searchClient != null) {
            try {
                searchClient.close();
            } catch (Exception exc) {}
        }

        if (landingDataSource != null
         && PooledDataSource.class.isAssignableFrom(landingDataSource.getClass())) {
            try {
                ((PooledDataSource) landingDataSource).close();
            } catch (Exception ex) {}
        }

        if (unidataDataSource != null
        && PooledDataSource.class.isAssignableFrom(unidataDataSource.getClass())) {
            try {
                ((PooledDataSource) unidataDataSource).close();
            } catch (Exception ex) {}
        }
    }


    /**
     * @return the landingDataSource
     */
    public DataSource getLandingDataSource() {
        return landingDataSource;
    }


    /**
     * @return the startOffset
     */
    public long getStartOffset() {
        return startOffset;
    }


    /**
     * @return the blockSize
     */
    public long getBlockSize() {
        return blockSize;
    }


    /**
     * @return the poolSize
     */
    public int getPoolSize() {
        return poolSize;
    }


    /**
     * @return the numberOfShards
     */
    public int getNumberOfShards() {
        return numberOfShards;
    }


    /**
     * @return the numberOfReplicas
     */
    public int getNumberOfReplicas() {
        return numberOfReplicas;
    }

    @Override
    public String getOperationId() {
        if (Objects.isNull(operationId)) {
            operationId = IdUtils.v1String();
        }
        return operationId;
    }

    public enum ImportMetaMode {
        RECREATE, UPDATE
    }

	/**
	 * @param initialArgs the initialArgs to set
	 */
	public void setInitialArgs(String[] initialArgs) {
		this.initialArgs = initialArgs;
	}

	/**
	 * @param migrationClasses the migrationClasses to set
	 */
	public void setMigrationClasses(List<String> migrationClasses) {
		this.migrationClasses = migrationClasses;
	}

	/**
	 * @param actions the actions to set
	 */
	public void setActions(Action[] actions) {
		this.actions = actions;
	}

	/**
	 * @param entityNames the entityNames to set
	 */
	public void setEntityNames(List<String> entityNames) {
		this.entityNames = entityNames;
	}

	/**
	 * @param storageId the storageId to set
	 */
	public void setStorageId(String storageId) {
		this.storageId = storageId;
	}

	/**
	 * @param searchCuster the searchCuster to set
	 */
	public void setSearchCuster(String searchCuster) {
		this.searchCuster = searchCuster;
	}

	/**
	 * @param searchType the searchType to set
	 */
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	/**
	 * @param numberOfShards the numberOfShards to set
	 */
	public void setNumberOfShards(int numberOfShards) {
		this.numberOfShards = numberOfShards;
	}

	/**
	 * @param numberOfReplicas the numberOfReplicas to set
	 */
	public void setNumberOfReplicas(int numberOfReplicas) {
		this.numberOfReplicas = numberOfReplicas;
	}

	/**
	 * @param searchValue the searchValue to set
	 */
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	/**
	 * @param searchFields the searchFields to set
	 */
	public void setSearchFields(List<String> searchFields) {
		this.searchFields = searchFields;
	}

	/**
	 * @param searchAddresses the searchAddresses to set
	 */
	public void setSearchAddresses(String searchAddresses) {
		this.searchAddresses = searchAddresses;
	}

	/**
	 * @param searchClient the searchClient to set
	 */
	public void setSearchClient(Client searchClient) {
		this.searchClient = searchClient;
	}

	/**
	 * @param searchForceCreate the searchForceCreate to set
	 */
	public void setSearchForceCreate(boolean searchForceCreate) {
		this.searchForceCreate = searchForceCreate;
	}

	/**
	 * @param bulkInputFile the bulkInputFile to set
	 */
	public void setBulkInputFile(String bulkInputFile) {
		this.bulkInputFile = bulkInputFile;
	}

	/**
	 * @param bulkOutputFile the bulkOutputFile to set
	 */
	public void setBulkOutputFile(String bulkOutputFile) {
		this.bulkOutputFile = bulkOutputFile;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(Model model) {
		this.model = model;
	}

	/**
	 * @param inputFormats the inputFormats to set
	 */
	public void setInputFormats(List<InputFormat> inputFormats) {
		this.inputFormats = inputFormats;
	}

	/**
	 * @param importSystem the importSystem to set
	 */
	public void setImportSystem(String importSystem) {
		this.importSystem = importSystem;
	}

	/**
	 * @param exchangeDefinition the exchangeDefinition to set
	 */
	public void setExchangeDefinition(ExchangeDefinition exchangeDefinition) {
		this.exchangeDefinition = exchangeDefinition;
	}

	/**
	 * @param startOffset the startOffset to set
	 */
	public void setStartOffset(long startOffset) {
		this.startOffset = startOffset;
	}

	/**
	 * @param blockSize the blockSize to set
	 */
	public void setBlockSize(long blockSize) {
		this.blockSize = blockSize;
	}

	/**
	 * @param poolSize the poolSize to set
	 */
	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	/**
	 * @param unidataDataSource the unidataDataSource to set
	 */
	public void setUnidataDataSource(DataSource unidataDataSource) {
		this.unidataDataSource = unidataDataSource;
	}

	/**
	 * @param landingDataSource the landingDataSource to set
	 */
	public void setLandingDataSource(DataSource landingDataSource) {
		this.landingDataSource = landingDataSource;
	}

	/**
	 * @param importMetaMode the importMetaMode to set
	 */
	public void setImportMetaMode(ImportMetaMode importMetaMode) {
		this.importMetaMode = importMetaMode;
	}

	/**
	 * @param dropMappings the dropMappings to set
	 */
	public void setDropMappings(boolean dropMappings) {
		this.dropMappings = dropMappings;
	}

	/**
	 * @param cleanTypes the cleanTypes to set
	 */
	public void setCleanTypes(boolean cleanTypes) {
		this.cleanTypes = cleanTypes;
	}

	/**
	 * @param helpRequest the helpRequest to set
	 */
	public void setHelpRequest(boolean helpRequest) {
		this.helpRequest = helpRequest;
	}

	/**
	 * @param operationId the operationId to set
	 */
	@Override
    public void setOperationId(String operationId) {
		this.operationId = operationId;
	}
}

package com.unidata.mdm.backend.service.job.exchange.in;

import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.exchange.def.ExchangeEntity;
import com.unidata.mdm.backend.service.job.JobCommonParameters;
import com.unidata.mdm.backend.service.job.exchange.in.types.ImportDataSet;
import com.unidata.mdm.backend.util.CryptUtils;
import com.unidata.mdm.backend.util.IdUtils;

/**
 * @author Mikhail Mikhailov
 * Import job constants.
 */
public class ImportDataJobConstants extends JobCommonParameters {
    /**
     * Import data job.
     */
    public static final String IMPORT_JOB_NAME = "importDataJob";
    /**
     * Param 'dataSetSize'.
     */
    public static final String PARAM_DATA_SET_SIZE = "dataSetSize";
    /**
     * Param 'mergeWithPreviousVersion'.
     */
    public static final String PARAM_MERGE_WITH_PREVIOUS_VERSION = "mergeWithPreviousVersion";
    /**
     * Param 'skipDq'.
     */
    public static final String PARAM_SKIP_DQ = "skipDq";
    /**
     * Param 'skipMatching'.
     */
    public static final String PARAM_SKIP_MATCHING = "skipMatching";
    /**
     * Param 'skipIndexRebuild'.
     */
    public static final String PARAM_SKIP_INDEX_REBUILD = "skipIndexRebuild";
    /**
     * Param 'skipIndexing'.
     */
    public static final String PARAM_SKIP_INDEXING = "skipIndexing";
    /**
     * Param 'skipNotifications'.
     */
    public static final String PARAM_SKIP_NOTIFICATIONS = "skipNotifications";
    /**
     * Param 'initialLoad'.
     */
    public static final String PARAM_INITIAL_LOAD = "initialLoad";
    /**
     * Param 'fromSourceSystem'.
     */
    public static final String PARAM_FROM_SOURCE_SYSTEM = "fromSourceSystem";
    /**
     * Param 'fromEntityName'.
     */
    public static final String PARAM_FROM_ENTITY_NAME = "fromEntityName";
    /**
     * Param 'indexTablespace'.
     */
    public static final String PARAM_INDEX_TABLESPACE = "indexTablespace";
    /**
     * Param 'importDatabaseVendor'.
     */
    public static final String PARAM_IMPORT_DATABASE_VENDOR = "importDatabaseVendor";
    /**
     * Import job logger name.
     */
    public static final String IMPORT_JOB_LOGGER_NAME = "import-job-logger";
    /**
     * Exchange objects map name.
     */
    public static final String EXCHANGE_OBJECTS_MAP_NAME = "import-data-job-exchange-objects";
    /**
     * Update result mark.
     */
    public static final String IMPORT_JOB_RECORDS_UPDATE_COUNTER = "#RECORDS_UPDATE";
    /**
     * Insert result mark.
     */
    public static final String IMPORT_JOB_RECORDS_INSERT_COUNTER = "#RECORDS_INSERT";
    /**
     * Skip result mark.
     */
    public static final String IMPORT_JOB_RECORDS_SKIP_COUNTER = "#RECORDS_SKIP";
    /**
     * Fail result mark.
     */
    public static final String IMPORT_JOB_RECORDS_FAIL_COUNTER = "#RECORDS_FAIL";
    /**
     * Delete result mark.
     */
    public static final String IMPORT_JOB_RECORDS_DELETE_COUNTER = "#RECORDS_DELETE";
    /**
     * Update result mark.
     */
    public static final String IMPORT_JOB_RELATIONS_UPDATE_COUNTER = "#RELATIONS_UPDATE";
    /**
     * Insert result mark.
     */
    public static final String IMPORT_JOB_RELATIONS_INSERT_COUNTER = "#RELATIONS_INSERT";
    /**
     * Skip result mark.
     */
    public static final String IMPORT_JOB_RELATIONS_SKIP_COUNTER = "#RELATIONS_SKIP";
    /**
     * Fail result mark.
     */
    public static final String IMPORT_JOB_RELATIONS_FAIL_COUNTER = "#RELATIONS_FAIL";
    /**
     * Delete result mark.
     */
    public static final String IMPORT_JOB_RELATIONS_DELETE_COUNTER = "#RELATIONS_DELETE";
    /**
     * Update result mark.
     */
    public static final String IMPORT_JOB_CLASSIFIERS_UPDATE_COUNTER = "#CLASSIFIERS_UPDATE";
    /**
     * Insert result mark.
     */
    public static final String IMPORT_JOB_CLASSIFIERS_INSERT_COUNTER = "#CLASSIFIERS_INSERT";
    /**
     * Skip result mark.
     */
    public static final String IMPORT_JOB_CLASSIFIERS_SKIP_COUNTER = "#CLASSIFIERS_SKIP";
    /**
     * Fail result mark.
     */
    public static final String IMPORT_JOB_CLASSIFIERS_FAIL_COUNTER = "#CLASSIFIERS_FAIL";
    /**
     * Delete result mark.
     */
    public static final String IMPORT_JOB_CLASSIFIERS_DELETE_COUNTER = "#CLASSIFIERS_DELETE";
    /**
     * Default batch size.
     */
    public static final long DEFAULT_BATCH_SIZE = 500;
    /**
     * TODO move to reader.
     */
    public static final String IMPORT_JOB_LAST_FIELD_INDICATOR = StringUtils.truncate("a_" + CryptUtils.toMurmurString(IdUtils.v4String()), 0, 30);
    /**
     * Sorts entity import definitions by import order field.
     */
    public static final Comparator<ExchangeEntity> ENTITY_IMPORT_ORDER_COMPARATOR = (o1, o2) -> o1.getImportOrder() - o2.getImportOrder();
    /**
     * Dummy record indicator.
     */
    public static final ImportDataSet DUMMY_RECORD = new ImportDataSet(null);
    /**
     * Run id tag for table names.
     */
    public static final String RUN_ID_REPLACEMENT_TAG = "{runId}";
    /**
     * Total records message.
     */
    public static final String MSG_REPORT_RECORDS_TOTAL = "app.data.import.stat.records.total";
    /**
     * Total relations message.
     */
    public static final String MSG_REPORT_RELATIONS_TOTAL = "app.data.import.stat.relations.total";
    /**
     * Total classifiers message.
     */
    public static final String MSG_REPORT_CLASSIFIERS_TOTAL = "app.data.import.stat.classifiers.total";
    /**
     * Inserted count.
     */
    public static final String MSG_REPORT_INSERTED = "app.data.import.stat.inserted";
    /**
     * Updated count.
     */
    public static final String MSG_REPORT_UPDATED = "app.data.import.stat.updated";
    /**
     * Skept count.
     */
    public static final String MSG_REPORT_SKEPT = "app.data.import.stat.skept";
    /**
     * Deleted count.
     */
    public static final String MSG_REPORT_DELETED = "app.data.import.stat.deleted";
    /**
     * Failed count.
     */
    public static final String MSG_REPORT_FAILED = "app.data.import.stat.failed";

    /**
     * Constructor.
     */
    private ImportDataJobConstants() {
        super();
    }
}

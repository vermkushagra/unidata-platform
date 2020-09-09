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

package com.unidata.mdm.backend.service.data.xlsximport;

import static java.util.stream.Collectors.toMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.unidata.mdm.backend.common.audit.AuditLevel;
import com.unidata.mdm.backend.common.context.SaveLargeObjectRequestContext;
import com.unidata.mdm.backend.common.context.SaveLargeObjectRequestContext.SaveLargeObjectRequestContextBuilder;
import com.unidata.mdm.backend.common.context.UpsertUserEventRequestContext;
import com.unidata.mdm.backend.common.context.UpsertUserEventRequestContext.UpsertUserEventRequestContextBuilder;
import com.unidata.mdm.backend.common.dto.job.JobDTO;
import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;
import com.unidata.mdm.backend.common.dto.security.UserEventDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.common.service.UserService;
import com.unidata.mdm.backend.configuration.application.ConfigurationUpdatesConsumer;
import com.unidata.mdm.backend.configuration.application.UnidataConfigurationProperty;
import com.unidata.mdm.backend.dao.DataImportDao;
import com.unidata.mdm.backend.exchange.def.ClassifierMapping;
import com.unidata.mdm.backend.exchange.def.ComplexAttributeExpansion;
import com.unidata.mdm.backend.exchange.def.ExchangeDefinition;
import com.unidata.mdm.backend.exchange.def.ExchangeField;
import com.unidata.mdm.backend.exchange.def.RelatesToRelation;
import com.unidata.mdm.backend.exchange.def.VersionRange;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeEntity;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeField;
import com.unidata.mdm.backend.exchange.def.db.DbNaturalKey;
import com.unidata.mdm.backend.exchange.def.db.DbRelatesToRelation;
import com.unidata.mdm.backend.exchange.def.db.DbSystemKey;
import com.unidata.mdm.backend.service.data.batch.BatchSetSize;
import com.unidata.mdm.backend.service.data.export.xlsx.XLSXProcessor;
import com.unidata.mdm.backend.service.data.xlsximport.XLSXHeader.TYPE;
import com.unidata.mdm.backend.service.job.JobServiceExt;
import com.unidata.mdm.backend.service.job.exchange.in.ImportDataJobConstants;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.IdUtils;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.backend.util.constants.UserMessageConstants;
import com.unidata.mdm.meta.AbstractExternalIdGenerationStrategyDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.ExternalIdGenerationStrategyType;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.SimpleDataType;

import reactor.core.publisher.Flux;

/**
 * The Class XLSXImportService.
 */
@Component
@Qualifier("xlsxImportService")
public class XLSXImportService extends XLSXProcessor implements DataImportService, ConfigurationUpdatesConsumer {

    /**
     * The executor.
     */
    private ExecutorService executor = Executors.newFixedThreadPool(10);

    /**
     * The meta model service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;

    /**
     * The data import dao.
     */
    @Autowired
    private DataImportDao dataImportDao;

    /**
     * The job service.
     */
    @Autowired
    private JobServiceExt jobServiceExt;

    @Autowired
    private DataRecordsService dataRecordService;

    /**
     * The user service.
     */
    @Autowired
    private UserService userService;

    private int asyncSaveDataBatchSize = (Integer) UnidataConfigurationProperty.IMPORT_XLSX_BATCH_SIZE.getDefaultValue().get();
    private int saveBatchProcessors = (Integer) UnidataConfigurationProperty.IMPORT_XLSX_THREADS_POOL_SIZE.getDefaultValue().get();

    /**
     * The Constant LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(XLSXImportService.class);

    /**
     * Import data.
     *
     * @param file the file
     * @param entityName the entity name
     * @param sourceSystem the source system
     * @param creationDate the creation date
     */
    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.data.xlsximport.DataImportService#
     * importData(java.io.File, java.lang.String)
     */
    @Override
    public void importData(File file, String entityName, String sourceSystem, String creationDate,
                           boolean mergeWithPrevious) {

        prevalidate(file, entityName);
        String userName = SecurityUtils.getCurrentUserName();
        String userToken = SecurityUtils.getCurrentUserToken();

        executor.submit(() -> {
            try {
                importDataSync(file, entityName, sourceSystem, creationDate, userName, userToken, mergeWithPrevious);
            } catch (Exception e) {
                LOGGER.error("Cannot import file due to exception", e);
                try {

                    final String message = e instanceof SystemRuntimeException
                            ? formatSystemRuntimeException((SystemRuntimeException) e) : e.getLocalizedMessage();
                    Path reportFile = Files.write(Paths.get("Oтчет.txt"), message.getBytes(Charset.forName("UTF-8")),
                            StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                    UpsertUserEventRequestContext uueCtx = new UpsertUserEventRequestContextBuilder()
                            .login(userName)
                            .type("XLSX_IMPORT")
                            .content(MessageUtils.getMessage(UserMessageConstants.DATA_IMPORT_UNSUCCESS, file.getName()))
                            .build();
                    UserEventDTO userEventDTO = userService.upsert(uueCtx);
                    // save result and attach it to the early created user event
                    SaveLargeObjectRequestContext slorCTX = new SaveLargeObjectRequestContextBuilder()
                            .eventKey(userEventDTO.getId()).mimeType("text/plain").binary(true)
                            .inputStream(
                                    Files.newInputStream(reportFile, StandardOpenOption.DELETE_ON_CLOSE))
                            .filename("Report.txt").build();
                    dataRecordService.saveLargeObject(slorCTX);
                } catch (Exception e1) {
                    LOGGER.error("Cannot create report file due to exception", e1);
                }
            }
        });
    }

    private String formatSystemRuntimeException(SystemRuntimeException sre) {
        return "Сообщение [" +
                MessageUtils.getMessage(sre.getId().getCode(), sre.getArgs()) +
                "], Системный код ошибки [" +
                sre.getId().name() +
                "]";
    }

    /**
     * Prevalidate.
     *
     * @param file the file
     * @param entityName the entity name
     */
    private void prevalidate(File file, String entityName) {
        if (!StringUtils.endsWith(file.getName().toLowerCase(), ".xlsx")) {
            throw new DataProcessingException("Unable to parse file {}. Invalid format. Accepted only XLSX files.",
                    ExceptionId.EX_DATA_XLSX_IMPORT_UNKNOWN_FILE_FORMAT, file.getName());
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook(file)) {

            XSSFSheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new DataProcessingException("Unknown entity name",
                        ExceptionId.EX_DATA_XLSX_IMPORT_UNKNOWN_ENTITY, file.getName(), entityName);
            }
        } catch (InvalidFormatException | InvalidOperationException | IOException e) {
            throw new DataProcessingException("Unable to parse incoming import file. Invalid format.",
                    ExceptionId.EX_DATA_XLSX_IMPORT_PARSE_FILE, file.getName(), e);
        }
    }

    /**
     * Import data sync.
     *
     * @param file the file
     * @param entityName the entity name
     * @param sourceSystem the source system
     * @param creationDate the creation date
     * @param userName the user name
     * @param userToken the user token
     */
    private void importDataSync(File file, String entityName, String sourceSystem, String creationDate,
                                String userName, String userToken, boolean mergeWithPrevious) {

        EntityDef entity = metaModelService.getEntityByIdNoDeps(entityName);
        LookupEntityDef lookupEntity = metaModelService.getLookupEntityById(entityName);
        AbstractExternalIdGenerationStrategyDef extIdGenerationStrategy = null;

        Long modelVersion;
        if (entity != null) {
            modelVersion = entity.getVersion();
            extIdGenerationStrategy = entity.getExternalIdGenerationStrategy();
        } else if (lookupEntity != null) {
            modelVersion = lookupEntity.getVersion();
            extIdGenerationStrategy = lookupEntity.getExternalIdGenerationStrategy();
        } else {
            throw new DataProcessingException(
                    "Entity or lookup entity with name {} doesn't found in the current metamodel.",
                    ExceptionId.EX_DATA_XLSX_IMPORT_UNKNOWN_ENTITY, file.getName(), entityName);
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook(file)) {

            XSSFFormulaEvaluator formulaEvaluator = new XSSFFormulaEvaluator(workbook);
            XSSFSheet sheet = workbook.getSheetAt(0);
            XSSFRow metadataRow = sheet.getRow(0);
            List<XLSXHeader> xlsxHeaders = createHeaders(entityName, true);
            // Import ID
            xlsxHeaders.add(new XLSXHeader().withSystemHeader(XLSX_IMPORT_ID).withHrHeader(XLSX_IMPORT_ID).withTypeHeader(SimpleDataType.STRING)
                    .withType(TYPE.SYSTEM).withIsMandatory(false));

            Map<String, XLSXHeader> typeMap = new HashMap<>();
            for (XLSXHeader xlsxHeader : xlsxHeaders) {
                typeMap.put(xlsxHeader.getSystemHeader(), xlsxHeader);
            }

            Map<Integer, String> headers = new HashMap<>();
            metadataRow.cellIterator().forEachRemaining(cell ->
                    headers.put(cell.getColumnIndex(), cell.getStringCellValue()));
            xlsxHeaders.stream().filter(XLSXHeader::isMandatory).forEach(rem -> {
                if (headers.values().stream().noneMatch(exi -> StringUtils.equals(rem.getSystemHeader(), exi))) {
                    throw new DataProcessingException("Required attribute {} is missing in headers!",
                            ExceptionId.EX_DATA_XLSX_IMPORT_UNKNOWN_ENTITY, file.getName(), entityName);
                }
            });

            final AtomicInteger optionalHeadersIndex = new AtomicInteger(0);
            typeMap.keySet()
                    .stream()
                    .filter(name -> !headers.values().contains(name))
                    .collect(toMap(s -> optionalHeadersIndex.decrementAndGet(), name -> name, (o, n) -> n, () -> headers));

            int rowNum = sheet.getLastRowNum();
            Map<String, List<Integer>> etalonIds = new HashMap<>();
            Map<String, List<Integer>> externalIds = new HashMap<>();
            Map<Integer, RowEntry> rowEntryMap = new HashMap<>();
            Map<String, Map<Object, List<Integer>>> uniqueDataMap = new HashMap<>();

            List<Map<String, Object>> data = new ArrayList<>();

            for (int i = 2; i < rowNum + 1; i++) {
                final XSSFRow currentRow = sheet.getRow(i);
                if (!isEmptyRow(currentRow)) {
                    Map<String, Object> rowData = new HashMap<>();

                    final AtomicBoolean withoutIdentifier = new AtomicBoolean(true);

                    headers.forEach((idx, name) -> {

                        try {

                            XLSXHeader hdr = typeMap.get(name);

                            if (Objects.nonNull(hdr)) {

                                Object val = idx >= 0 ? convertCell(currentRow.getCell(idx), hdr, formulaEvaluator) : null;

                                if (XLSXProcessor.ID.equals(hdr.getSystemHeader()) && val != null) {
                                    List<Integer> rowNumbers = etalonIds.get(val);
                                    if (Objects.isNull(rowNumbers)) {
                                        rowNumbers = new ArrayList<>();
                                        etalonIds.put(val.toString(), rowNumbers);
                                    }
                                    rowNumbers.add(currentRow.getRowNum());

                                    RowEntry rowEntry = getRowEntry(currentRow.getRowNum(), rowEntryMap);
                                    rowEntry.setId(val.toString());
                                } else if (XLSXProcessor.EXTERNAL_ID.equals(hdr.getSystemHeader()) && val != null) {
                                    withoutIdentifier.set(false);
                                    List<Integer> rowNumbers = externalIds.get(val);
                                    if (Objects.isNull(rowNumbers)) {
                                        rowNumbers = new ArrayList<>();
                                        externalIds.put(val.toString(), rowNumbers);
                                    }
                                    rowNumbers.add(currentRow.getRowNum());

                                    RowEntry rowEntry = getRowEntry(currentRow.getRowNum(), rowEntryMap);
                                    rowEntry.setExternalId(val.toString());
                                } else if (XLSXProcessor.FROM.equals(hdr.getSystemHeader()) && val != null) {
                                    if (val instanceof Date) {
                                        RowEntry rowEntry = getRowEntry(currentRow.getRowNum(), rowEntryMap);
                                        rowEntry.setFrom((Date) val);
                                    }
                                } else if (XLSXProcessor.TO.equals(hdr.getSystemHeader()) && val != null) {
                                    if (val instanceof Date) {
                                        RowEntry rowEntry = getRowEntry(currentRow.getRowNum(), rowEntryMap);
                                        rowEntry.setTo((Date) val);
                                    }
                                } else if (hdr.getAttributeHolder() != null && hdr.getAttributeHolder().isUnique() &&
                                        val != null) {
                                    Map<Object, List<Integer>> dataRowNumbers = uniqueDataMap.get(hdr.getSystemHeader());

                                    if (Objects.isNull(dataRowNumbers)) {
                                        dataRowNumbers = new HashMap<>();
                                        uniqueDataMap.put(hdr.getSystemHeader(), dataRowNumbers);
                                    }

                                    List<Integer> rowNumbers = dataRowNumbers.get(val);
                                    if (Objects.isNull(rowNumbers)) {
                                        rowNumbers = new ArrayList<>();
                                        dataRowNumbers.put(val, rowNumbers);
                                    }
                                    rowNumbers.add(currentRow.getRowNum());
                                }

                                rowData.put(MURMUR.hashString(name, Charsets.UTF_8).toString(), val);
                            }
                        } catch (ParseException e) {
                            throw new DataProcessingException(
                                    "Unable to parse incoming import file. Invalid cell format: row " +
                                            (currentRow.getRowNum() + 1) +
                                            ", column " +
                                            (currentRow.getCell(idx).getColumnIndex() + 1) +
                                            ". Expected format: " +
                                            typeMap.get(name).getTypeHeader().name(),
                                    ExceptionId.EX_DATA_XLSX_IMPORT_PARSE_FILE_INVALID_CELL_FORMAT,
                                    file.getName(),
                                    currentRow.getRowNum() + 1,
                                    currentRow.getCell(idx).getColumnIndex() + 1,
                                    MessageUtils.getEnumTranslation(typeMap.get(name).getTypeHeader()),
                                    e);
                        }
                    });

                    RowEntry rowEntry = getRowEntry(currentRow.getRowNum(), rowEntryMap);
                    rowData.put(MURMUR.hashString(XLSXProcessor.XLSX_IMPORT_ID, Charsets.UTF_8).toString(), rowEntry.generateImportId());

                    if (withoutIdentifier.get()) {
                        // UN-6427, id generation rules are in play
                        // Need to generate EXTERNAL_ID for undefined strategy or equals ExternalIdGenerationStrategyType.RANDOM
                        if (Objects.isNull(extIdGenerationStrategy) ||
                                extIdGenerationStrategy.getStrategyType() == ExternalIdGenerationStrategyType.RANDOM) {
                            rowData.put(MURMUR.hashString(XLSXProcessor.EXTERNAL_ID, Charsets.UTF_8).toString(), IdUtils.v1String());
                        }
                    }
                    data.add(rowData);
                }
            }
            // Special - check duplicates
            checkDuplicates(etalonIds, externalIds, uniqueDataMap, rowEntryMap);

            dataImportDao.createImportTable(xlsxHeaders, entityName, String.valueOf(modelVersion), file.getName(),
                    creationDate);

            saveDataAsync(data, xlsxHeaders, entityName, sourceSystem, modelVersion, file.getName(), creationDate);

            ExchangeDefinition exchangeDefinition = createExchangeDefinition(sourceSystem, entityName, String.valueOf(modelVersion),
                    file.getName(), creationDate, xlsxHeaders);
            JobDTO xslxJob = createImportJob(userName, userToken, exchangeDefinition, mergeWithPrevious);
            jobServiceExt.startSystemJob(xslxJob);

        } catch (InvalidFormatException | IOException e) {
            try {
                dataImportDao.dropImportTable(entityName, String.valueOf(modelVersion), file.getName(),
                        creationDate);
            } catch (Exception ex) {
                LOGGER.error("Can't drop import table", ex);
                // ignore
            }
            throw new DataProcessingException("Unable to parse incoming import file. Invalid format.",
                    ExceptionId.EX_DATA_XLSX_IMPORT_PARSE_FILE, file.getName(), e);
        } catch (Exception e) {
            try {
                dataImportDao.dropImportTable(entityName, String.valueOf(modelVersion), file.getName(),
                        creationDate);
            } catch (Exception ex) {
                LOGGER.error("Can't drop import table", ex);
            }
            throw e;
        } finally {
            file.delete();
        }
    }

    private void saveDataAsync(List<Map<String, Object>> data, List<XLSXHeader> xlsxHeaders, String entityName,
                               String sourceSystem, Long modelVersion, String fileName, String creationDate) {

        if (data.size() < asyncSaveDataBatchSize || saveBatchProcessors < 2) {
            dataImportDao.saveData(data, xlsxHeaders, entityName, sourceSystem, String.valueOf(modelVersion),
                    fileName, creationDate);
            return;
        }

        ExecutorService es = Executors.newFixedThreadPool(saveBatchProcessors);
        CompletionService<Object> completionService =
                new ExecutorCompletionService<>(es);

        AtomicInteger batchCounter = new AtomicInteger(0);

        //split into batches and save it async
        IntStream.range(0, (data.size() + asyncSaveDataBatchSize - 1) / asyncSaveDataBatchSize)
                .mapToObj(i -> data.subList(i * asyncSaveDataBatchSize, Math.min(asyncSaveDataBatchSize * (i + 1), data.size())))
                .forEach(d -> {
                    batchCounter.incrementAndGet();
                    completionService.submit(() -> {

                        dataImportDao.saveData(d, xlsxHeaders, entityName, sourceSystem, String.valueOf(modelVersion),
                                fileName, creationDate);

                        return "";
                    });
                });

        try {
            //wait for all tasks completion
            for (int t = 0; t < batchCounter.get(); t++) {
                Future<Object> f = completionService.take();
                Object o = f.get();
            }
        } catch (InterruptedException e) {
            LOGGER.error("Can't get result of a task, temporary table thread is interrupted", e);
            throw new DataProcessingException("XLSX import, temporary table thread is interrupted",
                    ExceptionId.EX_DATA_XLSX_IMPORT_TEMPORARY_TABLE, fileName, e);
        } catch (ExecutionException e) {
            LOGGER.error("Can't get result of a task", e);
            throw new DataProcessingException("XLSX import, can't get result of async temporary table task",
                    ExceptionId.EX_DATA_XLSX_IMPORT_TEMPORARY_TABLE, fileName, e);
        }
    }

    /**
     * Get row entry or create new if not exists.
     *
     * @param rowNumber row number index.
     * @param rowEntryMap map of row entries with row number as key.
     * @return row entry object.
     */
    private RowEntry getRowEntry(int rowNumber, Map<Integer, RowEntry> rowEntryMap) {
        RowEntry rowEntry = rowEntryMap.get(rowNumber);
        if (Objects.isNull(rowEntry)) {
            rowEntry = new RowEntry();
            rowEntryMap.put(rowNumber, rowEntry);
        }

        return rowEntry;
    }

    /**
     * Check duplicates with timelines.
     *
     * @param etalonIds collection of row numbers grouped by etalonId
     * @param externalIds collection of row numbers grouped by externalId
     * @param rowEntryMap map of entries with date period from/to values with row number as key.
     * @throws DataProcessingException throws exception in case of duplication check with timelines.
     */
    private void checkDuplicates(Map<String, List<Integer>> etalonIds,
                                 Map<String, List<Integer>> externalIds,
                                 Map<String, Map<Object, List<Integer>>> uniqueDataMap,
                                 Map<Integer, RowEntry> rowEntryMap) throws DataProcessingException {
        StringBuilder etalonIdDuplicates = new StringBuilder();
        etalonIds.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .forEach(entry -> {
                    List<RowEntry> rowEntries = entry.getValue().stream()
                            .map(rowEntryMap::get)
                            .collect(Collectors.toList());

                    if (!isValidTimelines(rowEntries)) {
                        etalonIdDuplicates
                                .append("\n id: ")
                                .append(entry.getKey())
                                .append(" - [")
                                .append(StringUtils.join(entry.getValue(), ", "))
                                .append("]");
                    }
                });

        StringBuilder externalIdDuplicates = new StringBuilder();
        externalIds.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .forEach(entry -> {
                    List<RowEntry> rowEntries = entry.getValue().stream()
                            .map(rowEntryMap::get)
                            .collect(Collectors.toList());

                    if (!isValidTimelines(rowEntries)) {
                        externalIdDuplicates
                                .append("\n id: ")
                                .append(entry.getKey())
                                .append(" - [")
                                .append(StringUtils.join(entry.getValue(), ", "))
                                .append("]");
                    }
                });

        if (etalonIdDuplicates.length() > 0 || externalIdDuplicates.length() > 0) {
            final String message = "Duplicate keys detected. Etalon keys ({}), external ids ({})";
            LOGGER.warn(message, etalonIdDuplicates.toString(), externalIdDuplicates.toString());
            throw new DataProcessingException(message, ExceptionId.EX_DATA_XLSX_IMPORT_DUPLICATED_IDS,
                    etalonIdDuplicates, externalIdDuplicates);
        }

        // Note, that we make duplication check first for externalIDs and etalonIDs.
        StringBuilder attributeDuplicates = new StringBuilder();
        // Check duplicates for other attributes marked as Unique.
        uniqueDataMap.forEach((attrHeaderName, valueRowNumbers) -> valueRowNumbers.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .forEach(entry -> {
                    List<RowEntry> rowEntries = entry.getValue().stream()
                            .map(rowEntryMap::get)
                            .collect(Collectors.toList());

                    if (!isValidTimelines(rowEntries)) {
                        attributeDuplicates.append("\n ").append(attrHeaderName).append(": ")
                                .append(entry.getKey())
                                .append(" - [")
                                .append(StringUtils.join(entry.getValue(), ", "))
                                .append("]");
                    }
                }));

        if (attributeDuplicates.length() > 0) {
            final String message = "Duplicate attribute values detected. Unique attributes({})";
            LOGGER.warn(message, attributeDuplicates.toString());
            throw new DataProcessingException(message, ExceptionId.EX_DATA_XLSX_IMPORT_NON_UNIQUE_VALUES,
                    attributeDuplicates);
        }
    }

    /**
     * Check overlaps for date periods.
     *
     * @param rowEntries Date periods.
     * @return true if no overlaps in date periods.
     */
    private boolean isValidTimelines(List<RowEntry> rowEntries) {
        final boolean[] validRanges = {true};
        rowEntries.stream()
                .filter(rowEntry -> rowEntry.getFrom() == null || rowEntry.getTo() == null)
                .findAny().ifPresent(rowEntry -> validRanges[0] = false);

        if (!validRanges[0]) {
            return false;
        }

        // Sort elements in collection by "From" to find overlaps between neighboring elements.
        rowEntries.sort(Comparator.comparing(RowEntry::getFrom));

        for (int i = 0; i < (rowEntries.size() - 1); i++) {
            RowEntry rowEntry = rowEntries.get(i);
            RowEntry nextRowEntry = rowEntries.get(i + 1);

            // Overlaps in date periods. First entry mus be before second period.
            if (!(rowEntry.getFrom().compareTo(nextRowEntry.getFrom()) < 0 &&
                    rowEntry.getTo().compareTo(nextRowEntry.getFrom()) < 0)) {
                validRanges[0] = false;
                return false;
            }
        }

        return true;
    }

    /**
     * Creates the import job.
     *
     * @param userName the user name
     * @param userToken the user token
     * @param exchangeDefinition the exchange definition
     * @param mergeWithPrevious
     * @return the job dto
     */
    private JobDTO createImportJob(String userName, String userToken, ExchangeDefinition exchangeDefinition,
                                   boolean mergeWithPrevious) {

        JobDTO xslxJob = new JobDTO();
        xslxJob.setDescription("Import records from XLSX");
        xslxJob.setName("Import Job");
        xslxJob.setEnabled(true);
        xslxJob.setJobNameReference(ImportDataJobConstants.IMPORT_JOB_NAME);
        xslxJob.setParameters(
                Arrays.asList(
                        new JobParameterDTO(ImportDataJobConstants.PARAM_OPERATION_ID, IdUtils.v1String()),
                        new JobParameterDTO(ImportDataJobConstants.PARAM_USER_NAME, userName),
                        new JobParameterDTO(ImportDataJobConstants.PARAM_INITIAL_LOAD, false),
                        new JobParameterDTO(ImportDataJobConstants.PARAM_USER_TOKEN, userToken),
                        new JobParameterDTO(ImportDataJobConstants.PARAM_DATABASE_URL, dataImportDao.getConnectionURL()),
                        new JobParameterDTO(ImportDataJobConstants.PARAM_BLOCK_SIZE, "1000"),
                        new JobParameterDTO(ImportDataJobConstants.PARAM_DEFINITION, jobServiceExt.putComplexParameter(exchangeDefinition)),
                        new JobParameterDTO(ImportDataJobConstants.PARAM_MERGE_WITH_PREVIOUS_VERSION, mergeWithPrevious),
                        new JobParameterDTO(ImportDataJobConstants.PARAM_SKIP_DQ, false),
                        new JobParameterDTO(ImportDataJobConstants.PARAM_SKIP_INDEXING, false),
                        new JobParameterDTO(ImportDataJobConstants.PARAM_RESOLVE_BY_MATCHING, false),
                        new JobParameterDTO(ImportDataJobConstants.PARAM_SKIP_MATCHING, false),
                        new JobParameterDTO(ImportDataJobConstants.PARAM_SKIP_NOTIFICATIONS, false),
                        new JobParameterDTO(ImportDataJobConstants.PARAM_SKIP_INDEX_REBUILD, false),
                        new JobParameterDTO(ImportDataJobConstants.PARAM_SKIP_MOVE, false),
                        new JobParameterDTO(ImportDataJobConstants.PARAM_DATA_SET_SIZE, BatchSetSize.SMALL.name()),
                        new JobParameterDTO(ImportDataJobConstants.PARAM_AUDIT_LEVEL, (long) AuditLevel.AUDIT_SUCCESS),
                        new JobParameterDTO(ImportDataJobConstants.PROCESSING_BATCH_CRASHES, true)));

        return xslxJob;
    }

    /**
     * Creates the template file.
     *
     * @param entityName the entity name
     * @return the byte[]
     */
    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.data.xlsximport.DataImportService#
     * createTemplateFile(java.lang.String)
     */
    @Override
    public ByteArrayOutputStream createTemplateFile(String entityName) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
             Workbook wb = createTemplateWorkbook(entityName, true)) {
            wb.write(output);
            return output;
        } catch (IOException e) {
            LOGGER.error("Unable to create template XLS file", e);
            throw new SystemRuntimeException("Unable to create template XLS file for {}.",
                    ExceptionId.EX_DATA_XLSX_IMPORT_UNABLE_TO_CREATE_TEMPLATE, entityName);
        }
    }

    /**
     * Creates the exchange entity.
     *
     * @param sourceSystem the source system
     * @param entityName the entity name
     * @param modelVersion the model version
     * @param fileName the file name
     * @param fileCreationDate the file creation date
     * @param headers the headers
     * @return the exchange entity
     */
    private ExchangeDefinition createExchangeDefinition(String sourceSystem, String entityName, String modelVersion,
                                                        String fileName, String fileCreationDate, List<XLSXHeader> headers) {

        DbExchangeEntity dbExchangeEntity = new DbExchangeEntity();
        dbExchangeEntity.setVersionRange(createVersionRange());
        dbExchangeEntity.setImportOrder(1);
        dbExchangeEntity.setName(entityName);

        DbSystemKey systemKey = new DbSystemKey();
        systemKey.setAlias("_" + MURMUR.hashString(ID, Charsets.UTF_8).toString());
        systemKey.setColumn("_" + MURMUR.hashString(ID, Charsets.UTF_8).toString());

        DbNaturalKey naturalKey = new DbNaturalKey();
        naturalKey.setAlias("_" + MURMUR.hashString(EXTERNAL_ID, Charsets.UTF_8).toString());
        naturalKey.setColumn("_" + MURMUR.hashString(EXTERNAL_ID, Charsets.UTF_8).toString());
        naturalKey.setType(String.class.getCanonicalName());

        dbExchangeEntity.setXlsxKey("_" + MURMUR.hashString(XLSX_IMPORT_ID, Charsets.UTF_8).toString());
        dbExchangeEntity.setSystemKey(systemKey);
        dbExchangeEntity.setNaturalKey(naturalKey);
        dbExchangeEntity.setSourceSystem(sourceSystem);
        dbExchangeEntity.setSkipCleanse(false);
        dbExchangeEntity.setDropAfter(true);
        dbExchangeEntity.setTables(Collections.singletonList(DataImportDao.IMPORT_SCHEMA
                + dataImportDao.constructTableName(entityName, modelVersion, fileName, fileCreationDate)));
        dbExchangeEntity.setUpdates(true);
        dbExchangeEntity.setFields(createDataFields(headers));
        dbExchangeEntity.setClassifierMappings(createClassifierMappings(headers));
        dbExchangeEntity.setRelates(createRelations(headers, sourceSystem, DataImportDao.IMPORT_SCHEMA
                + dataImportDao.constructTableName(entityName, modelVersion, fileName, fileCreationDate)));
        dbExchangeEntity.setMultiVersion(true);
        dbExchangeEntity.setUnique(false);

        ExchangeDefinition def = new ExchangeDefinition();
        def.getEntities().add(dbExchangeEntity);

        return def;
    }

    /**
     * Creates the fields.
     *
     * @param headers the headers
     * @return the list
     */
    private List<ExchangeField> createDataFields(List<XLSXHeader> headers) {

        List<ExchangeField> result = new ArrayList<>();
        headers.stream().filter(h -> (h.getType() == TYPE.DATA_ATTRIBUTE && h.getTypeHeader() != SimpleDataType.CLOB
                && h.getTypeHeader() != SimpleDataType.BLOB) && !h.isRel()).forEach(h -> result.add(createField(h)));
        return result;
    }

    private List<RelatesToRelation> createRelations(List<XLSXHeader> headers, String sourceSystem, String tableName) {

        List<RelatesToRelation> result = new ArrayList<>();

        Map<String, DbRelatesToRelation> res = new HashMap<>();
        for (XLSXHeader xlsxHeader : headers) {
            if (!StringUtils.startsWith(xlsxHeader.getSystemHeader(), "RELATION.")) {
                continue;
            }
            String name = StringUtils.substringBetween(xlsxHeader.getSystemHeader(), ".", ".");
            if (!res.containsKey(name)) {
                res.put(name, new DbRelatesToRelation());
            }
            DbRelatesToRelation relatesToRelation = res.get(name);
            relatesToRelation.setRelation(name);
            relatesToRelation.setToSourceSystem(sourceSystem);
            relatesToRelation.setVersionRange(createVersionRange());
            DbSystemKey systemKeyTo = new DbSystemKey();
            systemKeyTo.setAlias(
                    "_" + MURMUR.hashString(String.join(".", "RELATION", name, "etalonId"), Charsets.UTF_8).toString());
            systemKeyTo.setColumn(
                    "_" + MURMUR.hashString(String.join(".", "RELATION", name, "etalonId"), Charsets.UTF_8).toString());
            relatesToRelation.setToSystemKey(systemKeyTo);
            DbSystemKey systemKeyFrom = new DbSystemKey();
            systemKeyFrom.setAlias("_" + MURMUR.hashString(ID, Charsets.UTF_8).toString());
            systemKeyFrom.setColumn("_" + MURMUR.hashString(ID, Charsets.UTF_8).toString());
            relatesToRelation.setFromSystemKey(systemKeyFrom);

            DbNaturalKey naturalKeyFrom = new DbNaturalKey();
            naturalKeyFrom.setAlias("_" + MURMUR.hashString(EXTERNAL_ID, Charsets.UTF_8).toString());
            naturalKeyFrom.setColumn("_" + MURMUR.hashString(EXTERNAL_ID, Charsets.UTF_8).toString());
            naturalKeyFrom.setType(String.class.getCanonicalName());
            relatesToRelation.setFromNaturalKey(naturalKeyFrom);

            relatesToRelation.setTables(Collections.singletonList(tableName));
            if (!StringUtils.endsWithAny(xlsxHeader.getSystemHeader(), ".name", ".etalonId")) {
                relatesToRelation.getFields().add(createField(xlsxHeader));
            }
        }
        res.forEach((k, v) -> result.add(v));
        return result;
    }

    /**
     * Creates the field.
     *
     * @param source the source
     * @return the exchange field
     */
    private ExchangeField createField(XLSXHeader source) {
        if (source == null) {
            return null;
        }
        DbExchangeField target = new DbExchangeField();
        String columnName = "_" + MURMUR.hashString(source.getSystemHeader(), Charsets.UTF_8).toString();
        target.setAlias(columnName);
        target.setColumn(columnName);
        target.setType(source.getAttributeHolder() == null // classifier
                ? convertType(source.getTypeHeader())
                : source.getAttributeHolder().isArray()
                ? String.class.getCanonicalName()
                : convertType(source.getTypeHeader()));
        if (source.isRel()) {
            target.setName(StringUtils.substringAfterLast(source.getSystemHeader(), "."));
        } else {
            target.setName(StringUtils.contains(source.getSystemHeader(), ".")
                    ? StringUtils.substringAfter(source.getSystemHeader(), ".") : source.getSystemHeader());
        }
        int level = ModelUtils.getAttributeLevel(target.getName());
        List<ComplexAttributeExpansion> expansions = new ArrayList<>();
        for (int i = 0; i <= level; i++) {
            ComplexAttributeExpansion complexAttributeExpansion = new ComplexAttributeExpansion();
            complexAttributeExpansion.setExpand(false);
            complexAttributeExpansion.setLevel(i);
            complexAttributeExpansion.setIndex(0);
            expansions.add(complexAttributeExpansion);
        }
        target.setExpansions(expansions);
        return target;
    }

    /**
     * Creates the classifier mappings.
     *
     * @param headers the headers
     * @return the list
     */
    private List<ClassifierMapping> createClassifierMappings(List<XLSXHeader> headers) {
        List<ClassifierMapping> result = new ArrayList<>();
        headers.stream().filter(h -> h.getType() == TYPE.CLASSIFIER_NODE).forEach(h -> result.add(
                new ClassifierMapping()
                        .withNodeId(createField(h))
                        .withVersionRange(createClassifierVersionRange(h))
                        .withFields(createClassifierAttrs(headers, h))));

        return result;
    }

    private VersionRange createClassifierVersionRange(XLSXHeader clsf) {

        VersionRange versionRange = new VersionRange();
        versionRange.setNormalizeFrom(true);
        versionRange.setNormalizeTo(true);

        DbExchangeField active = new DbExchangeField();
        active.setAlias("_" + MURMUR.hashString(clsf.getSystemHeader() + SYSTEM_PATH_DELIMITER + IS_ACTIVE, Charsets.UTF_8));
        active.setType(Boolean.class.getCanonicalName());
        active.setColumn("_" + MURMUR.hashString(clsf.getSystemHeader() + SYSTEM_PATH_DELIMITER + IS_ACTIVE, Charsets.UTF_8));
        versionRange.setIsActive(active);

        return versionRange;
    }

    /**
     * Creates the classifier attrs.
     *
     * @param headers the headers
     * @param classifierHeader the classifier header
     * @return the list
     */
    private List<ExchangeField> createClassifierAttrs(List<XLSXHeader> headers, XLSXHeader classifierHeader) {
        List<ExchangeField> result = new ArrayList<>();
        headers.stream()
                .filter(h -> (h.getType() == TYPE.CLASSIFIER_ATTRIBUTE
                        && StringUtils.startsWith(h.getSystemHeader(), classifierHeader.getSystemHeader())))
                .forEach(h -> result.add(createField(h)));
        return result;
    }

    /**
     * Creates the version range.
     *
     * @return the version range
     */
    private VersionRange createVersionRange() {
        VersionRange versionRange = new VersionRange();
        versionRange.setNormalizeFrom(true);
        versionRange.setNormalizeTo(true);

        DbExchangeField validFrom = new DbExchangeField();
        validFrom.setAlias("_" + MURMUR.hashString(FROM, Charsets.UTF_8));
        validFrom.setType(java.sql.Date.class.getCanonicalName());
        validFrom.setColumn("_" + MURMUR.hashString(FROM, Charsets.UTF_8));
        versionRange.setValidFrom(validFrom);

        DbExchangeField validTo = new DbExchangeField();
        validTo.setAlias("_" + MURMUR.hashString(TO, Charsets.UTF_8));
        validTo.setType(java.sql.Date.class.getCanonicalName());
        validTo.setColumn("_" + MURMUR.hashString(TO, Charsets.UTF_8));
        versionRange.setValidTo(validTo);

        DbExchangeField active = new DbExchangeField();
        active.setAlias("_" + MURMUR.hashString(IS_ACTIVE, Charsets.UTF_8));
        active.setType(Boolean.class.getCanonicalName());
        active.setColumn("_" + MURMUR.hashString(IS_ACTIVE, Charsets.UTF_8));
        versionRange.setIsActive(active);

        return versionRange;
    }

    /**
     * Convert type.
     *
     * @param dataType the data type
     * @return the string
     */
    private String convertType(SimpleDataType dataType) {
        if (dataType == null) {
            return String.class.getCanonicalName();
        }
        String result;
        switch (dataType) {
            case BOOLEAN:
                result = Boolean.class.getCanonicalName();
                break;
            case TIME:
                result = Time.class.getCanonicalName();
                break;
            case TIMESTAMP:
                result = Timestamp.class.getCanonicalName();
                break;
            case DATE:
                result = java.sql.Date.class.getCanonicalName();
                break;
            case INTEGER:
                result = Long.class.getCanonicalName();
                break;
            case MEASURED:
            case NUMBER:
                result = Double.class.getCanonicalName();
                break;
            case STRING:
            default:
                result = String.class.getCanonicalName();
                break;
        }
        return result;
    }

    @Override
    public void subscribe(Flux<Map<String, Optional<? extends Serializable>>> updates) {
        final String importXlsxBatchSizeKey = UnidataConfigurationProperty.IMPORT_XLSX_BATCH_SIZE.getKey();
        final String importXlsxThreadsPoolSizeKey = UnidataConfigurationProperty.IMPORT_XLSX_THREADS_POOL_SIZE.getKey();
        updates
                .filter(values ->
                        values.containsKey(importXlsxBatchSizeKey) && values.get(importXlsxBatchSizeKey).isPresent()
                )
                .map(values -> (Integer) values.get(importXlsxBatchSizeKey).get())
                .subscribe(value -> {
                    if (value > 0) {
                        asyncSaveDataBatchSize = value;
                    }
                });

        updates
                .filter(values ->
                        values.containsKey(importXlsxThreadsPoolSizeKey) && values.get(importXlsxThreadsPoolSizeKey).isPresent()
                )
                .map(values -> (Integer) values.get(importXlsxThreadsPoolSizeKey).get())
                .subscribe(value -> {
                    if (value > 0 && value <= Runtime.getRuntime().availableProcessors() * 2) {
                        saveBatchProcessors = value;
                    }
                });
    }

    private static class RowEntry {
        private String id;
        private String externalId;
        private Date to;
        private Date from;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getExternalId() {
            return externalId;
        }

        public void setExternalId(String externalId) {
            this.externalId = externalId;
        }

        public Date getTo() {
            return to;
        }

        public void setTo(Date to) {
            this.to = to;
        }

        public Date getFrom() {
            return from;
        }

        public void setFrom(Date from) {
            this.from = from;
        }

        public String generateImportId() {
            if (StringUtils.isBlank(id) && StringUtils.isBlank(externalId)) {
                return IdUtils.v1String() + "_" + IdUtils.v4String();
            }
            return (StringUtils.isNotBlank(id) ? id : "") + "_" + (StringUtils.isNotBlank(externalId) ? externalId : "");
        }

    }
}

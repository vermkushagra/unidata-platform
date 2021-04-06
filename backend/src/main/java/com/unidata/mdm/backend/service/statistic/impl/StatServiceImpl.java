package com.unidata.mdm.backend.service.statistic.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.unidata.mdm.backend.api.rest.dto.ErrorInfo;
import com.unidata.mdm.backend.api.rest.dto.table.AddressedTableCell;
import com.unidata.mdm.backend.api.rest.dto.table.NameTableCell;
import com.unidata.mdm.backend.api.rest.dto.table.SearchableTable;
import com.unidata.mdm.backend.common.context.AggregationRequestContext;
import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.NestedAggregationRequestContext;
import com.unidata.mdm.backend.common.context.SaveLargeObjectRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.StatisticRequestContext;
import com.unidata.mdm.backend.common.context.TermsAggregationRequestContext;
import com.unidata.mdm.backend.common.context.UpsertUserEventRequestContext;
import com.unidata.mdm.backend.common.dto.AggregationResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.security.UserEventDTO;
import com.unidata.mdm.backend.common.dto.statistic.ErrorsStatDTO;
import com.unidata.mdm.backend.common.dto.statistic.StatisticDTO;
import com.unidata.mdm.backend.common.dto.statistic.StatisticResponseDTO;
import com.unidata.mdm.backend.common.dto.statistic.TimeSerieDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.integration.auth.Right;
import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchRequestType;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.statistic.StatisticType;
import com.unidata.mdm.backend.common.types.SeverityType;
import com.unidata.mdm.backend.dao.StatisticDao;
import com.unidata.mdm.backend.service.cleanse.DQUtils;
import com.unidata.mdm.backend.common.service.ClusterService;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.search.util.DqHeaderField;
import com.unidata.mdm.backend.service.search.util.RecordHeaderField;
import com.unidata.mdm.backend.service.security.UserService;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.service.statistic.StatErrorsCacheLoader;
import com.unidata.mdm.backend.service.statistic.StatServiceExt;
import com.unidata.mdm.backend.service.statistic.StatisticCacheLoader;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.backend.util.constants.UserMessageConstants;
import com.unidata.mdm.meta.AbstractEntityDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.SimpleDataType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.unidata.mdm.backend.common.context.SearchRequestContext.forEtalonData;
import static com.unidata.mdm.backend.common.search.FormField.FormType.POSITIVE;
import static com.unidata.mdm.backend.service.search.util.RecordHeaderField.FIELD_FROM;
import static com.unidata.mdm.backend.service.search.util.RecordHeaderField.FIELD_TO;

/**
 * The Class StatisticService.
 */
@Component
public class StatServiceImpl implements StatServiceExt {

    private static final Integer MAX_ERRORS_AGGREGATION_SIZE = 10;

    private static final Logger LOGGER = LoggerFactory.getLogger(StatServiceImpl.class);

    private static final String STATISTIC_EXPORT_FILE_NAME = "export_statistic_";

    private static final String STATISTIC_EXPORT_SHEET_NAME = "data_statistic";

    private static final String STATISTIC_ERRORS_EXPORT_SHEET_NAME = "errors_statistic";

    private static final  String STATISTIC_TITLE_PREFIX = "app.statistic.export.title.";

    private static final  String STATISTIC_ERRORS_TITLE_PREFIX = "app.statistic.export.error.title.";

    private static final String STATISTIC_ENTITY_NAME = "app.statistic.export.entity.name";

    private static final String STATISTIC_DISPLAYABLE_ENTITY_NAME = "app.statistic.export.entity.name.displayable";

    // statistic by errors
    private static final String SEVERITY = "severity";
    private static final String DISPLAY_SEVERITY = "app.statistic.severity.name.display";
    private static final String SEARCH_SEVERITY = DqHeaderField.SEVERITY.getField();
    private static final String CATEGORY = "category";
    private static final String DISPLAY_CATEGORY = "app.statistic.category.name.display";
    private static final String SEARCH_CATEGORY = DqHeaderField.CATEGORY.getField();
    private static final String TOTAL = "Total";
    private static final String DISPLAY_TOTAL ="app.statistic.total.name.display";
    private static final String DISPLAY_SYSTEM_CATEGORY_NAME = "app.statistic.systemCategory.name.display";

    private static Map<StatisticType, Integer> statisticIndex = new LinkedHashMap<>();
    static {
        statisticIndex.put(StatisticType.TOTAL, 2);
        statisticIndex.put(StatisticType.NEW, 3);
        statisticIndex.put(StatisticType.UPDATED, 4);
        statisticIndex.put(StatisticType.MERGED, 5);
        statisticIndex.put(StatisticType.DUPLICATES, 6);
        statisticIndex.put(StatisticType.ERRORS, 7);

    }

    /**
     * Cache time to live.
     */
    @Value(value = "${unidata.stat.cache.ttl:10}")
    private int cacheTTL;
    /**
     * The stat cache.
     */
    private LoadingCache<StatisticRequestContext, StatisticResponseDTO> statCache;
    /**
     * The errors stat cache.
     */
    private LoadingCache<ErrorsStatDTO, ErrorsStatDTO> statErrorCache;
    /**
     * The statistic dao.
     */
    @Autowired
    private StatisticDao statisticDao;
    /**
     * Search service.
     */
    @Autowired
    private SearchService searchService;
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
    /**
     * Cluster Service
     */
    @Autowired
    private ClusterService clusterService;
    /**
     * User service
     */
    @Autowired
    private UserService userService;
    /**
     * The data record service.
     */
    @Autowired
    private DataRecordsService dataRecordService;

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.statistic.IStatService#getStatistic(com.unidata.mdm.backend.service.statistic.dto.StatisticRequestContext)
     */
    @Override
    public StatisticResponseDTO getStatistic(StatisticRequestContext request) throws Exception {
        String entityName = request.getEntityName();
        boolean isRealEntity = metaModelService.isEntity(entityName) || metaModelService.isLookupEntity(entityName);
        if (!isRealEntity) {
            throw new DataProcessingException("Entity not exist", ExceptionId.EX_META_NOT_FOUND, entityName);
        }
        return statCache.get(request);
    }


    @Override
    @Transactional
    public void persistStatistic(Date fromDate, Date toDate) {
        List<String> names = new ArrayList<>();
        names.addAll(metaModelService.getEntitiesList().stream().map(EntityDef::getName).collect(Collectors.toList()));
        names.addAll(metaModelService.getLookupEntitiesList().stream().map(LookupEntityDef::getName)
                .collect(Collectors.toList()));

        for (String entityName : names) {
            List<StatisticDTO> statistics = gatherStatisticForDates(fromDate, toDate, entityName);
            statisticDao.persistSlice(statistics, entityName);
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.statistic.IStatService#gatherStatisticForDates(java.util.Date, java.util.Date, java.lang.String)
     */
    @Override
    public List<StatisticDTO> gatherStatisticForDates(Date fromDate, Date toDate, @Nonnull String entityName) {
        List<StatisticDTO> statistics = new ArrayList<>();
        LocalDateTime startDateDBSearch = fromDate == null ? null
                : LocalDateTime.ofInstant(fromDate.toInstant(), ZoneId.systemDefault());
        LocalDateTime endDateDbSearch = toDate == null ? null
                : LocalDateTime.ofInstant(toDate.toInstant(), ZoneId.systemDefault());
        for (StatisticType statisticType : StatisticType.values()) {
            List<TimeSerieDTO> series = new ArrayList<>();
            StatisticDTO statistic = new StatisticDTO();
            statistic.setType(statisticType);
            int count = 0;
            if (StatisticType.ERRORS.equals(statisticType)) {
                SearchRequestContext ctx = forEtalonData(entityName)
                        .asOf(new Date())
                        .runExits(false)
                        .build();
                count = (int) searchService.countErrorRecords(ctx);
            } else if (StatisticType.MERGED.equals(statisticType)) {
                //todo
                count = statisticDao.countMerged(entityName, startDateDBSearch, endDateDbSearch);
            } else if (StatisticType.NEW.equals(statisticType)) {
                count = countNew(entityName, fromDate, toDate);
            } else if (StatisticType.TOTAL.equals(statisticType)) {
                count = countTotal(entityName, fromDate, toDate);
            } else if (StatisticType.UPDATED.equals(statisticType)) {
                count = countUpdated(entityName, fromDate, toDate);
            } else if (StatisticType.DUPLICATES == statisticType) {
                count = clusterService.getUniqueEtalonsCount(ClusterMetaData.builder()
                        .entityName(entityName)
                        .build()).intValue();
            }
            TimeSerieDTO serie = new TimeSerieDTO();
            serie.setTime(fromDate);
            serie.setValue(count);
            series.add(serie);
            statistic.setSeries(series);
            statistics.add(statistic);
        }
        return statistics;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.statistic.IStatService#getErrorsStat(java.lang.String, java.lang.String)
     */
    @Override
    public ErrorsStatDTO getErrorsStat(String entityName, String sourceSystemName) throws ExecutionException {
        ErrorsStatDTO key = new ErrorsStatDTO();
        key.setEntityName(entityName);
        key.setSourceSystemName(sourceSystemName);
        return statErrorCache.get(key);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.statistic.IStatService#gatherHistoricalStatistic(java.util.Date, java.util.Date, java.lang.String)
     */
    @Override
    public List<StatisticDTO> gatherHistoricalStatistic(Date startDate, Date endDate, String entityName) {
        return statisticDao.getSlice(startDate, endDate, entityName);
    }

    /**
     * get last available statistic for entity
     *
     * @param entityName entity name.
     * @return last available statistic slice
     */
    @Override
    public List<StatisticDTO> gatherLastAvailableStatistic(String entityName) {
        return statisticDao.getLastSlice(entityName);
    }

    /**
     * export statistic to user event (from excel format)
     */
    @Override
    public void exportStatistic() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Workbook wb = new XSSFWorkbook();
            convertStatisticToExcel(wb);
            convertErrorStatisticToExcel(wb);
            wb.write(outputStream);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            UpsertUserEventRequestContext uueCtx = new UpsertUserEventRequestContext.UpsertUserEventRequestContextBuilder()
                    .login(SecurityUtils.getCurrentUserName())
                    .type("STATISTIC_FULL_EXPORT")
                    .content(MessageUtils.getMessage(UserMessageConstants.STATISTIC_EXPORT_SUCCESS))
                    .build();
            UserEventDTO userEventDTO = userService.upsert(uueCtx);
            // save result and attach it to the early created user event
            SaveLargeObjectRequestContext slorCTX = new SaveLargeObjectRequestContext.SaveLargeObjectRequestContextBuilder()
                    .eventKey(userEventDTO.getId())
                    .mimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .binary(true)
                    .inputStream(inputStream)
                    .filename(STATISTIC_EXPORT_FILE_NAME
                            + DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd_HH-mm-ss")
                            + ".xlsx")
                    .build();
            dataRecordService.saveLargeObject(slorCTX);

        } catch (IOException e) {
            LOGGER.error("Statistic export finished with errors", e);
            UpsertUserEventRequestContext uueCtx = new UpsertUserEventRequestContext.UpsertUserEventRequestContextBuilder()
                    .login(SecurityUtils.getCurrentUserName())
                    .type("STATISTIC_FULL_EXPORT")
                    .content(MessageUtils.getMessage(UserMessageConstants.STATISTIC_EXPORT_UNSUCCESS))
                    .build();
            userService.upsert(uueCtx);
        }
    }
    @Override
    public SearchableTable getErrorStatisticAggregation(String entityName) {
        Date now = new Date();
        FormField fromFormField = FormField.range(SimpleDataType.TIMESTAMP, FIELD_FROM.getField(), POSITIVE, null, now);
        FormField toFromField = FormField.range(SimpleDataType.TIMESTAMP, FIELD_TO.getField(), POSITIVE, now, null);
        FormField published = FormField.strictValue(SimpleDataType.BOOLEAN, RecordHeaderField.FIELD_PUBLISHED.getField(),true);
        FormField active = FormField.strictValue(SimpleDataType.BOOLEAN, RecordHeaderField.FIELD_DELETED.getField(),false);
        FormFieldsGroup time = FormFieldsGroup.createAndGroup(fromFormField, toFromField, published, active);

        AggregationRequestContext top = NestedAggregationRequestContext.builder()
                .name(entityName)
                .path(DqHeaderField.getParentField())
                .subAggregation(TermsAggregationRequestContext.builder()
                        .name(SEVERITY)
                        .size(MAX_ERRORS_AGGREGATION_SIZE)
                        .path(DqHeaderField.SEVERITY.getField())
                        .subAggregation(TermsAggregationRequestContext.builder()
                                .name(CATEGORY)
                                .path(DqHeaderField.CATEGORY.getField())
                                .size(MAX_ERRORS_AGGREGATION_SIZE)
                                .build())
                        .build())
                .build();

        SearchRequestContext requestContext = SearchRequestContext.forEtalonData(entityName)
                .form(time)
                .count(0)
                .onlyQuery(true)
                .skipEtalonId(true)
                .aggregations(Collections.singletonList(top))
                .build();

        SearchResultDTO result = searchService.search(requestContext);
        AggregationResultDTO aggregationResultDTO = CollectionUtils.isNotEmpty(result.getAggregates())
                ? result.getAggregates().get(0)
                : new AggregationResultDTO(entityName, AggregationRequestContext.AggregationType.NESTED, -1, false);
        return toTable(aggregationResultDTO);
    }

    //we can't generalize this method!
    private SearchableTable toTable(AggregationResultDTO resultDTO) {
        SearchableTable table = new SearchableTable();
        table.setEntityName(resultDTO.getAggregationName());
        table.setColumnName(SEVERITY);
        table.setColumnSearchName(SEARCH_SEVERITY);
        table.setColumnDisplayName(MessageUtils.getMessage(DISPLAY_SEVERITY));
        table.setRowName(CATEGORY);
        table.setRowSearchName(SEARCH_CATEGORY);
        table.setRowDisplayName(MessageUtils.getMessage(DISPLAY_CATEGORY));

        if (resultDTO.getDocumentsCount() == 0) {
            return table;
        }

        AggregationResultDTO severity = resultDTO.getSubAggregations().get(SEVERITY).get(SEVERITY);
        Map<String, Long> rowTotals = new HashMap<>();
        for (String columnName : severity.getCountMap().keySet()) {
            AggregationResultDTO category = severity.getSubAggregations().get(columnName).get(CATEGORY);

            for (Map.Entry<String, Long> row : category.getCountMap().entrySet()) {
                String rowName = row.getKey();

                NameTableCell rowCell = rowName.equals(DQUtils.CATEGORY_SYSTEM)
                        ? new NameTableCell(rowName, MessageUtils.getMessage(DISPLAY_SYSTEM_CATEGORY_NAME))
                        : new NameTableCell(rowName);
                table.addRow(rowCell);

                AddressedTableCell addressedTableCell = new AddressedTableCell();
                addressedTableCell.setValue(row.getValue().toString());
                addressedTableCell.setDisplayValue(row.getValue().toString());
                addressedTableCell.setRow(rowName);
                addressedTableCell.setColumn(columnName);
                table.addCell(addressedTableCell);

                Long rowTotal = rowTotals.get(rowName);
                rowTotal = rowTotal == null ? 0: rowTotal;
                rowTotal = rowTotal + row.getValue();
                rowTotals.put(rowName, rowTotal);
            }

            Long columnTotal = severity.getCountMap().get(columnName);
            AddressedTableCell addressedTableCell = new AddressedTableCell();
            addressedTableCell.setValue(columnTotal.toString());
            addressedTableCell.setDisplayValue(columnTotal.toString());
            addressedTableCell.setRow(TOTAL);
            addressedTableCell.setColumn(columnName);
            table.addCell(addressedTableCell);
        }

        for(Map.Entry<String, Long> row :rowTotals.entrySet()){
            AddressedTableCell addressedTableCell = new AddressedTableCell();
            addressedTableCell.setValue(row.getValue().toString());
            addressedTableCell.setDisplayValue(row.getValue().toString());
            addressedTableCell.setRow(row.getKey());
            addressedTableCell.setColumn(TOTAL);
            table.addCell(addressedTableCell);
        }
        Arrays.stream(ErrorInfo.Severity.values())
                .map(sev -> new NameTableCell(sev.name(), sev.getDisplayName()))
                .forEach(table::addColumn);
        NameTableCell totalCell = new NameTableCell(TOTAL, MessageUtils.getMessage(DISPLAY_TOTAL));
        table.addRow(totalCell);
        table.addColumn(totalCell);
        return table;
    }

    private void convertStatisticToExcel(Workbook wb) {
        List<StatisticDTO> statistics = statisticDao.getLastSlice(null);
        Sheet sheet = wb.createSheet(STATISTIC_EXPORT_SHEET_NAME);
        Row headerRow = sheet.createRow(0);

        CellStyle headerCellStyle = wb.createCellStyle();
        headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerCellStyle.setBorderBottom(BorderStyle.THIN);
        headerCellStyle.setBorderTop(BorderStyle.THIN);
        headerCellStyle.setBorderLeft(BorderStyle.THIN);
        headerCellStyle.setBorderRight(BorderStyle.THIN);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellStyle(headerCellStyle);
        headerCell.setCellValue(MessageUtils.getMessage(STATISTIC_ENTITY_NAME));
        headerCell = headerRow.createCell(1);
        headerCell.setCellStyle(headerCellStyle);
        headerCell.setCellValue(MessageUtils.getMessage(STATISTIC_DISPLAYABLE_ENTITY_NAME));
        int headerIndex = 2;
        for (StatisticType type : statisticIndex.keySet()) {
            headerCell = headerRow.createCell(headerIndex++);
            headerCell.setCellValue(MessageUtils.getMessage(STATISTIC_TITLE_PREFIX + type.name().toLowerCase()));
            headerCell.setCellStyle(headerCellStyle);
        }

        if (CollectionUtils.isNotEmpty(statistics)) {
            int rowNum = 1;
            Map<String, List<StatisticDTO>> statisticsByName = statistics.stream()
                    .collect(Collectors.groupingBy(StatisticDTO::getEntityName));

            List<String> filteredResourceNames =
                    SecurityUtils.filterResourcesByRights(statisticsByName.keySet(), Right::isRead);

            for (Map.Entry<String, List<StatisticDTO>> statisticByName : statisticsByName.entrySet()) {
                if (!filteredResourceNames.contains(statisticByName.getKey())) {
                    continue;
                }
                AbstractEntityDef entityDef = metaModelService.getEntityByIdNoDeps(statisticByName.getKey());
                if(entityDef == null){
                    entityDef = metaModelService.getLookupEntityById(statisticByName.getKey());
                }

                if(entityDef == null){
                    continue;
                }

                Row row = sheet.createRow(rowNum++);
                for (StatisticDTO statistic : statisticByName.getValue()) {
                    Cell cell = row.createCell(0);
                    cell.setCellValue(statisticByName.getKey());

                    // fill display entity name for entity
                    cell = row.createCell(1);
                    cell.setCellValue(entityDef.getDisplayName());

                    Integer typeNumber = statisticIndex.get(statistic.getType());
                    if(typeNumber == null){
                        continue;
                    }

                    cell = row.createCell(typeNumber);
                    if (CollectionUtils.isNotEmpty(statistic.getSeries())) {
                        cell.setCellValue(statistic.getSeries().get(0).getValue());
                    } else {
                        cell.setCellValue(0);
                    }
                }
            }
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        for (int i = 2; i <= statisticIndex.size(); i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void convertErrorStatisticToExcel(Workbook wb){
        Sheet sheet = wb.createSheet(STATISTIC_ERRORS_EXPORT_SHEET_NAME);
        Row headerRow = sheet.createRow(0);
        CellStyle headerCellStyle = wb.createCellStyle();
        headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerCellStyle.setBorderBottom(BorderStyle.THIN);
        headerCellStyle.setBorderTop(BorderStyle.THIN);
        headerCellStyle.setBorderLeft(BorderStyle.THIN);
        headerCellStyle.setBorderRight(BorderStyle.THIN);

        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue(MessageUtils.getMessage(STATISTIC_ENTITY_NAME));
        headerCell.setCellStyle(headerCellStyle);

        headerCell = headerRow.createCell(1);
        headerCell.setCellValue(MessageUtils.getMessage(STATISTIC_DISPLAYABLE_ENTITY_NAME));
        headerCell.setCellStyle(headerCellStyle);

        headerCell = headerRow.createCell(2);
        headerCell.setCellValue(MessageUtils.getMessage(
                STATISTIC_ERRORS_TITLE_PREFIX + SeverityType.CRITICAL.name().toLowerCase()));
        headerCell.setCellStyle(headerCellStyle);

        headerCell = headerRow.createCell(3);
        headerCell.setCellValue(MessageUtils.getMessage(
                STATISTIC_ERRORS_TITLE_PREFIX + SeverityType.HIGH.name().toLowerCase()));
        headerCell.setCellStyle(headerCellStyle);

        headerCell = headerRow.createCell(4);
        headerCell.setCellValue(MessageUtils.getMessage(
                STATISTIC_ERRORS_TITLE_PREFIX + SeverityType.NORMAL.name().toLowerCase()));
        headerCell.setCellStyle(headerCellStyle);

        headerCell = headerRow.createCell(5);
        headerCell.setCellValue(MessageUtils.getMessage(
                STATISTIC_ERRORS_TITLE_PREFIX + SeverityType.LOW.name().toLowerCase()));
        headerCell.setCellStyle(headerCellStyle);

        int rowNum = 1;

        List<AbstractEntityDef> entityDefs = new ArrayList<>();
        entityDefs.addAll(metaModelService.getEntitiesList());
        entityDefs.addAll(metaModelService.getLookupEntitiesList());

        if(CollectionUtils.isNotEmpty(entityDefs)){
            for(AbstractEntityDef entityDef : entityDefs){
                Right right = SecurityUtils.getRightsForResource(entityDef.getName());
                if(right == null || !SecurityUtils.getRightsForResource(entityDef.getName()).isRead()){
                    continue;
                }
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entityDef.getName());
                row.createCell(1).setCellValue(entityDef.getDisplayName());
                try {
                    ErrorsStatDTO errorStat = getErrorsStat(entityDef.getName(), null);
                    row.createCell(2).setCellValue(errorStat.getData().get(SeverityType.CRITICAL));
                    row.createCell(3).setCellValue(errorStat.getData().get(SeverityType.HIGH));
                    row.createCell(4).setCellValue(errorStat.getData().get(SeverityType.NORMAL));
                    row.createCell(5).setCellValue(errorStat.getData().get(SeverityType.LOW));
                } catch (ExecutionException e) {
                    LOGGER.error("Can't extract error statistic", e);
                }

            }
        }
        for (int i = 0; i <= 5; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.statistic.IStatService#getCacheTTL()
     */
    @Override
    public int getCacheTTL() {
        return cacheTTL;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.statistic.IStatService#setCacheTTL(int)
     */
    @Override
    public void setCacheTTL(int cacheTTL) {
        this.cacheTTL = cacheTTL;
    }

    /**
     * Inits the cache.
     */
    @Override
    public void afterContextRefresh() {
        this.statCache = CacheBuilder.newBuilder().expireAfterWrite(cacheTTL, TimeUnit.SECONDS)
                .build(new StatisticCacheLoader(this));
        this.statErrorCache = CacheBuilder.newBuilder().expireAfterWrite(cacheTTL, TimeUnit.SECONDS)
                .build(new StatErrorsCacheLoader(searchService));
    }

    private int countTotal(String entityName, Date fromDate, Date toDate) {

        FormField createdBefore = FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_CREATED_AT.getField(), null, toDate);
        FormField published = FormField.strictValue(SimpleDataType.BOOLEAN, RecordHeaderField.FIELD_PUBLISHED.getField(), true);
        FormField active = FormField.strictValue(SimpleDataType.BOOLEAN, RecordHeaderField.FIELD_DELETED.getField(), false);
        Date now = new Date();
        FormField from = FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_FROM.getField(), null, now);
        FormField to = FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_TO.getField(), now, null);

        SearchRequestContext mainRequest = SearchRequestContext.forEtalon(EntitySearchType.ETALON, entityName)
                .onlyQuery(true)
                .runExits(false)
                .fetchAll(true)
                .build();

        SearchRequestContext subRequest = SearchRequestContext.forEtalonData(entityName)
                .search(SearchRequestType.TERM)
                .form(FormFieldsGroup.createAndGroup(Arrays.asList(createdBefore, active, published, from, to)))
                .countOnly(false)
                .onlyQuery(true)
                .totalCount(true)
                .runExits(false)
                .build();

        ComplexSearchRequestContext ctx = ComplexSearchRequestContext.hierarchical(mainRequest, subRequest);

        SearchResultDTO searchResult = searchService.search(ctx).get(mainRequest);
        Long count = searchResult != null ? searchResult.getTotalCount() : 0L;
        return count.intValue();

    }

    private int countNew(String entityName, Date fromDate, Date toDate) {
        FormField createdBefore = FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_CREATED_AT.getField(), fromDate, toDate);
        FormField published = FormField.strictValue(SimpleDataType.BOOLEAN, RecordHeaderField.FIELD_PUBLISHED.getField(), true);
        FormField active = FormField.strictValue(SimpleDataType.BOOLEAN, RecordHeaderField.FIELD_DELETED.getField(), false);

        SearchRequestContext mainRequest = SearchRequestContext.forEtalon(EntitySearchType.ETALON, entityName)
                .onlyQuery(true)
                .runExits(false)
                .fetchAll(true)
                .build();

        SearchRequestContext subRequest = SearchRequestContext.forEtalonData(entityName)
                .search(SearchRequestType.TERM)
                .form(FormFieldsGroup.createAndGroup(Arrays.asList(createdBefore, active, published)))
                .countOnly(false)
                .onlyQuery(true)
                .totalCount(true)
                .runExits(false)
                .build();

        ComplexSearchRequestContext ctx = ComplexSearchRequestContext.hierarchical(mainRequest, subRequest);

        SearchResultDTO searchResult = searchService.search(ctx).get(mainRequest);
        Long count = searchResult != null ? searchResult.getTotalCount() : 0L;
        return count.intValue();
       }

    private int countUpdated(String entityName, Date fromDate, Date toDate) {
        FormField createdBefore = FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_UPDATED_AT.getField(), fromDate, toDate);
        FormField published = FormField.strictValue(SimpleDataType.BOOLEAN, RecordHeaderField.FIELD_PUBLISHED.getField(), true);
        FormField active = FormField.strictValue(SimpleDataType.BOOLEAN, RecordHeaderField.FIELD_DELETED.getField(), false);

        SearchRequestContext mainRequest = SearchRequestContext.forEtalon(EntitySearchType.ETALON, entityName)
                .onlyQuery(true)
                .runExits(false)
                .fetchAll(true)
                .build();

        SearchRequestContext subRequest = SearchRequestContext.forEtalonData(entityName)
                .search(SearchRequestType.TERM)
                .form(FormFieldsGroup.createAndGroup(Arrays.asList(createdBefore, active, published)))
                .countOnly(false)
                .onlyQuery(true)
                .totalCount(true)
                .runExits(false)
                .build();

        ComplexSearchRequestContext ctx = ComplexSearchRequestContext.hierarchical(mainRequest, subRequest);

        SearchResultDTO searchResult = searchService.search(ctx).get(mainRequest);
        Long count = searchResult != null ? searchResult.getTotalCount() : 0L;
        return count.intValue();
    }

}

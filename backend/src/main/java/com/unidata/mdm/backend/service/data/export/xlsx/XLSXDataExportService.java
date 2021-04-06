package com.unidata.mdm.backend.service.data.export.xlsx;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.unidata.mdm.backend.common.ConvertUtils;
import com.unidata.mdm.backend.common.context.GetMultipleRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.GetClassifierDTO;
import com.unidata.mdm.backend.common.dto.GetRecordsDTO;
import com.unidata.mdm.backend.common.dto.GetRelationDTO;
import com.unidata.mdm.backend.common.dto.RelationStateDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.search.SearchRequestType;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.Attribute.AttributeType;
import com.unidata.mdm.backend.common.types.BinaryLargeValue;
import com.unidata.mdm.backend.common.types.CharacterLargeValue;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.ComplexAttribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.RelationType;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.DateSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.export.DataExportService;
import com.unidata.mdm.backend.service.data.export.ExportUtils;
import com.unidata.mdm.backend.service.data.util.AttributeUtils;
import com.unidata.mdm.backend.service.data.xlsximport.XLSXHeader;
import com.unidata.mdm.backend.service.search.util.RecordHeaderField;
import com.unidata.mdm.meta.ArrayAttributeDef;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.SimpleAttributeDef;

/**
 * The Class DataExportService.
 *
 * @author ilya.bykov
 */
@Qualifier(value = "xlsxExportService")
@Component(value = "xlsxExportService")
public class XLSXDataExportService extends XLSXProcessor implements DataExportService {

    /**
     * The data records service.
     */
    @Autowired
    private DataRecordsService dataRecordsService;

    /** The cr component. */
    @Autowired
    private CommonRecordsComponent crComponent;
    /** The search service. */
    @Autowired
    private SearchService searchService;
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(XLSXDataExportService.class);
    /** The display names. */
    private LoadingCache<DNKey, String> displayNames = CacheBuilder.newBuilder().expireAfterWrite(100, TimeUnit.SECONDS)
            .build(new DisplayNames());

    /** The main displayable. */
    private LoadingCache<String, List<String>> mainDisplayable = CacheBuilder.newBuilder()
            .expireAfterWrite(100, TimeUnit.SECONDS).build(new MainDisplayable());

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.data.DataExportService#exportDataAsXSLX(
     * com.unidata.mdm.backend.common.context.GetMultipleRequestContext)
     */
    @Override
    public ByteArrayOutputStream exportData(GetMultipleRequestContext ctx) {

        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
             Workbook wb = createTemplateWorkbook(ctx.getEntityName(), false)) {

            GetRecordsDTO records = dataRecordsService.getRecords(ctx);
            List<XLSXHeader> headers = createHeaders(ctx.getEntityName(), false);
            fillTemplateWbWithData(wb, records, ctx.getEntityName(), headers);
            wb.write(output);
            return output;

        } catch (IOException e) {
            throw new DataProcessingException("Unable to export data for {} to XLS.",
                    ExceptionId.EX_DATA_EXPORT_UNABLE_TO_EXPORT_XLS, ctx.getEntityName());
        }
    }

    /**
     * Fill template wb with data. Structure must be like: </b>
     * <ul>
     * <li>ID</li>
     * <li>Simple attributes</li>
     * <li>Classifiers(if any)</li>
     * <li>Complex attributes</li>
     * <li>Relations</li>
     *
     * </ul>
     *
     * @param wb
     *            the wb
     * @param records
     *            the records
     * @param entityName
     *            the entity name
     * @param headers
     *            the headers
     */
    private void fillTemplateWbWithData(Workbook wb, GetRecordsDTO records, String entityName,
            List<XLSXHeader> headers) {

        List<LinkedHashMap<String, Attribute>> list = denormalizeRecords(records);
        CellStyle cellStyleData = wb.createCellStyle();
        cellStyleData.setWrapText(true);
        Sheet sheet = wb.getSheet(entityName);

        for (int i = SYSTEM_ROWS_QTY; i < list.size() + SYSTEM_ROWS_QTY; i++) {
            Row row = sheet.createRow(i);
            int columnIdx = 0;
            Map<String, Attribute> dataMap = list.get(i - SYSTEM_ROWS_QTY);
            for (XLSXHeader header : headers) {

                Attribute data = dataMap.get(header.getSystemHeader());
                Cell cell = row.createCell(columnIdx);
                cell.setCellStyle(cellStyleData);
                fillCellWithData(data, cell, header);
                columnIdx++;
            }
        }

    }

    /**
     * Fill cell with data.
     * @param data
     *            the data
     * @param cell
     *            the cell
     * @param header the header
     */
    private void fillCellWithData(Attribute data, Cell cell, XLSXHeader header) {

        if (Objects.isNull(data)) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(StringUtils.EMPTY);
            return;
        }

        switch (data.getAttributeType()) {
        case SIMPLE:
            fillCellWithSimpleAttributeData(data.narrow(), cell, header);
            break;
        case CODE:
            fillCellWithCodeAttributeData(data.narrow(), cell, header);
            break;
        case ARRAY:
            fillCellWithArrayAttributeData(data.narrow(), cell, header);
            break;
        default:
            // TODO throw for complex attribute. Programming error.
            break;
        }


    }

    /**
     * Fills cell with simple attribute data.
     * @param attr the attribute
     * @param cell the cell
     * @param header the header
     */
    private void fillCellWithSimpleAttributeData(SimpleAttribute<?> attr, Cell cell, XLSXHeader header) {

        if (Objects.isNull(attr.getValue())) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(StringUtils.EMPTY);
            return;
        }

        switch (attr.getDataType()) {
        case STRING:
        case ENUM:
        case LINK:
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue((String) attr.castValue());
            break;
        case BLOB:
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(((BinaryLargeValue) attr.getValue()).getFileName());
            break;
        case BOOLEAN:
            cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
            cell.setCellValue((Boolean) attr.getValue());
            break;
        case CLOB:
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(((CharacterLargeValue) attr.getValue()).getFileName());
            break;
        case DATE:
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(EXCEL_DATE_FORMAT.format(attr.castValue()));
            break;
        case INTEGER:
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
            cell.setCellValue((Long) attr.getValue());
            break;
        case NUMBER:
        case MEASURED:
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
            cell.setCellValue((Double) attr.getValue());
            break;
        case TIME:
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(EXCEL_TIME_FORMAT.format(attr.castValue()));
            break;
        case TIMESTAMP:
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(EXCEL_DATE_TIME_FORMAT.format(attr.castValue()));
            break;
        default:
            break;
        }
    }

    /**
     * Fills cell with code attribute data.
     * @param attr the attribute
     * @param cell the cell
     * @param header the header
     */
    private void fillCellWithCodeAttributeData(CodeAttribute<?> attr, Cell cell, XLSXHeader header) {

        if (Objects.isNull(attr.getValue())) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(StringUtils.EMPTY);
            return;
        }

        switch (attr.getDataType()) {
        case STRING:
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue((String) attr.castValue());
            break;
        case INTEGER:
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
            cell.setCellValue((Long) attr.getValue());
            break;
        default:
            break;
        }
    }

    /**
     * Fills cell with simple attribute data.
     * @param attr the attribute
     * @param cell the cell
     * @param header the header
     */
    private void fillCellWithArrayAttributeData(ArrayAttribute<?> attr, Cell cell, XLSXHeader header) {

        if (attr.isEmpty()) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(StringUtils.EMPTY);
            return;
        }

        ArrayAttributeDef aad = header.getAttributeHolder().narrow();
        switch (attr.getDataType()) {
        case STRING:
        case INTEGER:
        case NUMBER:
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(AttributeUtils.joinArrayValues(attr.toArray(), aad.getExchangeSeparator()));
            break;
        case DATE:
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(AttributeUtils.joinArrayValues(
                    Arrays.stream(attr.toArray()).map(v -> EXCEL_DATE_FORMAT.format((LocalDate) v)).toArray(),
                    aad.getExchangeSeparator()));
            break;
        case TIME:
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(AttributeUtils.joinArrayValues(
                    Arrays.stream(attr.toArray()).map(v -> EXCEL_TIME_FORMAT.format((LocalTime) v)).toArray(),
                    aad.getExchangeSeparator()));
            break;
        case TIMESTAMP:
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(AttributeUtils.joinArrayValues(
                    Arrays.stream(attr.toArray()).map(v -> EXCEL_DATE_TIME_FORMAT.format((LocalDateTime) v)).toArray(),
                    aad.getExchangeSeparator()));
            break;
        default:
            break;
        }
    }

    /**
     * Denormalize goldens.
     *
     * @param records
     *            the records
     * @return the list
     */
    private List<LinkedHashMap<String, Attribute>> denormalizeRecords(GetRecordsDTO records) {

        List<LinkedHashMap<String, Attribute>> result = new LinkedList<>();
        for (EtalonRecord record : records.getEtalons()) {
            denormalizeRecord(record,
                    CollectionUtils.isEmpty(records.getRelations()) ? null : records.getRelations().get(record),
                    CollectionUtils.isEmpty(records.getClassifiers()) ? null : records.getClassifiers().get(record),
                            result);
        }

        return result;
    }

    /**
     * Denormalize golden.
     *
     * @param record
     *            the record
     * @param relations relations map
     * @param classifiers classifiers map
     * @param result
     *            the result
     * @return the list
     */
    @SuppressWarnings("unchecked")
    private List<LinkedHashMap<String, Attribute>> denormalizeRecord(EtalonRecord record,
            Map<RelationStateDTO, List<GetRelationDTO>> relations,
            Map<String, List<GetClassifierDTO>> classifiers,
            List<LinkedHashMap<String, Attribute>> result) {

        if (record == null) {
            return result;
        }

        LinkedHashMap<String, Attribute> row = new LinkedHashMap<>();

        // for each row create default column 'ID'
        SimpleAttribute<?> id = new StringSimpleAttributeImpl(ID, record.getInfoSection().getEtalonKey().getId());

        row.put(ID, id);
        // for each row create default column 'EXTERNAL_ID'
        // for export it is empty
        SimpleAttribute<?> externalId = new StringSimpleAttributeImpl(EXTERNAL_ID, "");

        row.put(EXTERNAL_ID, externalId);
        // for each row create default column 'ORIGIN_KEYS'
        StringBuilder originKeysString = new StringBuilder("");
        RecordKeys keys = crComponent.identify(record.getInfoSection().getEtalonKey());
        boolean notFirst = false;
        for (OriginKey originKey : keys.getNotEnrichedSupplementaryKeys()) {
            if (notFirst) {
                originKeysString.append("\n");
            }
            originKeysString.append(originKey.getSourceSystem())
                    .append(" | ")
                    .append(originKey.getExternalId());
            notFirst = true;
        }
        SimpleAttribute<?> originKeys = new StringSimpleAttributeImpl(ORIGIN_KEYS, originKeysString.toString());

        row.put(ORIGIN_KEYS, originKeys);

        // for each row create default column 'FROM'
        SimpleAttribute<?> from = new DateSimpleAttributeImpl(FROM, ConvertUtils.date2LocalDate(record.getInfoSection().getValidFrom()));
        row.put(FROM, from);

        // for each row create default column 'TO'
        SimpleAttribute<?> to = new DateSimpleAttributeImpl(TO, ConvertUtils.date2LocalDate(record.getInfoSection().getValidTo()));
        row.put(TO, to);

        if (!CollectionUtils.isEmpty(relations)) {

            for (Entry<RelationStateDTO, List<GetRelationDTO>> entry : relations.entrySet()) {

                entry.getValue().forEach(dto -> {

                    if (dto.getEtalon() == null) {
                        return;
                    }

                    try {
                        String entityName = crComponent.identify(dto.getEtalon().getInfoSection().getToEtalonKey()).getEntityName();
                        String name = "RELATION." + dto.getEtalon().getInfoSection().getRelationName() + ".etalonId";
                       
                        StringSimpleAttributeImpl newOne = new StringSimpleAttributeImpl(
                                name,
                                dto.getEtalon().getInfoSection().getToEtalonKey().getId());

                        Attribute previous = row.get(name);
                        if (previous != null) {
                            SimpleAttribute<?> val = previous.narrow();
                            newOne.setValue(val.getValue() + "\n" + newOne.getValue());
                        }
                        row.put(name, newOne);

                        name = "RELATION." + dto.getEtalon().getInfoSection().getRelationName() + ".name";
                        newOne = new StringSimpleAttributeImpl(
                                name,
                                displayNames.get(DNKey.newInstance()
                                        .withAsOf(record.getInfoSection().getValidTo())
                                        .withEtalonId(dto.getEtalon().getInfoSection().getToEtalonKey().getId())
                                        .withEntityName(entityName)
                                        .withFieldName(mainDisplayable.get(entityName))));
                        previous = row.get(name);
                        if (previous != null) {
                            SimpleAttribute<?> val = previous.narrow();
                            newOne.setValue(val.getValue() + "\n" + newOne.getValue());
                        }
                        row.put(name, newOne);
                    

                        // add each first level attribute to the row
                        for (SimpleAttribute<?> simpleAttribute : dto.getEtalon().getSimpleAttributes()) {
                            newOne =  new StringSimpleAttributeImpl(simpleAttribute.getName(), simpleAttribute.getValue().toString());
                            name = "RELATION." + dto.getEtalon().getInfoSection().getRelationName()
                                            + SYSTEM_PATH_DELIMITER + simpleAttribute.getName();
                            previous = row.get(name);
                            if (previous != null) {
                                SimpleAttribute<?> val = previous.narrow();
                                newOne.setValue(val.getValue().toString() + "\n" + newOne.getValue());
                            }
                            row.put(name, (dto.getEtalon().getInfoSection().getType() != RelationType.REFERENCES)
                                    ?newOne:simpleAttribute);

                        }
                    } catch (Exception e) {
                        LOGGER.error("Error occured while trying to retrieve display name from the search service", e);
                    }
                });
            }
        }

        // for each row create default column 'is_active'
        SimpleAttribute<?> active = new BooleanSimpleAttributeImpl(IS_ACTIVE, true);
        row.put(IS_ACTIVE, active);

        // add each first level attribute to the row
        Collection<Attribute> attributes = record.getAllAttributes();
        for (Attribute attr : attributes) {
            // Collect simple, array and code attribute values only.
            if (attr.getAttributeType() == AttributeType.COMPLEX) {
                continue;
            }

            row.put(record.getInfoSection().getEntityName() +
                    SYSTEM_PATH_DELIMITER + attr.getName(), attr);
        }

        if (!CollectionUtils.isEmpty(classifiers)) {
            classifiers.values().forEach(clss -> {
                clss.forEach(cls -> {
                    // Node id
                    SimpleAttribute<?> nodeIdAttr
                        = new StringSimpleAttributeImpl(EXTERNAL_ID, cls.getClassifierKeys().getNodeId());
                    row.put("CL_" + cls.getClassifierKeys().getName(), nodeIdAttr);

                    // Simple attributes
                    for (SimpleAttribute<?> sa : cls.getEtalon().getSimpleAttributes()) {
                        row.put("CL_" + cls.getClassifierKeys().getName() + SYSTEM_PATH_DELIMITER + sa.getName(), sa);
                    }

                    // Complex attributes
                    // Currently not supported
                });
            });
        }

        // interim result
        List<List<Map<String, Attribute>>> interimResult = new ArrayList<>();

        // iterate over all complex attributes
        Collection<ComplexAttribute> complexAttributes = record.getComplexAttributes();

        for (ComplexAttribute complexAttribute : complexAttributes) {

            List<DataRecord> nestedRecords = complexAttribute.getRecords();
            List<Map<String, Attribute>> interimEntries = new ArrayList<>();

            // iterate over all nested records inside complex attribute
            for (DataRecord nestedRecord : nestedRecords) {

                Collection<Attribute> nestedAttrs = nestedRecord.getAllAttributes();
                Map<String, Attribute> interimMap = new HashMap<>();
                for (Attribute nestedAttr : nestedAttrs) {

                    if (nestedAttr.getAttributeType() == AttributeType.COMPLEX) {
                        continue;
                    }

                    interimMap.put(record.getInfoSection().getEntityName() + SYSTEM_PATH_DELIMITER
                            + complexAttribute.getName() + SYSTEM_PATH_DELIMITER + nestedAttr.getName(),
                            nestedAttr);
                }

                interimEntries.add(interimMap);
            }

            interimResult.add(interimEntries);
        }

        // denormalize interim results
        interimResult = ExportUtils.denormalize(interimResult);
        // put denormalized interim results to result data structure
        if (CollectionUtils.isEmpty(interimResult)) {
            Map<String, Attribute> interimMap = new HashMap<>();
            List<Map<String, Attribute>> interimEntries = new ArrayList<>();
            interimEntries.add(interimMap);
            interimResult.add(interimEntries);
        }

        for (List<Map<String, Attribute>> list : interimResult) {

            LinkedHashMap<String, Attribute> hashMap = (LinkedHashMap<String, Attribute>) row.clone();
            for (Map<String, Attribute> simpleAttribute : list) {
                hashMap.putAll(simpleAttribute);
            }

            result.add(hashMap);
        }

        return result;
    }

    /**
     * The Class DisplayNames.
     */
    private class DisplayNames extends CacheLoader<DNKey, String> {

        /**
         * Load.
         *
         * @param key
         *            the key
         * @return the string
         * @throws Exception
         *             the exception
         */
        /*
         * (non-Javadoc)
         *
         * @see com.google.common.cache.CacheLoader#load(java.lang.Object)
         */
        @Override
        public String load(DNKey key) throws Exception {
            SearchRequestContext ctx = SearchRequestContext.forEtalonData(key.getEntityName())
                                                           .asOf(key.getAsOf())
                                                           .search(SearchRequestType.TERM)
                                                           .count(1)
                                                           .values(Collections.singletonList(key.getEtalonId()))
                                                           .searchFields(Collections.singletonList(
                                                                   RecordHeaderField.FIELD_ETALON_ID.getField()))
                                                           .returnFields(key.getFieldName())
                                                           .build();
            SearchResultHitDTO resultHitDTO = searchService.search(ctx).getHits().stream().findAny().get();
            String result = "";
            for (String field : key.getFieldName()) {
                SearchResultHitFieldDTO fieldValue = resultHitDTO.getFieldValue(field);
                if (fieldValue.isNullField()) {
                    continue;
                }
                result = result + " " + String.valueOf(fieldValue.getFirstValue());
            }
            return result;
        }

    }

    /**
     * The Class MainDisplayable.
     */
    private class MainDisplayable extends CacheLoader<String, List<String>> {

        /**
         * Load.
         *
         * @param key
         *            the key
         * @return the list
         * @throws Exception
         *             the exception
         */
        @Override
        public List<String> load(String key) throws Exception {
            NestedEntityDef entityDef = metaModelService.getEntityByIdNoDeps(key);
            List<String> result = entityDef.getSimpleAttribute().stream().filter(sa -> sa.isMainDisplayable())
                    .map(SimpleAttributeDef::getName).collect(Collectors.toList());
            return result;
        }
    }
}

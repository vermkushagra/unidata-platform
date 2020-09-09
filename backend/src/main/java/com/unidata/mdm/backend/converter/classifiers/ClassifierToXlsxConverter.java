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

package com.unidata.mdm.backend.converter.classifiers;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.core.StreamingOutput;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unidata.mdm.backend.common.dto.CustomPropertyDefinition;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeArrayAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeSimpleAttrDTO;
import com.unidata.mdm.backend.service.classifier.converters.ClsfCustomPropertyToPOConverter;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.api.rest.dto.XlsxClassifierWrapper;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.converter.ConverterQualifier;

/**
 * The Class ClassifierToXlsxConverter.
 */
@ConverterQualifier
@Component
public class ClassifierToXlsxConverter implements Converter<XlsxClassifierWrapper, StreamingOutput> {

    /**
     * Name of sheet for classifier info.
     */
    public static final String CLASSIFIER = "classifier";

    /**
     * Name of sheet for node info.
     */
    private static final String NODES = "nodes";

    /**
     * Special notation for node attributes.
     */
    static final String NODE_ATTR = "nodeAttr";

    private static Set<String> TO_EXCLUDE = new HashSet<>(Arrays.asList("createdAt", "updatedAt", "createdBy",
            "updatedBy", "rootNode", "hasOwnAttrs", "childCount", "defaultValue", "attrName"));

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * The Constant SDF.
     */
    private static final SimpleDateFormat SDF_DATE_TIME = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final SimpleDateFormat SDF_TIME = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyy-MM-dd");

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.core.convert.converter.Converter#convert(java.lang.
     * Object)
     */
    @Override
    public StreamingOutput convert(XlsxClassifierWrapper source) {
        ClsfDTO classifierPresentation = source.getClassifierPresentation();
        return output -> {
            try (Workbook wb = new SXSSFWorkbook()) {
                fillClassifierSheet(wb, classifierPresentation);
                List<ClsfNodeDTO> allNodes = collectNodes(classifierPresentation.getRootNode());
                fillNodeSheet(wb, allNodes);
                wb.write(output);
            } catch (IOException | IllegalAccessException e) {
                throw new DataProcessingException("Unable to export data for {} to XLS.", e,
                        ExceptionId.EX_CONVERSION_CLASSIFIER_TO_XLSX_FAILED);
            }
        };
    }

    /**
     * Collect nodes.
     *
     * @param root the root
     * @return the list
     */
    private List<ClsfNodeDTO> collectNodes(ClsfNodeDTO root) {
        List<ClsfNodeDTO> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        result.add(root);
        root.getChildren().forEach(ch -> addToResult(result, ch));
        return result;

    }

    /**
     * Adds the to result.
     *
     * @param result the result
     * @param toAdd  the to add
     */
    private void addToResult(List<ClsfNodeDTO> result, ClsfNodeDTO toAdd) {
        result.add(toAdd);
        toAdd.getChildren().forEach(ch -> addToResult(result, ch));
    }

    /**
     * Fill classifier sheet.
     *
     * @param wb         the wb
     * @param classifier the classifier
     * @throws IllegalAccessException the illegal access exception
     */
    private void fillClassifierSheet(Workbook wb, ClsfDTO classifier) throws IllegalAccessException {
        Sheet classifierSheet = wb.createSheet(CLASSIFIER);

        Field[] fields = FieldUtils.getAllFields(ClsfDTO.class);

        // fill headers
        Row headerRow = classifierSheet.createRow(0);
        CellStyle headerCellStyle = wb.createCellStyle();
        headerCellStyle.setWrapText(true);
        int i = 0;
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) || TO_EXCLUDE.contains(field.getName())) {
                continue;
            }
            Cell cell = headerRow.createCell(i);
            cell.setCellType(CellType.STRING);
            cell.setCellStyle(headerCellStyle);
            cell.setCellValue(field.getName());
            if (classifierSheet instanceof SXSSFSheet) {
                ((SXSSFSheet) classifierSheet).trackColumnForAutoSizing(i);
            }
            classifierSheet.autoSizeColumn(i);
            i++;
        }

        // fill data
        Row dataRow = classifierSheet.createRow(1);
        CellStyle dataCellStyle = wb.createCellStyle();
        i = 0;
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) || TO_EXCLUDE.contains(field.getName())) {
                continue;
            }
            Cell cell = dataRow.createCell(i);
            cell.setCellType(CellType.STRING);
            cell.setCellStyle(dataCellStyle);
            Object fieldValue = FieldUtils.readField(classifier, field.getName(), true);
            if (fieldValue != null) {
                cell.setCellValue(String.valueOf(fieldValue));
            }
            i++;
        }
    }

    /**
     * Fill node sheet.
     *
     * @param wb    the wb
     * @param nodes the nodes
     * @throws IllegalAccessException the illegal access exception
     */
    private void fillNodeSheet(
            final Workbook wb, Collection<ClsfNodeDTO> nodes
    ) throws IllegalAccessException, JsonProcessingException {
        Sheet nodeSheet = wb.createSheet(NODES);

        Field[] nodeFields = FieldUtils.getAllFields(ClsfNodeDTO.class);
        Field[] simpleAttrFields = FieldUtils.getAllFields(ClsfNodeSimpleAttrDTO.class);
        Field[] arrayAttrFields = FieldUtils.getAllFields(ClsfNodeArrayAttrDTO.class);
        int maxNumberOfSimpleAttrs = nodes.stream().mapToInt(node -> node.getNodeSimpleAttrs().size()).max().orElse(0);
        int maxNumberOfArrayAttrs = nodes.stream().mapToInt(node -> node.getNodeArrayAttrs().size()).max().orElse(0);
        Row headerRow = nodeSheet.createRow(0);
        CellStyle headerCellStyle = wb.createCellStyle();
        headerCellStyle.setWrapText(true);

        int arrayAttrsCellStartIndex = 0;

        int cellIndex = 0;
        for (Field field : nodeFields) {
            // skip attrs
            if ((Collection.class.isAssignableFrom(field.getType()) && !"customProperties".equals(field.getName()))
                    || Modifier.isStatic(field.getModifiers())
                    || TO_EXCLUDE.contains(field.getName())) {
                continue;
            }
            Cell cell = headerRow.createCell(cellIndex);
            cell.setCellType(CellType.STRING);
            cell.setCellStyle(headerCellStyle);
            cell.setCellValue(field.getName());
            if (nodeSheet instanceof SXSSFSheet) {
                ((SXSSFSheet) nodeSheet).trackColumnForAutoSizing(cellIndex);
            }
            nodeSheet.autoSizeColumn(cellIndex);
            cellIndex++;
        }

        for (int j = 0; j < maxNumberOfSimpleAttrs; j++) {
            cellIndex = fillHeaderCells(nodeSheet, simpleAttrFields, headerRow, headerCellStyle, cellIndex, j);
        }

        arrayAttrsCellStartIndex = cellIndex;

        for (int j = maxNumberOfSimpleAttrs; j < maxNumberOfSimpleAttrs + maxNumberOfArrayAttrs; j++) {
            cellIndex = fillHeaderCells(nodeSheet, arrayAttrFields, headerRow, headerCellStyle, cellIndex, j);
        }

        // fill data
        CellStyle dataCellStyle = wb.createCellStyle();
        int row = 1;
        for (ClsfNodeDTO node : nodes) {
            Row dataRow = nodeSheet.createRow(row);
            cellIndex = 0;
            for (Field field : nodeFields) {

                if ((Collection.class.isAssignableFrom(field.getType()) && !"customProperties".equals(field.getName()))
                        || Modifier.isStatic(field.getModifiers())
                        || TO_EXCLUDE.contains(field.getName())) {
                    continue;
                }
                Cell cell = dataRow.createCell(cellIndex);
                cell.setCellType(CellType.STRING);
                cell.setCellStyle(dataCellStyle);
                Object fieldValue = getFieldValue(FieldUtils.readField(node, field.getName(), true), field.getName());

                if (fieldValue != null) {
                    cell.setCellValue(String.valueOf(fieldValue));
                }
                cellIndex++;
            }

            for (ClsfNodeSimpleAttrDTO classifierAttr : node.getNodeSimpleAttrs()) {
                cellIndex = fillAttrDataCells("SIMPLE", simpleAttrFields, cellIndex, dataCellStyle, dataRow, classifierAttr, classifierAttr.getDataType());
            }

            cellIndex = arrayAttrsCellStartIndex;

            for (ClsfNodeArrayAttrDTO classifierAttr : node.getNodeArrayAttrs()) {
                cellIndex = fillAttrDataCells("ARRAY", arrayAttrFields, cellIndex, dataCellStyle, dataRow, classifierAttr, classifierAttr.getDataType());
            }
            row++;
        }
    }

    private int fillAttrDataCells(
            String attrType,
            Field[] attrFields,
            int cellIndex,
            CellStyle dataCellStyle,
            Row dataRow,
            Object classifierAttr,
            DataType dataType
    ) throws IllegalAccessException, JsonProcessingException {
        Cell cell = dataRow.createCell(cellIndex);
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(dataCellStyle);
        cell.setCellValue(attrType);
        cellIndex++;
        for (Field field : attrFields) {
            if (Modifier.isStatic(field.getModifiers()) || TO_EXCLUDE.contains(field.getName())) {
                continue;
            }
            cell = dataRow.createCell(cellIndex);
            cell.setCellType(CellType.STRING);
            cell.setCellStyle(dataCellStyle);
            Object fieldValue = getFieldValue(FieldUtils.readField(classifierAttr, field.getName(), true), field.getName());
            cellIndex++;
            if (fieldValue == null) {
                continue;
            }

            if (fieldValue instanceof Date && dataType == DataType.DATE) {
                cell.setCellValue(SDF_DATE.format(fieldValue));
            } else if (fieldValue instanceof Date && dataType == DataType.TIME) {
                cell.setCellValue(SDF_TIME.format(fieldValue));
            } else if (fieldValue instanceof Date && dataType == DataType.TIMESTAMP) {
                cell.setCellValue(SDF_DATE_TIME.format(fieldValue));
            } else if (fieldValue instanceof Collection) {
                cell.setCellValue(
                        objectMapper.writeValueAsString(
                                ((Collection) fieldValue).stream().
                                        filter(Objects::nonNull)
                                        .collect(Collectors.toList())
                        )
                );
            } else {
                cell.setCellValue(String.valueOf(fieldValue));
            }
        }
        return cellIndex;
    }

    private int fillHeaderCells(Sheet nodeSheet, Field[] simpleAttrFields, Row headerRow, CellStyle headerCellStyle, int cellIndex, int j) {
        Cell cell = headerRow.createCell(cellIndex);
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(headerCellStyle);
        cell.setCellValue(NODE_ATTR + "." + j + ".attrType");
        cellIndex++;
        for (Field field : simpleAttrFields) {
            if (Modifier.isStatic(field.getModifiers()) || TO_EXCLUDE.contains(field.getName())) {
                continue;
            }
            cell = headerRow.createCell(cellIndex);
            cell.setCellType(CellType.STRING);
            cell.setCellStyle(headerCellStyle);
            cell.setCellValue(NODE_ATTR + "." + j + "." + field.getName());
            if (nodeSheet instanceof SXSSFSheet) {
                ((SXSSFSheet) nodeSheet).trackColumnForAutoSizing(cellIndex);
            }
            nodeSheet.autoSizeColumn(cellIndex);
            cellIndex++;
        }
        return cellIndex;
    }

    private Object getFieldValue(Object fieldValue, String fieldName) {
        return "customProperties".equals(fieldName)
                ? ClsfCustomPropertyToPOConverter.convert((Collection<CustomPropertyDefinition>) fieldValue)
                : fieldValue;
    }
}

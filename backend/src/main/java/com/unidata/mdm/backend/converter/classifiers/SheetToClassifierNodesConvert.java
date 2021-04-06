package com.unidata.mdm.backend.converter.classifiers;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.StringJoiner;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.classifier.ClassifierNodeDef;
import com.unidata.mdm.classifier.SimpleAttributeWithOptionalValueDef;

@ConverterQualifier
@Component

public class SheetToClassifierNodesConvert implements Converter<XSSFSheet, Collection<ClassifierNodeDef>> {

    private final Converter<ClsfNodeAttrDTO, SimpleAttributeWithOptionalValueDef> converter;

    @Autowired
    public SheetToClassifierNodesConvert(Converter<ClsfNodeAttrDTO, SimpleAttributeWithOptionalValueDef> converter) {
        this.converter = converter;
    }

    @Override
    public Collection<ClassifierNodeDef> convert(XSSFSheet sheet) {
    	//TODO: get rid of this converter, move everything excel related to the service. 
        Iterator<Row> rows = sheet.iterator();
        int i = 0;
        Row metadataRow = null;
        Collection<Row> dataRows = new ArrayList<>();
        while (rows.hasNext()) {
            if (i == 0) {
                metadataRow = rows.next();
                i++;
            } else {
                final Row dataRow = rows.next();
                if (!isBlankRow(dataRow)) {
                    dataRows.add(dataRow);
                }
            }
        }
        if (dataRows.isEmpty() || metadataRow == null) {
            return Collections.emptyList();
        }
        try {
            Collection<ClassifierNodeDef> result = new ArrayList<>();
            for (Row dataRow : dataRows) {
                ClassifierNodeDef node = new ClassifierNodeDef();
                Iterator<Cell> metaIterator = metadataRow.cellIterator();
                Map<Integer, ClsfNodeAttrDTO> classifierAttrs = new HashMap<>();
                Map<Integer, String> values = new HashMap<>();
                int linkedDataIndex = -1;
                while (metaIterator.hasNext()) {
                    linkedDataIndex++;
                    handleDataCell(dataRow, node, metaIterator, classifierAttrs, values, linkedDataIndex);
                }
                Collection<SimpleAttributeWithOptionalValueDef> attrs = new ArrayList<>();
                for (Integer attrNumber : classifierAttrs.keySet()) {
                    String value = values.get(attrNumber);
                    ClsfNodeAttrDTO classifierAttr = classifierAttrs.get(attrNumber);
                    classifierAttr.setDefaultValue(value);
                    attrs.add(converter.convert(classifierAttr));
                }
                node.withAttributes(attrs);
                result.add(node);
            }
            return result;
        } catch (DataProcessingException dpe) {
            throw dpe;
        } catch (Exception e) {
            throw new DataProcessingException("Conversion to classifier node failed", e, ExceptionId.EX_CONVERSION_EXCEL_TO_CLASSIFIER_NODE_FAILED);
        }
    }

    private boolean isBlankRow(Row dataRow) {
        final Stream<Cell> cellStream = StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(dataRow.cellIterator(), Spliterator.ORDERED),
                false
        );
        return cellStream.allMatch(
                cell -> StringUtils.isBlank(ExcelUtils.extractCellValue(cell))
        );
    }

    private void handleDataCell(
            final Row dataRow,
            final ClassifierNodeDef node,
            final Iterator<Cell> metaIterator,
            final Map<Integer, ClsfNodeAttrDTO> classifierAttrs,
            final Map<Integer, String> values,
            final int linkedDataIndex
    ) throws IllegalAccessException {
        Cell meta = metaIterator.next();
        Cell data = dataRow.getCell(linkedDataIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (data == null) {
            return;
        }
        final String value = ExcelUtils.extractCellValue(data);
        if (StringUtils.isBlank(value)) {
            return;
        }
        //TODO: yeah...
        final String fieldName = meta.getStringCellValue();
        final String[] splitName = fieldName.split("\\.");
        if (splitName.length == 1) {
            nodeFieldValue(node, value, fieldName);
        } else if (splitName.length == 3 && splitName[0].equals(ClassifierToXlsxConverter.NODE_ATTR)) {
            Integer attrNumber = Integer.parseInt(splitName[1]);
            ClsfNodeAttrDTO attr = classifierAttrs.get(attrNumber);
            if (attr == null) {
                attr = new ClsfNodeAttrDTO();
                classifierAttrs.put(attrNumber, attr);
            }
            String attrFieldName = splitName[2];
            Field field = FieldUtils.getField(attr.getClass(), attrFieldName, true);
            if (field == null) {
                return;
            }
            attrFieldValue(values, value, attrNumber, attr, attrFieldName, field);
        } else {
            throw new DataProcessingException("Conversion to classifier node failed", ExceptionId.EX_CONVERSION_EXCEL_TO_CLASSIFIER_NODE_FAILED);
        }
    }

    private void attrFieldValue(Map<Integer, String> values, String value, Integer attrNumber, ClsfNodeAttrDTO attr, String attrFieldName, Field field) throws IllegalAccessException {
        if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
            FieldUtils.writeField(field, attr, Boolean.valueOf(value), true);
        } else if (field.getType().equals(com.unidata.mdm.backend.common.types.SimpleAttribute.DataType.class)) {
            FieldUtils.writeField(field, attr, com.unidata.mdm.backend.common.types.SimpleAttribute.DataType.valueOf(value), true);
        } else if (attrFieldName.equals("value")) {
            values.put(attrNumber, value);
        } else {
            FieldUtils.writeField(field, attr, value, true);
        }
    }

    private void nodeFieldValue(final ClassifierNodeDef node, final String value, final String fieldName) {
        if (StringUtils.equals(fieldName,"name")){
            node.setName(value);
        } else if(StringUtils.equals(fieldName,"description")) {
            node.setDescription(value);
        } else if(StringUtils.equals(fieldName,"code")) {
            node.setCode(value);
        } else if(StringUtils.equals(fieldName,"nodeId")) {
            node.setId(value);
        } else if(StringUtils.equals(fieldName,"parentId")) {
            node.setParentId(value);
        } else if(StringUtils.equals(fieldName,"classifierName")) {
            node.setClassifierName(value);
        }
    }
}

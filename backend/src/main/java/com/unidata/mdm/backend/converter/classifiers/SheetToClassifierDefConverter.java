package com.unidata.mdm.backend.converter.classifiers;


import java.lang.reflect.Field;
import java.util.Iterator;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.classifier.ClassifierDef;

@ConverterQualifier
@Component
public class SheetToClassifierDefConverter implements Converter<XSSFSheet, ClassifierDef> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SheetToClassifierDefConverter.class);

    @Override
    public ClassifierDef convert(XSSFSheet sheet) {
        try {
            ClassifierDef classifierDef = new ClassifierDef();
            XSSFRow metadataRow = sheet.getRow(0);
            XSSFRow dataRow = sheet.getRow(1);
            Iterator<Cell> metaIterator = metadataRow.cellIterator();
            while (metaIterator.hasNext()) {
                Cell meta = metaIterator.next();
                Cell data = dataRow.getCell(meta.getColumnIndex());
                String fieldName = ExcelUtils.extractCellValue(meta);
                try {
                    final Field field = classifierDef.getClass().getDeclaredField(fieldName);
                    final String value = ExcelUtils.extractCellValue(data);
                    if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                        FieldUtils.writeField(field, classifierDef, Boolean.valueOf(value), true);
                    }
                    else {
                        FieldUtils.writeField(field, classifierDef, value, true);
                    }

                }
                catch (NoSuchFieldException e) {
                    LOGGER.warn("Unknown field " + fieldName, e);
                }
            }
            return classifierDef;
        } catch (Exception e) {
            throw new DataProcessingException("Conversion to classifier failed", e, ExceptionId.EX_CONVERSION_EXCEL_TO_CLASSIFIER_FAILED);
        }
    }
}

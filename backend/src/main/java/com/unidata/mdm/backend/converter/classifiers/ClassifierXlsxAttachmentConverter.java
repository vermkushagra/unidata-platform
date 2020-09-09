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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.api.rest.dto.XlsxAttachmentWrapper;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.classifier.ClassifierDef;
import com.unidata.mdm.classifier.ClassifierNodeDef;
import com.unidata.mdm.classifier.FullClassifierDef;


/**
 * The Class XlsxAttachmentConverter.
 */
@ConverterQualifier
@Component
public class ClassifierXlsxAttachmentConverter implements Converter<XlsxAttachmentWrapper, FullClassifierDef> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifierXlsxAttachmentConverter.class);

    /**
     * The classifier def converter.
     */
    @Autowired
    private Converter<XSSFSheet, ClassifierDef> classifierDefConverter;

    /**
     * The collection converter.
     */
    @Autowired
    private Converter<XSSFSheet, Collection<ClassifierNodeDef>> collectionConverter;

    /* (non-Javadoc)
     * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
     */
    @Override
    public FullClassifierDef convert(XlsxAttachmentWrapper source) {
        File toImport = source.getAttachment().toFile();
        try (XSSFWorkbook workbook = new XSSFWorkbook(toImport)) {
            FullClassifierDef fullClassifierDef = new FullClassifierDef();
            //add classifier description
            XSSFSheet classifierSheet = workbook.getSheetAt(0);
            ClassifierDef classifierDef = classifierDefConverter.convert(classifierSheet);
            fullClassifierDef.setClassifier(classifierDef);
            //add nodes
            XSSFSheet classifierNodeSheet = workbook.getSheetAt(1);
            Collection<ClassifierNodeDef> classifierNodeDefs = collectionConverter.convert(classifierNodeSheet);
            fullClassifierDef.withClassifierNodes(classifierNodeDefs);
            return fullClassifierDef;
        } catch (Exception e) {
            throw new DataProcessingException("Conversion from classifier failed", e, ExceptionId.EX_CONVERSION_EXCEL_TO_FULL_CLASSIFIER_FAILED);
        } finally {
            if (!toImport.delete()) {
                LOGGER.error("Can't delete file {}", toImport.getPath());
            }
        }
    }

}

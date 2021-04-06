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
import java.util.Set;

import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.api.rest.dto.XlsxClassifierWrapper;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.util.JaxbUtils;

/**
 * The Class ClassifierToXlsxConverter.
 */
@ConverterQualifier
@Component
public class ClassifierToXlsxConverter implements Converter<XlsxClassifierWrapper, StreamingOutput> {

	/** Name of sheet for classifier info. */
	public static final String CLASSIFIER = "classifier";

	/** Name of sheet for node info. */
	public static final String NODES = "nodes";

	/** Special notation for node attributes. */
	public static final String NODE_ATTR = "nodeAttr";
	private static Set<String> TO_EXCLUDE = new HashSet<>(Arrays.asList("createdAt", "updatedAt", "createdBy",
			"updatedBy", "rootNode", "hasOwnAttrs", "childCount", "defaultValue", "attrName"));

	/** The Constant SDF. */
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
	 * @param root
	 *            the root
	 * @return the list
	 */
	private List<ClsfNodeDTO> collectNodes(ClsfNodeDTO root) {
		List<ClsfNodeDTO> result = new ArrayList<>();
		if (root == null) {
			return result;
		}
		result.add(root);
		root.getChildren().stream().forEach(ch -> {
			addToResult(result, ch);
		});
		return result;

	}

	/**
	 * Adds the to result.
	 *
	 * @param result
	 *            the result
	 * @param toAdd
	 *            the to add
	 */
	private void addToResult(List<ClsfNodeDTO> result, ClsfNodeDTO toAdd) {
		result.add(toAdd);
		toAdd.getChildren().stream().forEach(ch -> {
			addToResult(result, ch);
		});
	}

	/**
	 * Fill classifier sheet.
	 *
	 * @param wb
	 *            the wb
	 * @param classifier
	 *            the classifier
	 * @throws IllegalAccessException
	 *             the illegal access exception
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
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellStyle(headerCellStyle);
			cell.setCellValue(field.getName());
			if(classifierSheet instanceof SXSSFSheet){
				((SXSSFSheet)classifierSheet).trackColumnForAutoSizing(i);
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
			cell.setCellType(Cell.CELL_TYPE_STRING);
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
	 * @param wb
	 *            the wb
	 * @param nodes
	 *            the nodes
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 */
	private void fillNodeSheet(Workbook wb, Collection<ClsfNodeDTO> nodes) throws IllegalAccessException {
		Sheet nodeSheet = wb.createSheet(NODES);

		Field[] nodeFields = FieldUtils.getAllFields(ClsfNodeDTO.class);
		Field[] attrFields = FieldUtils.getAllFields(ClsfNodeAttrDTO.class);
		int maxNumberOfAttrs = nodes.stream().mapToInt(node -> node.getNodeAttrs().size()).max().orElse(0);

		Row headerRow = nodeSheet.createRow(0);
		CellStyle headerCellStyle = wb.createCellStyle();
		headerCellStyle.setWrapText(true);

		int cellIndex = 0;
		for (Field field : nodeFields) {
			// skip attrs
			if (Collection.class.isAssignableFrom(field.getType()) || Modifier.isStatic(field.getModifiers())
					|| TO_EXCLUDE.contains(field.getName())) {
				continue;
			}
			Cell cell = headerRow.createCell(cellIndex);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellStyle(headerCellStyle);
			cell.setCellValue(field.getName());
			if(nodeSheet instanceof SXSSFSheet){
				((SXSSFSheet)nodeSheet).trackColumnForAutoSizing(cellIndex);
			}
			nodeSheet.autoSizeColumn(cellIndex);
			cellIndex++;
		}

		for (int j = 0; j < maxNumberOfAttrs; j++) {
			for (Field field : attrFields) {
				if (Modifier.isStatic(field.getModifiers()) || TO_EXCLUDE.contains(field.getName())) {
					continue;
				}
				Cell cell = headerRow.createCell(cellIndex);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellStyle(headerCellStyle);
				cell.setCellValue(NODE_ATTR + "." + j + "." + field.getName());
				if(nodeSheet instanceof SXSSFSheet){
					((SXSSFSheet)nodeSheet).trackColumnForAutoSizing(cellIndex);
				}
				nodeSheet.autoSizeColumn(cellIndex);
				cellIndex++;
			}
		}

		// fill data

		CellStyle dataCellStyle = wb.createCellStyle();
		int row = 1;
		for (ClsfNodeDTO node : nodes) {
			Row dataRow = nodeSheet.createRow(row);
			cellIndex = 0;
			for (Field field : nodeFields) {

				if (Collection.class.isAssignableFrom(field.getType()) || Modifier.isStatic(field.getModifiers())
						|| TO_EXCLUDE.contains(field.getName())) {
					continue;
				}
				Cell cell = dataRow.createCell(cellIndex);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellStyle(dataCellStyle);
				Object fieldValue = FieldUtils.readField(node, field.getName(), true);
				if (fieldValue != null) {
					cell.setCellValue(String.valueOf(fieldValue));
				}
				cellIndex++;
			}

			for (ClsfNodeAttrDTO classifierAttr : node.getNodeAttrs()) {
				for (Field field : attrFields) {
					if (Modifier.isStatic(field.getModifiers()) || TO_EXCLUDE.contains(field.getName())) {
						continue;
					}
					Cell cell = dataRow.createCell(cellIndex);
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellStyle(dataCellStyle);
					Object fieldValue = FieldUtils.readField(classifierAttr, field.getName(), true);
					cellIndex++;
					if (fieldValue == null) {
						continue;
					}

					if (fieldValue instanceof Date && classifierAttr.getDataType() == DataType.DATE) {
						cell.setCellValue(SDF_DATE.format(fieldValue));
					} else if (fieldValue instanceof Date && classifierAttr.getDataType() == DataType.TIME) {
						cell.setCellValue(SDF_TIME.format(fieldValue));
					} else if (fieldValue instanceof Date && classifierAttr.getDataType() == DataType.TIMESTAMP) {
						cell.setCellValue(SDF_DATE_TIME.format(fieldValue));
					} else {
						cell.setCellValue(String.valueOf(fieldValue));
					}
				}
			}
			row++;
		}
	}
}

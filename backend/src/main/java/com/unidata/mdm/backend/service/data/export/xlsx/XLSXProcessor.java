package com.unidata.mdm.backend.service.data.export.xlsx;

import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.service.classifier.ClsfService;
import com.unidata.mdm.backend.service.data.xlsximport.XLSXHeader;
import com.unidata.mdm.backend.service.data.xlsximport.XLSXHeader.TYPE;
import com.unidata.mdm.backend.service.measurement.MetaMeasurementService;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.meta.ArrayAttributeDef;
import com.unidata.mdm.meta.ArrayValueType;
import com.unidata.mdm.meta.CodeAttributeDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SimpleAttributeDef;
import com.unidata.mdm.meta.SimpleDataType;

/**
 * The Class XLSXProcessor.
 */
public abstract class XLSXProcessor {
	/** The Constant TO_RU. */
	protected static final String TO_RU = "Валиден по";

	/** The Constant TO. */
	protected static final String TO = "TO";

	/** The Constant FROM_RU. */
	protected static final String FROM_RU = "Валиден с";

	/** The Constant FROM. */
	protected static final String FROM = "FROM";
	/** The Constant IS_ACTIVE. */
	protected static final String IS_ACTIVE = "IS_ACTIVE";
	/** The Constant IS_ACTIVE_RU. */
	protected static final String IS_ACTIVE_RU = "Активен";
	/**
	 * The Constant H_R_HEADER_IDX.
	 */
	protected static final int H_R_HEADER_IDX = 1;

	/**
	 * The Constant SYSTEM_HEADER_IDX.
	 */
	protected static final int SYSTEM_HEADER_IDX = 0;
	/**
	 * Quantity of the system rows.
	 */
	protected static final int SYSTEM_ROWS_QTY = 2;

	/**
	 * The Constant INITIAL_PATH.
	 */
	protected static final String INITIAL_PATH = "";

	/**
	 * The Constant ID.
	 */
	protected static final String ID = "ID";
	/**
	 * The Constant EXTERNAL ID.
	 */
	protected static final String EXTERNAL_ID = "EXTERNAL_ID";
	/**
	 * The Constant ORIGIN KEYS.
	 */
	protected static final String ORIGIN_KEYS = "ORIGIN_KEYS";

	/**
	 * The Constant H_R_PATH_DELIMITER.
	 */
	protected static final String H_R_PATH_DELIMITER = " >> ";

	/**
	 * The Constant SYSTEM_PATH_DELIMITER.
	 */
	protected static final String SYSTEM_PATH_DELIMITER = ".";

	/** The Constant MURMUR. */
	protected static final HashFunction MURMUR = Hashing.murmur3_128();

	/** The Constant XLSX_FORMATTER. */
	protected static final DataFormatter XLSX_FORMATTER = new DataFormatter();

	/** The date formats. */
	public static final String DATE_FORMATS[] = { "dd.MM.yyyy HH:mm:ss", "dd.MM.yyyy", "HH:mm:ss", "M/dd/yyyy",
			"dd.M.yyyy", "M/dd/yyyy HH:mm:ss a", "dd.M.yyyy HH:mm:ss a", "dd.MMM.yyyy", "dd-MMM-yyyy" };
	/**
	 * The Constant EXCEL_DATE_FORMAT.
	 */
	public static final DateTimeFormatter EXCEL_DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	/**
	 * The Constant EXCEL_TIME_FORMAT.
	 */
	public static final DateTimeFormatter EXCEL_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
	/**
	 * The Constant EXCEL_DATE_TIME_FORMAT.
	 */
	public static final DateTimeFormatter EXCEL_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

	/** The meta model service. */
	@Autowired
	protected MetaModelServiceExt metaModelService;

	/** classifier cache. */
	@Autowired
	protected ClsfService classifierService;

    /** measurement cache. */
    @Autowired
    protected MetaMeasurementService measurementService;
	/**
	 * Checks if is empty row.
	 *
	 * @param row
	 *            the row
	 * @return true, if is empty row
	 */
	protected boolean isEmptyRow(Row row) {
		if(row==null){
			return true;
		}
		for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
			Cell cell = row.getCell(cellNum);
			if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK && StringUtils.isNotBlank(cell.toString())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Convert cell.
	 *
	 * @param cell
	 *            the cell
	 * @param header
	 *            the type
	 * @param evaluator evaluator null
	 * @return the object
	 * @throws ParseException
	 *             the parse exception
	 */
	protected Object convertCell(Cell cell, XLSXHeader header, FormulaEvaluator evaluator) throws ParseException {

	    if (Objects.isNull(cell)) {
	        return null;
	    }

	    AttributeInfoHolder aih = header.getAttributeHolder();
	    if (aih == null || aih.isSimple()) {

	        SimpleDataType type = aih == null
	                ? header.getTypeHeader()
	                : aih.isLookupLink()
	                    ? ((SimpleAttributeDef) header.getAttributeHolder().getAttribute()).getLookupEntityCodeAttributeType()
	                    : aih.isEnumValue()
	                        ? header.getTypeHeader()
	                        : ((SimpleAttributeDef) header.getAttributeHolder().getAttribute()).getSimpleDataType();

    		switch (type) {
    		case BOOLEAN:
    			return convertToBoolean(cell, evaluator);
    		case BLOB:
    		case CLOB:
    		case ANY:
    		case STRING:
    			return convertToString(cell, evaluator);
    		case DATE:
    		case TIME:
    		case TIMESTAMP:
    			return convertToDate(cell, evaluator);
    		case INTEGER:
    			return convertToLong(cell, evaluator);
    		case NUMBER:
    		case MEASURED:
    			return convertToNumber(cell, evaluator);
    		default:
    			break;
    		}
	    } else if (aih.isCode()) {
	        CodeAttributeDef cad = aih.narrow();
	        switch (cad.getSimpleDataType()) {
            case INTEGER:
                return convertToLong(cell, evaluator);
            case STRING:
                return convertToString(cell, evaluator);
            default:
                break;
            }
	    } else if (aih.isArray()) {
	        // Array values are always strings
	        return convertToString(cell, evaluator);
	    }

		return null;
	}

	/**
	 * Convert to boolean.
	 *
	 * @param cell
	 *            the cell
	 * @param evaluator the evaluator
	 * @return the boolean
	 */
	protected Boolean convertToBoolean(Cell cell, FormulaEvaluator evaluator) {

	    if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            return cell.getBooleanCellValue();
        }

	    String asString = convertToString(cell, evaluator);
		if (StringUtils.isEmpty(asString)) {
			return null;
		}

		if (asString.length() == 1 && asString.charAt(0) == '0') {
		    return Boolean.FALSE;
		} else if (asString.length() == 1 && asString.charAt(0) == '1') {
		    return Boolean.TRUE;
		}

		return BooleanUtils.toBooleanObject(asString);
	}

	/**
	 * Convert to date.
	 *
	 * @param cell
	 *            the cell
	 * @param evaluator the evaluator
	 * @return the date
	 * @throws ParseException
	 *             the parse exception
	 */
	protected Date convertToDate(Cell cell, FormulaEvaluator evaluator) throws ParseException {

	    if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            return cell.getDateCellValue();
        }

	    String asString = convertToString(cell, evaluator);
		if (StringUtils.isEmpty(asString)) {
			return null;
		}

		return DateUtils.parseDate(asString, DATE_FORMATS);
	}

	/**
	 * Convert to Long.
	 *
	 * @param cell
	 *            the cell
	 * @param evaluator the formula evaluator
	 * @return the Long
	 */
	protected Long convertToLong(Cell cell, FormulaEvaluator evaluator) throws ParseException {

	    if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
	        return (long) cell.getNumericCellValue();
	    }

	    String asString = convertToString(cell, evaluator);
		if (StringUtils.isEmpty(asString)) {
			return null;
		}

		asString = asString.trim();
		try {
			return Long.parseLong(asString);
		} catch (NumberFormatException e) {
			throw new ParseException("Failed to parse long value from string: " + asString, 0);
		}
	}

	/**
	 * Convert to number.
	 *
	 * @param cell
	 *            the cell
	 * @param evaluator formula evaluator
	 * @return the double
	 */
	protected Double convertToNumber(Cell cell, FormulaEvaluator evaluator) throws ParseException {

	    if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            return cell.getNumericCellValue();
        }

		String asString = convertToString(cell, evaluator);
		if (StringUtils.isEmpty(asString)) {
			return null;
		}

		//UN-3841 - we made a decision replace symbols, because a server locale is Russian always.
        // MSK locale for UI.
		asString = asString.replace(",",".").trim();
		try {
			return Double.parseDouble(asString);
		} catch (NumberFormatException e) {
			throw new ParseException("Failed to parse double value from string: " + asString, 0);
		}
	}

	/**
     * Convert to string.
     *
     * @param cell
     *            the cell
     * @param evaluator the evaluator
     * @return string
     */
    protected String convertToString(Cell cell, FormulaEvaluator evaluator) {

        String result = evaluator == null
                ? XLSX_FORMATTER.formatCellValue(cell)
                : XLSX_FORMATTER.formatCellValue(cell, evaluator);

        return "".equals(result) ? null : result;
    }

	/**
	 * Creates the headers.
	 *
	 * @param entityName
	 *            the entity name
	 * @param isImport
	 *            the is import
	 * @return the list
	 */
	protected List<XLSXHeader> createHeaders(String entityName, boolean isImport) {

		List<XLSXHeader> headers = new ArrayList<>();
		// Add default headers
		// Record ID
		headers.add(new XLSXHeader().withSystemHeader(ID).withHrHeader(ID).withTypeHeader(SimpleDataType.STRING)
				.withType(TYPE.SYSTEM).withIsMandatory(true));
		// External Record ID
		headers.add(new XLSXHeader().withSystemHeader(EXTERNAL_ID).withHrHeader(EXTERNAL_ID)
				.withTypeHeader(SimpleDataType.STRING).withType(TYPE.SYSTEM).withIsMandatory(true));
		// All Origin Keys
		headers.add(new XLSXHeader().withSystemHeader(ORIGIN_KEYS).withHrHeader(ORIGIN_KEYS)
				.withTypeHeader(SimpleDataType.STRING).withType(TYPE.SYSTEM).withIsMandatory(false));
		// Left validity period boundary
		headers.add(new XLSXHeader().withSystemHeader(FROM).withHrHeader(FROM_RU)
				.withTypeHeader(SimpleDataType.DATE).withType(TYPE.SYSTEM).withIsMandatory(true));
		// Right validity period boundary
		headers.add(new XLSXHeader().withSystemHeader(TO).withHrHeader(TO_RU).withTypeHeader(SimpleDataType.DATE)
				.withType(TYPE.SYSTEM).withIsMandatory(true));
		// Sign of period activity
		headers.add(new XLSXHeader().withSystemHeader(IS_ACTIVE).withHrHeader(IS_ACTIVE_RU)
				.withTypeHeader(SimpleDataType.BOOLEAN).withType(TYPE.SYSTEM).withIsMandatory(true));

		// fill headers with attributes data
		Map<String, AttributeInfoHolder> attrsInfo = metaModelService.getAttributesInfoMap(entityName);
		for (Entry<String, AttributeInfoHolder> entry : attrsInfo.entrySet()) {

		    if (entry.getValue().isCode()) {
		        headers.add(codeAttributeToHeader(null, entry.getValue()));
		    } else if (entry.getValue().isArray()) {
		        headers.add(arrayAttributeToHeader(null,entry.getValue()));
		    } else if (entry.getValue().isSimple()) {

		        SimpleAttributeDef sad = entry.getValue().narrow();
		        if (!entry.getValue().isLinkTemplate()
                    && (SimpleDataType.BLOB != sad.getSimpleDataType()
                     || SimpleDataType.CLOB != sad.getSimpleDataType())) {
		            headers.add(simpleAttributeToHeader(null, entry.getValue()));
                }
		    } else if (entry.getValue().isComplex()) {
		        // Skip
		    }
		}

		// add classifiers to headers
		addClassifiersToHeaders(metaModelService.getClassifiersForEntity(entityName), headers);

        // add relations to headers
        addRelationsToHeaders(entityName, headers, isImport);

		for (int i = 0; i < headers.size(); i++) {
			headers.get(i).setOrder(i);
		}

		return headers;
	}

	/**
	 * Simple attribute to header.
	 * @param attributeHolder
	 *            the simple attribute def
	 *
	 * @return the XLSX header
	 */
	protected XLSXHeader simpleAttributeToHeader(String prefix, AttributeInfoHolder attributeHolder) {

	    SimpleAttributeDef simpleAttribute = attributeHolder.narrow();
	    Pair<String, String> paths = getPaths(prefix, attributeHolder);

        String unit = "";
        if (simpleAttribute.getSimpleDataType() == SimpleDataType.MEASURED && simpleAttribute.getMeasureSettings() != null) {
            unit = " (";
            String valueId  = simpleAttribute.getMeasureSettings().getValueId();
            unit += measurementService.getValueById(valueId).getBaseUnit().getShortName() + ")";
        }

		return new XLSXHeader()
		        .withType(TYPE.DATA_ATTRIBUTE)
		        .withIsMandatory(!simpleAttribute.isNullable())
		        .withSystemHeader(paths.getLeft())
		        .withHrHeader(paths.getRight() + unit)
				.withTypeHeader(simpleAttribute.getSimpleDataType() != null
				    ? simpleAttribute.getSimpleDataType()
					: SimpleDataType.STRING)
				.withAttributeHolder(attributeHolder);
	}

	/**
     * Simple attribute to header.
	 * @param attributeHolder
     *            the simple attribute def
     *
     * @return the XLSX header
     */
	 protected XLSXHeader arrayAttributeToHeader(String prefix, AttributeInfoHolder attributeHolder) {

        ArrayAttributeDef arrayAttribute = attributeHolder.narrow();
        Pair<String, String> paths = getPaths(prefix, attributeHolder);

        ArrayValueType type = attributeHolder.isLookupLink()
                ? arrayAttribute.getLookupEntityCodeAttributeType()
                : arrayAttribute.getArrayValueType();

        return new XLSXHeader()
                .withType(TYPE.DATA_ATTRIBUTE)
                .withIsMandatory(!arrayAttribute.isNullable())
                .withSystemHeader(paths.getLeft())
                .withHrHeader(paths.getRight())
                .withTypeHeader(SimpleDataType.valueOf(type.name()))
                .withAttributeHolder(attributeHolder);
    }

    /**
     * Code attribute to header.
     * @param attributeHolder
     *            the code attribute def
     *
     * @return the XLSX header
     */
	 protected XLSXHeader codeAttributeToHeader(String prefix, AttributeInfoHolder attributeHolder) {

        CodeAttributeDef codeAttribute = attributeHolder.narrow();
        Pair<String, String> paths = getPaths(prefix, attributeHolder);

        return new XLSXHeader()
                .withType(TYPE.DATA_ATTRIBUTE)
                .withIsMandatory(true)
                .withSystemHeader(paths.getLeft())
                .withHrHeader(paths.getRight())
                .withTypeHeader(codeAttribute.getSimpleDataType())
                .withAttributeHolder(attributeHolder);
    }

    /**
     * Gets sys path and display path as a pair.
     * @param attributeHolder holder being processed
     * @return pair, where left is sys path and right is display names path
     */
	 protected Pair<String, String> getPaths(String prefix, AttributeInfoHolder attributeHolder) {



        List<String> displayNames = new ArrayList<>();
        AttributeInfoHolder next = attributeHolder;
        AttributeInfoHolder current;
        do {
            current = next;
            displayNames.add(current.getAttribute().getDisplayName());
            next = current.getParent();
        }
        while (next != null);

        displayNames.add(current.getEntity().getDisplayName());

        Collections.reverse(displayNames);

        if(prefix != null){
            return new ImmutablePair<>(
                    String.join(SYSTEM_PATH_DELIMITER, prefix, current.getEntity().getName(), attributeHolder.getPath()),
                    String.join(H_R_PATH_DELIMITER, displayNames));
        } else {
            return new ImmutablePair<>(
                    String.join(SYSTEM_PATH_DELIMITER, current.getEntity().getName(), attributeHolder.getPath()),
                    String.join(H_R_PATH_DELIMITER, displayNames));

        }
    }

	/**
	 * Method adds relations to workbook headers.
	 *
	 * @param entityName
	 *            entity name.
	 * @param headers
	 *            the headers
	 * @param isImport is template for import?
	 */
	protected void addRelationsToHeaders(String entityName, List<XLSXHeader> headers, boolean isImport) {
		Map<RelationDef, EntityDef> relations = metaModelService.getEntityRelations(entityName, true, true);
		if (relations != null && relations.size() != 0) {
		    Set<RelationDef> keys = relations.keySet();
		    if(isImport){
		        keys = keys.stream().filter(r->r.getRelType() == RelType.REFERENCES).collect(Collectors.toSet());
		    }
		    keys.forEach(re -> {
				// etalon id
				String systemHeader = "RELATION." + re.getName() + ".etalonId";
				String hrHeader = "Идентификатор записи реестра " + re.getToEntity();
				headers.add(new XLSXHeader().withHrHeader(hrHeader).withType(TYPE.SYSTEM)
						.withTypeHeader(SimpleDataType.STRING).withSystemHeader(systemHeader));
				// main display name
				systemHeader = "RELATION." + re.getName() + ".name";
				hrHeader = "Имя записи реестра " + re.getToEntity();
				headers.add(new XLSXHeader().withHrHeader(hrHeader).withType(TYPE.SYSTEM)
						.withTypeHeader(SimpleDataType.STRING).withSystemHeader(systemHeader));

				// add simple attributes to headers
				Map<String, AttributeInfoHolder> attrs = metaModelService.getAttributesInfoMap(re.getName());
				for (Entry<String, AttributeInfoHolder> entry : attrs.entrySet()) {
				    if (!entry.getValue().isSimple()) {
				        continue;
				    }

			        SimpleAttributeDef sad = entry.getValue().narrow();
			        if (!entry.getValue().isLinkTemplate()
                     && (SimpleDataType.BLOB != sad.getSimpleDataType()
                      || SimpleDataType.CLOB != sad.getSimpleDataType())) {
			            headers.add(simpleAttributeToHeader("RELATION", entry.getValue()).withType(TYPE.RELATION).withIsRel(true));
                    }
				}
			});
		}
	}

	/**
	 * Adds the classifiers to headers.
	 *
	 * @param classifierNames
	 *            the classifier names
	 * @param headers
	 *            the headers
	 */
	protected void addClassifiersToHeaders(List<String> classifierNames, List<XLSXHeader> headers) {

	    if (CollectionUtils.isEmpty(classifierNames)) {
			return;
		}

		for (String name : classifierNames) {
			final ClsfDTO icp = classifierService.getClassifierByName(name);
			if (icp == null) {
				return;
			}

			headers.add(new XLSXHeader().withHrHeader(icp.getDisplayName()).withSystemHeader("CL_" + icp.getName())
					.withType(TYPE.CLASSIFIER_NODE)
					.withTypeHeader(SimpleDataType.STRING));

			// Sign of period activity
	        headers.add(new XLSXHeader()
	                .withSystemHeader("CL_" + icp.getName() + SYSTEM_PATH_DELIMITER + IS_ACTIVE)
	                .withHrHeader(icp.getDisplayName() + H_R_PATH_DELIMITER + IS_ACTIVE_RU)
	                .withTypeHeader(SimpleDataType.BOOLEAN)
	                .withType(TYPE.SYSTEM)
	                .withIsMandatory(true));

			List<ClsfNodeAttrDTO> nodes = classifierService.getAllClsfAttr(name);
			nodes.stream().forEach(n -> {
				headers.add(
						new XLSXHeader().withHrHeader(icp.getDisplayName() + H_R_PATH_DELIMITER + n.getDisplayName())
								.withSystemHeader("CL_" + icp.getName() + SYSTEM_PATH_DELIMITER + n.getAttrName())
								.withType(TYPE.CLASSIFIER_ATTRIBUTE)
								.withTypeHeader(SimpleDataType.valueOf(n.getDataType().name())));
			});

		}
	}


	/**
	 * Creates the template workbook.
	 *
	 * @param entityName
	 *            the entity name
	 * @param isImport
	 *            the is import
	 * @return the workbook
	 */
	protected Workbook createTemplateWorkbook(String entityName, boolean isImport) {

		List<XLSXHeader> headers = createHeaders(entityName, isImport);
		// create new workbook
		final Workbook wb = new SXSSFWorkbook();
		SXSSFSheet sheet = (SXSSFSheet) wb.createSheet(entityName);
		// create cell style
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setWrapText(true);
		Row sysRow = sheet.createRow(SYSTEM_HEADER_IDX);
		Row hrRow = sheet.createRow(H_R_HEADER_IDX);
		for (XLSXHeader header : headers) {
			Cell sysCell = sysRow.createCell(header.getOrder(), Cell.CELL_TYPE_STRING);
			sysCell.setCellStyle(cellStyle);
			sysCell.setCellValue(header.getSystemHeader());
			Cell hrCell = hrRow.createCell(header.getOrder(), Cell.CELL_TYPE_STRING);
			hrCell.setCellStyle(cellStyle);
			hrCell.setCellValue(header.getHrHeader());
			sheet.trackColumnForAutoSizing(header.getOrder());
			sheet.autoSizeColumn(header.getOrder());
		}
		// block editing for headers
		sheet.createFreezePane(0, 2);
		// set headers as repeating rows
		sheet.setRepeatingRows(CellRangeAddress.valueOf("1:" + headers.size()));
		return wb;
	}
}

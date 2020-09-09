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

package com.unidata.mdm.backend.api.rest;

import static com.unidata.mdm.backend.common.context.SearchRequestContext.forAuditEvents;
import static com.unidata.mdm.backend.common.search.FormFieldsGroup.createAndGroup;
import static com.unidata.mdm.backend.common.search.SortField.SortOrder.DESC;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.api.rest.converter.ExportParamsConverter;
import com.unidata.mdm.backend.api.rest.converter.SearchResultToRestSearchResultConverter;
import com.unidata.mdm.backend.api.rest.dto.AuditSearchRequest;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.UpdateResponse;
import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaGraphRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchResultRO;
import com.unidata.mdm.backend.api.rest.dto.security.ExportParamsRO;
import com.unidata.mdm.backend.common.ConvertUtils;
import com.unidata.mdm.backend.common.context.SaveLargeObjectRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext.SearchRequestContextBuilder;
import com.unidata.mdm.backend.common.context.UpsertUserEventRequestContext;
import com.unidata.mdm.backend.common.context.SaveLargeObjectRequestContext.SaveLargeObjectRequestContextBuilder;
import com.unidata.mdm.backend.common.context.UpsertUserEventRequestContext.UpsertUserEventRequestContextBuilder;
import com.unidata.mdm.backend.common.dto.GetRecordsDTO;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.common.dto.security.UserEventDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.SearchRequestOperator;
import com.unidata.mdm.backend.common.search.SortField;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.service.UserService;
import com.unidata.mdm.backend.service.data.xlsximport.XLSXHeader;
import com.unidata.mdm.backend.service.search.Event;
import com.unidata.mdm.backend.service.search.util.AuditHeaderField;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.backend.util.constants.UserMessageConstants;
import com.unidata.mdm.meta.SimpleDataType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The Class AuditLogSearchRestService.
 *
 * @author Denis Kostovarov
 */
@Path("audit")
@Api(value = "audit_service", description = "Данные аудита", produces = "application/json")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class AuditLogSearchRestService extends AbstractRestService {

	/** The Constant SEARCH_PARAM_USERNAME. */
	private static final String SEARCH_PARAM_USERNAME = "username";

	/** The Constant SEARCH_PARAM_REGISTRY. */
	private static final String SEARCH_PARAM_REGISTRY = "registry";

	/** The Constant SEARCH_PARAM_OPERATION. */
	private static final String SEARCH_PARAM_OPERATION = "operation";

	/** The Constant SEARCH_PARAM_TYPE. */
	private static final String SEARCH_PARAM_TYPE = "type";

	/** The Constant SEARCH_PARAM_ETALON. */
	private static final String SEARCH_PARAM_ETALON = "etalon";

	/** The Constant SEARCH_PARAM_COUNT. */
	private static final String SEARCH_PARAM_COUNT = "count";

	/** The Constant SEARCH_PARAM_PAGE. */
	private static final String SEARCH_PARAM_PAGE = "page";

	/** The Constant SEARCH_PARAM_START_DATE. */
	private static final String SEARCH_PARAM_START_DATE = "startDate";

	/** The Constant SEARCH_PARAM_END_DATE. */
	private static final String SEARCH_PARAM_END_DATE = "endDate";

	/** The Constant SEARCH_PARAM_EXTERAL_ID. */
	private static final String SEARCH_PARAM_EXTERAL_ID = "externalId";

	/** The Constant DEFAULT_OBJ_COUNT_VALUE. */
	private static final int DEFAULT_OBJ_COUNT_VALUE = 10;

	/** The Constant DEFAULT_PAGE_NUMBER_VALUE. */
	private static final int DEFAULT_PAGE_NUMBER_VALUE = 1;

	/** The search service. */
	@Autowired
	private SearchService searchService;
	
	/** The user service. */
	@Autowired
	private UserService userService;
	
	/** The data records service. */
	@Autowired
	private DataRecordsService dataRecordsService;
    private ExecutorService executor = Executors.newFixedThreadPool(10);
	private static final FastDateFormat DEFAULT_TIMESTAMP = FastDateFormat.getInstance("yyyy_MM_dd'T'HH_mm_ss_SSS");
	/**
	 * Parses the date time.
	 *
	 * @param dateTimeStr
	 *            the date time str
	 * @return the zoned date time
	 */
	private static ZonedDateTime parseDateTime(final String dateTimeStr) {
		if (StringUtils.isNotEmpty(dateTimeStr)) {
			final Calendar cal = DatatypeConverter.parseDateTime(dateTimeStr);
			return ZonedDateTime.ofInstant(cal.toInstant(), cal.getTimeZone().toZoneId());
		} else {
			return null;
		}

	}

	/**
	 * Search.
	 *
	 * @param username
	 *            the username
	 * @param externalId
	 *            the external id
	 * @param registry
	 *            the registry
	 * @param type
	 *            the type
	 * @param operation
	 *            the operation
	 * @param etalon
	 *            the etalon
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 * @param countStr
	 *            the count str
	 * @param pageStr
	 *            the page str
	 * @return the response
	 */
	@GET
	@Path("/search")
	@ApiOperation(value = "Поиск записей аудита по параметрам", response = SearchResultRO.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 500, message = "Error occurred", response = Array.class) })
	@SuppressWarnings("squid:S00107")
	public Response search(@QueryParam(SEARCH_PARAM_USERNAME) String username,
			@QueryParam(SEARCH_PARAM_EXTERAL_ID) String externalId, @QueryParam(SEARCH_PARAM_REGISTRY) String registry,
			@QueryParam(SEARCH_PARAM_TYPE) String type, @QueryParam(SEARCH_PARAM_OPERATION) String operation,
			@QueryParam(SEARCH_PARAM_ETALON) String etalon, @QueryParam(SEARCH_PARAM_START_DATE) String startDate,
			@QueryParam(SEARCH_PARAM_END_DATE) String endDate, @QueryParam(SEARCH_PARAM_COUNT) String countStr,
			@QueryParam(SEARCH_PARAM_PAGE) String pageStr) {
		AuditSearchRequest request = new AuditSearchRequest();
		request.setCount(countStr);
		request.setEndDate(endDate);
		request.setEtalon(etalon);
		request.setExternalId(externalId);
		request.setOperation(operation);
		request.setPage(pageStr);
		request.setRegistry(registry);
		request.setStartDate(startDate);
		request.setType(type);
		request.setUsername(username);
		SearchResultDTO result = searchAudit(request);
		return ok(SearchResultToRestSearchResultConverter.convert(result, false));
	}

	/**
	 * Export.
	 *
	 * @param request
	 *            the request
	 * @return the response
	 */
	@POST
	@Path("/export")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Экспортировать записи аудита по параметрам.", notes = "", response = UpdateResponse.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Request processed"),
			@ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class) })
	public Response export(final AuditSearchRequest request) {
		final String storageId = "default";
		request.setExport(true);
		String userName = SecurityUtils.getCurrentUserName();
		executor.submit(() -> {
			SearchResultDTO result = searchAudit(request);
			export(result, userName);
		});
		return ok(new UpdateResponse(true, storageId));
	}

	/**
	 * Search audit.
	 *
	 * @param request
	 *            the request
	 * @return the search result DTO
	 */
	private SearchResultDTO searchAudit(AuditSearchRequest request) {
		ZonedDateTime startDateObj = parseDateTime(request.getStartDate());
		ZonedDateTime endDateObj = parseDateTime(request.getEndDate());

		int count;
		try {
			count = Integer.parseInt(request.getCount());
		} catch (NumberFormatException ignore) {
			count = DEFAULT_OBJ_COUNT_VALUE;
		}

		int page;
		try {
			page = Integer.parseInt(request.getPage());
			page = page < DEFAULT_PAGE_NUMBER_VALUE ? DEFAULT_PAGE_NUMBER_VALUE : page;
		} catch (NumberFormatException ignore) {
			page = DEFAULT_PAGE_NUMBER_VALUE;
		}

		FormFieldsGroup formGroup = createAndGroup();
		if (isNotBlank(request.getUsername())) {
			formGroup.addFormField(FormField.strictString(Event.USER, request.getUsername()));
		}
		if (isNotBlank(request.getRegistry())) {
			formGroup.addFormField(FormField.strictString(Event.ENTITY, request.getRegistry()));
		}
		if (isNotBlank(request.getEtalon())) {
			formGroup.addFormField(FormField.strictString(Event.ETALON_ID, request.getEtalon()));
		}
		if (isNotBlank(request.getType())) {
			formGroup.addFormField(FormField.strictString(Event.ACTION, request.getType()));
		}
		if (isNotBlank(request.getOperation())) {
			formGroup.addFormField(FormField.strictString(Event.OPERATION_ID, request.getOperation()));
		}
		if (isNotBlank(request.getExternalId())) {
			formGroup.addFormField(FormField.strictString(Event.EXTERNAL_ID, request.getExternalId()));
		}
		if (startDateObj != null || endDateObj != null) {
			formGroup.addFormField(FormField.range(SimpleDataType.DATE, Event.DATE, FormField.FormType.POSITIVE,
					ConvertUtils.zonedDateTime2Date(startDateObj), ConvertUtils.zonedDateTime2Date(endDateObj)));
		}

		SearchRequestContextBuilder fCtx = forAuditEvents().form(formGroup)
				.returnFields(
						Arrays.stream(AuditHeaderField.values()).map(AuditHeaderField::getField).collect(toList()))
				.addSorting(singletonList(new SortField(Event.DATE, DESC, false))).operator(SearchRequestOperator.OP_OR)
				.count(request.isExport() ? Integer.MAX_VALUE : count).fetchAll(formGroup.isEmpty());
		if (!request.isExport()) {
			fCtx = fCtx.page(page > 0 ? page - 1 : page);
		}

		SearchRequestContext ctx = fCtx.totalCount(true).build();
		SearchResultDTO result = searchService.search(ctx);
		return result;
	}

	/**
	 * Export.
	 *
	 * @param result
	 *            the result
	 */
	private void export(SearchResultDTO result, String user) {
		try (ByteArrayOutputStream output = new ByteArrayOutputStream(); Workbook wb = createTemplateWorkbook(result)) {
			fillTemplateWbWithData(wb, result);
			wb.write(output);
			InputStream is = new ByteArrayInputStream(output.toByteArray());
			UpsertUserEventRequestContext uueCtx = new UpsertUserEventRequestContextBuilder()
					.login(user).type("AUDIT_EXPORT").content("Экспорт журнала аудита.")
					.build();
			UserEventDTO userEventDTO = userService.upsert(uueCtx);
			// save result and attach it to the early created user event
			SaveLargeObjectRequestContext slorCTX = new SaveLargeObjectRequestContextBuilder()
					.eventKey(userEventDTO.getId())
					.mimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").binary(true)
					.inputStream(is).filename("audit_"+DEFAULT_TIMESTAMP.format(new Date())+".xlsx").build();
			dataRecordsService.saveLargeObject(slorCTX);

		} catch (IOException e) {
			throw new DataProcessingException("Unable to export data for {} to XLS.",
					ExceptionId.EX_DATA_EXPORT_UNABLE_TO_EXPORT_XLS, "");
		}
		result.getFields();
	}

	/**
	 * Fill template wb with data.
	 *
	 * @param wb
	 *            the wb
	 * @param result
	 *            the result
	 */
	private void fillTemplateWbWithData(Workbook wb, SearchResultDTO result) {
		SXSSFSheet sheet = (SXSSFSheet) wb.getSheet("AUDIT");
		Map<String, Integer> indexes = new HashMap<>();
		for (int i = 0; i < result.getFields().size(); i++) {
			indexes.put(result.getFields().get(i), i);
		}
		for (int i = 0; i < result.getHits().size(); i++) {
			SXSSFRow row = sheet.createRow(i + 1);
			result.getHits().get(i).getPreview().forEach((k, v) -> {
				if (indexes.containsKey(k)) {

					SXSSFCell cell = row.createCell(indexes.get(k));

					cell.setCellValue(cell.getStringCellValue() + " "
							+ ((v.getValues() != null && v.getValues().size() != 0) ? v.getFirstValue().toString()
									: ""));
				}
			});

		}

	}

	/**
	 * Creates the template workbook.
	 *
	 * @param result
	 *            the result
	 * @return the workbook
	 */
	private Workbook createTemplateWorkbook(SearchResultDTO result) {
		// create new workbook
		final Workbook wb = new SXSSFWorkbook();
		SXSSFSheet sheet = (SXSSFSheet) wb.createSheet("AUDIT");
		// create cell style
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setWrapText(true);
		Row sysRow = sheet.createRow(0);
		for (int i = 0; i < result.getFields().size(); i++) {
			Cell sysCell = sysRow.createCell(i);
			sysCell.setCellStyle(cellStyle);
			sysCell.setCellValue(result.getFields().get(i));
//			sheet.trackColumnForAutoSizing(i);
//			sheet.autoSizeColumn(i);
		}
		// block editing for headers
		sheet.createFreezePane(0, 1);
		// set headers as repeating rows
		sheet.setRepeatingRows(CellRangeAddress.valueOf("1:1"));
		return wb;
	}

}

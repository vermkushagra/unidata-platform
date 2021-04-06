package com.unidata.mdm.backend.api.rest;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.unidata.mdm.backend.common.dto.SplitRecordsDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Multimap;
import com.unidata.mdm.backend.api.rest.converter.ClassifierRecordConverter;
import com.unidata.mdm.backend.api.rest.converter.DataRecordEtalonConverter;
import com.unidata.mdm.backend.api.rest.converter.DataRecordOriginConverter;
import com.unidata.mdm.backend.api.rest.converter.ErrorInfoToRestErrorInfoConverter;
import com.unidata.mdm.backend.api.rest.converter.EtalonPreviewConverter;
import com.unidata.mdm.backend.api.rest.converter.LargeObjectToRestLargeObjectConverter;
import com.unidata.mdm.backend.api.rest.converter.RecordDiffStateConverter;
import com.unidata.mdm.backend.api.rest.converter.RecordKeysConverter;
import com.unidata.mdm.backend.api.rest.converter.RolesConverter;
import com.unidata.mdm.backend.api.rest.converter.TimelineToTimelineROConverter;
import com.unidata.mdm.backend.api.rest.converter.WorkflowTaskConverter;
import com.unidata.mdm.backend.api.rest.dto.ErrorInfo;
import com.unidata.mdm.backend.api.rest.dto.ErrorInfo.Severity;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.Param;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.UpdateResponse;
import com.unidata.mdm.backend.api.rest.dto.data.EtalonRecordRO;
import com.unidata.mdm.backend.api.rest.dto.data.ExtendedRecordRO;
import com.unidata.mdm.backend.api.rest.dto.data.LargeObjectRO;
import com.unidata.mdm.backend.api.rest.dto.data.OriginRecordRO;
import com.unidata.mdm.backend.api.rest.util.RestUtils;
import com.unidata.mdm.backend.common.ConvertUtils;
import com.unidata.mdm.backend.common.context.ClassifierIdentityContext;
import com.unidata.mdm.backend.common.context.DeleteClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.DeleteClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.DeleteLargeObjectRequestContext;
import com.unidata.mdm.backend.common.context.DeleteLargeObjectRequestContext.DeleteLargeObjectRequestContextBuilder;
import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRequestContext.DeleteRequestContextBuilder;
import com.unidata.mdm.backend.common.context.FetchLargeObjectRequestContext;
import com.unidata.mdm.backend.common.context.FetchLargeObjectRequestContext.FetchLargeObjectRequestContextBuilder;
import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.context.GetRequestContext.GetRequestContextBuilder;
import com.unidata.mdm.backend.common.context.MergeRequestContext;
import com.unidata.mdm.backend.common.context.MergeRequestContext.MergeRequestContextBuilder;
import com.unidata.mdm.backend.common.context.RecordIdentityContext;
import com.unidata.mdm.backend.common.context.SaveLargeObjectRequestContext;
import com.unidata.mdm.backend.common.context.SaveLargeObjectRequestContext.SaveLargeObjectRequestContextBuilder;
import com.unidata.mdm.backend.common.context.UpsertClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.UpsertClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext.UpsertRequestContextBuilder;
import com.unidata.mdm.backend.common.dto.DeleteClassifiersDTO;
import com.unidata.mdm.backend.common.dto.DeleteRecordDTO;
import com.unidata.mdm.backend.common.dto.EtalonRecordDTO;
import com.unidata.mdm.backend.common.dto.GetClassifierDTO;
import com.unidata.mdm.backend.common.dto.GetRecordDTO;
import com.unidata.mdm.backend.common.dto.LargeObjectDTO;
import com.unidata.mdm.backend.common.dto.MergeRecordsDTO;
import com.unidata.mdm.backend.common.dto.TimelineDTO;
import com.unidata.mdm.backend.common.dto.UpsertClassifiersDTO;
import com.unidata.mdm.backend.common.dto.UpsertRecordDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.runtime.MeasurementContextName;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.OriginClassifier;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.service.data.ValidationServiceExt;
import com.unidata.mdm.backend.util.FileUtils;
import com.unidata.mdm.backend.util.IdUtils;
import com.unidata.mdm.backend.util.ValidityPeriodUtils;
import com.unidata.mdm.meta.AbstractExternalIdGenerationStrategyDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.data.domain.PageImpl;

/**
 * @author Michael Yashin. Created on 19.05.2015.
 */
@Path("data/entities")
@Api(value = "data_entities", description = "Данные справочников/реестров", produces = "application/json")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class DataEntityRestService extends AbstractRestService {
    private static final String START = "start";
    private static final String LIMIT = "limit";

    /**
     * Default value for delete cascade.
     */
    public static final boolean DEFAULT_DELETE_CASCADE_VALUE = true;
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataEntityRestService.class);

    /**
     * Data records service.
     */
    @Autowired
    private DataRecordsService dataRecordsService;
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * Validation service.
     */
    @Autowired
    private ValidationServiceExt validationService;

    /**
     * Gets a data record by ID.
     *
     * @param id the id
     * @return a data record
     */
    @GET
    @Path("{" + RestConstants.DATA_PARAM_ID + "}{p:/?}{"
            + RestConstants.DATA_PARAM_DATE + ": " + RestConstants.DEFAULT_TIMESTAMP_PATTERN + "}")
    @ApiOperation(value = "Получить запись по ID", notes = "", response = EntityRecordRestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getById(
            @ApiParam("ID сущности") @PathParam(RestConstants.DATA_PARAM_ID) String id,
            @ApiParam("Дата на таймлайне") @PathParam(RestConstants.DATA_PARAM_DATE) String dateAsString,
            @ApiParam("Включать неактивные версии в эталон или нет (true/false)") @QueryParam(RestConstants.DATA_PARAM_INCLUDE_INACTIVE) String includeInactiveAsString,
            @ApiParam("Включать неподтвержденные версии в эталон или нет (true/false)") @QueryParam(RestConstants.DATA_PARAM_INCLUDE_DRAFTS) String includeDraftsAsString,
            @ApiParam("Включить версии совместные с указанным operationId") @QueryParam(RestConstants.DATA_PARAM_OPERATION_ID) String operationId,
            @ApiParam("Вернуть разницу между драфтом и эталоном") @QueryParam(RestConstants.DATA_PARAM_DIFF_TO_DRAFT) String diffToDraftAsString,
            @ApiParam("Вернуть разницу между эталоном и предыдущим состоянием (одну версию назад)") @QueryParam(RestConstants.DATA_PARAM_DIFF_TO_PREVIOUS) String diffToPreviousAsString) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_GET);
        MeasurementPoint.start();
        try {

            Date asOf = ValidityPeriodUtils.parse(dateAsString);
            GetRequestContext ctx = new GetRequestContextBuilder()
                    .etalonKey(id)
                    .forDate(asOf)
                    .forOperationId(operationId)
                    .fetchRelations(false)
                    .includeInactive(includeInactiveAsString == null ? false : Boolean.valueOf(includeInactiveAsString))
                    .includeDrafts(includeDraftsAsString == null ? false : Boolean.valueOf(includeDraftsAsString))
                    .diffToDraft(diffToDraftAsString == null ? false : Boolean.valueOf(diffToDraftAsString))
                    .diffToPrevious(diffToPreviousAsString == null ? false : Boolean.valueOf(diffToPreviousAsString))
                    .fetchClusters(true)
                    .tasks(true)
                    .build();

            GetRecordDTO result = dataRecordsService.getRecord(ctx);
            EtalonRecordRO record = null;
            if (result.getEtalon() != null) {

                EtalonRecord etalonRecord = result.getEtalon();

                record = DataRecordEtalonConverter.to(etalonRecord, result.getDqErrors(), Collections.emptyList());
                if (MapUtils.isNotEmpty(result.getClassifiers())) {
                    record.setClassifiers(ClassifierRecordConverter.to(result.getClassifiers().values().stream()
                            .flatMap(List::stream)
                            .collect(Collectors.toList())));
                }

                record.setWorkflowState(WorkflowTaskConverter.to(result.getTasks()));
                record.setRights(RolesConverter.convertResourceSpecificRights(result.getRights()));
                record.setDiffToDraft(RecordDiffStateConverter.to(result.getDiffToDraft()));
                record.setEntityType(metaModelService.isEntity(record.getEntityName()) ? RestConstants.REGISTER_ENTITY_TYPE : RestConstants.LOOKUP_ENTITY_TYPE);
            }

            return ok(new RestResponse<>(record));
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Gets a data record by ID.
     *
     * @param id the id
     * @return a data record
     */
    @GET
    @Path("{" + RestConstants.DATA_PARAM_ID + "}/{"
            + RestConstants.DATA_PARAM_DATE + "}/{"
            + RestConstants.DATA_PARAM_LUD + "}")
    @ApiOperation(value = "Получить запись по ID", notes = "", response = EntityRecordRestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getById(
            @ApiParam("ID сущности") @PathParam(RestConstants.DATA_PARAM_ID) String id,
            @ApiParam("Дата на таймлайне") @PathParam(RestConstants.DATA_PARAM_DATE) String dateAsString,
            @ApiParam("Дата последнего обновления (вид на дату)") @PathParam(RestConstants.DATA_PARAM_LUD) String ludAsString,
            @ApiParam("Включать неактивные версии в эталон или нет (true/false)") @QueryParam(RestConstants.DATA_PARAM_INCLUDE_INACTIVE) String includeInactiveAsString,
            @ApiParam("Включать неподтвержденные версии в эталон или нет (true/false)") @QueryParam(RestConstants.DATA_PARAM_INCLUDE_DRAFTS) String includeDraftsAsString,
            @ApiParam("Включить версии совместные с указанным operationId") @QueryParam(RestConstants.DATA_PARAM_OPERATION_ID) String operationId,
            @ApiParam("Вернуть разницу между драфтом и эталоном") @QueryParam(RestConstants.DATA_PARAM_DIFF_TO_DRAFT) String diffToDraftAsString,
            @ApiParam("Вернуть разницу между эталоном и предыдущим состоянием (одну версию назад)") @QueryParam(RestConstants.DATA_PARAM_DIFF_TO_PREVIOUS) String diffToPreviousAsString) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_GET);
        MeasurementPoint.start();
        try {

            Date asOf = ValidityPeriodUtils.parse(dateAsString);
            Date lastUpdate = ValidityPeriodUtils.parse(ludAsString);

            GetRequestContext ctx = new GetRequestContextBuilder()
                    .etalonKey(id)
                    .forDate(asOf)
                    .forLastUpdate(lastUpdate)
                    .forOperationId(operationId)
                    .fetchRelations(false)
                    .includeInactive(includeInactiveAsString == null ? false : Boolean.valueOf(includeInactiveAsString))
                    .includeDrafts(includeDraftsAsString == null ? false : Boolean.valueOf(includeDraftsAsString))
                    .diffToDraft(diffToDraftAsString == null ? false : Boolean.valueOf(diffToDraftAsString))
                    .diffToPrevious(diffToPreviousAsString == null ? false : Boolean.valueOf(diffToPreviousAsString))
                    .tasks(true)
                    .build();

            GetRecordDTO result = dataRecordsService.getRecord(ctx);
            EtalonRecordRO record = null;
            if (result.getEtalon() != null) {

                EtalonRecord etalonRecord = result.getEtalon();

                record = DataRecordEtalonConverter.to(etalonRecord, result.getDqErrors(), Collections.emptyList());
                if (MapUtils.isNotEmpty(result.getClassifiers())) {
                    record.setClassifiers(ClassifierRecordConverter.to(result.getClassifiers().values().stream()
                            .flatMap(List::stream)
                            .collect(Collectors.toList())));
                }

                record.setWorkflowState(WorkflowTaskConverter.to(result.getTasks()));
                record.setRights(RolesConverter.convertResourceSpecificRights(result.getRights()));
                record.setDiffToDraft(RecordDiffStateConverter.to(result.getDiffToDraft()));
                record.setEntityType(metaModelService.isEntity(record.getEntityName()) ? RestConstants.REGISTER_ENTITY_TYPE : RestConstants.LOOKUP_ENTITY_TYPE);
            }

            return ok(new RestResponse<>(record));
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Create a record.
     *
     * @param record the record to save
     * @return created record
     */
    @POST
    @ApiOperation(value = "Создать запись", notes = "", response = EntityRecordRestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response create(EtalonRecordRO record) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_CREATE);
        MeasurementPoint.start();
        try {
            return ok(upsertRecord(record));
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Update golden record.
     *
     * @param id     the record id
     * @param record the record
     * @return updated record
     */
    @PUT
    @Path("{" + RestConstants.DATA_PARAM_ID + "}")
    @ApiOperation(value = "Обновить запись", notes = "На обновление присылается полная запись", response = EntityRecordRestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response update(
            @ApiParam("ID сущности")
            @PathParam(RestConstants.DATA_PARAM_ID) String id,
            EtalonRecordRO record) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_UPDATE);
        MeasurementPoint.start();
        try {
            return ok(upsertRecord(record));
        } finally {
            MeasurementPoint.stop();
        }
    }

    private RestResponse<EtalonRecordRO> upsertRecord(EtalonRecordRO record) {

        if (record.getDqErrors() != null) {
            record.setDqErrors(new ArrayList<>());
        }

        String externalId = null;
        if (Objects.isNull(record.getEtalonId())) {

            AbstractExternalIdGenerationStrategyDef strategy = null;
            EntityDef entity = metaModelService.getEntityByIdNoDeps(record.getEntityName());
            if (entity != null) {
                strategy = entity.getExternalIdGenerationStrategy();
            } else {
                LookupEntityDef lookup = metaModelService.getLookupEntityById(record.getEntityName());
                strategy = lookup != null ? lookup.getExternalIdGenerationStrategy() : null;
            }

            if (strategy == null) {
                externalId = IdUtils.v1String();
            }
        }

        String adminSourceSystem = metaModelService.getAdminSourceSystem().getName();
        UpsertRequestContext ctx = new UpsertRequestContextBuilder()
                .record(DataRecordEtalonConverter.from(record))
                .etalonKey(record.getEtalonId())
                .externalId(externalId)
                .sourceSystem(record.getEtalonId() == null ? adminSourceSystem : null)
                .entityName(record.getEtalonId() == null ? record.getEntityName() : null)
                .validFrom(ConvertUtils.localDateTime2Date(record.getValidFrom()))
                .validTo(ConvertUtils.localDateTime2Date(record.getValidTo()))
                .returnEtalon(true)
                .build();

        Boolean success = Boolean.TRUE;
        EtalonRecordRO retval = null;
        List<ErrorInfo> messages = new ArrayList<>();
        try {

            // Data
            UpsertRecordDTO result = dataRecordsService.upsertRecord(ctx);
            retval = DataRecordEtalonConverter.to(result.getEtalon(), ctx.getDqErrors(), result.getDuplicateIds());

            if(CollectionUtils.isNotEmpty(result.getErrors())){
                messages.addAll(result.getErrors().stream()
                        .map(errorInfoDTO -> ErrorInfoToRestErrorInfoConverter.convert(errorInfoDTO))
                        .collect(Collectors.toList()));
            }
            // Classifiers
            List<ClassifierIdentityContext> classifiers = ClassifierRecordConverter.from(record.getClassifiers());
            if (!CollectionUtils.isEmpty(classifiers)) {

                Map<String, List<UpsertClassifierDataRequestContext>> upserts = new HashMap<>();
                Map<String, List<DeleteClassifierDataRequestContext>> deletes = new HashMap<>();
                for (ClassifierIdentityContext cCtx : classifiers) {
                    if (cCtx instanceof UpsertClassifierDataRequestContext) {

                        UpsertClassifierDataRequestContext current = (UpsertClassifierDataRequestContext) cCtx;
                        current.setOperationId(ctx.getOperationId());
                        current.putToStorage(current.keysId(), ctx.keys());
                        if (!upserts.containsKey(current.getClassifierName())) {
                            upserts.put(current.getClassifierName(), new ArrayList<>());
                        }

                        upserts.get(current.getClassifierName()).add(current);
                    } else if (cCtx instanceof DeleteClassifierDataRequestContext) {

                        DeleteClassifierDataRequestContext current = (DeleteClassifierDataRequestContext) cCtx;
                        current.setOperationId(ctx.getOperationId());
                        current.putToStorage(current.keysId(), ctx.keys());
                        if (!deletes.containsKey(current.getClassifierName())) {
                            deletes.put(current.getClassifierName(), new ArrayList<>());
                        }

                        deletes.get(current.getClassifierName()).add(current);
                    }
                }

                if (MapUtils.isNotEmpty(upserts)) {

                    UpsertClassifiersDataRequestContext ucCtx = UpsertClassifiersDataRequestContext.builder()
                            .classifiers(upserts)
                            .build();

                    ucCtx.setOperationId(ctx.getOperationId());
                    ucCtx.putToStorage(ucCtx.keysId(), ctx.keys());

                    UpsertClassifiersDTO upsertResult = dataRecordsService.upsertClassifiers(ucCtx);
                    if (Objects.nonNull(upsertResult) && MapUtils.isNotEmpty(upsertResult.getClassifiers())) {
                        retval.setClassifiers(ClassifierRecordConverter.to(upsertResult.getClassifiers().values().stream()
                                .flatMap(List::stream)
                                .collect(Collectors.toList())));
                    }
                }

                if (MapUtils.isNotEmpty(deletes)) {

                    DeleteClassifiersDataRequestContext dcCtx = DeleteClassifiersDataRequestContext.builder()
                            .classifiers(deletes)
                            .build();

                    dcCtx.setOperationId(ctx.getOperationId());
                    dcCtx.putToStorage(dcCtx.keysId(), ctx.keys());

                    DeleteClassifiersDTO deleteResult = dataRecordsService.deleteClassifiers(dcCtx);
                    if (Objects.isNull(deleteResult)) {
                        LOGGER.warn("Unsuccessful classifiers delete.");
                    }
                }
            }
        } catch (DataProcessingException exc) {
            if (ctx.getDqErrors() != null && !ctx.getDqErrors().isEmpty() && exc.getId().equals(ExceptionId.EX_DATA_ORIGIN_UPSERT_NEW_DQ_FAILED_BEFORE)) {
                retval = record;
                DataRecordEtalonConverter.copyDQErrors(ctx.getDqErrors(), retval.getDqErrors());
                success = Boolean.FALSE;
            } else {
                throw exc;
            }
        }

        retval.setEntityType(metaModelService.isEntity(retval.getEntityName()) ? RestConstants.REGISTER_ENTITY_TYPE : RestConstants.LOOKUP_ENTITY_TYPE);
        RestResponse<EtalonRecordRO> response = new RestResponse<>(retval);
        response.setErrors(messages);
        response.setSuccess(success);
        return response;
    }

    /**
     * Deletes a etalon record.
     *
     * @param id etalon record id
     * @return id of the record deleted
     */
    @DELETE
    @Path("{" + RestConstants.DATA_PARAM_ID + "}")
    @ApiOperation(value = "Удалить запись", notes = "")
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response delete(@ApiParam("ID сущности") @PathParam(RestConstants.DATA_PARAM_ID) String id) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_DELETE_BY_ETALON);
        MeasurementPoint.start();
        try {

            DeleteRequestContext ctx = new DeleteRequestContextBuilder()
                    .etalonKey(id)
                    .inactivateEtalon(true)
                    .cascade(DEFAULT_DELETE_CASCADE_VALUE)
                    .build();

            DeleteRecordDTO deleteResult = dataRecordsService.deleteRecord(ctx);
            String deletedKey = getDeletedKey(deleteResult);
            if (StringUtils.isBlank(deletedKey)) {
                final String message = "Golden delete failed (no key received) for ID [{}]!";
                LOGGER.warn(message, id);
                throw new DataProcessingException(message, ExceptionId.EX_DATA_ETALON_DELETE_FAILED, id);
            }

            return Response.ok(new UpdateResponse(true, id)).build();
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Deletes a etalon record.
     *
     * @param id etalon record id
     * @return id of the record deleted
     */
    @DELETE
    @Path("/" + RestConstants.PATH_PARAM_WIPE + "/{" + RestConstants.DATA_PARAM_ID + "}")
    @ApiOperation(value = "Удалить запись", notes = "")
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response wipe(@ApiParam("ID сущности") @PathParam(RestConstants.DATA_PARAM_ID) String id) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_DELETE_BY_ETALON);
        MeasurementPoint.start();
        try {

            DeleteRequestContext ctx = new DeleteRequestContextBuilder()
                    .etalonKey(id)
                    .wipe(true)
                    .cascade(DEFAULT_DELETE_CASCADE_VALUE)
                    .build();

            DeleteRecordDTO deleteResult = dataRecordsService.deleteRecord(ctx);
            String deletedKey = getDeletedKey(deleteResult);
            if (StringUtils.isBlank(deletedKey)) {
                final String message = "Etalon wipe failed (no key received) for ID [{}]!";
                LOGGER.warn(message, id);
                throw new DataProcessingException(message, ExceptionId.EX_DATA_ETALON_WIPE_FAILED, id);
            }

            return Response.ok(new UpdateResponse(true, id)).build();
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Gets a data record by ID.
     *
     * @param etalonId the id
     * @return a data record
     */
    @GET
    @Path("/" + RestConstants.PATH_PARAM_ORIGIN + "/{" + RestConstants.DATA_PARAM_ID + "}" + "{p:/?}{" + RestConstants.DATA_PARAM_DATE + ": " + RestConstants.DEFAULT_TIMESTAMP_PATTERN + "}")
    @ApiOperation(value = "Получить ориджин записи по ID эталона", notes = "", response = List.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getOriginsByEtalonId(
            @ApiParam("ID сущности") @PathParam(RestConstants.DATA_PARAM_ID) String etalonId,
            @PathParam(RestConstants.DATA_PARAM_DATE) String dateAsString,
            @ApiParam("Включать неактивные версии в эталон или нет (true/false)") @QueryParam(RestConstants.DATA_PARAM_INCLUDE_INACTIVE) String includeInactiveAsString,
            @ApiParam("Включать неподтвержденные версии в эталон или нет (true/false)") @QueryParam(RestConstants.DATA_PARAM_INCLUDE_DRAFTS) String includeDraftsAsString) {

        Date asOf = ValidityPeriodUtils.parse(dateAsString);

        GetRequestContext ctx = new GetRequestContextBuilder()
                .etalonKey(etalonId)
                .fetchOrigins(true)
                .includeMerged(true)
                .forDate(asOf)
                .fetchRelations(false)
                .fetchClassifiers(true)
                .includeInactive(includeInactiveAsString == null ? false : Boolean.valueOf(includeInactiveAsString))
                .includeDrafts(includeDraftsAsString == null ? false : Boolean.valueOf(includeDraftsAsString))
                .includeWinners(true)
                .tasks(true)
                .build();

        GetRecordDTO result = dataRecordsService.getRecord(ctx);
        if (result == null || result.getEtalon() == null) {
            final String message = "Etalon record not found for ID [{}].";
            LOGGER.warn(message, etalonId);
            throw new DataProcessingException(message,
                    ExceptionId.EX_DATA_ETALON_NOT_FOUND, etalonId);
        }

        List<OriginRecordRO> origins = new ArrayList<>();
        for (OriginRecord o : result.getOrigins()) {

            OriginRecordRO origin = DataRecordOriginConverter.to(o, result.getEtalon());
            for (Entry<String, List<GetClassifierDTO>> entry : result.getClassifiers().entrySet()) {

                if (CollectionUtils.isEmpty(entry.getValue())) {
                    continue;
                }

                for (GetClassifierDTO cdto : entry.getValue()) {

                    if (CollectionUtils.isEmpty(cdto.getOrigins())) {
                        continue;
                    }

                    OriginClassifier ocl = cdto.getOrigins().stream()
                            .filter(vocl ->
                                    Objects.nonNull(vocl.getInfoSection().getRecordOriginKey())
                                            &&  Objects.nonNull(vocl.getInfoSection().getRecordOriginKey().getId())
                                            &&  vocl.getInfoSection().getRecordOriginKey().getId().equals(o.getInfoSection().getOriginKey().getId()))
                            .findFirst()
                            .orElse(null);

                    if (Objects.nonNull(ocl)) {
                        origin.getClassifiers().add(ClassifierRecordConverter.to(ocl));
                        cdto.getOrigins().remove(ocl);
                    }
                }
            }

            origins.add(origin);
        }

        // UN-6431 Check for orphaned classifier record without origins data versions
        Map<String, OriginRecordRO> missing = new HashMap<>();
        result.getClassifiers().values().stream()
                .flatMap(Collection::stream)
                .map(GetClassifierDTO::getOrigins)
                .flatMap(Collection::stream)
                .forEach(orphan -> {

                    OriginRecordRO oro = missing.get(orphan.getInfoSection().getRecordOriginKey().getId());
                    if (Objects.isNull(oro)) {

                        oro = new OriginRecordRO();
                        oro.setOriginId(orphan.getInfoSection().getRecordOriginKey().getId()); // System ID
                        oro.setExternalId(orphan.getInfoSection().getRecordOriginKey().getExternalId()); // Foreign ID
                        oro.setSourceSystem(orphan.getInfoSection().getRecordOriginKey().getSourceSystem()); // Source system
                        oro.setEntityName(orphan.getInfoSection().getRecordOriginKey().getEntityName()); // Entity name
                        oro.setGsn("0");

                        missing.put(oro.getOriginId(), oro);
                    }

                    oro.getClassifiers().add(ClassifierRecordConverter.to(orphan));
                });

        origins.addAll(missing.values());

        return ok(new RestResponse<>(origins));
    }

    /**
     * Gets a data record by ID.
     *
     * @param etalonId the id
     * @return a data record
     */
    @GET
    @Path("/paged/" + RestConstants.PATH_PARAM_ORIGIN + "/{" + RestConstants.DATA_PARAM_ID + "}" + "{p:/?}{"
            + RestConstants.DATA_PARAM_DATE + ": " + RestConstants.DEFAULT_TIMESTAMP_PATTERN + "}")
    @ApiOperation(value = "Получить ориджин записи по ID эталона", notes = "", response = List.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getPagedOriginsByEtalonId(
            @ApiParam("ID сущности") @PathParam(RestConstants.DATA_PARAM_ID) String etalonId,
            @PathParam(RestConstants.DATA_PARAM_DATE) String dateAsString,
            @ApiParam("Включать неактивные версии в эталон или нет (true/false)")
            @QueryParam(RestConstants.DATA_PARAM_INCLUDE_INACTIVE) String includeInactiveAsString,
            @ApiParam("Включать неподтвержденные версии в эталон или нет (true/false)")
            @QueryParam(RestConstants.DATA_PARAM_INCLUDE_DRAFTS) String includeDraftsAsString,
            @ApiParam(value = "С ")
            @QueryParam(START)
                    Integer start,
            @ApiParam(value = "Количество")
            @QueryParam(LIMIT)
                    Integer limit
            ) {
        // Find all origins.
        Response response = getOriginsByEtalonId(etalonId, dateAsString, includeInactiveAsString, includeDraftsAsString);
        RestResponse restResponse = (RestResponse)response.getEntity();

        List<OriginRecordRO> origins = (List<OriginRecordRO>)restResponse.getContent();
        List<OriginRecordRO> pageContent = Collections.emptyList();

        if (start != null && start >= 0 && start < origins.size()) {
            if (limit != null && limit > 0) {
                pageContent = origins.subList(start, (start + limit) > origins.size() ?
                        origins.size() : (start + limit));
            }
        }

        PageImpl<OriginRecordRO> page = new PageImpl<>(pageContent, null, origins.size());

        return ok(new RestResponse<>(page));
    }

    /**
     * Deletes an origin record.
     *
     * @param id origin record id
     * @return deleted id
     */
    @DELETE
    @Path("/" + RestConstants.PATH_PARAM_ORIGIN + "/{" + RestConstants.DATA_PARAM_ID + "}")
    @ApiOperation(value = "Удалить оригинальную запись", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response deleteOrigin(
            @ApiParam("ID сущности")
            @PathParam(RestConstants.DATA_PARAM_ID) String id) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_DELETE_BY_ORIGIN);
        MeasurementPoint.start();

        DeleteRequestContext ctx = new DeleteRequestContextBuilder().originKey(id).inactivateOrigin(true).build();

        try {
            String deletedKey = getDeletedKey(dataRecordsService.deleteRecord(ctx));
            if (StringUtils.isBlank(deletedKey)) {
                final String message = "Origin delete failed (no key received) for ID [{}]!";
                LOGGER.warn(message, id);
                throw new DataProcessingException(message, ExceptionId.EX_DATA_ORIGIN_DELETE_FAILED, id);
            }
            return Response.ok(new UpdateResponse(true, deletedKey)).build();
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Deletes an origin record.
     *
     * @param id origin record id
     * @return deleted id
     */
    @DELETE
    @Path("/"
            + RestConstants.PATH_PARAM_VERSION + "/{"
            + RestConstants.DATA_PARAM_ID + "}/{"
            + RestConstants.DATA_PARAM_TIMESTAMPS + ":.*}")
    @ApiOperation(value = "Пометить версию, как удаленную", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response deleteVersion(
            @ApiParam("ID сущности") @PathParam(RestConstants.DATA_PARAM_ID) String id,
            @ApiParam("Значения границ интервалов. Всегда 2 элемента") @PathParam(RestConstants.DATA_PARAM_TIMESTAMPS) List<PathSegment> timestamps) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_DELETE_BY_PERIOD);
        MeasurementPoint.start();

        Date validFrom = RestUtils.extractStart(timestamps);
        Date validTo = RestUtils.extractEnd(timestamps);

        DeleteRequestContext ctx = new DeleteRequestContextBuilder()
                .etalonKey(id)
                .validFrom(validFrom)
                .validTo(validTo)
                .inactivatePeriod(true)
                .build();

        try {
            String deletedKey = getDeletedKey(dataRecordsService.deleteRecord(ctx));
            if (StringUtils.isBlank(deletedKey)) {
                final String message = "Inactive version PUT failed (no key received) for ID [{}]!";
                LOGGER.warn(message, id);
                throw new DataProcessingException(message, ExceptionId.EX_DATA_ORIGIN_DEACTIVATION_FAILED, id);
            }
            return Response.ok(new UpdateResponse(true, deletedKey)).build();
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Merges several records to a winner record.
     *
     * @param etalonIds the IDs to merge
     * @return winner id
     */
    @PUT
    @Consumes("application/x-www-form-urlencoded; charset=UTF-8")
    @Path("/" + RestConstants.PATH_PARAM_MERGE)
    @ApiOperation(value = "Объединить дубликаты", notes = "Объединить несколько записей дубликатов в пользу одной записи.", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response merge(@ApiParam("ID эталона") @FormParam(RestConstants.DATA_PARAM_ID) List<String> etalonIds, @ApiParam("ID эталона победителя") @FormParam(RestConstants.DATA_PARAM_WINNER_ID) String winnerId) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_MERGE);
        MeasurementPoint.start();
        try {

            if (Objects.isNull(etalonIds) || etalonIds.isEmpty()) {
                return notFound();
            }

            if (Objects.isNull(winnerId) && etalonIds.size() < 2) {
                return notFound();
            }

            List<RecordIdentityContext> contexts = etalonIds.stream()
                    .map(id -> new GetRequestContextBuilder().etalonKey(id).build())
                    .collect(Collectors.toList());

            MergeRequestContext ctx = new MergeRequestContextBuilder().etalonKey(winnerId).duplicates(contexts).manual(true).build();
            MergeRecordsDTO result = dataRecordsService.merge(ctx);
            return ok(new RestResponse<>(result.getWinnerId()));

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Return materialized etalon view in merge case
     *
     * @param etalonIds the
     * @return materialized etalon view
     */
    @GET
    @Path("/" + RestConstants.PATH_PARAM_MERGE)
    @ApiOperation(value = "Виртуально объединить эталоны", notes = "Виртуально объединяет эталоны и возвращает материализованное представление объединенных эталонов", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response mergePreview(@ApiParam("ID эталона") @QueryParam(RestConstants.DATA_PARAM_ID) List<String> etalonIds) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_MERGE_PREVIEW);
        MeasurementPoint.start();
        try {

            if (Objects.isNull(etalonIds) || etalonIds.size() < 2) {
                return notFound();
            }

            //todo think about asOf and lud as parameters of request!
            GetRequestContext ctx = GetRequestContext.builder()
                    .previewKeys(etalonIds)
                    //for security check!
                    //todo fix after add strategy for classifiers
                    .etalonKey(etalonIds.get(etalonIds.size() - 1))
                    .forDate(new Date())
                    .forLastUpdate(null)
                    .build();
            GetRecordDTO getRecordDTO = dataRecordsService.getEtalonRecordPreview(ctx);
            ExtendedRecordRO extendedRecordRO = EtalonPreviewConverter.convert(getRecordDTO);
            extendedRecordRO.setWinnerEtalonId(ctx.getEtalonKey());
            return ok(extendedRecordRO);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Gets the time line for an etalon.
     *
     * @param etalonId the etalon ID
     * @return response
     */
    @GET
    @Path("/" + RestConstants.PATH_PARAM_TIMELINE + "/{"
            + RestConstants.DATA_PARAM_ID + "}")
    @ApiOperation(value = "Запросить таймлайн", notes = "Запросить данные изменения эталона по времени.", response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response recordsTimeline(@PathParam(RestConstants.DATA_PARAM_ID) String etalonId,
                                    @ApiParam("Включать неактивные версии в эталон или нет (true/false)") @QueryParam(RestConstants.DATA_PARAM_INCLUDE_INACTIVE) String includeInactiveAsString,
                                    @ApiParam("Включать неподтвержденные версии в эталон или нет (true/false)") @QueryParam(RestConstants.DATA_PARAM_INCLUDE_DRAFTS) String includeDraftsAsString) {

        GetRequestContext ctx = new GetRequestContextBuilder()
                .etalonKey(etalonId)
                .includeInactive(includeDraftsAsString == null ? false : Boolean.valueOf(includeInactiveAsString))
                .includeDrafts(includeDraftsAsString == null ? false : Boolean.valueOf(includeDraftsAsString))
                .tasks(true)
                .build();

        TimelineDTO timeline = dataRecordsService.getRecordsTimeline(ctx);
        return Response
                .ok(new RestResponse<>(TimelineToTimelineROConverter.convert(timeline)))
                .build();
    }

    /**
     * Gets the etalon id for an external id.
     *
     * @param externalId   external ID
     * @param sourceSystem source system
     * @param entityName   entity name
     * @return response
     */
    @GET
    @Path("/" + RestConstants.PATH_PARAM_KEYS + "/" + RestConstants.PATH_PARAM_EXTERNAL
            + "/{" + RestConstants.DATA_PARAM_EXT_ID + "}"
            + "/{" + RestConstants.DATA_PARAM_SOURCE_SYSTEM + "}"
            + "/{" + RestConstants.DATA_PARAM_NAME + "}")
    @ApiOperation(value = "Запросить ключи по внешнему ID записи", notes = "Запросить ключи по внешнему ID записи.", response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response fetchKeysByExternalId(
            @ApiParam("Внешний ключ записи") @PathParam(RestConstants.DATA_PARAM_EXT_ID) String externalId,
            @ApiParam("Система источник записи") @PathParam(RestConstants.DATA_PARAM_SOURCE_SYSTEM) String sourceSystem,
            @ApiParam("Имя справочника/реестра") @PathParam(RestConstants.DATA_PARAM_NAME) String entityName) {

        GetRequestContext ctx = GetRequestContext.builder()
                .externalId(externalId)
                .sourceSystem(sourceSystem)
                .entityName(entityName)
                .build();

        RecordKeys keys = dataRecordsService.identify(ctx);
        return Response
                .ok(new RestResponse<>(RecordKeysConverter.to(keys)))
                .build();
    }

    /**
     * Gets keys for an etalon id.
     *
     * @param id etalon ID
     * @return response
     */
    @GET
    @Path("/" + RestConstants.PATH_PARAM_KEYS + "/" + RestConstants.PATH_PARAM_ETALON + "/{" + RestConstants.DATA_PARAM_ID + "}")
    @ApiOperation(value = "Запросить ключи по эталонному ID записи", notes = "Запросить ключи по эталонному ID записи.", response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response fetchKeysByEtalonId(
            @ApiParam("Эталонный ключ записи") @PathParam(RestConstants.DATA_PARAM_ID) String id) {

        GetRequestContext ctx = GetRequestContext.builder()
                .etalonKey(id)
                .build();

        RecordKeys keys = dataRecordsService.identify(ctx);
        return Response
                .ok(new RestResponse<>(RecordKeysConverter.to(keys)))
                .build();
    }

    /**
     * Gets the keys for an origin id.
     *
     * @param originId origin ID
     * @return response
     */
    @GET
    @Path("/" + RestConstants.PATH_PARAM_KEYS + "/" + RestConstants.PATH_PARAM_ORIGIN + "/{" + RestConstants.DATA_PARAM_ID + "}")
    @ApiOperation(value = "Запросить ключи по оригинальному ID записи", notes = "Запросить ключи по оригинальному ID записи.", response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response fetchKeysByOriginId(
            @ApiParam("Оригинальный ключ записи") @PathParam(RestConstants.DATA_PARAM_ID) String originId) {

        GetRequestContext ctx = GetRequestContext.builder()
                .originKey(originId)
                .build();

        RecordKeys keys = dataRecordsService.identify(ctx);
        return Response
                .ok(new RestResponse<>(RecordKeysConverter.to(keys)))
                .build();
    }

    /**
     * Gets BLOB data associated with an attribute of a golden record.
     *
     * @param id   record id.
     * @param attr attribute name
     * @return byte stream
     */
    @GET
    @Path("/" + RestConstants.PATH_PARAM_BLOB + "/{" + RestConstants.DATA_PARAM_ID + "}")
    @ApiOperation(value = "Получить двоичные данные записи по ID и имени аттрибута.", notes = "", response = StreamingOutput.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response fetchEtalonBlobData(
            @ApiParam("ID записи") @PathParam(RestConstants.DATA_PARAM_ID) String id) {
        FetchLargeObjectRequestContext ctx = new FetchLargeObjectRequestContextBuilder()
                .binary(true)
                .recordKey(id)
                .build();
        final LargeObjectDTO result = dataRecordsService.fetchLargeObject(ctx);
        String encodedFilename = FileUtils.encodePath(result.getFileName());

        return Response.ok(createStreamingOutputForLargeObject(result))
                .encoding("UTF-8")
                .header("Content-Disposition", "attachment; filename="
                        + result.getFileName()
                        + "; filename*=UTF-8''"
                        + encodedFilename)
                .header("Content-Type", StringUtils.isEmpty(result.getMimeType()) ? MediaType.APPLICATION_OCTET_STREAM_TYPE : result.getMimeType())
                .build();
    }

    /**
     * Gets BLOB data associated with an attribute of an origin record.
     *
     * @param id   record id.
     * @param attr attribute name
     * @return byte stream
     */
    @GET
    @Path("/" + RestConstants.PATH_PARAM_ORIGIN + "/" + RestConstants.PATH_PARAM_BLOB + "/{" + RestConstants.DATA_PARAM_ID + "}")
    @ApiOperation(value = "Получить двоичные данные origin записи по ID и имени аттрибута.", notes = "", response = StreamingOutput.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response fetchOriginBlobData(
            @ApiParam("ID записи") @PathParam(RestConstants.DATA_PARAM_ID) String id) {
        FetchLargeObjectRequestContext ctx = new FetchLargeObjectRequestContextBuilder()
                .binary(true)
                .recordKey(id)
                .build();
        final LargeObjectDTO result = dataRecordsService.fetchLargeObject(ctx);

        String encodedFilename = FileUtils.encodePath(result.getFileName());

        return Response.ok(createStreamingOutputForLargeObject(result))
                .encoding("UTF-8")
                .header("Content-Disposition", "attachment; filename="
                        + result.getFileName()
                        + "; filename*=UTF-8''"
                        + encodedFilename)
                .header("Content-Type", StringUtils.isEmpty(result.getMimeType()) ? MediaType.APPLICATION_OCTET_STREAM_TYPE : result.getMimeType())
                .build();
    }

    /**
     * Gets CLOB data associated with an attribute of a golden record.
     *
     * @param id   record id.
     * @param attr attribute name
     * @return byte stream
     */
    @GET
    @Path("/" + RestConstants.PATH_PARAM_CLOB + "/{" + RestConstants.DATA_PARAM_ID + "}")
    @ApiOperation(value = "Получить большие символьные данные записи по ID и имени аттрибута.", notes = "", response = StreamingOutput.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    @Produces("text/plain")
    public Response fetchEtalonClobData(
            @ApiParam("ID записи") @PathParam(RestConstants.DATA_PARAM_ID) String id) {
        FetchLargeObjectRequestContext ctx = new FetchLargeObjectRequestContextBuilder()
                .binary(false)
                .recordKey(id)
                .build();
        final LargeObjectDTO result = dataRecordsService.fetchLargeObject(ctx);
        String encodedFilename;
        try {
            encodedFilename = URLEncoder.encode(result.getFileName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            final String message = "Incorrect file name encoding [{}].";
            LOGGER.warn(message, result.getFileName(), e);
            throw new DataProcessingException(message, ExceptionId.EX_DATA_INCORRECT_ENCODING,
                    result.getFileName());
        }
        return Response.ok(createStreamingOutputForLargeObject(result))
                .encoding("UTF-8")
                .header("Content-Disposition", "attachment; filename="
                        + result.getFileName()
                        + "; filename*=UTF-8''"
                        + encodedFilename)
                .header("Content-Type", StringUtils.isEmpty(result.getMimeType()) ? MediaType.TEXT_PLAIN_TYPE : result.getMimeType())
                .build();
    }

    /**
     * Gets CLOB data associated with an attribute of an origin record.
     *
     * @param id   record id.
     * @param attr attribute name
     * @return byte stream
     */
    @GET
    @Path("/" + RestConstants.PATH_PARAM_ORIGIN + "/" + RestConstants.PATH_PARAM_CLOB + "/{" + RestConstants.DATA_PARAM_ID + "}")
    @ApiOperation(value = "Получить большие символьные  данные origin записи по ID и имени аттрибута.", notes = "", response = StreamingOutput.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    @Produces("text/plain")
    public Response fetchOriginClobData(
            @ApiParam("ID записи") @PathParam(RestConstants.DATA_PARAM_ID) String id) {
        FetchLargeObjectRequestContext ctx = new FetchLargeObjectRequestContextBuilder()
                .binary(false)
                .recordKey(id)
                .build();
        final LargeObjectDTO result = dataRecordsService.fetchLargeObject(ctx);
        String encodedFilename;
        try {
            encodedFilename = URLEncoder.encode(result.getFileName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            final String message = "Incorrect file name encoding [{}].";
            LOGGER.warn(message, result.getFileName(), e);
            throw new DataProcessingException(message, ExceptionId.EX_DATA_INCORRECT_ENCODING,
                    result.getFileName());
        }

        return Response.ok(createStreamingOutputForLargeObject(result))
                .encoding("UTF-8")
                .header("Content-Disposition", "attachment; filename="
                        + result.getFileName()
                        + "; filename*=UTF-8''"
                        + encodedFilename)
                .header("Content-Type", StringUtils.isEmpty(result.getMimeType()) ? MediaType.TEXT_PLAIN_TYPE : result.getMimeType())
                .build();
    }

    /**
     * Saves binary large object.
     *
     * @param id         golden record id
     * @param attr       attribute
     * @param attachment attachment object
     * @return ok/nok
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/" + RestConstants.PATH_PARAM_BLOB + "/{" + RestConstants.DATA_PARAM_ATTR + "}" + "{p:/?}{" + RestConstants.DATA_PARAM_ID + ": (([a-zA-Z0-9\\-]{36})?)}")
    @ApiOperation(value = "Загрузить двоичные данные записи для ID и имени аттрибута.", notes = "", response = LargeObjectRO.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response uploadEtalonBlobData(
            @ApiParam("Имя аттрибута") @PathParam(RestConstants.DATA_PARAM_ATTR) String attr,
            @ApiParam("ID объекта") @PathParam(RestConstants.DATA_PARAM_ID) String id,
            @Multipart(required = true, value = RestConstants.DATA_PARAM_FILE) Attachment attachment) {

        SaveLargeObjectRequestContext ctx = new SaveLargeObjectRequestContextBuilder()
                .attribute(attr)
                .recordKey(id)
                .binary(true)
                .inputStream(attachment.getObject(InputStream.class))
                .filename(attachment.getContentDisposition().getParameter(RestConstants.DATA_PARAM_FILENAME))
                .mimeType(attachment.getContentType().toString())
                .build();

        LargeObjectDTO result = dataRecordsService.saveLargeObject(ctx);
        return Response.ok(new RestResponse<>(LargeObjectToRestLargeObjectConverter.convert(result))).build();
    }

    /**
     * Saves character large object.
     *
     * @param id         golden record id
     * @param attr       attribute
     * @param attachment attachment object
     * @return ok/nok
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/" + RestConstants.PATH_PARAM_CLOB + "/{" + RestConstants.DATA_PARAM_ATTR + "}" + "{p:/?}{" + RestConstants.DATA_PARAM_ID + ": (([a-zA-Z0-9\\-]{36})?)}")
    @ApiOperation(value = "Загрузить символьные данные записи для ID и имени аттрибута.", notes = "", response = EntityRecordRestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response uploadEtalonСlobData(
            @ApiParam(value = "Имя аттрибута") @PathParam(RestConstants.DATA_PARAM_ATTR) String attr,
            @ApiParam(value = "ID объекта") @PathParam(RestConstants.DATA_PARAM_ID) String id,
            @Multipart(required = true, value = RestConstants.DATA_PARAM_FILE) Attachment attachment) {

        if (!"text".equals(attachment.getContentType().getType())) {
            throw new DataProcessingException("The media type [{}] is not allowed for character objects.",
                    ExceptionId.EX_DATA_INVALID_CLOB_OBJECT, attachment.getContentType().toString());
        }

        SaveLargeObjectRequestContext ctx = new SaveLargeObjectRequestContextBuilder()
                .attribute(attr)
                .recordKey(id)
                .binary(false)
                .inputStream(attachment.getObject(InputStream.class))
                .filename(attachment.getContentDisposition().getParameter(RestConstants.DATA_PARAM_FILENAME))
                .mimeType(attachment.getContentType().toString())
                .build();

        LargeObjectDTO result = dataRecordsService.saveLargeObject(ctx);
        return Response.ok(new RestResponse<>(LargeObjectToRestLargeObjectConverter.convert(result))).build();
    }

    /**
     * Deletes golden blob data.
     *
     * @param id   the id
     * @param attr the attribute
     * @return ok/nok
     */
    @DELETE
    @Path("/" + RestConstants.PATH_PARAM_BLOB + "/{" + RestConstants.DATA_PARAM_ID + "}")
    @ApiOperation(value = "Удалить двоичные данные записи для ID и имени аттрибута.", notes = "", response = EntityRecordRestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response deleteEtalonBlobData(
            @ApiParam(value = "ID сущности") @PathParam(RestConstants.DATA_PARAM_ID) String id,
            @ApiParam(value = "Имя аттрибута") @PathParam(RestConstants.DATA_PARAM_ATTR) String attr) {

        DeleteLargeObjectRequestContext ctx = new DeleteLargeObjectRequestContextBuilder()
                .recordKey(id)
                .attribute(attr)
                .binary(true)
                .build();

        UpdateResponse response = new UpdateResponse(dataRecordsService.deleteLargeObject(ctx), id);
        return Response.ok(new RestResponse<>(response)).build();
    }

    /**
     * Deletes golden CLOB data.
     *
     * @param id   the id
     * @param attr the attribute
     * @return ok/nok
     */
    @DELETE
    @Path("/" + RestConstants.PATH_PARAM_CLOB + "/{" + RestConstants.DATA_PARAM_ID + "}")
    @ApiOperation(value = "Удалить двоичные данные записи для ID и имени аттрибута.", notes = "", response = EntityRecordRestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response deleteEtalonClobData(
            @ApiParam(value = "ID сущности") @PathParam(RestConstants.DATA_PARAM_ID) String id,
            @ApiParam(value = "Имя аттрибута") @PathParam(RestConstants.DATA_PARAM_ATTR) String attr) {

        DeleteLargeObjectRequestContext ctx = new DeleteLargeObjectRequestContextBuilder()
                .recordKey(id)
                .attribute(attr)
                .binary(false)
                .build();

        UpdateResponse response = new UpdateResponse(dataRecordsService.deleteLargeObject(ctx), id);
        return Response.ok(new RestResponse<>(response)).build();
    }

    /**
     * Restore previously deleted record.
     *
     * @param record record id.
     * @return
     * @throws Exception
     */
    @PUT
    @Path("/restore/" + "{" + RestConstants.DATA_PARAM_ID + "}")
    @ApiOperation(value = "Восстанавливает запись по определенному ID", notes = "Восстановить запись с определенным ID.", response = RestResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occured", response = ErrorResponse.class)})
    public Response restore(
            @ApiParam(value = "Запись требующая восстановления. Передается полная запись!") EtalonRecordRO record) {
        RestResponse<EtalonRecordRO> response = null;
        // TODO: rewrite restore validation (need to validate new record, not the existing)
        Response validationResponse
                = check(record.getEtalonId(), ValidityPeriodUtils.asString(ConvertUtils.localDateTime2Date(record.getValidFrom())));
        if (!record.isModified()
                && !(validationResponse.getEntity() instanceof UpdateResponse
                && ((UpdateResponse) validationResponse.getEntity()).isSuccess())) {
            return validationResponse;
        }
        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_RESTORE);
        MeasurementPoint.start();
        // try to restore given record.
        // It may fail in case if it's have references to inactive records.
        UpsertRequestContext ctx = new UpsertRequestContextBuilder()
                .record(DataRecordEtalonConverter.from(record))
                .etalonKey(record.getEtalonId())
                .sourceSystem(metaModelService.getAdminSourceSystem().getName())
                .entityName(record.getEntityName())
                .validFrom(ConvertUtils.localDateTime2Date(record.getValidFrom()))
                .validTo(ConvertUtils.localDateTime2Date(record.getValidTo()))
                .returnEtalon(true)
                .originStatus(RecordStatus.ACTIVE)
                .build();

        EtalonRecordRO retval = null;
        try {
            EtalonRecordDTO result = dataRecordsService.restore(ctx, record.isModified());
            retval = DataRecordEtalonConverter.to(result.getEtalon(), ctx.getDqErrors(), null);
            response = new RestResponse<>(retval);
            response.setSuccess(ctx.getDqErrors().isEmpty());
        } catch (Exception exc) {
            retval = record;
            retval.getDqErrors().clear();
            DataRecordEtalonConverter.copyDQErrors(ctx.getDqErrors(), retval.getDqErrors());
            retval.setEntityType(metaModelService.isEntity(record.getEntityName()) ? RestConstants.REGISTER_ENTITY_TYPE : RestConstants.LOOKUP_ENTITY_TYPE);
            response = new RestResponse<>(retval);
            response.setSuccess(false);
        } finally {
            MeasurementPoint.stop();
        }

        return ok(response);
    }

    /**
     * Check that deleted record can be restored.
     *
     * @param record record id.
     * @return
     * @throws ParseException
     */
    @GET
    @Path("/pre-restore-validation/" + "{" + RestConstants.DATA_PARAM_ID + "}{p:/?}{" + RestConstants.DATA_PARAM_DATE
            + ": " + RestConstants.DEFAULT_TIMESTAMP_PATTERN + "}")
    @ApiOperation(value = "Проверяет может ли быть восстановлена запись", notes = "Проверяет может ли быть восстановлена запись.", response = RestResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occured", response = ErrorResponse.class)})
    public Response check(@PathParam(RestConstants.DATA_PARAM_ID) String etalonId, @PathParam(RestConstants.DATA_PARAM_DATE) String dateAsString) {

        Date asOf = ValidityPeriodUtils.parse(dateAsString);
        Multimap<AttributeInfoHolder, Object> missedLookupEntities = validationService.getMissedLinkedLookupEntities(
                etalonId, asOf);
        if (missedLookupEntities == null || missedLookupEntities.size() == 0) {
            return Response.ok(new UpdateResponse(true, etalonId)).build();
        }
        UpdateResponse response = new UpdateResponse(false, etalonId);
        List<ErrorInfo> errors = new ArrayList<>();
        ErrorInfo error = new ErrorInfo();
        error.setErrorCode(ExceptionId.EX_DATA_CANNOT_DELETE_REF_EXIST.name());
        error.setSeverity(Severity.LOW);
        error.setInternalMessage("Some lookup entities referenced by this record are missing!");
        error.setUserMessage("Повторите восстановление после проверки значений");
        errors.add(error);
        response.setErrors(errors);
        List<Param> params = missedLookupEntities.entries()
                .stream()
                .filter(ent -> ent.getValue() != null)
                .map(ent -> new Param(ent.getKey().getPath(),
                        ent.getValue().toString()))
                .collect(Collectors.toList());
        error.setParams(params);
        return Response.ok(response).build();
    }

    //DO NOT REMOVE! This crappy workaround required for Swagger to generate API docs
    private static class EntityRecordRestResponse extends RestResponse<EtalonRecordRO> {
        @Override
        public EtalonRecordRO getContent() {
            return null;
        }
    }

    /**
     * Gets the key, that was actually deleted.
     *
     * @param result delete operation result
     * @return key
     */
    private String getDeletedKey(DeleteRecordDTO result) {
        return result.wasSuccess()
                ? result.getEtalonKey().getId()
                : StringUtils.EMPTY;
    }

    /**
     * Gets {@link StreamingOutput} for a context.
     *
     * @param ctx the context
     * @return streaming output
     */
    private StreamingOutput createStreamingOutputForLargeObject(final LargeObjectDTO result) {
        return output -> {

            try (InputStream is = result.getInputStream()) {

                byte[] buf = new byte[FileUtils.DEFAULT_BUFFER_SIZE];
                int count = -1;
                while ((count = is.read(buf, 0, buf.length)) != -1) {
                    output.write(buf, 0, count);
                }
            } catch (Exception exc) {
                LOGGER.warn("Exception cought while BLOB I/O.", exc);
            }
        };
    }

    /**
     * Detach origin record from current etalon record.
     *
     * @param originId Origin id, which detach
     */
    @GET
    @Path("/detach-origin/" + "{" + RestConstants.PATH_PARAM_ORIGIN_ID + "}")
    @ApiOperation(value = "Отцеляет запись от текущего эталона.", notes = "Отцеляет запись от текущего эталона.", response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occured", response = ErrorResponse.class)
    })
    public Response detachOrigin(@PathParam(RestConstants.PATH_PARAM_ORIGIN_ID) String originId) {
        final SplitRecordsDTO splitResult = dataRecordsService.detachOrigin(originId);
        if(CollectionUtils.isEmpty(splitResult.getErrors())) {
            return Response.ok(new RestResponse<>(splitResult.getEtalonId())).build();
        } else {
            RestResponse response = new RestResponse<>(splitResult.getEtalonId(), true);
            response.setErrors(splitResult.getErrors()
                    .stream()
                    .map(ErrorInfoToRestErrorInfoConverter::convert)
                    .collect(Collectors.toList()));
            return Response.ok(response).build();
        }
    }
}

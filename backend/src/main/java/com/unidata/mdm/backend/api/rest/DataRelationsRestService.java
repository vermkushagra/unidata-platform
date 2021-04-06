package com.unidata.mdm.backend.api.rest;

import static com.unidata.mdm.backend.service.search.util.RelationHeaderField.FIELD_ETALON_ID;
import static com.unidata.mdm.backend.service.search.util.RelationHeaderField.FIELD_FROM_ETALON_ID;
import static com.unidata.mdm.backend.service.search.util.RelationHeaderField.REL_NAME;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.api.rest.converter.DataRecordEtalonConverter;
import com.unidata.mdm.backend.api.rest.converter.ErrorInfoToRestErrorInfoConverter;
import com.unidata.mdm.backend.api.rest.converter.IntegralRecordEtalonConverter;
import com.unidata.mdm.backend.api.rest.converter.RelationToEtalonConverter;
import com.unidata.mdm.backend.api.rest.converter.RolesConverter;
import com.unidata.mdm.backend.api.rest.converter.SearchResultToRestSearchResultConverter;
import com.unidata.mdm.backend.api.rest.converter.TimelineToTimelineROConverter;
import com.unidata.mdm.backend.api.rest.converter.WorkflowTaskConverter;
import com.unidata.mdm.backend.api.rest.dto.ErrorInfo;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.data.BaseRelationRO;
import com.unidata.mdm.backend.api.rest.dto.data.EtalonIntegralRecordRO;
import com.unidata.mdm.backend.api.rest.dto.data.EtalonRecordRO;
import com.unidata.mdm.backend.api.rest.dto.data.EtalonRelationToRO;
import com.unidata.mdm.backend.api.rest.dto.data.RelationDigestRO;
import com.unidata.mdm.backend.api.rest.dto.data.TimelineRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchResultHitFieldRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchResultHitRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchResultRO;
import com.unidata.mdm.backend.api.rest.util.RestUtils;
import com.unidata.mdm.backend.api.rest.util.SearchResultHitModifier;
import com.unidata.mdm.backend.common.ConvertUtils;
import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext.DeleteRelationRequestContextBuilder;
import com.unidata.mdm.backend.common.context.GetRelationRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationRequestContext.GetRelationRequestContextBuilder;
import com.unidata.mdm.backend.common.context.GetRelationsDigestRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationsDigestRequestContext.GetRelationsDigestRequestContextBuilder;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext.UpsertRelationRequestContextBuilder;
import com.unidata.mdm.backend.common.context.UpsertRelationsRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRelationsRequestContext.UpsertRelationsRequestContextBuilder;
import com.unidata.mdm.backend.common.dto.DeleteRelationDTO;
import com.unidata.mdm.backend.common.dto.GetRelationDTO;
import com.unidata.mdm.backend.common.dto.RelationDigestDTO;
import com.unidata.mdm.backend.common.dto.RelationStateDTO;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.common.dto.TimeIntervalDTO;
import com.unidata.mdm.backend.common.dto.TimelineDTO;
import com.unidata.mdm.backend.common.dto.UpsertRelationDTO;
import com.unidata.mdm.backend.common.dto.UpsertRelationsDTO;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.runtime.MeasurementContextName;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchRequestType;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.EtalonRelation;
import com.unidata.mdm.backend.common.types.RelationSide;
import com.unidata.mdm.backend.common.types.RelationType;
import com.unidata.mdm.backend.service.search.util.RecordHeaderField;
import com.unidata.mdm.backend.service.search.util.RelationHeaderField;
import com.unidata.mdm.backend.util.IdUtils;
import com.unidata.mdm.backend.util.ValidityPeriodUtils;
import com.unidata.mdm.meta.AbstractAttributeDef;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SimpleDataType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
/**
 * @author Mikhail Mikhailov
 */
@Path(RestConstants.PATH_PARAM_DATA + "/" + DataRelationsRestService.PATH_PARAM_RELATIONS )
@Api(value = "relation_entities", description = "Данные связей справочников", produces = "application/json")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class DataRelationsRestService extends AbstractRestService {

    /**
     * Relations.
     */
    public static final String PATH_PARAM_RELATIONS = "relations";
    /**
     * Relations digest.
     */
    public static final String PATH_PARAM_DIGEST = "digest";
    /**
     * Relation.
     */
    public static final String PATH_PARAM_RELATION = "relation";
    /**
     * Relation to specific.
     */
    public static final String PATH_PARAM_RELTO = "relto";
    /**
     * Integral specific.
     */
    public static final String PATH_PARAM_INTEGRAL = "integral";
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
     * Search service.
     */
    @Autowired
    private SearchService searchService;
    /**
     * Modify search result for ui presentation
     */
    @Autowired
    private SearchResultHitModifier searchResultHitModifier;
    /**
     * Constructor.
     */
    public DataRelationsRestService() {
        super();
    }

    /**
     * Gets the time line for an etalon.
     * @param etalonId the etalon ID
     * @return response
     */
    /*
    @GET
    @Path("/" + PATH_PARAM_RELATIONS + "/" + PATH_PARAM_TIMELINE + "/{" + RestConstants.DATA_PARAM_ID + "}/{" + RestConstants.DATA_PARAM_NAME + "}")
    @ApiOperation(value = "Запросить таймлайн для связей эталона", notes = "Запросить данные изменения связей эталона по времени.",
        response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getRelationsTimelineByRecordEtalonId(
            @PathParam(RestConstants.DATA_PARAM_ID) String etalonId,
            @PathParam(RestConstants.DATA_PARAM_NAME) String name) {

        List<TimelineRO> timelines = new ArrayList<>();
        List<TimelineDTO> returned = dataRecordsService.getRelationsTimeline(etalonId, name);
        TimelineToTimelineROConverter.convert(returned, timelines);

        return Response.ok(new RestResponse<>(timelines)).build();
    }
    */

    /**
     * Gets the time line for an etalon.
     * @param etalonId the etalon ID
     * @return response
     */
    /*
    @GET
    @Path("/" + PATH_PARAM_RELATIONS + "/" + PATH_PARAM_TIMELINE + "/{" + RestConstants.DATA_PARAM_ID + "}/{" + RestConstants.DATA_PARAM_NAME + "}")
    @ApiOperation(value = "Запросить таймлайн для связей эталона", notes = "Запросить данные изменения связей эталона по времени.",
        response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getRelationsTimelineByRecordEtalonId(
            @PathParam(RestConstants.DATA_PARAM_ID) String etalonId,
            @PathParam(RestConstants.DATA_PARAM_NAME) String name) {

        List<TimelineRO> timelines = new ArrayList<>();
        List<TimelineDTO> returned = dataRecordsService.getRelationsTimeline(etalonId, name);
        TimelineToTimelineROConverter.convert(returned, timelines);

        return Response.ok(new RestResponse<>(timelines)).build();
    }
    */



    /**
     * Gets all etalon relations for a date.
     * @param etalonId
     * @param name
     * @param dateAsString
     * @return
     * @throws ParseException
     */
    /*
    @GET
    @Path("/" + PATH_PARAM_RELATIONS + "/{" + RestConstants.DATA_PARAM_ID + "}/{" + RestConstants.DATA_PARAM_NAME + "}" + "{p:/?}{" + RestConstants.DATA_PARAM_DATE + ": (([0-9T+\\-\\:\\.]{29})?)}")
    @ApiOperation(value = "Запросить связи эталона на дату", notes = "Запросить связи эталона на определенную дату либо на сейчас, если дата не задана.",
        response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getRelationsByEtalonId(
            @PathParam(RestConstants.DATA_PARAM_ID) String etalonId,
            @PathParam(RestConstants.DATA_PARAM_NAME) String name,
            @PathParam(RestConstants.DATA_PARAM_DATE) String dateAsString)
                    throws ParseException {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_RELATIONS_GET);
        MeasurementPoint.start();
        try {

            Date asOf = null;
            if (dateAsString != null) {
                asOf = DateUtils.parseDate(dateAsString, Constants.INPUT_TIMESTAMP_PATTERN);
            }

            GetRelationsRequestContext ctx = new GetRelationsRequestContextBuilder()
                    .etalonKey(etalonId)
                    .relationName(name)
                    .forDate(asOf)
                    .build();

            GetRelationsDTO result = dataRecordsService.getRelations(ctx);

            List<RelationRO> found = new ArrayList<>();
            for (Entry<RelationStateDTO, List<RelationDTO>> entry
                    : result.getRelations().entrySet()) {

                found.add(RelationSetToRelationROConverter.convert(entry.getKey(),
                        entry.getValue()));
            }

            return Response.ok(new RestResponse<>(found)).build();

        } finally {
            MeasurementPoint.stop();
        }
    }
    */


    /**
     * Gets etalon relation data object by relation etalon ID.
     * @param etalonId relation's etalon ID
     * @param name rel name
     * @param dateAsString date
     * @param includeInactiveAsString include inactive
     * @param includeDraftsAsString include drafts
     * @return response array of time line objects
     */
    @GET
    @Path("/" + PATH_PARAM_RELATION
            + "/" + RestConstants.PATH_PARAM_TIMELINE
            + "/{" + RestConstants.DATA_PARAM_ID + "}{p:/?}{"
            + RestConstants.DATA_PARAM_DATE
            + ": " + RestConstants.DEFAULT_TIMESTAMP_PATTERN + "}")
    @ApiOperation(value = "Запросить связь эталона на дату", notes = "Запросить связи эталона на определенную дату либо на сейчас, если дата не задана.",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getRelationTimelineByRelationEtalonIdAndDate(
            @ApiParam(value = "ID эталонной записи.") @PathParam(RestConstants.DATA_PARAM_ID) String etalonId,
            @ApiParam(value = "Имя связи.") @PathParam(RestConstants.DATA_PARAM_NAME) String name,
            @ApiParam(value = "Дата для получения периода.") @PathParam(RestConstants.DATA_PARAM_DATE) String dateAsString,
            @ApiParam("Включать неактивные версии в эталон или нет (true/false)") @QueryParam(RestConstants.DATA_PARAM_INCLUDE_INACTIVE) String includeInactiveAsString,
            @ApiParam("Включать неподтвержденные версии в эталон или нет (true/false)") @QueryParam(RestConstants.DATA_PARAM_INCLUDE_DRAFTS) String includeDraftsAsString) {

        Date asOf = ValidityPeriodUtils.parse(dateAsString);

        TimelineDTO returned = dataRecordsService.getRelationTimeline(etalonId, asOf,
                includeDraftsAsString == null ? false : Boolean.valueOf(includeDraftsAsString),
                true);

        return Response.ok(new RestResponse<>(TimelineToTimelineROConverter.convert(returned))).build();
    }

    /**
     * Gets etalon relation objects along with their time lines by record etalon ID.
     * @param etalonId record's etalon ID
     * @param timestamps records validity period boundary
     * @return response array of time line objects
     */
    @GET
    @Path("/" + PATH_PARAM_RELATION
            + "/" + RestConstants.PATH_PARAM_TIMELINE
            + "/{" + RestConstants.DATA_PARAM_ID + "}/{" + RestConstants.DATA_PARAM_TIMESTAMPS + ":.*}")
    @ApiOperation(value = "Запросить связи эталона на дату", notes = "Запросить связи эталона на определенную дату либо на сейчас, если дата не задана.",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getRelationTimelineByRelationEtalonIdAndBoundary(
            @ApiParam(value = "ID эталонной записи.") @PathParam(RestConstants.DATA_PARAM_ID) String etalonId,
            @ApiParam(value = "Значения границ интервалов. Всегда 2 элемента") @PathParam(RestConstants.DATA_PARAM_TIMESTAMPS) List<PathSegment> timestamps,
            @ApiParam("Включать неактивные версии в эталон или нет (true/false)") @QueryParam(RestConstants.DATA_PARAM_INCLUDE_INACTIVE) String includeInactiveAsString,
            @ApiParam("Включать неподтвержденные версии в эталон или нет (true/false)") @QueryParam(RestConstants.DATA_PARAM_INCLUDE_DRAFTS) String includeDraftsAsString)
            throws ParseException {

        Date validFrom = RestUtils.extractStart(timestamps);
        Date validTo = RestUtils.extractEnd(timestamps);

        TimelineDTO returned = dataRecordsService.getRelationTimeline(etalonId, validFrom, validTo,
                includeDraftsAsString == null ? false : Boolean.valueOf(includeDraftsAsString),
                true);

        return Response.ok(new RestResponse<>(TimelineToTimelineROConverter.convert(returned))).build();
    }

    /**
     * Gets etalon relation data object by relation etalon ID.
     * @param etalonId relation's etalon ID
     * @param dateAsString
     * @return response array of time line objects
     */
    @GET
    @Path("/" + RestConstants.PATH_PARAM_TIMELINE
            + "/{" + RestConstants.DATA_PARAM_ID + "}/{" + RestConstants.DATA_PARAM_NAME + "}{p:/?}{"
            + RestConstants.DATA_PARAM_DATE + ": " + RestConstants.DEFAULT_TIMESTAMP_PATTERN + "}")
    @ApiOperation(value = "Запросить связь эталона на дату", notes = "Запросить связи эталона на определенную дату либо на сейчас, если дата не задана.",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getRelationsTimelineByRecordEtalonIdAndDate(
            @ApiParam(value = "ID эталонной записи.") @PathParam(RestConstants.DATA_PARAM_ID) String etalonId,
            @ApiParam(value = "Имя связи.") @PathParam(RestConstants.DATA_PARAM_NAME) String name,
            @ApiParam(value = "Дата для получения периода.") @PathParam(RestConstants.DATA_PARAM_DATE) String dateAsString,
            @ApiParam("Включать неактивные версии в эталон или нет (true/false)") @QueryParam(RestConstants.DATA_PARAM_INCLUDE_INACTIVE) String includeInactiveAsString,
            @ApiParam("Включать неподтвержденные версии в эталон или нет (true/false)") @QueryParam(RestConstants.DATA_PARAM_INCLUDE_DRAFTS) String includeDraftsAsString) {

        Date asOf = ValidityPeriodUtils.parse(dateAsString);

        List<TimelineRO> timelines = new ArrayList<>();
        List<TimelineDTO> returned = dataRecordsService.getRelationsTimeline(etalonId, name, asOf,
                includeDraftsAsString == null ? false : Boolean.valueOf(includeDraftsAsString),
                true);

        TimelineToTimelineROConverter.convert(returned, timelines);

        return Response.ok(new RestResponse<>(timelines)).build();
    }

    /**
     * Gets etalon relation objects along with their time lines by record etalon ID.
     * @param etalonId record's etalon ID
     * @param name name of the relation
     * @param timestamps records validity period boundary
     * @return response array of time line objects
     */
    @GET
    @Path("/" + RestConstants.PATH_PARAM_TIMELINE + "/{"
            + RestConstants.DATA_PARAM_ID + "}/{"
            + RestConstants.DATA_PARAM_NAME + "}/{"
            + RestConstants.DATA_PARAM_TIMESTAMPS + ":.*}")
    @ApiOperation(value = "Запросить связи эталона на дату", notes = "Запросить связи эталона на определенную дату либо на сейчас, если дата не задана.",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getRelationsTimelineByRecordEtalonIdAndBoundary(
            @ApiParam(value = "ID эталонной записи.") @PathParam(RestConstants.DATA_PARAM_ID) String etalonId,
            @ApiParam(value = "Имя связи.")@PathParam(RestConstants.DATA_PARAM_NAME) String name,
            @ApiParam(value = "Значения границ интервалов. Всегда 2 элемента") @PathParam(RestConstants.DATA_PARAM_TIMESTAMPS) List<PathSegment> timestamps,
            @ApiParam("Включать неактивные версии в эталон или нет (true/false)") @QueryParam(RestConstants.DATA_PARAM_INCLUDE_INACTIVE) String includeInactiveAsString,
            @ApiParam("Включать неподтвержденные версии в эталон или нет (true/false)") @QueryParam(RestConstants.DATA_PARAM_INCLUDE_DRAFTS) String includeDraftsAsString)
            throws ParseException {

        Date validFrom = RestUtils.extractStart(timestamps);
        Date validTo = RestUtils.extractEnd(timestamps);

        List<TimelineRO> timelines = new ArrayList<>();
        List<TimelineDTO> returned = dataRecordsService.getRelationsTimeline(etalonId, name, validFrom, validTo,
                includeDraftsAsString == null ? false : Boolean.valueOf(includeDraftsAsString),
                true);

        for (Iterator<TimelineDTO> tl = returned.iterator(); tl.hasNext(); ) {
            boolean hasActivePeriods = tl.next().getIntervals().stream().anyMatch(in -> in.isActive());
            if (!hasActivePeriods) {
                tl.remove();
            }
        }

        TimelineToTimelineROConverter.convert(returned, timelines);

        return Response.ok(new RestResponse<>(timelines)).build();
    }

    /**
     * Gets etalon relation objects along with their time lines by record etalon ID.
     * @param etalonId record's etalon ID
     * @param name name of the relation
     * @param timestamps records validity period boundary
     * @return response array of time line objects
     */
    @GET
    @Path("/" + RestConstants.PATH_PARAM_RELATION_BULK + "/{"
            + RestConstants.DATA_PARAM_ID + "}/{"
            + RestConstants.DATA_PARAM_NAME + "}/{"
            + RestConstants.DATA_PARAM_TIMESTAMPS + ":.*}")
    @ApiOperation(value = "Запросить связи эталона на дату", notes = "Запросить связи эталона на определенную дату либо на сейчас, если дата не задана.",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getRelationsByRecordEtalonIdAndBoundary(
            @ApiParam(value = "ID эталонной записи.") @PathParam(RestConstants.DATA_PARAM_ID) String etalonId,
            @ApiParam(value = "Имя связи.")@PathParam(RestConstants.DATA_PARAM_NAME) String name,
            @ApiParam(value = "Значения границ интервалов. Всегда 2 элемента") @PathParam(RestConstants.DATA_PARAM_TIMESTAMPS) List<PathSegment> timestamps,
            @ApiParam("Включать неактивные версии в эталон или нет (true/false)") @QueryParam(RestConstants.DATA_PARAM_INCLUDE_INACTIVE) String includeInactiveAsString,
            @ApiParam("Включать неподтвержденные версии в эталон или нет (true/false)") @QueryParam(RestConstants.DATA_PARAM_INCLUDE_DRAFTS) String includeDraftsAsString,
            @ApiParam("Список возвращаемых полей") @QueryParam(RestConstants.DATA_PARAM_RETURN_FIELDS) List<String> returnFields)
            throws ParseException {
        Date validFrom = RestUtils.extractStart(timestamps);
        Date validTo = RestUtils.extractEnd(timestamps);
        boolean includeDrafts = includeDraftsAsString == null ? false : Boolean.valueOf(includeDraftsAsString);
        boolean includeInactive = includeInactiveAsString == null ? false : Boolean.valueOf(includeInactiveAsString);
        List<BaseRelationRO> relations = new ArrayList<>();
        RelationDef relationDef = metaModelService.getRelationById(name);

        Map<String, String> displayNames = getDisplayNamesForRelationTo(etalonId, null, relationDef);
        if (includeDrafts) {

            // todo temporal decision, remove later.
            List<TimelineDTO> returned = dataRecordsService.getRelationsTimeline(etalonId, name, validFrom, validTo,
                    includeDrafts,true);
            for (Iterator<TimelineDTO> tl = returned.iterator(); tl.hasNext(); ) {
                boolean hasActivePeriods = tl.next().getIntervals().stream().anyMatch(TimeIntervalDTO::isActive);
                if (!hasActivePeriods) {
                    tl.remove();
                }
            }
            if(!returned.isEmpty()){
                for(TimelineDTO timeline : returned){
                    for(TimeIntervalDTO timeInterval : timeline.getIntervals()){
                        GetRelationRequestContext ctx = new GetRelationRequestContextBuilder()
                                .relationEtalonKey(timeline.getEtalonId())
                                .tasks(true)
                                .includeDrafts(includeDrafts)
                                .forDate(timeInterval.getValidFrom())
                                .build();
                        BaseRelationRO converted = null;
                        GetRelationDTO result = dataRecordsService.getRelation(ctx);
                        if (result != null && result.getEtalon()!= null) {
                            if (result.getRelationType() == RelationType.CONTAINS) {
                                converted = IntegralRecordEtalonConverter.to(result.getEtalon());
                                if (converted != null) {
                                    ((EtalonIntegralRecordRO) converted).setEtalonId(result.getRelationKeys().getEtalonId());
                                }
                            } else if (result.getRelationType() == RelationType.REFERENCES
                                    || result.getRelationType() == RelationType.MANY_TO_MANY) {

                                converted = RelationToEtalonConverter.to(result.getEtalon());
                                if (converted != null) {
                                    EtalonRelationToRO etalonRelationToRO = (EtalonRelationToRO) converted;
                                    etalonRelationToRO.setEtalonId(result.getRelationKeys().getEtalonId());
                                    etalonRelationToRO.setEtalonDisplayNameTo(displayNames.get(etalonRelationToRO.getEtalonIdTo()));
                                }
                            }
                            relations.add(converted);
                        }
                    }
                }
            }
        } else {
            if(RelType.CONTAINS.equals(relationDef.getRelType())){
                if(CollectionUtils.isEmpty(returnFields)){
                    returnFields = metaModelService.findMainDisplayableAttrNamesSorted(relationDef.getToEntity());
                }
                return Response.ok(new RestResponse<>(getRelationsToSideByTimelineAndFromEtalonId(etalonId,
                        validFrom,
                        validTo,
                        relationDef,
                        returnFields)))
                        .build();
            } else {
                if(CollectionUtils.isEmpty(returnFields)){
                    returnFields = relationDef.getSimpleAttribute()
                            .stream()
                            .map(AbstractAttributeDef::getName)
                            .collect(Collectors.toList());
                }

                SearchResultDTO relationsSearchResult = getRelationsByTimelineAndFromEtalonId(etalonId, validFrom, validTo, relationDef, returnFields);

                relations.addAll(RelationToEtalonConverter.to(relationsSearchResult, relationDef));
                relations.forEach(relation -> {
                    EtalonRelationToRO relationTO = (EtalonRelationToRO) relation;
                    relationTO.setEtalonDisplayNameTo(displayNames.get(((EtalonRelationToRO) relation).getEtalonIdTo()));
                });
            }
        }
        relations.sort(Comparator.comparing(BaseRelationRO::getCreateDate));
        return Response.ok(new RestResponse<>(relations)).build();
    }


    @POST
    @Path("/" + PATH_PARAM_DIGEST)
    @ApiOperation(
            value = "Запросить короткую информацию по связям эталона на дату.",
            notes = "Запросить короткую информацию по связям эталона на определенную дату либо на сейчас, если дата не задана.",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getRelationDigest(RelationDigestRO request) {

        RelationDef def = metaModelService.getRelationById(request.getRelName());
        if (def == null) {
            return emptySearchResult();
        }

        GetRelationsDigestRequestContext ctx = new GetRelationsDigestRequestContextBuilder()
                .count(request.getCount())
                .totalCount(request.isTotalCount())
                .from(request.getFrom())
                .to(request.getTo())
                .direction(request.getDirection() == null ? RelationSide.FROM : request.getDirection())
                .etalonId(request.getEtalonId())
                .fields(request.getFields())
                .page(request.getPage() > 0 ? request.getPage() - 1 : 0)
                .relName(request.getRelName())
                .build();

        RelationDigestDTO digest = dataRecordsService.loadRelatedEtalonIdsForDigest(ctx);
        if (digest == null || digest.getTotalCount() == 0
                || (digest.getEtalonIds() == null || digest.getEtalonIds().isEmpty())) {
            return emptySearchResult();
        }

        String entityName = request.getDirection() == RelationSide.FROM ? def.getFromEntity() : def.getToEntity();
        SearchRequestContext sCtx = SearchRequestContext.forEtalonData(entityName)
                .values(digest.getEtalonIds())
                .totalCount(ctx.isTotalCount())
                .search(SearchRequestType.TERM)
                .searchFields(Collections.singletonList(RecordHeaderField.FIELD_ETALON_ID.getField()))
                .returnFields(request.getFields())
                .count(request.getCount())
                .page(0)
                .build();

        SearchResultDTO result = searchService.search(sCtx);
        SearchResultRO ro = SearchResultToRestSearchResultConverter.convert(result, false);

        // Post process for records, which have no valid periods for right now
        List<String> allIds = new ArrayList<>(digest.getEtalonIds());
        for (SearchResultHitDTO hit : result.getHits()) {
            allIds.remove(hit.getId());
        }

        // Add remaining IDs
        for (String id : allIds) {
            SearchResultHitRO emptyHit = new SearchResultHitRO(id);
            emptyHit.getPreview().add(new SearchResultHitFieldRO(RecordHeaderField.FIELD_ETALON_ID.getField(), id, Collections.singletonList(id)));
            ro.getHits().add(emptyHit);
        }

        ro.setTotalCount(digest.getTotalCount());
        ro.setHasRecords(digest.getTotalCount() != 0);

        return ok(ro);
    }

    private Response emptySearchResult() {
        SearchResultRO ro = new SearchResultRO();
        ro.setSuccess(true);
        ro.setTotalCount(0);
        ro.setHasRecords(false);
        return ok(ro);
    }

    /**
     * Gets the time line for an etalon.
     * @param etalonId the etalon ID
     * @return response
     */
    /*
    @GET
    @Path("/" + PATH_PARAM_RELATIONS + "/" + PATH_PARAM_TIMELINE + "/{" + DATA_PARAM_ID + "}/{" + DATA_PARAM_NAME + "}")
    @ApiOperation(value = "Запросить таймлайн для связей эталона", notes = "Запросить данные изменения связей эталона по времени.",
        response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getRelationsTimelineByRecordEtalonId(
            @PathParam(DATA_PARAM_ID) String etalonId,
            @PathParam(DATA_PARAM_NAME) String name) {

        List<TimelineRO> timelines = new ArrayList<>();
        List<TimelineDTO> returned = dataRecordsService.getRelationsTimeline(etalonId, name);
        TimelineToTimelineROConverter.convert(returned, timelines);

        return Response.ok(new RestResponse<>(timelines)).build();
    }
    */

    /**
     * Get relation"s content (either containment or relation to).
     * @param relationEtalonId
     * @param dateAsString
     * @return
     * @throws ParseException
     */
/*
    @GET
    @Path("/" + PATH_PARAM_RELATION + "/" + RestConstants.PATH_PARAM_RANGE + "/"
        + "{" + RestConstants.DATA_PARAM_ID + "}/"
        + "{" + RestConstants.DATA_PARAM_TIMESTAMPS + ":.*}")
    @ApiOperation(value = "Запросить состояние эталона связи на дату",
        notes = "Запросить состояние эталона связи на определенную дату либо на сейчас, если дата не задана.",
        response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getRelationByEtalonIdAndBoundary(
            @PathParam(RestConstants.DATA_PARAM_ID) String relationEtalonId,
            @ApiParam(value = "Значения границ интервалов. Всегда 2 элемента") @PathParam(RestConstants.DATA_PARAM_TIMESTAMPS) List<PathSegment> timestamps) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_RELATIONS_GET);
        MeasurementPoint.start();
        try {

            Date validFrom = RestUtils.extractStart(timestamps);
            Date validTo = RestUtils.extractEnd(timestamps);

            GetRelationRequestContext ctx = new GetRelationRequestContextBuilder()
                    .relationEtalonKey(relationEtalonId)
                    .forRange(new ImmutablePair<Date, Date>(validFrom, validTo))
                    .build();

            BaseRelationRO converted = null;
            GetRelationDTO result = dataRecordsService.getRelation(ctx);
            if (result != null) {
                if (result.getRelType() == RelType.CONTAINS) {
                    converted = IntegralRecordEtalonConverter.to((IntegralRecord) result.getRelation());
                    if (converted != null) {
                        ((EtalonIntegralRecordRO) converted).setEtalonId(result.getEtalonKey());
                        // EtalonRecordRO etalonRecord = ((EtalonIntegralRecordRO) converted).getEtalonRecord();
                        // List<DataQualityError> dataQualityErrors = dataRecordsService
                        //        .getDQErrors(etalonRecord.getEtalonId(), etalonRecord.getEntityName(), asOf);
                        // DataRecordEtalonConverter.copyDQErrors(dataQualityErrors, etalonRecord.getDqErrors());
                    }
                } else if (result.getRelType() == RelType.REFERENCES
                        || result.getRelType() == RelType.MANY_TO_MANY) {
                    converted = RelationToEtalonConverter.to((RelationTo) result.getRelation());
                    if (converted != null) {
                        ((EtalonRelationToRO) converted).setEtalonId(result.getEtalonKey());
                    }
                }
            }

            return Response.ok(new RestResponse<>(converted)).build();

        } finally {
            MeasurementPoint.stop();
        }
    }
*/

    /**
     * Get relation"s content (either containment or relation to).
     * @param relationEtalonId
     * @param dateAsString
     * @return
     * @throws ParseException
     */
    @GET
    @Path("/" + PATH_PARAM_RELATION
            + "/{" + RestConstants.DATA_PARAM_ID + "}"
            + "{p:/?}{" + RestConstants.DATA_PARAM_DATE + ": " + RestConstants.DEFAULT_TIMESTAMP_PATTERN + "}")
    @ApiOperation(value = "Запросить состояние эталона связи на дату",
            notes = "Запросить состояние эталона связи на определенную дату либо на сейчас, если дата не задана.",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response getRelationByEtalonId(
            @ApiParam("Эталон ID связи") @PathParam(RestConstants.DATA_PARAM_ID) String relationEtalonId,
            @ApiParam("Эталон ID связи") @PathParam(RestConstants.DATA_PARAM_DATE) String dateAsString,
            @ApiParam("Включать неактивные версии в эталон или нет (true/false)") @QueryParam(RestConstants.DATA_PARAM_INCLUDE_INACTIVE) String includeInactiveAsString,
            @ApiParam("Включать неподтвержденные версии в эталон или нет (true/false)") @QueryParam(RestConstants.DATA_PARAM_INCLUDE_DRAFTS) String includeDraftsAsString,
            @ApiParam("Включить версии совместные с указанным operationId") @QueryParam(RestConstants.DATA_PARAM_OPERATION_ID) String operationId) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_RELATIONS_GET);
        MeasurementPoint.start();
        try {

            Date asOf = ValidityPeriodUtils.parse(dateAsString);

            GetRelationRequestContext ctx = new GetRelationRequestContextBuilder()
                    .relationEtalonKey(relationEtalonId)
                    .tasks(true)
                    .includeDrafts(includeDraftsAsString == null ? false : Boolean.valueOf(includeDraftsAsString))
                    .forDate(asOf)
                    .forOperationId(operationId)
                    .build();

            BaseRelationRO converted = null;
            GetRelationDTO result = dataRecordsService.getRelation(ctx);
            if (result != null) {
                if (result.getRelationType() == RelationType.CONTAINS) {
                    converted = IntegralRecordEtalonConverter.to(result.getEtalon());
                    if (converted != null) {
                        ((EtalonIntegralRecordRO) converted).setEtalonId(result.getRelationKeys().getEtalonId());
                        EtalonRecordRO etalonRecord = ((EtalonIntegralRecordRO) converted).getEtalonRecord();
                        if (etalonRecord != null) {
                            List<DataQualityError> dataQualityErrors = dataRecordsService
                                    .getDQErrors(etalonRecord.getEtalonId(), etalonRecord.getEntityName(), asOf);
                            DataRecordEtalonConverter.copyDQErrors(dataQualityErrors, etalonRecord.getDqErrors());
                            etalonRecord.setWorkflowState(WorkflowTaskConverter.to(result.getTasks()));
                            etalonRecord.setRights(RolesConverter.convertResourceSpecificRights(result.getRights()));
                        }
                    }
                } else if (result.getRelationType() == RelationType.REFERENCES
                        || result.getRelationType() == RelationType.MANY_TO_MANY) {
                    converted = RelationToEtalonConverter.to(result.getEtalon());
                    if (converted != null) {
                        ((EtalonRelationToRO) converted).setEtalonId(result.getRelationKeys().getEtalonId());
                    }
                }
            }

            return Response.ok(new RestResponse<>(converted)).build();

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Upserts etalon relation to relation.
     * @param etalonId the
     * @param ro
     * @return
     * @throws Exception
     */
    @POST
    @Path("/" + PATH_PARAM_RELATION
            + "/" + DataRelationsRestService.PATH_PARAM_RELTO + "/{" + RestConstants.DATA_PARAM_ID + "}")
    @ApiOperation(value = "Вставить список связей к текущему объекту", notes = "Вставить список связей к текущему объекту.",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response upsertRelationToRelation(
            @ApiParam(value = "ID эталона") @PathParam(RestConstants.DATA_PARAM_ID) String etalonId,
            EtalonRelationToRO ro){

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_RELATIONS_TO_UPSERT);
        MeasurementPoint.start();
        try {

            DataRecord converted = RelationToEtalonConverter.from(ro);
            UpsertRelationsRequestContext ctx = new UpsertRelationsRequestContextBuilder()
                    .etalonKey(etalonId)
                    .relations(Collections.singletonMap(ro.getRelName(), Collections.singletonList(
                            new UpsertRelationRequestContextBuilder()
                                    .etalonKey(ro.getEtalonIdTo())
                                    .relation(converted)
                                    .relationName(ro.getRelName())
                                    .validFrom(ConvertUtils.localDateTime2Date(ro.getValidFrom()))
                                    .validTo(ConvertUtils.localDateTime2Date(ro.getValidTo()))
                                    .sourceSystem(metaModelService.getAdminSourceSystem().getName())
                                    .build())))
                    .build();

            UpsertRelationsDTO result = dataRecordsService.upsertRelations(ctx);
            EtalonRelationToRO updated = null;

            // Single record i expected
            Iterator<Entry<RelationStateDTO, List<UpsertRelationDTO>>> i
                    = result != null && result.getRelations() != null
                    ? result.getRelations().entrySet().iterator()
                    : null;

            List<ErrorInfo> errors = null;
            while (i != null && i.hasNext()) {
                List<UpsertRelationDTO> relations = i.next().getValue();

                UpsertRelationDTO dto = relations != null && !relations.isEmpty() ? relations.get(0) : null;
                EtalonRelation relation = dto != null ? dto.getEtalon() : null;

                updated = RelationToEtalonConverter.to(relation);

                if(dto != null && CollectionUtils.isNotEmpty(dto.getErrors())){
                    errors = dto.getErrors()
                            .stream()
                            .map(ErrorInfoToRestErrorInfoConverter::convert)
                            .collect(Collectors.toList());
                }

                if (updated != null) {
                    updated.setEtalonId(dto == null ? null : dto.getRelationKeys().getEtalonId());
                }

                break;
            }


            // populate to side display name
            Map<String, String> displayNames = getDisplayNamesForRelationTo(etalonId, updated.getEtalonId(),
                    metaModelService.getRelationById(updated.getRelName()));
            updated.setEtalonDisplayNameTo(displayNames.get(updated.getEtalonIdTo()));

            RestResponse response = new RestResponse<>(updated);
            response.setErrors(errors);

            return Response.ok(response).build();
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Upserts etalon relation to relation.
     * @param etalonId the
     * @param ro
     * @return
     * @throws Exception
     */
    @POST
    @Path("/" + PATH_PARAM_RELATION
            + "/" + DataRelationsRestService.PATH_PARAM_INTEGRAL + "/{" + RestConstants.DATA_PARAM_ID + "}")
    @ApiOperation(value = "Вставить список связей к текущему объекту", notes = "Вставить список связей к текущему объекту.",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response upsertIntegralRelation(
            @ApiParam(value = "ID эталона") @PathParam(RestConstants.DATA_PARAM_ID) String etalonId,
            EtalonIntegralRecordRO ro) {

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_RELATIONS_INTEGRAL_UPSERT);
        MeasurementPoint.start();
        try {

            DataRecord converted = IntegralRecordEtalonConverter.from(ro);
            ro.getEtalonRecord().setDqErrors(new ArrayList<>());

            String toEtalonId = ro.getEtalonRecord() != null ? ro.getEtalonRecord().getEtalonId() : null;
            String toSsourceSystem = toEtalonId == null ? metaModelService.getAdminSourceSystem().getName() : null;
            String toExternalId = toEtalonId == null ? IdUtils.v1String() : null;
            String toEntityName = toEtalonId == null ? metaModelService.getRelationById(ro.getRelName()).getToEntity() : null;
            Date validFrom = ro.getEtalonRecord() != null ? ConvertUtils.localDateTime2Date(ro.getEtalonRecord().getValidFrom()) : null;
            Date validTo = ro.getEtalonRecord() != null ? ConvertUtils.localDateTime2Date(ro.getEtalonRecord().getValidTo()) : null;

            UpsertRelationsRequestContext ctx = new UpsertRelationsRequestContextBuilder()
                    .etalonKey(etalonId)
                    .relations(Collections.singletonMap(ro.getRelName(), Collections.singletonList(
                            new UpsertRelationRequestContextBuilder()
                                    .relationEtalonKey(ro.getEtalonId())
                                    .etalonKey(toEtalonId)
                                    .sourceSystem(toSsourceSystem)
                                    .externalId(toExternalId)
                                    .entityName(toEntityName)
                                    .relation(converted)
                                    .relationName(ro.getRelName())
                                    .validFrom(validFrom)
                                    .validTo(validTo)
                                    .build())))
                    .build();

            UpsertRelationsDTO result = null;
            //TODO: Same as in data entity service. Need to add normal error processing.

            try {
                result = dataRecordsService.upsertRelations(ctx);
            } catch (Exception exc) {
                if (CollectionUtils.isNotEmpty(ctx.getDqErrors())) {
                    DataRecordEtalonConverter.copyDQErrors(ctx.getDqErrors(), ro.getEtalonRecord().getDqErrors());
                    return Response.ok(new RestResponse<>(ro, false)).build();
                } else {
                    throw exc;
                }
            }

            EtalonIntegralRecordRO updated = null;
            List<ErrorInfo> errors = null;

            // Single record i expected
            Iterator<Entry<RelationStateDTO, List<UpsertRelationDTO>>> i
                    = result != null && result.getRelations() != null
                    ? result.getRelations().entrySet().iterator()
                    : null;

            while (i != null && i.hasNext()) {
                List<UpsertRelationDTO> relations = i.next().getValue();
                UpsertRelationDTO dto = relations != null && !relations.isEmpty() ? relations.get(0) : null;
                EtalonRelation relation = dto != null ? dto.getEtalon() : null;

                if(dto != null && CollectionUtils.isNotEmpty(dto.getErrors())){
                    errors = dto.getErrors()
                            .stream()
                            .map(ErrorInfoToRestErrorInfoConverter::convert)
                            .collect(Collectors.toList());
                }


                updated = IntegralRecordEtalonConverter.to(relation);
                if (updated != null) {
                    updated.setEtalonId(dto == null ? null : dto.getRelationKeys().getEtalonId());
                    updated.getEtalonRecord().setWorkflowState(WorkflowTaskConverter.to(dto == null ? null : dto.getTasks()));
                    updated.getEtalonRecord().setRights(RolesConverter.convertResourceSpecificRights(dto == null ? null : dto.getRights()));
                }

                break;
            }

            RestResponse response = new RestResponse<>(updated);
            response.setErrors(errors);

            return Response.ok(response).build();
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Inactivation of an etalon relation.
     * @param etalonId etalon id
     * @param relations the relations
     * @return response
     */
    @DELETE
    @Path("/" + PATH_PARAM_RELATION + "/{" + RestConstants.DATA_PARAM_ID + "}")
    @ApiOperation(value = "Удалить список связей к текущему объекту", notes = "Вставить список связей к текущему объекту.",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response deactivateEtalonRelation(
            @ApiParam(value = "ID эталона") @PathParam(RestConstants.DATA_PARAM_ID) String etalonId){

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_RELATIONS_ETALON_DELETE);
        MeasurementPoint.start();
        try {

            DeleteRelationRequestContext ctx = new DeleteRelationRequestContextBuilder()
                    .relationEtalonKey(etalonId)
                    .inactivateEtalon(true)
                    .build();

            DeleteRelationDTO result = dataRecordsService.deleteRelation(ctx);
            RestResponse response = new RestResponse<>(result != null ? Boolean.TRUE : Boolean.FALSE);
            if(CollectionUtils.isNotEmpty(result.getErrors())){
                response.setErrors(result.getErrors()
                        .stream()
                        .map(errorInfo -> ErrorInfoToRestErrorInfoConverter.convert(errorInfo))
                        .collect(Collectors.toList()));
            }

            return Response.ok(response).build();
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Inactivation of an origin relation.
     * @param etalonId etalon id
     * @param relations the relations
     * @return response
     */
    @DELETE
    @Path("/" + PATH_PARAM_RELATION + "/" + RestConstants.PATH_PARAM_ORIGIN + "/{" + RestConstants.DATA_PARAM_ID + "}")
    @ApiOperation(value = "Удалить список связей к текущему объекту", notes = "Вставить список связей к текущему объекту.",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response deactivateOriginRelation(
            @ApiParam(value = "ID эталона") @PathParam(RestConstants.DATA_PARAM_ID) String originId){

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_RELATIONS_ORIGIN_DELETE);
        MeasurementPoint.start();
        try {

            DeleteRelationRequestContext ctx = new DeleteRelationRequestContextBuilder()
                    .relationOriginKey(originId)
                    .inactivateOrigin(true)
                    .build();

            DeleteRelationDTO result = dataRecordsService.deleteRelation(ctx);
            RestResponse response = new RestResponse<>(result != null ? Boolean.TRUE : Boolean.FALSE);
            if(CollectionUtils.isNotEmpty(result.getErrors())){
                response.setErrors(result.getErrors()
                        .stream()
                        .map(errorInfo -> ErrorInfoToRestErrorInfoConverter.convert(errorInfo))
                        .collect(Collectors.toList()));
            }

            return Response.ok(response).build();
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Inactivation of an origin relation.
     * @param etalonId etalon id
     * @param relations the relations
     * @return response
     */
    @DELETE
    @Path("/" + PATH_PARAM_RELATION
            + "/" + RestConstants.PATH_PARAM_VERSION + "/{" + RestConstants.DATA_PARAM_ID + "}/{" + RestConstants.DATA_PARAM_TIMESTAMPS + ":.*}")
    @ApiOperation(value = "Удалить список связей к текущему объекту", notes = "Вставить список связей к текущему объекту.",
            response = RestResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)
    })
    public Response deactivateVersionRelation(
            @ApiParam(value = "ID эталона") @PathParam(RestConstants.DATA_PARAM_ID) String versionId,
            @ApiParam(value = "Значения границ интервалов. Всегда 2 элемента") @PathParam(RestConstants.DATA_PARAM_TIMESTAMPS) List<PathSegment> timestamps){

        MeasurementPoint.init(MeasurementContextName.MEASURE_UI_RELATIONS_VERSION_DELETE);
        MeasurementPoint.start();
        try {

            if (timestamps == null || timestamps.size() != 2) {
                throw new RuntimeException("Timestamps aren't even!");
            }

            Date validFrom = RestUtils.extractStart(timestamps);
            Date validTo = RestUtils.extractEnd(timestamps);

            DeleteRelationRequestContext ctx = new DeleteRelationRequestContextBuilder()
                    .relationEtalonKey(versionId)
                    .inactivatePeriod(true)
                    .validFrom(validFrom)
                    .validTo(validTo)
                    .build();

            DeleteRelationDTO result = dataRecordsService.deleteRelation(ctx);
            RestResponse response = new RestResponse<>(result != null ? Boolean.TRUE : Boolean.FALSE);
            if(CollectionUtils.isNotEmpty(result.getErrors())){
                response.setErrors(result.getErrors()
                        .stream()
                        .map(errorInfo -> ErrorInfoToRestErrorInfoConverter.convert(errorInfo))
                        .collect(Collectors.toList()));
            }

            return Response.ok(response).build();
        } finally {
            MeasurementPoint.stop();
        }
    }


    private SearchResultRO getRelationsToSideByTimelineAndFromEtalonId(String etalonId,
                                                                       Date validFrom,
                                                                       Date validTo,
                                                                       RelationDef relationDef,
                                                                       List<String> returnFields){
        FormField fromEtalon = FormField.strictString(FIELD_FROM_ETALON_ID.getField(), etalonId);
        FormField notFrom = FormField.notRange(SimpleDataType.TIMESTAMP, RelationHeaderField.FIELD_TO.getField(),null, validFrom);
        FormField notTo = FormField.notRange(SimpleDataType.TIMESTAMP, RelationHeaderField.FIELD_FROM.getField(), validTo, null);

        List<String> searchFields = new ArrayList<>();
        searchFields.add(RelationHeaderField.FIELD_CREATED_AT.getField());
        searchFields.add(RelationHeaderField.FIELD_UPDATED_AT.getField());
        searchFields.addAll(returnFields);

        SearchRequestContext mainContext = SearchRequestContext.forEtalonData(relationDef.getToEntity())
                .totalCount(true)
                .onlyQuery(true)
                .fetchAll(true)
                .count(1000)
                .returnFields(searchFields)
                .build();
        SearchRequestContext relationsContext = SearchRequestContext.forEtalonRelation(relationDef.getToEntity())
                .form(FormFieldsGroup.createAndGroup(fromEtalon, notFrom, notTo))
                .returnFields(Arrays.asList(RelationHeaderField.FIELD_FROM.getField(),
                        RelationHeaderField.FIELD_TO.getField()))
                .count(1000)
                .onlyQuery(true)
                .build();
        ComplexSearchRequestContext getToSide = ComplexSearchRequestContext.hierarchical(mainContext, relationsContext);
        Map<SearchRequestContext, SearchResultDTO> toSide = searchService.search(getToSide);
        SearchResultDTO  toSideSearchResult = toSide.get(mainContext);
        searchResultHitModifier.modifySearchResult(toSideSearchResult, mainContext);
        return SearchResultToRestSearchResultConverter.convert(toSideSearchResult, false);

    }

    private SearchResultDTO getRelationsByTimelineAndFromEtalonId(String etalonId,
                                                                  Date validFrom,
                                                                  Date validTo,
                                                                  RelationDef relationDef,
                                                                  List<String> returnFields) {        // restriction for get relations data
        FormField fromEtalon = FormField.strictString(FIELD_FROM_ETALON_ID.getField(), etalonId);
        FormField notFrom = FormField.notRange(SimpleDataType.TIMESTAMP, RelationHeaderField.FIELD_TO.getField(),null, validFrom);
        FormField notTo = FormField.notRange(SimpleDataType.TIMESTAMP, RelationHeaderField.FIELD_FROM.getField(), validTo, null);
        FormField rel_name = FormField.strictString(REL_NAME.getField(), relationDef.getName());

        List<String> searchFields = new ArrayList<>();
        searchFields.add(RelationHeaderField.FIELD_FROM.getField());
        searchFields.add(RelationHeaderField.FIELD_TO.getField());
        searchFields.add(RelationHeaderField.FIELD_TO_ETALON_ID.getField());
        searchFields.add(RelationHeaderField.FIELD_CREATED_AT.getField());
        searchFields.add(RelationHeaderField.FIELD_UPDATED_AT.getField());
        if(CollectionUtils.isNotEmpty(returnFields)){
            returnFields.forEach(field -> searchFields.add(relationDef.getName() + "." + field));
        }

        SearchRequestContext relationsContext = SearchRequestContext.forEtalonRelation(relationDef.getToEntity())
                .form(FormFieldsGroup.createAndGroup(fromEtalon, notFrom, notTo, rel_name))
                .count(1000)
                .scrollScan(true)
                .onlyQuery(true)
                .returnFields(searchFields)
                .build();
        return searchService.search(relationsContext);
    }

    private Map<String, String> getDisplayNamesForRelationTo(@Nonnull  String fromEtalonId, String relationId, RelationDef relationDef) {

        List<String> displayAttributes = CollectionUtils.isNotEmpty(relationDef.getToEntityDefaultDisplayAttributes())
                ? relationDef.getToEntityDefaultDisplayAttributes()
                : metaModelService.findMainDisplayableAttrNamesSorted(relationDef.getToEntity());

        Map<String, String> displayNames = new HashMap<>();
        SearchRequestContext main = SearchRequestContext.forEtalonData(relationDef.getToEntity())
                .totalCount(true)
                .onlyQuery(true)
                .fetchAll(true)
                .count(1000)
                .returnFields(displayAttributes)
                .build();

        List<FormField> restrictions = new ArrayList<>();
        restrictions.add(FormField.strictString(FIELD_FROM_ETALON_ID.getField(), fromEtalonId));

        if(StringUtils.isNotEmpty(relationId)){
            restrictions.add(FormField.strictString(FIELD_ETALON_ID.getField(), relationId));
        }

        SearchRequestContext rels = SearchRequestContext.forEtalonRelation(relationDef.getToEntity())
                .form(FormFieldsGroup.createAndGroup(restrictions))
                .count(1000)
                .onlyQuery(true)
                .build();

        ComplexSearchRequestContext getToSide = ComplexSearchRequestContext.hierarchical(main, rels);
        Map<SearchRequestContext, SearchResultDTO> toSide = searchService.search(getToSide);
        SearchResultDTO toSideDTO = toSide.values().iterator().next();
        searchResultHitModifier.modifySearchResult(toSideDTO, main);
        if (MapUtils.isNotEmpty(toSide)) {

            Map<String, AttributeInfoHolder> attrsMap = Collections.emptyMap();
            if (relationDef.isUseAttributeNameForDisplay()) {
                attrsMap = metaModelService.getAttributesInfoMap(relationDef.getToEntity());
            }

            for(SearchResultHitDTO hit : toSideDTO.getHits()){
                List<String> arrtValues = new ArrayList<>();
                for(String attr : displayAttributes){
                    SearchResultHitFieldDTO hf = hit.getFieldValue(attr);
                    if(hf != null){
                        String converted = String.valueOf(hf.isCollection()
                                ? hf.getFirstValue() + " (" + String.join(", ", hf.getValues().subList(1, hf.getValues().size()).stream()
                                .map(Object::toString).collect(Collectors.toList())) + ")"
                                : hf.getFirstValue());

                        if (relationDef.isUseAttributeNameForDisplay()) {
                            AttributeInfoHolder attrHolder = attrsMap.get(hf.getField());
                            converted = attrHolder != null
                                    ? attrHolder.getAttribute().getDisplayName() + ": " + converted
                                    : converted;
                        }

                        arrtValues.add(converted);
                    }
                }

                displayNames.put(hit.getId(), String.join(StringUtils.SPACE, arrtValues));
            }
        }
        return displayNames;
    }

}

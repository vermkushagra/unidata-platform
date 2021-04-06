package com.unidata.mdm.backend.api.rest;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.unidata.mdm.backend.api.rest.converter.ClusterConverter;
import com.unidata.mdm.backend.api.rest.converter.SearchResultToRestSearchResultConverter;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.api.rest.dto.matching.RecordsClusterRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchResultHitFieldRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchResultHitRO;
import com.unidata.mdm.backend.api.rest.dto.search.SearchResultRO;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.common.matching.ClusterRecord;
import com.unidata.mdm.backend.common.search.SearchRequestType;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.service.ClusterService;
import com.unidata.mdm.backend.service.search.util.RecordHeaderField;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;


@Path("clusters")
@Api(value = "clusters", description = "API для работы с кластерами сопоставленных записей", produces = "application/json")
@Consumes({"application/json"})
@Produces({"application/json"})
public class ClusterRestService extends AbstractRestService {

    private static final String ENTITY_NAME = "entityName";
    private static final String GROUP_ID = "groupId";
    private static final String RULE_ID = "ruleId";
    private static final String ETALON_ID = "etalonId";
    private static final String FIELDS = "fields";
    private static final String START = "start";
    private static final String LIMIT = "limit";
    private static final String CLUSTER_ID = "clusterId";

    @Autowired
    private SearchService searchService;

    @Autowired
    private ClusterService clusterService;

    @GET
    @Path("/{" + ENTITY_NAME + "}/records")
    @ApiOperation(value = "Список кластеров", notes = "", response = RestResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response getClusters(@ApiParam(value = "Имя реестра/справочника")
                                @PathParam(ENTITY_NAME)
                                        String entityName,
                                @ApiParam(value = "Id группы сопоставления")
                                @QueryParam(GROUP_ID)
                                        Integer groupId,
                                @ApiParam(value = "Id правила сопоставления")
                                @QueryParam(RULE_ID)
                                        Integer ruleId,
                                @ApiParam(value = "Id эталоной записи")
                                @QueryParam(ETALON_ID)
                                        String etalonId,
                                @ApiParam(value = "С ")
                                @QueryParam(START)
                                        Integer start,
                                @ApiParam(value = "Количество")
                                @QueryParam(LIMIT)
                                        Integer limit,
                                @ApiParam(value = "Поля")
                                @QueryParam(FIELDS)
                                        List<String> fields) {

        Collection<Cluster> clusters;
        if (etalonId == null || etalonId.isEmpty()) {
            ClusterMetaData clusterMetaData = ClusterMetaData.builder()
                    .groupId(groupId)
                    .ruleId(ruleId)
                    .entityName(entityName)
                    .storage(SecurityUtils.getCurrentUserStorageId())
                    .build();
            clusters = clusterService.getClusters(clusterMetaData, limit, start, null);
        } else {
            clusters = clusterService.getClusters(etalonId, limit, start);
        }

        if (clusters.isEmpty()) {
            return ok(new RestResponse<>(Collections.emptyList()));
        }

        Collection<RecordsClusterRO> recordsCluster = clusters.stream()
                .map(ClusterConverter::convert)
                .collect(Collectors.toList());

        Multimap<String, RecordsClusterRO> etalonClusterMap = HashMultimap.create();

        for (RecordsClusterRO clusterDto : recordsCluster) {
            String etalonID = clusterDto.getRecords().iterator().next().getEtalonId();
            etalonClusterMap.put(etalonID, clusterDto);
        }

        SearchRequestContext searchContext = SearchRequestContext.forEtalonData(entityName)
                .values(new ArrayList<>(etalonClusterMap.keySet()))
                .count(etalonClusterMap.keySet().size())
                .page(0)
                .search(SearchRequestType.TERM)
                .searchFields(singletonList(RecordHeaderField.FIELD_ETALON_ID.getField()))
                .returnFields(fields.isEmpty() ? null : fields)
                .build();

        SearchResultDTO result = searchService.search(searchContext);
        SearchResultRO resultRo = SearchResultToRestSearchResultConverter.convert(result, false);
        for (SearchResultHitRO hit : resultRo.getHits()) {
            SearchResultHitFieldRO field = hit.getPreview().stream()
                    .filter(prev -> prev.getField().equals(RecordHeaderField.FIELD_ETALON_ID.getField()))
                    .findAny().orElse(null);
            if (field == null) {
                continue;
            }
            String etalonID = field.getValue().toString();
            etalonClusterMap.get(etalonID).forEach(cluster -> cluster.setPreview(hit.getPreview()));
        }

        return ok(new RestResponse<>(recordsCluster));
    }

    @GET
    @Path("/{" + ENTITY_NAME + "}/count")
    @ApiOperation(value = "Количество кластеров", notes = "", response = RestResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response getCount(@ApiParam(value = "Имя реестра/справочника")
                             @PathParam(ENTITY_NAME)
                                     String entityName,
                             @ApiParam(value = "Id группы сопоставления")
                             @QueryParam(GROUP_ID)
                                     Integer groupId,
                             @ApiParam(value = "Id правила сопоставления")
                             @QueryParam(RULE_ID)
                                     Integer ruleId,
                             @ApiParam(value = "Id эталоной записи")
                             @QueryParam(ETALON_ID)
                                     String etalonId) {
        Long count;
        if (etalonId == null || etalonId.isEmpty()) {
            ClusterMetaData clusterMetaData = ClusterMetaData.builder()
                    .groupId(groupId)
                    .ruleId(ruleId)
                    .entityName(entityName)
                    .storage(SecurityUtils.getCurrentUserStorageId())
                    .build();
            count = clusterService.getClustersCount(clusterMetaData);
        } else {
            count = clusterService.getClustersCount(etalonId);
        }

        return ok(new RestResponse<>(count));
    }

    @PUT
    @Path("/{" + ENTITY_NAME + "}/blockList/{" + CLUSTER_ID + "}")
    @Consumes("application/x-www-form-urlencoded; charset=UTF-8")
    @ApiOperation(value = "Исключить из кластера", notes = "", response = RestResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response markAsNotDuplicates(@ApiParam(value = "Имя реестра/справочника")
                                        @PathParam(ENTITY_NAME)
                                                String entityName,
                                        @ApiParam(value = "Id кластера")
                                        @PathParam(CLUSTER_ID)
                                                Long clusterId,
                                        @ApiParam(value = "Id записeй")
                                        @FormParam(ETALON_ID)
                                                List<String> etalonIds) {

        Cluster cluster = clusterService.getCluster(clusterId);
        if (cluster == null) {
            throw new BusinessException("Cluster not found", ExceptionId.EX_MATCHING_CLUSTER_NOT_FOUND);
        }
        boolean contains = cluster.getClusterRecords()
                .stream()
                .map(ClusterRecord::getEtalonId)
                .collect(Collectors.toList())
                .containsAll(etalonIds);
        if (!contains) {
            throw new BusinessException("Cluster doesn't contain record", ExceptionId.EX_MATCHING_CLUSTER_DOES_NOT_CONTAINS_RECORD);
        }
        clusterService.excludeFromCluster(etalonIds, clusterId);
        clusterService.addToBlockList(etalonIds, cluster);
        return ok(new RestResponse<>());
    }

}

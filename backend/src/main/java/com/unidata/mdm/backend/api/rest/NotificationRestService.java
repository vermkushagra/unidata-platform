package com.unidata.mdm.backend.api.rest;

import com.unidata.mdm.api.UnidataMessageDef;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.RestResponse;
import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.dto.GetRecordDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.service.notification.Notification;
import com.unidata.mdm.backend.service.notification.NotificationService;
import com.unidata.mdm.backend.service.notification.ProcessedAction;
import com.unidata.mdm.backend.service.notification.configs.NotificationConfig;
import com.unidata.mdm.backend.service.notification.messages.UnidataMessage;
import com.unidata.mdm.backend.util.ValidityPeriodUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.Date;

import static com.unidata.mdm.backend.service.notification.NotificationUtils.createEtalonUpsertNotification;

/**
 *
 * Notification rest service
 *
 * @author amagdenko, dmitry kopin
 */
@Path("/notifications")
@Api(value = "notification", description = "Уведомления", produces = "application/json")
@Consumes({"application/json"})
@Produces({"application/json"})
public class NotificationRestService extends AbstractRestService {

    /**
     * Notification service
     */
    @Autowired
    private NotificationService notificationService;

    /**
     * Data records service.
     */
    @Autowired
    private DataRecordsService dataRecordsService;

    @POST
    @ApiOperation(value = "Отправить уведомление о записи", notes = "", response = Boolean.class)
    @Path("/send/{" + RestConstants.DATA_PARAM_ID + "}{p:/?}{"
            + RestConstants.DATA_PARAM_DATE + ": " + RestConstants.DEFAULT_TIMESTAMP_PATTERN + "}")
    @ApiResponses({@ApiResponse(code = 200, message = "Request processed"),
            @ApiResponse(code = 500, message = "Error occurred", response = ErrorResponse.class)})
    public Response sendEtalonNotification(
            @ApiParam("ID сущности") @PathParam(RestConstants.DATA_PARAM_ID) String id,
            @ApiParam("Дата на таймлайне") @PathParam(RestConstants.DATA_PARAM_DATE) String dateAsString,
            @ApiParam("Включать неактивные версии в эталон или нет (true/false)") @QueryParam(RestConstants.DATA_PARAM_INCLUDE_INACTIVE) String includeInactiveAsString,
            @ApiParam("Включать неподтвержденные версии в эталон или нет (true/false)") @QueryParam(RestConstants.DATA_PARAM_INCLUDE_DRAFTS) String includeDraftsAsString,
            @ApiParam("Включить версии совместные с указанным operationId") @QueryParam(RestConstants.DATA_PARAM_OPERATION_ID) String operationId) {

        Date asOf = ValidityPeriodUtils.parse(dateAsString);
        GetRequestContext ctx = new GetRequestContext.GetRequestContextBuilder()
                .etalonKey(id)
                .forDate(asOf)
                .forOperationId(operationId)
                .fetchRelations(false)
                .includeInactive(includeInactiveAsString == null ? false : Boolean.valueOf(includeInactiveAsString))
                .includeDrafts(includeDraftsAsString == null ? false : Boolean.valueOf(includeDraftsAsString))
                .tasks(false)
                .strictDraft(true)
                .build();

        GetRecordDTO record = dataRecordsService.getRecord(ctx);
        EtalonRecord etalonRecord = record.getEtalon();
        if(etalonRecord == null){
            throw new DataProcessingException("Sending data empty or has incorrect identifier", ExceptionId.EX_DATA_REPUBLICATION_INCORRECT_KEYS);
        }

        RecordKeys recordKeys = record.getRecordKeys();

        final UnidataMessageDef message = createEtalonUpsertNotification(etalonRecord, null, UpsertAction.NO_ACTION,
                recordKeys.getSupplementaryKeys(), operationId);
        final UnidataMessage unidataMessage = new UnidataMessage(message);
        final NotificationConfig notificationConfig = new NotificationConfig(ProcessedAction.RESEND_ETALON, recordKeys);
        Notification notification = new Notification<>(notificationConfig, unidataMessage);
        notificationService.notify(notification);
        return ok(new RestResponse(null));
    }
}

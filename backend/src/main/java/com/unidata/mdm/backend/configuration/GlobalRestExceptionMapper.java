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

package com.unidata.mdm.backend.configuration;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.unidata.mdm.backend.api.rest.dto.ErrorInfo;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.Param;
import com.unidata.mdm.backend.api.rest.dto.UpdateResponse;
import com.unidata.mdm.backend.common.exception.AbstractValidationException;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.exception.ConsistencyException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.JobException;
import com.unidata.mdm.backend.common.exception.LicenseException;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.common.exception.SystemSecurityException;
import com.unidata.mdm.backend.common.exception.ValidationResult;
import com.unidata.mdm.backend.common.integration.exits.ExitException;
import com.unidata.mdm.backend.configuration.application.ConfigurationValidationException;
import com.unidata.mdm.backend.service.classifier.CodePatternValidationException;
import com.unidata.mdm.backend.util.MessageUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.cxf.interceptor.Fault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.security.access.AccessDeniedException;

/**
 * @author Michael Yashin. Created on 26.05.2015.
 */
public class GlobalRestExceptionMapper implements ExceptionMapper<Exception> {
    /**
     * UTF-8 encoding.
     */
    private static final String UTF_8 = "UTF-8";

    private static final String LAYER_INDENT = "-";

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalRestExceptionMapper.class);

    /**
     * Map to response.
     */
    @Override
    public Response toResponse(Exception exception) {

        LOGGER.error("REST invocation error. Reason: " + exception.getMessage(), exception);

        Response result = null;

        if (exception instanceof CleanseFunctionExecutionException) {
            result = processCleanseFunctionExecutionException((CleanseFunctionExecutionException) exception);
        }else if( exception instanceof Fault){
            result = Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }else if( exception instanceof AccessDeniedException){
            result = Response
                    .status(Response.Status.FORBIDDEN)
                    .build();
        } else if (exception instanceof ConversionFailedException) {
            if (exception.getCause() != null) {
                result = toResponse((Exception) exception.getCause());
            }
        } else if (exception instanceof ConsistencyException) {
            result = processConsistencyException((ConsistencyException) exception);
        }  else if (exception instanceof SystemSecurityException) {
            result = processSystemSecurityException((SystemSecurityException) exception);
        } else if (exception instanceof BusinessException && ((BusinessException) exception).getId()==ExceptionId.EX_META_NESTED_ENTITIES_DUPLICATE2) {
        	result = processMetaDuplicatesException((BusinessException) exception);
        } else if (exception instanceof BusinessException) {
            result = processBusinessException((BusinessException) exception);
        } else if (exception instanceof JobException) {
            result = processJobException((JobException) exception);
        } else if (exception instanceof ConfigurationValidationException) {
            result = processValidationExceptionWithDetails((ConfigurationValidationException) exception);
        } else if (exception instanceof CodePatternValidationException) {
            result = processValidationExceptionWithDetails((CodePatternValidationException) exception);
        } else if (exception instanceof AbstractValidationException) {
            result = processValidationException((AbstractValidationException) exception);
        } else if (exception instanceof LicenseException) {
            result = processLicenseException((LicenseException) exception);
        } else if (exception instanceof SystemRuntimeException) {
            result = processSystemRuntimeException((SystemRuntimeException) exception);
        } else if ((exception instanceof WebApplicationException)
                && (((WebApplicationException) exception).getResponse().getStatus() == 413)) {
            BusinessException newExc = new BusinessException("Request entity too large",
                    ExceptionId.EX_SYSTEM_REQUEST_TOO_LARGE, "100");
            result = processBusinessException(newExc);
        } else if (exception instanceof ExitException) {
            result = processExitException((ExitException) exception);
        }

        if(result == null){
            result = processUndefinedException(exception);
        }

        return result;
    }

    private Response processValidationExceptionWithDetails(AbstractValidationException exception) {
        List<ErrorInfo> errorsInfo = new ArrayList<>();
        ErrorInfo validationError = new ErrorInfo(ErrorInfo.Type.INTERNAL_ERROR);
        validationError.setInternalMessage(exception.getMessage());
        validationError.setErrorCode(exception.getId().name());
        errorsInfo.add(validationError);

        final String message = MessageUtils.getMessage(exception.getId().getCode(), exception.getArgs());
        validationError.setUserMessage(message);

        if (CollectionUtils.isNotEmpty(exception.getValidationResult())) {
            List<String> validationMessages = exception.getValidationResult()
                    .stream()
                    .map((res) -> getValidationResultMessage(res, 1))
                    .collect(Collectors.toList());

            if(validationMessages.size() == 1) {
                validationError.setUserMessage(
                        message + System.lineSeparator() + LAYER_INDENT + " " + validationMessages.get(0)
                );
            }
            else {
                String overAllMessage = validationMessages
                        .stream()
                        .collect(
                                Collectors.joining(
                                        System.lineSeparator() + LAYER_INDENT + " ",
                                        System.lineSeparator() + LAYER_INDENT + " ",
                                        ""
                                )
                        );
                validationError.setUserMessageDetails(message + overAllMessage);
            }
        }

        UpdateResponse response = new UpdateResponse(false, null);
        response.setErrors(errorsInfo);

        return Response
                .status(Response.Status.OK)
                .entity(response)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .encoding(UTF_8)
                .build();
    }

    /**
     * {@link LicenseException} processing.
     * @param le the exception
     * @return response
     */
    private Response processLicenseException(LicenseException le) {

        ErrorInfo errorInfo = new ErrorInfo(ErrorInfo.Type.INTERNAL_ERROR);
        fillErrorInfo(errorInfo, le);
        if (le.getExpirationDate() != null) {
            errorInfo.setUserMessageDetails(le.getExpirationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")));
        }
        ErrorResponse errorResponse = new ErrorResponse(le);
        errorResponse.getErrors().add(errorInfo);

        return Response
                .status(Response.Status.PAYMENT_REQUIRED)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .encoding(UTF_8)
                .build();
    }

    /**
     * {@link SystemSecurityException} processing.
     * @param exception the exception
     * @return response
     */
    private Response processSystemSecurityException(SystemSecurityException exception) {
        ErrorInfo errorInfo = new ErrorInfo(ErrorInfo.Type.INTERNAL_ERROR);
        fillErrorInfo(errorInfo, exception);

        UpdateResponse response = new UpdateResponse(false, null);
        response.setErrors(Collections.singletonList(errorInfo));

        return Response
                .status(Response.Status.OK)
                .entity(response)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .encoding(UTF_8)
                .build();
    }
    /**
     * Custom processor for the meta model duplicates.
     * {@link BusinessException} processing.
     * @param exception the exception
     * @return response
     */
    private Response processMetaDuplicatesException(BusinessException exception) {

        ErrorInfo errorInfo = new ErrorInfo(ErrorInfo.Type.INTERNAL_ERROR);
        errorInfo.setInternalMessage(exception.getMessage());
        errorInfo.setErrorCode(exception.getId().name());
        errorInfo.setUserMessage(
                MessageUtils.getMessage(ExceptionId.EX_META_NESTED_ENTITIES_DUPLICATE1.getCode())
        );
        errorInfo.setUserMessageDetails(MessageUtils.getMessage(exception.getId().getCode(), exception.getArgs()));
        UpdateResponse response = new UpdateResponse(false, null);
        response.setErrors(Collections.singletonList(errorInfo));

        return Response
                .status(Response.Status.OK)
                .entity(response)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .encoding(UTF_8)
                .build();
    }
    /**
     * {@link BusinessException} processing.
     * @param exception the exception
     * @return response
     */
    private Response processBusinessException(BusinessException exception) {

        ErrorInfo errorInfo = new ErrorInfo(ErrorInfo.Type.INTERNAL_ERROR);
        fillErrorInfo(errorInfo, exception);

        UpdateResponse response = new UpdateResponse(false, null);
        response.setErrors(Collections.singletonList(errorInfo));

        return Response
                .status(Response.Status.OK)
                .entity(response)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .encoding(UTF_8)
                .build();
    }

    /**
     * Process base validation exception.
     * @param ave the exception
     * @return response
     */
    private Response processValidationException(AbstractValidationException ave) {

        List<ErrorInfo> errorsInfo = new ArrayList<>();
        // 1. Main
        ErrorInfo validationError = new ErrorInfo(ErrorInfo.Type.INTERNAL_ERROR);
        fillErrorInfo(validationError, ave);
        errorsInfo.add(validationError);

        // 2. The others.
        if (CollectionUtils.isNotEmpty(ave.getValidationResult())) {
            List<String> validationMessages = ave.getValidationResult()
                    .stream()
                    .map((res) -> getValidationResultMessage(res, 1))
                    .collect(Collectors.toList());

            // For only one validation error show without title.
            if(validationMessages.size() == 1) {
                validationError.setUserMessage(System.lineSeparator() + LAYER_INDENT + " " + validationMessages.get(0));
            }

            String overAllMessage = validationMessages
                    .stream()
                    .collect(Collectors.joining(System.lineSeparator() + LAYER_INDENT + " ",
                            System.lineSeparator() + LAYER_INDENT + " ",
                            ""));

            validationError.setUserMessageDetails(overAllMessage);
        }

        UpdateResponse response = new UpdateResponse(false, null);
        response.setErrors(errorsInfo);

        return Response
                .status(Response.Status.OK)
                .entity(response)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .encoding(UTF_8)
                .build();
    }

    /**
     * {@link BusinessException} processing.
     * @param exception the exception
     * @return response
     */
    private Response processSystemRuntimeException(SystemRuntimeException exception) {
        ErrorInfo errorInfo = new ErrorInfo(ErrorInfo.Type.INTERNAL_ERROR);
        fillErrorInfo(errorInfo, exception);

        ErrorResponse response = new ErrorResponse(exception);
        response.getErrors().add(errorInfo);

        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(response)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .encoding(UTF_8)
                .build();
    }

    /**
     * {@link CleanseFunctionExecutionException} processing.
     * @param exception the exception
     * @return response
     */
    private Response processCleanseFunctionExecutionException(CleanseFunctionExecutionException exception) {

        ErrorInfo errorInfo = new ErrorInfo(ErrorInfo.Type.INTERNAL_ERROR);
        errorInfo.setErrorCode(ExceptionId.EX_SYSTEM_CLEANSE_EXEC_FAILED.name());

        ErrorResponse errorResponse = new ErrorResponse(exception);
        errorResponse.getErrors().add(errorInfo);
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .encoding(UTF_8)
                .build();
    }


    /**
     * {@link Exception} processing.
     * @param exception the exception
     * @return response
     */
    private Response processUndefinedException(Exception exception) {

        ErrorInfo errorInfo = new ErrorInfo(ErrorInfo.Type.INTERNAL_ERROR);
        errorInfo.setInternalMessage(exception.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(exception);
        errorResponse.getErrors().add(errorInfo);

        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .encoding(UTF_8)
                .build();
    }

    private String getValidationResultMessage(ValidationResult res, int layer) {
        String mainMessage = MessageUtils.getMessage(res.getTranslationCode(), res.getArgs());
        if (CollectionUtils.isNotEmpty(res.getNestedValidations())) {
            String prefix = System.lineSeparator();
            prefix += StringUtils.repeat(LAYER_INDENT, layer);
            prefix += " ";

            mainMessage = mainMessage + prefix;
            return res.getNestedValidations()
                    .stream()
                    .map((res1) -> getValidationResultMessage(res1, layer + 1))
                    .collect(Collectors.joining(prefix, mainMessage, ""));
        } else {
            return mainMessage;
        }
    }

    /**
     * {@link JobException} processing.
     * @param je the exception
     * @return response
     */
    private Response processJobException(JobException je) {

        ErrorInfo errorInfo = new ErrorInfo(ErrorInfo.Type.INTERNAL_ERROR);
        errorInfo.setParams(fillParamsList(je.getParams()));

        fillErrorInfo(errorInfo, je);

        if (ExceptionId.EX_JOB_PARAMETER_VALIDATION_ERROR == je.getId()
                || ExceptionId.EX_JOB_SAME_NAME == je.getId()
                || ExceptionId.EX_JOB_BATCH_EXECUTION_FAILED == je.getId()
                || ExceptionId.EX_JOB_BATCH_STOP_FAILED == je.getId()
                || ExceptionId.EX_JOB_CRON_EXPRESSION == je.getId()
                || ExceptionId.EX_JOB_CRON_SUSPICIOUS_SECOND == je.getId()
                || ExceptionId.EX_JOB_CRON_SUSPICIOUS_MINUTE == je.getId()
                || ExceptionId.EX_JOB_CRON_SUSPICIOUS_SHORT_CYCLES_DOM == je.getId()
                || ExceptionId.EX_JOB_SAME_PARAMETERS == je.getId()
                || ExceptionId.EX_JOB_TRIGGER_SAME_NAME == je.getId()
                || ExceptionId.EX_JOB_TRIGGER_PARAMETER_VALIDATION_ERROR == je.getId()
                || ExceptionId.EX_JOB_TRIGGER_RECURSIVE_CALL == je.getId()) {

            final UpdateResponse response = new UpdateResponse(false, null);
            response.setErrors(Collections.singletonList(errorInfo));
            response.setParams(Collections.singletonList(new Param("errorMessage", je.getMessage())));
            return Response
                    .status(Response.Status.OK)
                    .entity(response)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .encoding(UTF_8)
                    .build();
        }

        ErrorResponse errorResponse = new ErrorResponse(je);
        errorResponse.getErrors().add(errorInfo);

        return Response
                .status(Response.Status.OK)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .encoding(UTF_8)
                .build();
    }

    /**
     * {@link ConsistencyException} prcessing.
     * @param exception the exception
     * @return response
     */
    private Response processConsistencyException(ConsistencyException exception) {

        UpdateResponse response = new UpdateResponse(false, null);
        ErrorInfo validationError = new ErrorInfo(ErrorInfo.Type.VALIDATION_ERROR);
        validationError.setUserMessageDetails(getConsistencyUserMessageDetails(exception));
        fillErrorInfo(validationError, exception);

        response.setErrors(Collections.singletonList(validationError));

        return Response
                .status(Response.Status.OK)
                .entity(response)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .encoding(UTF_8)
                .build();
    }

    /**
     * {@link ConsistencyException} user details.
     * @param exception the exception
     * @return response
     */
    private String getConsistencyUserMessageDetails(ConsistencyException exception) {
        if (MapUtils.isNotEmpty(exception.getLinks())) {
            Map<String, Long> links = exception.getLinks();
            StringBuilder stringBuilder = new StringBuilder();
//            final String messageCode = exception.getArgs().length == 2 ?
//                    "app.data.consistency.removeUnavailableWithPeriods" : ;
            stringBuilder.append(MessageUtils.getMessage("app.data.consistency.removeUnavailable", exception.getArgs()));

            for (Entry<String, Long> link : links.entrySet()) {
                stringBuilder.append("\n- ");
                stringBuilder.append(link.getKey());
                stringBuilder.append(" : ");
                stringBuilder.append(link.getValue());
            }

            return stringBuilder.toString();
        }
        else if (CollectionUtils.isNotEmpty(exception.getEntities())) {
            final Set<String> entities = exception.getEntities();

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(MessageUtils.getMessage("app.data.consistency.removeUnavailable", entities.size()));

            for (String entity : entities) {
                stringBuilder.append("\n- ");
                stringBuilder.append(entity);
            }

            return stringBuilder.toString();

        }
        return null;
    }

    /**
     * Custom processor for the user exit exception
     * {@link ExitException} processing.
     * @param exception the exception
     * @return response
     */
    private Response processExitException(ExitException exception) {
        ErrorInfo errorInfo = new ErrorInfo(ErrorInfo.Type.INTERNAL_ERROR);
        errorInfo.setInternalMessage(exception.getMessage());
        errorInfo.setErrorCode(ExceptionId.EX_RUN_EXIT.name());
        errorInfo.setUserMessage(MessageUtils.getMessage(ExceptionId.EX_RUN_EXIT.getCode(),
                StringUtils.isEmpty(exception.getMessage()) ? "" : exception.getMessage()));

        ErrorResponse response = new ErrorResponse();
        response.setErrors(Collections.singletonList(errorInfo));

        return Response
                .status(Response.Status.OK)
                .entity(response)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .encoding(UTF_8)
                .build();
    }

    private <T extends SystemRuntimeException> void fillErrorInfo(ErrorInfo errorInfo, T exception) {
        errorInfo.setInternalMessage(exception.getMessage());
        errorInfo.setErrorCode(exception.getId().name());
        errorInfo.setUserMessage(
                MessageUtils.getMessage(exception.getId().getCode(), exception.getArgs())
        );
    }

    private List<Param> fillParamsList(List<Pair<String, String>> params) {

        if (params != null && !params.isEmpty()) {
            List<Param> result = new ArrayList<>(params.size());
            for (Pair<String, String> p : params) {
                result.add(new Param(p.getKey(), p.getValue()));
            }

            return result;
        }

        return Collections.emptyList();
    }
}

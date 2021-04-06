package com.unidata.mdm.backend.exceptions;

import com.unidata.mdm.backend.api.rest.dto.ErrorInfo;
import com.unidata.mdm.backend.api.rest.dto.ErrorResponse;
import com.unidata.mdm.backend.api.rest.dto.UpdateResponse;
import com.unidata.mdm.backend.common.exception.*;
import com.unidata.mdm.backend.configuration.GlobalRestExceptionMapper;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.security.access.AccessDeniedException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * @author Dmitrii Kopin. Created on 28.03.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class GlobalRestExceptionMapperUnitTest {

    private static final String TEST_EXCEPTION_MESSAGE = "test_exception_message";
    @InjectMocks
    private ExceptionMapper globalRestExceptionMapper = new GlobalRestExceptionMapper();
    @Mock
    private MessageSource messageSource;



    @Test
    public void checkAccessDeniedExceptionMapperTest() {
        Response response = globalRestExceptionMapper.toResponse(new AccessDeniedException(TEST_EXCEPTION_MESSAGE));
        assertEquals(response.getStatus(), Response.Status.FORBIDDEN.getStatusCode());
        assertNull(response.getEntity());
    }


    @Test
    public void checkCleanseFunctionExecutionExceptionMapperTest() {
        Response response = globalRestExceptionMapper.toResponse(new CleanseFunctionExecutionException(TEST_EXCEPTION_MESSAGE, TEST_EXCEPTION_MESSAGE));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        assertTrue(response.getEntity() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertTrue(CollectionUtils.isNotEmpty(errorResponse.getErrors()));
        ErrorInfo errorInfo = errorResponse.getErrors().iterator().next();

        assertEquals(ErrorInfo.Type.INTERNAL_ERROR.name(), errorInfo.getType());
        assertNull(errorInfo.getInternalMessage());
        assertEquals(ExceptionId.EX_SYSTEM_CLEANSE_EXEC_FAILED.name(), errorInfo.getErrorCode());
    }

    @Test
    public void checkConversionFailedExceptionMapperTest() {
        Response response = globalRestExceptionMapper.toResponse(new ConversionFailedException(null, null, null, null));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        assertTrue(response.getEntity() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertTrue(CollectionUtils.isNotEmpty(errorResponse.getErrors()));
        ErrorInfo errorInfo = errorResponse.getErrors().iterator().next();

        assertEquals(ErrorInfo.Type.INTERNAL_ERROR.name(), errorInfo.getType());
        assertNotNull(errorInfo.getInternalMessage());

        response = globalRestExceptionMapper.toResponse(new ConversionFailedException(null, null, null, new AccessDeniedException("test")));
        assertEquals(response.getStatus(), Response.Status.FORBIDDEN.getStatusCode());
        assertNull(response.getEntity());
    }

    @Test
    public void checkConsistencyExceptionMapperTest() {
        Response response = globalRestExceptionMapper.toResponse(
                new ConsistencyException(TEST_EXCEPTION_MESSAGE, ExceptionId.EX_DATA_ETALON_CONTAINS_LINKS_TO_YOURSELF_WITH_PERIODS, Collections.emptyMap())
        );
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        assertTrue(response.getEntity() instanceof UpdateResponse);
        UpdateResponse errorResponse = (UpdateResponse) response.getEntity();
        assertTrue(CollectionUtils.isNotEmpty(errorResponse.getErrors()));
        ErrorInfo errorInfo = errorResponse.getErrors().iterator().next();

        assertEquals(ErrorInfo.Type.VALIDATION_ERROR.name(), errorInfo.getType());
        assertNotNull(errorInfo.getInternalMessage());

    }

    @Test
    public void checkSystemSecurityExceptionMapperTest() {
        Response response = globalRestExceptionMapper.toResponse(new SystemSecurityException(TEST_EXCEPTION_MESSAGE, ExceptionId.EX_DATA_ETALON_CONTAINS_LINKS_TO_YOURSELF, null));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        assertTrue(response.getEntity() instanceof UpdateResponse);
        UpdateResponse errorResponse = (UpdateResponse) response.getEntity();
        assertTrue(CollectionUtils.isNotEmpty(errorResponse.getErrors()));
        ErrorInfo errorInfo = errorResponse.getErrors().iterator().next();

        assertEquals(ErrorInfo.Type.INTERNAL_ERROR.name(), errorInfo.getType());
        assertNotNull(errorInfo.getInternalMessage());

    }

    @Test
    public void checkBusinessExceptionMapperTest() {
        Response response = globalRestExceptionMapper.toResponse(new BusinessException(TEST_EXCEPTION_MESSAGE, ExceptionId.EX_DATA_ETALON_CONTAINS_LINKS_TO_YOURSELF, null));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        assertTrue(response.getEntity() instanceof UpdateResponse);
        UpdateResponse errorResponse = (UpdateResponse) response.getEntity();
        assertTrue(CollectionUtils.isNotEmpty(errorResponse.getErrors()));
        ErrorInfo errorInfo = errorResponse.getErrors().iterator().next();

        assertEquals(ErrorInfo.Type.INTERNAL_ERROR.name(), errorInfo.getType());
        assertNotNull(errorInfo.getInternalMessage());

    }

    @Test
    public void checkLicenseExceptionMapperTest() {
        Response response = globalRestExceptionMapper.toResponse(new LicenseException(TEST_EXCEPTION_MESSAGE, ExceptionId.EX_DATA_ETALON_CONTAINS_LINKS_TO_YOURSELF, null));
        assertEquals(Response.Status.PAYMENT_REQUIRED.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        assertTrue(response.getEntity() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertTrue(CollectionUtils.isNotEmpty(errorResponse.getErrors()));
        ErrorInfo errorInfo = errorResponse.getErrors().iterator().next();

        assertEquals(ErrorInfo.Type.INTERNAL_ERROR.name(), errorInfo.getType());
        assertNotNull(errorInfo.getInternalMessage());

    }

    @Test
    public void checkDataProcessingExceptionMapperTest() {
        Response response = globalRestExceptionMapper.toResponse(new DataProcessingException(TEST_EXCEPTION_MESSAGE, ExceptionId.EX_DATA_ETALON_CONTAINS_LINKS_TO_YOURSELF, null));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        assertTrue(response.getEntity() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertTrue(CollectionUtils.isNotEmpty(errorResponse.getErrors()));
        ErrorInfo errorInfo = errorResponse.getErrors().iterator().next();

        assertEquals(ErrorInfo.Type.INTERNAL_ERROR.name(), errorInfo.getType());
        assertNotNull(errorInfo.getInternalMessage());

    }
}

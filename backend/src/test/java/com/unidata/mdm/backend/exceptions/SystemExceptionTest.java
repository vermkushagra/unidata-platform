package com.unidata.mdm.backend.exceptions;

import org.junit.Test;

import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;

public class SystemExceptionTest {

    private static final String Message = "Message";

    @Test(expected = SystemRuntimeException.class)
    public void stackTraceTest() {
        SystemRuntimeException systemRuntimeException = new SystemRuntimeException(Message, ExceptionId.EX_META_ROOT_GROUP_IS_ABSENT);
        assert systemRuntimeException.getStackTrace().length == 0;
        throw systemRuntimeException;
    }

    @Test(expected = SystemRuntimeException.class)
    public void stackTraceWithCauseTest() {
        try {
            int x = exceptionMethod();
            System.out.print(x);
        } catch (Exception e) {
            SystemRuntimeException systemRuntimeException = new SystemRuntimeException(Message, e, ExceptionId.EX_META_ROOT_GROUP_IS_ABSENT);
            assert systemRuntimeException.getStackTrace().length != 0;
            throw systemRuntimeException;
        }
    }


    private int exceptionMethod() {
        int i = 0;
        int b = 5;
        return b / i;
    }
}

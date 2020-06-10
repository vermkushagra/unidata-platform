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

package com.unidata.mdm.api.wsdl.v4;

import java.util.Locale;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

public class NonStandardErrorInterceptor extends AbstractPhaseInterceptor<MessageImpl> {

    /**
     * Russian locale.
     */
    private static final Locale RU = new Locale("ru");

    @Autowired
    private MessageSource messageSource;

    public NonStandardErrorInterceptor() {
        super(Phase.PRE_LOGICAL);
    }

    @Override
    public void handleMessage(MessageImpl message) throws Fault {
        Exception fault = message.getContent(Exception.class);
        if (fault == null || !(fault instanceof Fault)) {
            return;
        }
        Throwable faultCause = fault.getCause();
        if (faultCause instanceof NumberFormatException) {
            String error = messageSource.getMessage("app.incorrect.xml.attribute.number",
                    new Object[] { fault.getMessage() }, fault.getMessage(), RU);
            ((Fault) fault).setMessage(error);
        }
    }
}

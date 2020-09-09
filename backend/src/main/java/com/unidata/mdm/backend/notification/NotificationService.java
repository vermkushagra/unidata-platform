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

package com.unidata.mdm.backend.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.unidata.mdm.backend.common.dto.NotificationAttachment;
import com.unidata.mdm.backend.service.mail.EmailService;

import java.util.*;

/**
 * @author Michael Yashin. Created on 25.03.2015.
 */
@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public final static String FROM_ADDRESS = "lesegais@mail.ru";

    @Autowired
    private EmailService emailService;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    public void sendNotification(String to, String subjectTemplateName, String templateName,
                                 Map<String, Object>params, List<NotificationAttachment>attachments) {
        Set<String> recipients = new HashSet<>();
        recipients.add(to);
        sendNotification(recipients, subjectTemplateName, templateName, params, attachments);
    }

    public void sendNotification(Set<String> to, String subjectTemplateName, String templateName,
                       Map<String, Object>params, List<NotificationAttachment>attachments) {
        try {
            emailService.send(to, FROM_ADDRESS,
                    renderContent(subjectTemplateName, params),
                    renderContent(templateName, params),
                    attachments);
        } catch (Exception ex) {
            log.error("Failed to send message: " + ex.getMessage(), ex);
        }

    }

    private String renderContent(String template, Map<String, Object> model) throws Exception {
        return FreeMarkerTemplateUtils.processTemplateIntoString(freeMarkerConfig.getConfiguration().getTemplate(template), model);
    }
}

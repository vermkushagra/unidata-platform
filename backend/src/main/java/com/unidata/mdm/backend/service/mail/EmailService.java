package com.unidata.mdm.backend.service.mail;

import javax.mail.MessagingException;

import com.unidata.mdm.backend.common.dto.NotificationAttachment;

import java.util.List;
import java.util.Set;

/**
 * @author Michael Yashin
 */
public interface EmailService {

    public void send(Set<String> to, String from, String subject, String body) throws MessagingException;

    public void send(Set<String> to, String from, String subject, String body,
                     List<NotificationAttachment> attachments) throws MessagingException;

}

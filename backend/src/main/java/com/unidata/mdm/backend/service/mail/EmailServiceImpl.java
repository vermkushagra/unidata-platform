package com.unidata.mdm.backend.service.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.unidata.mdm.backend.common.dto.NotificationAttachment;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Michael Yashin
 */
@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    //@Autowired
    private Session session;

    public void send(Set<String> to, String from, String subject, String body) throws MessagingException {
        send(to, from, subject, body, null);
    }

    @Async
    public void send(Set<String> to, String from, String subject, String body,
                     List<NotificationAttachment> attachments) throws MessagingException {
        log.debug("Sending mail message to {} subject [{}]", to, subject);
        long start = System.currentTimeMillis();
        try {
            List<Address> addressList = new ArrayList<>();
            for (String rcpt : to) {
                addressList.add(new InternetAddress(rcpt));
            }
            Address[] recipients = addressList.toArray(new Address[to.size()]);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, recipients);
            message.setSubject(subject);

            BodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(body, "text/plain; charset=utf-8");

            Multipart multipart = new MimeMultipart("mixed");
            multipart.addBodyPart(bodyPart);
            if (attachments != null) {
                for (NotificationAttachment attachment : attachments) {
                    byte[] attachmentData = attachment.getData();
                    String filename = attachment.getFilename();
                    MimeBodyPart messageBodyPart = new MimeBodyPart();
                    DataSource source = new ByteArrayDataSource(attachmentData, "application/octet-stream");
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(filename);
                    multipart.addBodyPart(messageBodyPart);
                }
            }
            message.setContent(multipart);

            Transport.send(message);
        } catch (Exception ex) {
            log.error("Failed to send message: " + ex.getMessage(), ex);
        } finally {
            log.debug(System.currentTimeMillis() - start + " ms Finished email sending");
        }
    }

}

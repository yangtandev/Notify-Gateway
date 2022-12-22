package com.gini.notifyGateway.service;

import com.gini.notifyGateway.model.CalendarRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CalendarService {
    public static final Logger logger = LoggerFactory.getLogger(CalendarService.class);
    private JavaMailSender mailSender;

    public CalendarService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendCalendarInvite(String fromEmail, CalendarRequest calendarRequest, String organizer) throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        mimeMessage.addHeaderLine("method=REQUEST");
        mimeMessage.addHeaderLine("charset=UTF-8");
        mimeMessage.addHeaderLine("component=VEVENT");
        mimeMessage.setFrom(new InternetAddress(fromEmail));
        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(calendarRequest.getToEmail()));
        mimeMessage.setSubject(calendarRequest.getSubject());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
        StringBuilder builder = new StringBuilder();

        builder.append("BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:" + organizer + "\n" +
            "CALSCALE:GREGORIAN\n" +
            "METHOD:" + calendarRequest.getMethod() + "\n" +
            "BEGIN:VEVENT\n" +
            "UID:" + calendarRequest.getUid() + "\n" +
            "ORGANIZER;CN=" + organizer + ":MAILTO:" + fromEmail + "\n" +
            "ATTENDEE;CUTYPE=INDIVIDUAL;ROLE=REQ-PARTICIPANT;X-NUM-GUESTS=0:MAILTO:" + calendarRequest.getToEmail() + "\n" +
            "DTSTAMP:" + formatter.format(LocalDateTime.now()).replace(" ", "T") + "\n" +
            "DTSTART:" + formatter.format(calendarRequest.getStartTime()).replace(" ", "T") + "\n" +
            "DTEND:" + formatter.format(calendarRequest.getEndTime()).replace(" ", "T") + "\n" +
            "SUMMARY:" + calendarRequest.getSubject() + "\n" +
            "DESCRIPTION:" + calendarRequest.getBody() + "\n" +
            "LOCATION:" + calendarRequest.getLocation() + "\n" +
            "CLASS:PRIVATE\n" +
            "SEQUENCE:0\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR"
        );
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setHeader("Content-Class", "urn:content-classes:calendarmessage");
        messageBodyPart.setHeader("Content-ID", "calendar_message");
        messageBodyPart.setHeader("Content-Type", "text/calendar; charset=UTF-8; method=REQUEST");
        messageBodyPart.setDataHandler(new DataHandler(
            new ByteArrayDataSource(builder.toString(), "text/calendar;method=REQUEST;name=\"calendar.ics\"")));
        MimeMultipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        mimeMessage.setContent(multipart);
        mailSender.send(mimeMessage);
    }

}
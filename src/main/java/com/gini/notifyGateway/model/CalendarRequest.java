package com.gini.notifyGateway.model;

import com.gini.notifyGateway.utils.UUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CalendarRequest {
    public static final Logger logger = LoggerFactory.getLogger(CalendarRequest.class);
    private String uid = UUIDGenerator.generateUUID22();
    private String toEmail;
    private String subject;
    private String body;
    private String location;
    private String method;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private CalendarRequest(Builder builder) {
        uid = builder.toUid;
        toEmail = builder.toEmail;
        subject = builder.subject;
        body = builder.body;
        location = builder.location;
        method = builder.method;
        startTime = builder.startTime;
        endTime = builder.endTime;
    }

    public String getUid() {
        return uid;
    }

    public String getToEmail() {
        return toEmail;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getLocation() {
        return location;
    }

    public String getMethod() {
        return method;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public static final class Builder {
        private String toUid;
        private String toEmail;
        private String subject;
        private String body;
        private String location;
        private String method;
        private LocalDateTime startTime;
        private LocalDateTime endTime;

        public Builder() {
        }

        public Builder withUid(String val) {
            toUid = val;
            return this;
        }

        public Builder withToEmail(String val) {
            toEmail = val;
            return this;
        }

        public Builder withSubject(String val) {
            subject = val;
            return this;
        }

        public Builder withBody(String val) {
            body = val;
            return this;
        }

        public Builder withLocation(String val) {
            location = val;
            return this;
        }

        public Builder withMethod(String val) {
            method = val.toUpperCase();
            return this;
        }

        public Builder withStartTime(String val) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(val, formatter);
            startTime = dateTime;
            return this;
        }

        public Builder withEndTime(String val) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(val, formatter);
            endTime = dateTime;
            return this;
        }

        public CalendarRequest build() {
            return new CalendarRequest(this);
        }
    }
}
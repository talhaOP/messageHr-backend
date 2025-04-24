package com.messageHr.messageHr.dto;

import java.util.List;

//package com.example.email;
import org.springframework.web.multipart.MultipartFile;

public class EmailRequest {
    private List<String> toEmails;  // List of recipient email addresses
    private String subject;
    private String body;

    // Getters and Setters
    public List<String> getToEmails() {
        return toEmails;
    }

    public void setToEmails(List<String> toEmails) {
        this.toEmails = toEmails;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
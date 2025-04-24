package com.messageHr.messageHr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.messageHr.messageHr.dto.EmailRequest;
import com.messageHr.messageHr.service.EmailService;
import jakarta.mail.MessagingException;
import java.io.IOException;

@RestController
@RequestMapping("/email")
@CrossOrigin
public class EmailController {
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(value = "/sendWithAttachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> sendEmailWithAttachment(
            @RequestParam("emailRequest") String emailRequestJson,
            @RequestParam("attachment") MultipartFile attachment) {
    	 
        System.out.println("Received emailRequest: " + emailRequestJson);
        System.out.println("Received file name: " + attachment.getOriginalFilename());
        System.out.println("File size: " + attachment.getSize());
        System.out.println("Content type: " + attachment.getContentType());
        try {
            EmailRequest emailRequest = objectMapper.readValue(emailRequestJson, EmailRequest.class);
            
            emailService.sendEmailWithAttachment(
                    emailRequest.getToEmails().toArray(new String[0]),
                    emailRequest.getSubject(),
                    emailRequest.getBody(),
                    attachment
            );
            
            return ResponseEntity.ok("Email sent successfully to: " + String.join(", ", emailRequest.getToEmails()));
        } catch (MessagingException | IOException e) {
            e.printStackTrace(); // Add this for debugging
            return ResponseEntity.badRequest().body("Error sending email: " + e.getMessage());
        }
    }
}
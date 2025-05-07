package com.messageHr.messageHr.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;
	@Value("${spring.mail.username}")
	private String fromEmail;

	public void sendEmailWithAttachment(String[] toEmails, String subject, String body, MultipartFile attachment)
			throws MessagingException, IOException {

		for (String toEmail : toEmails) {
			try {
				MimeMessage mimeMessage = mailSender.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

				helper.setTo(toEmail); 
				helper.setSubject(subject);
				helper.setText(body, true); 
				helper.setFrom(fromEmail, "Talha Ansari");

				if (attachment != null && !attachment.isEmpty()) {
					helper.addAttachment(attachment.getOriginalFilename(),
							new ByteArrayResource(attachment.getBytes()));
				}

				mailSender.send(mimeMessage);
				System.out.println("Mail sent to: " + toEmail);

			} catch (MailException | MessagingException e) {
				System.err.println("Failed to send email to " + toEmail + ": " + e.getMessage());
			}
		}
	}

}

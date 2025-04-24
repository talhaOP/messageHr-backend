package com.messageHr.messageHr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class OtpEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OtpService otpService;

    public String sendOtpToUser(String email) {
        int otp = otpService.generateOtp(email);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code for Login/Signup");
        message.setText("Your OTP code is: " + otp + ". It is valid for 5 minutes.");

        mailSender.send(message);
        return "OTP sent successfully to " + email;
    }
}

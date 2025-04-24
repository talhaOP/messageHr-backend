package com.messageHr.messageHr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
	private final Random random = new Random();
	@Autowired
	private JavaMailSender mailSender;

	private final ConcurrentHashMap<String, OtpDetails> otpStorage = new ConcurrentHashMap<>();
	private final long OTP_VALID_DURATION = 5 * 60 * 1000;

	public int generateOtp(String email) {
		int otp = 100000 + random.nextInt(900000); 
		otpStorage.put(email, new OtpDetails(otp, System.currentTimeMillis()));
		return otp;
	}

	public boolean sendPasswordResetOtp(String email) {
		int otp = generateOtp(email); 
		sendOtpEmail(email, otp); 
		return true; 
	}

	public void sendOtpEmail(String email, int otp) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("Password Reset OTP");
		message.setText("Your OTP for password reset is: " + otp);
		mailSender.send(message); 
	}

	public boolean verifyOtp(String email, int inputOtp) {
		if (!otpStorage.containsKey(email)) {
			return false;
		}

		OtpDetails otpDetails = otpStorage.get(email);

		if (System.currentTimeMillis() - otpDetails.getTimestamp() > OTP_VALID_DURATION) {
			otpStorage.remove(email);
			return false; 
		}

		if (otpDetails.getOtp() == inputOtp) {
			otpStorage.remove(email); 
			return true;
		}

		return false;
	}

	private static class OtpDetails {
		private final int otp;
		private final long timestamp;

		public OtpDetails(int otp, long timestamp) {
			this.otp = otp;
			this.timestamp = timestamp;
		}

		public int getOtp() {
			return otp;
		}

		public long getTimestamp() {
			return timestamp;
		}
	}
}

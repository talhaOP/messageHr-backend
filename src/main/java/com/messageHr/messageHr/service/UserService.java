package com.messageHr.messageHr.service;

import com.messageHr.messageHr.controller.LoginStatus;
import com.messageHr.messageHr.dto.User;
import com.messageHr.messageHr.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

	@Autowired
	private OtpService otpService;
	@Autowired
	private PasswordEncoder passwordEncoder; 

	@Autowired
	private UserRepository userRepository;

	public String registerUser(String name, String email, String password) {
		if (userRepository.findByEmail(email).isPresent()) {
			return "User already exists!";
		}
		User user = new User();
		user.setName(name);
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));
		user.setIsVerified(false); 

		userRepository.save(user);
		return "User registered successfully. Please verify your email.";
	}

	public void updateUserVerification(String email) {
		Optional<User> userOptional = userRepository.findByEmail(email);

		if (userOptional.isPresent()) {
			User user = userOptional.get();

			if (user.getIsVerified()) {
				System.out.println("User is already verified: " + email);
				return; 
			}

			user.setIsVerified(true);
			userRepository.save(user);

			System.out.println("User verification updated successfully for: " + email);
		} else {
			System.out.println("User not found for verification: " + email);
		}
	}

	public LoginStatus authenticateUser(String email, String password) {
		Optional<User> userOptional = userRepository.findByEmail(email);
		if (userOptional.isEmpty()) {
			return LoginStatus.INVALID_CREDENTIALS;
		}

		User user = userOptional.get();

		if (!passwordEncoder.matches(password, user.getPassword())) {
			return LoginStatus.INVALID_CREDENTIALS;
		}

		if (!user.getIsVerified()) {
			int otp = otpService.generateOtp(email);
			otpService.sendOtpEmail(email, otp);
			return LoginStatus.NOT_VERIFIED;
		}

		return LoginStatus.SUCCESS;
	}

	public boolean resetPassword(String email, String newPassword) {
		User user = userRepository.findByEmail(email).orElse(null);
		if (user != null) {
			user.setPassword(passwordEncoder.encode(newPassword)); 
			userRepository.save(user); 
			return true;
		}
		return false; 
	}
}

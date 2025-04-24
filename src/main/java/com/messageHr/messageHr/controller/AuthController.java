package com.messageHr.messageHr.controller;

import com.messageHr.messageHr.config.JwtService;
import com.messageHr.messageHr.dto.SignupRequest;
import com.messageHr.messageHr.dto.User;
import com.messageHr.messageHr.repo.UserRepository;
import com.messageHr.messageHr.service.OtpEmailService;
import com.messageHr.messageHr.service.OtpService;
import com.messageHr.messageHr.service.UserService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private OtpEmailService otpEmailService;

	@Autowired
	private OtpService otpService;

	@Autowired
	private UserService userService;

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
			UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("/send-otp")
	public String sendOtp(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		return otpEmailService.sendOtpToUser(email);
	}

	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		int otp = Integer.parseInt(request.get("otp"));

		boolean isValid = otpService.verifyOtp(email, otp);

		if (isValid) {
			userService.updateUserVerification(email);
			return "OTP Verified! Your account is now activated.";
		}

		return "Invalid or expired OTP.";
	}

	@PostMapping("/register")
	public String register(@RequestBody User user) {
		String response = userService.registerUser(user.getName(), user.getEmail(), user.getPassword());

		if (response.startsWith("User registered")) {
			otpEmailService.sendOtpToUser(user.getEmail()); 
		}

		return response;
	}

	@PostMapping("/login")
	public Map<String, String> login(@RequestBody Map<String, String> body) {
		try {
			String email = body.get("email");
			String password = body.get("password");

			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

			User user = userRepository.findByEmail(email).orElseThrow();
			String token = jwtService.generateToken(org.springframework.security.core.userdetails.User
					.withUsername(user.getEmail()).password(user.getPassword()).roles("USER").build());

			return Map.of("token", token);
		} catch (AuthenticationException e) {
			throw new RuntimeException("Invalid email/password");
		}
	}

	
	@PostMapping("/forgot-password")
	public String forgotPassword(@RequestParam String email) {
		boolean result = otpService.sendPasswordResetOtp(email); // Send OTP for reset
		return result ? "OTP sent to your email for password reset." : "Error sending OTP.";
	}

	@PostMapping("/reset-password")
	public String resetPassword(@RequestParam String email, @RequestParam int otp, @RequestParam String newPassword) {
		boolean isValidOtp = otpService.verifyOtp(email, otp);
		if (!isValidOtp) {
			return "Invalid OTP.";
		}

		boolean result = userService.resetPassword(email, newPassword);
		return result ? "Password reset successful!" : "Error resetting password.";
	}

}

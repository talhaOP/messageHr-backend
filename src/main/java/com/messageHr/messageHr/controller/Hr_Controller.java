package com.messageHr.messageHr.controller;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.messageHr.messageHr.dto.Hr;
import com.messageHr.messageHr.response.ApiResponse;
import com.messageHr.messageHr.service.Hr_Service;

@RestController
@Component
@RequestMapping("/hr")
@CrossOrigin(origins = "http://localhost:5173") // Vite default port

public class Hr_Controller {

	@Autowired
	private Hr_Service service;

	@PostMapping("/save/hr")
	public Hr save(@RequestBody Hr hr) {
		return service.save(hr);

	}

	@GetMapping("/find/{id}")
	public Hr findById(@PathVariable int id) {
		return service.findById(id);

	}

	@GetMapping("/find/all")
	public List<Hr> findAll() {
		return service.findAll();
	}

	@GetMapping("/findByName")
	public List<Hr> findByName(@RequestParam String name) {
		return service.findByName(name);
	}

	@GetMapping("/findByNameIgnoreCase")
	public ResponseEntity<ApiResponse> findByNameIgnoreCase(@RequestParam String name) {
		List<Hr> result = service.findByNameIgnoreCase(name);
		if (result.isEmpty()) {
			return new ResponseEntity<>(new ApiResponse("No HR data found for name: " + name, null),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(new ApiResponse("Success", result), HttpStatus.OK);
	}

	@GetMapping("/findByLocation")
	public ResponseEntity<?> findByLocation(@RequestParam String location) {
		try {
			System.out.println("Received location: '" + location + "'");

			location = location.trim();

			if (location.isEmpty()) {
				return new ResponseEntity<>("Location parameter cannot be empty", HttpStatus.BAD_REQUEST);
			}

			System.out.println("Trimmed location: '" + location + "'");

			List<Hr> result = service.findByLocation(location);

			return new ResponseEntity<>(new ApiResponse("Success", result), HttpStatus.OK);

		} catch (RuntimeException e) {
			return new ResponseEntity<>(new ApiResponse(e.getMessage(), null), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(new ApiResponse("An unexpected error occurred: " + e.getMessage(), null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/uploadCsv")
	public String uploadCsv(@RequestParam("file") MultipartFile file) {
		try {
			// Parse the CSV file
			InputStreamReader reader = new InputStreamReader(file.getInputStream());
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());

			List<Hr> hrList = new ArrayList<>();
			for (CSVRecord record : csvParser) {
				Hr hr = new Hr();
				hr.setName(record.get("name"));
				hr.setEmail(record.get("email"));
				hr.setLocation(record.get("location"));
				hrList.add(hr);
				csvParser.close();
			}

			service.saveAll(hrList);

			return "Successfully uploaded and saved " + hrList.size() + " HR records.";
		} catch (Exception e) {
			return "Error processing the file: " + e.getMessage();
		}
	}

	@DeleteMapping("/delete")
	public ResponseEntity<String> deleteHrRecords(@RequestParam(required = false) String ids,
			@RequestParam(required = false) String names, @RequestParam(required = false) String emails) {

		// Parse comma-separated values into lists
		List<Integer> idList = parseIds(ids);
		List<String> nameList = parseNames(names);
		List<String> emailList = parseEmails(emails);

		// Validate at least one parameter is provided
		if (idList.isEmpty() && nameList.isEmpty() && emailList.isEmpty()) {
			return ResponseEntity.badRequest().body("At least one parameter (ids, names, or emails) must be provided.");
		}

		try {
			service.deleteByIdsOrNamesOrEmails(idList, nameList, emailList);
			return ResponseEntity.ok("Records deleted successfully.");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	// Helper methods to parse comma-separated values
	private List<Integer> parseIds(String ids) {
		if (ids == null || ids.trim().isEmpty())
			return Collections.emptyList();
		return Arrays.stream(ids.split(",")).map(String::trim).map(Integer::parseInt).collect(Collectors.toList());
	}

	private List<String> parseNames(String names) {
		if (names == null || names.trim().isEmpty())
			return Collections.emptyList();
		return Arrays.stream(names.split(",")).map(String::trim).map(String::toLowerCase).collect(Collectors.toList());
	}

	private List<String> parseEmails(String emails) {
		if (emails == null || emails.trim().isEmpty())
			return Collections.emptyList();
		return Arrays.stream(emails.split(",")).map(String::trim).map(String::toLowerCase).collect(Collectors.toList());
	}

	// In Hr_Controller.java
	@PutMapping("/update/{id}")
	public ResponseEntity<ApiResponse> updateHr(@PathVariable int id, @RequestBody Hr updatedHr) {

		try {
			Hr result = service.updateHr(id, updatedHr);
			return new ResponseEntity<>(new ApiResponse("Success", result), HttpStatus.OK);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(new ApiResponse(e.getMessage(), null), HttpStatus.NOT_FOUND);
		}
	}
}

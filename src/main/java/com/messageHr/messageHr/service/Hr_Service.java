package com.messageHr.messageHr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.messageHr.messageHr.dao.Dao;
import com.messageHr.messageHr.dto.Hr;

@Service
@Component
public class Hr_Service {

	@Autowired
	private Dao dao;

	public Hr save(Hr hr) {
		return dao.save(hr);

	}

	public Hr findById(int id) {
		return dao.findById(id);

	}

	public List<Hr> findAll() {
		return dao.findAll();
	}

	public List<Hr> findByName(String name) {
		return dao.findByName(name);
	}

	public List<Hr> findByNameIgnoreCase(String name) {
		List<Hr> result = dao.findByNameIgnoreCase(name);
		if (result.isEmpty()) {
			throw new RuntimeException("No HR data found for name: " + name);
		}
		return result;
	}

	public List<Hr> findByLocation(String location) {
		List<Hr> result = dao.findByLocation(location.trim());
		if (result.isEmpty()) {
			throw new RuntimeException("No HR data found for location service: " + location);
		}
		return result;
	}

	public void saveAll(List<Hr> hrList) {
		dao.saveAll(hrList);
	}

	public void deleteByIdsOrNamesOrEmails(List<Integer> ids, List<String> names, List<String> emails) {
		System.out.println(
				"Service: Deleting HRs with parameters - IDs: " + ids + ", Names: " + names + ", Emails: " + emails);
		dao.deleteByIdsOrNamesOrEmails(ids, names, emails);
	}

	public Hr updateHr(int id, Hr updatedHr) {
		Hr existingHr = dao.findById(id);

		if (updatedHr.getName() != null) {
			existingHr.setName(updatedHr.getName().trim());
		}
		if (updatedHr.getEmail() != null) {
			existingHr.setEmail(updatedHr.getEmail().trim().toLowerCase());
		}
		if (updatedHr.getLocation() != null) {
			existingHr.setLocation(updatedHr.getLocation().trim());
		}

		return dao.save(existingHr);
	}
}

package com.messageHr.messageHr.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import com.messageHr.messageHr.dto.Hr;
import com.messageHr.messageHr.repo.Hr_Repository;

@Component
public class Dao {
	@Autowired
	private Hr_Repository repository;

	public Hr save(Hr hr) {
		return repository.save(hr);
		
	}
	  public Hr findById(int id) {
	        return repository.findById(id)
	                         .orElseThrow(() -> new RuntimeException("Hr not found with id: " + id));
	    }
	  
	  public List<Hr> findAll() {
	        return repository.findAll();
	    }
	  public List<Hr> findByName(String name) {
	        return repository.findByName(name);
	    }
	  
	  public List<Hr> findByNameIgnoreCase(String name) {
	        return repository.findByNameIgnoreCase(name);
	    }
	  
	  public List<Hr> findByLocation(String location) {
	        List<Hr> result = repository.findByLocation(location.trim());
	        if (result.isEmpty()) {
	            throw new RuntimeException("No HR data found for location dao: " + location);
	        }
	        return result;
	    }
	  
	  public void saveAll(List<Hr> hrList) {
	        repository.saveAll(hrList);
	    }
	  
	 


	// In Dao.java
	  public void deleteByIdsOrNamesOrEmails(List<Integer> ids, List<String> names, List<String> emails) {
	      int deletedCount = repository.deleteByIdsOrNamesOrEmails(ids, names, emails);
	      if (deletedCount == 0) {
	          throw new RuntimeException("No records found with the provided parameters.");
	      }
	  }
}

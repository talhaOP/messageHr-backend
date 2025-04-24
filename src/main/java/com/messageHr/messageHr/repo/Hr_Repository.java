package com.messageHr.messageHr.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.messageHr.messageHr.dto.Hr;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.*;

@Repository
public interface Hr_Repository extends JpaRepository<Hr, Integer>, CustomHrRepository {
	
	 List<Hr> findByName(String name);

	 
	 @Query("SELECT h FROM Hr h WHERE LOWER(h.name) = LOWER(:name)")
	 List<Hr> findByNameIgnoreCase(@Param("name") String name);

	 @Query("SELECT h FROM Hr h WHERE LOWER(TRIM(h.location)) LIKE LOWER(CONCAT('%', TRIM(:location), '%'))")
	 List<Hr> findByLocation(@Param("location") String location);

	 @Transactional
	 @Modifying
	 @Query("DELETE FROM Hr h WHERE "
	      + "(:id IS NOT NULL AND h.id = :id) OR "
	      + "(:name IS NOT NULL AND LOWER(h.name) = LOWER(:name)) OR "
	      + "(:email IS NOT NULL AND LOWER(h.email) = LOWER(:email))")
	 int deleteByIdOrNameOrEmail(@Param("id") Integer id, @Param("name") String name, @Param("email") String email);
}
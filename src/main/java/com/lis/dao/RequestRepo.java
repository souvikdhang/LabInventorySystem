package com.lis.dao;

import com.lis.model.Requests;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepo extends JpaRepository<Requests, Integer> {

	Requests findByUserID(int userID);

	boolean existsByUserID(int userID);
	
	


}

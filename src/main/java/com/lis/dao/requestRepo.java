package com.lis.dao;

import java.util.List;

import com.lis.model.Requests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RequestRepo extends JpaRepository<Requests, Integer> {

	Requests findByUserID(int userID);

	boolean existsByUserID(int userID);
	


}

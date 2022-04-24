package com.lis.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lis.model.UserProfile;

@Repository
public interface User_repo extends JpaRepository<UserProfile, Integer>{
	Boolean existsByEmail(String email_id);
//	Boolean existsByEmailId(String email_id);

}

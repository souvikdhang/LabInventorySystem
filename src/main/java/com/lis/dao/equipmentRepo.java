package com.lis.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lis.model.UserProfile;
import com.lis.model.equipmentDetails;

@Repository
public interface equipmentRepo extends JpaRepository<equipmentDetails, Integer>{
	

}

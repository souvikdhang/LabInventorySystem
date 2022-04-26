package com.lis.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lis.model.equipmentAvailability;
import com.lis.model.equipmentDetails;

public interface equipmentAvailabilityRepo extends JpaRepository<equipmentAvailability, Integer>{
	
	public static void setAvailableAmount(int x, int y) {
		
	}

}

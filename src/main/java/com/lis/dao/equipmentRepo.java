package com.lis.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lis.model.EquipmentDetails;


@Repository
public interface EquipmentRepo extends JpaRepository<EquipmentDetails, Integer>{

	boolean existsByRackNumber(String rackNumber);
	

}

package com.lis.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lis.model.equipmentDetails;
import com.lis.model.requests;

public interface requestRepo extends JpaRepository<requests, Integer>{ 

}

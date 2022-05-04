package com.lis.dao;

import com.lis.model.EquipmentAvailability;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentAvailabilityRepo extends JpaRepository<EquipmentAvailability, Integer> {

    // public static void setAvailableAmount(int x, int y) {
    //
    // }

}

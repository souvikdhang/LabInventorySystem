package com.lis.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.stereotype.Component;

@Component
@Entity
public class equipmentAvailability {
	@Id
	private int equipmentID;
	private int availableamount = 0;
	public int getEquipmentID() {
		return equipmentID;
	}
	public void setEquipmentID(int equipmentID) {
		this.equipmentID = equipmentID;
	}
	public int getAvailableamount() {
		return availableamount;
	}
	public void setAvailableamount(int availableamount) {
		this.availableamount = availableamount;
	}
	@Override
	public String toString() {
		return "equipmentAvailability [equipmentID=" + equipmentID + ", availableamount=" + availableamount + "]";
	}
	
	
	

}

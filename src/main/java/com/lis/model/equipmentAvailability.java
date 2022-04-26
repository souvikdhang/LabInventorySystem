package com.lis.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import org.springframework.stereotype.Component;

@Component
@Entity
public class EquipmentAvailability {
	@Id
	private int Id;
	private int available_amount = 0;
	@OneToOne()
	@MapsId
	EquipmentDetails equipment;
	
	public EquipmentDetails getDetails() {
		return equipment;
	}
	public void setDetails(EquipmentDetails details) {
		this.equipment = details;
	}
	public int getEquipmentID() {
		return Id;
	}
	public void setEquipmentID(int equipmentID) {
		this.Id = equipmentID;
	}
	public int getAvailableamount() {
		return available_amount;
	}
	public void setAvailableamount(int availableamount) {
		this.available_amount = availableamount;
	}
	@Override
	public String toString() {
		return "equipmentAvailability [equipmentID=" + Id + ", availableamount=" + available_amount
				+ ", details=" + equipment + "]";
	}

	
	
	

}

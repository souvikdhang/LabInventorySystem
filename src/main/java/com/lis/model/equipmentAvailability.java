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
	private int issued_amount=0;
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
	public int getIssued_amount() {
		return issued_amount;
	}
	public void setIssued_amount(int issued_amount) {
		this.issued_amount = issued_amount;
	}
	@Override
	public String toString() {
		return "EquipmentAvailability [Id=" + Id + ", available_amount=" + available_amount + ", issued_amount="
				+ issued_amount + ", equipment=" + equipment + "]";
	}



	
	
	

}

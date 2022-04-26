package com.lis.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.stereotype.Component;

@Component
@Entity
public class requests {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int requestID;
	private int equipmentID;
	private int userID;
	private String requestStatus;
	
	
	public int getRequestID() {
		return requestID;
	}
	public void setRequestID(int requestID) {
		this.requestID = requestID;
	}
	public int getEquipmentID() {
		return equipmentID;
	}
	public void setEquipmentID(int equipmentID) {
		this.equipmentID = equipmentID;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public String getRequestStatus() {
		return requestStatus;
	}
	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}
	
	
	@Override
	public String toString() {
		return "requests [requestID=" + requestID + ", equipmentID=" + equipmentID + ", userID=" + userID
				+ ", requestStatus=" + requestStatus + "]";
	}
	

}

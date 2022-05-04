package com.lis.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.springframework.stereotype.Component;

@Component
@Entity
public class Requests {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="request_sequence")
	@SequenceGenerator(name = "request_sequence", sequenceName = "request_sequence", allocationSize = 1, initialValue= 1)
	private int requestID;
	private int equipmentID;
	private int userID;
	private String requestStatus;
	private LocalDate dateofRequest;
	
	
	public LocalDate getDate() {
		return dateofRequest;
	}
	public void setDate(LocalDate d) {
		this.dateofRequest = d;
	}
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
				+ ", requestStatus=" + requestStatus + ", date=" + dateofRequest + "]";
	}
	
	
	

}

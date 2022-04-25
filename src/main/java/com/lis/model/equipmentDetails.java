package com.lis.model;

import javax.annotation.Generated;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.stereotype.Component;

@Entity
@Component
public class equipmentDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int equipmentID;
	private String orgName;
	private String labName;
	private String rackNumber;
	private String serverName;
	private String serverIPAddress;
	private String loginID;
	private String loginPassword;
	private int virtualMachine;
	private int serverStatus;
	private int serverPower;
	
	/*public void set_equipmentID(int equipmentID) {
		this.equipmentID = equipmentID;
	}*/
	
	/*public int get_equipmentID() {
		return equipmentID;
	}*/
	
	
	/*public void set_orgName(String orgName) {
		this.orgName = orgName;
	}*/
	
	/*public String get_orgName() {
		return orgName;
	}*/
	
	/*public void set_labName(String labName) {
		this.labName = labName;
	}*/
	
	/*public String get_labName() {
		return labName;
	}*/
	

	public int getEquipmentID() {
		return equipmentID;
	}

	public void setEquipmentID(int equipmentID) {
		this.equipmentID = equipmentID;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getLabName() {
		return labName;
	}

	public void setLabName(String labName) {
		this.labName = labName;
	}

	public String getRackNumber() {
		return rackNumber;
	}

	public void setRackNumber(String rackNumber) {
		this.rackNumber = rackNumber;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getServerIPAddress() {
		return serverIPAddress;
	}

	public void setServerIPAddress(String serverIPAddress) {
		this.serverIPAddress = serverIPAddress;
	}

	public String getLoginID() {
		return loginID;
	}

	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public int getVirtualMachine() {
		return virtualMachine;
	}

	public void setVirtualMachine(int virtualMachine) {
		this.virtualMachine = virtualMachine;
	}

	public int getServerStatus() {
		return serverStatus;
	}

	public void setServerStatus(int serverStatus) {
		this.serverStatus = serverStatus;
	}

	public int getServerPower() {
		return serverPower;
	}

	public void setServerPower(int serverPower) {
		this.serverPower = serverPower;
	}

	
	

}

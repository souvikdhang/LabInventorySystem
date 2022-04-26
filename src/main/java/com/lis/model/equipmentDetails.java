package com.lis.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.springframework.stereotype.Component;

@Entity
@Component
public class EquipmentDetails {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="equipment_sequence_2")
	@SequenceGenerator(name = "equipment_sequence_2", sequenceName = "equipment_sequence_2", allocationSize = 1, initialValue= 1)
	private int equipment_Id;
	private String org_Name;
	private String lab_Name;
	private String rack_Number;
	private String server_Name;
	private String server_IPAddress;
	private String login_Id;
	private String login_Password;
	private int virtual_Machine;
	private int server_Status;
	private int server_Power;
	
	
	
	public int getEquipmentID() {
		return equipment_Id;
	}

	public void setEquipmentID(int equipmentID) {
		this.equipment_Id = equipmentID;
	}

	public String getOrgName() {
		return org_Name;
	}

	public void setOrgName(String orgName) {
		this.org_Name = orgName;
	}

	public String getLabName() {
		return lab_Name;
	}

	public void setLabName(String labName) {
		this.lab_Name = labName;
	}

	public String getRackNumber() {
		return rack_Number;
	}

	public void setRackNumber(String rackNumber) {
		this.rack_Number = rackNumber;
	}

	public String getServerName() {
		return server_Name;
	}

	public void setServerName(String serverName) {
		this.server_Name = serverName;
	}

	public String getServerIPAddress() {
		return server_IPAddress;
	}

	public void setServerIPAddress(String serverIPAddress) {
		this.server_IPAddress = serverIPAddress;
	}

	public String getLoginID() {
		return login_Id;
	}

	public void setLoginID(String loginID) {
		this.login_Id = loginID;
	}

	public String getLoginPassword() {
		return login_Password;
	}

	public void setLoginPassword(String loginPassword) {
		this.login_Password = loginPassword;
	}

	public int getVirtualMachine() {
		return virtual_Machine;
	}

	public void setVirtualMachine(int virtualMachine) {
		this.virtual_Machine = virtualMachine;
	}

	public int getServerStatus() {
		return server_Status;
	}

	public void setServerStatus(int serverStatus) {
		this.server_Status = serverStatus;
	}

	public int getServerPower() {
		return server_Power;
	}

	public void setServerPower(int serverPower) {
		this.server_Power = serverPower;
	}

	@Override
	public String toString() {
		return "equipmentDetails [equipmentID=" + equipment_Id + ", orgName=" + org_Name + ", labName=" + lab_Name
				+ ", rackNumber=" + rack_Number + ", serverName=" + server_Name + ", serverIPAddress=" + server_IPAddress
				+ ", loginID=" + login_Id + ", loginPassword=" + login_Password + ", virtualMachine=" + virtual_Machine
				+ ", serverStatus=" + server_Status + ", serverPower=" + server_Power + "]";
	}
	
	

	
	

}

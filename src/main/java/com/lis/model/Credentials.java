package com.lis.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import org.springframework.stereotype.Component;

@Entity
@Component
public class Credentials {
	
	@Id
	private int id;
	private String UserType;
	private String password;
	private boolean LoginStatus;
	
	@OneToOne
	@MapsId
	
	private UserProfile user;
	
	public void set_UserType(String UserType) {
		this.UserType = UserType;
	}
	
	public String get_UserType() {
		return UserType;
	}
	
	public void set_UserID(int UserID) {
		this.id = UserID;
	}
	
	public int get_UserID() {
		return id;
	}
	
	public UserProfile getUser() {
		return user;
	}

	public void setUser(UserProfile user) {
		this.user = user;
	}

	public void set_password(String password) {
		this.password = password;
	}
	
	public String get_password() {
		return password;
	}
	
	public void set_LoginStatus(boolean b) {
		this.LoginStatus = b;
	}
	
	public boolean get_LoginStatus() {
		return LoginStatus;
	}
	
	
	
}
	
	
	

package com.lis.model;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.stereotype.Component;
@Component
@Entity
@DynamicUpdate
@Cacheable
public class Credentials {
	
	@Id
	private int id;
	private String type;
	private String password;
	private boolean loginStatus;
	
	@OneToOne()
	@MapsId
	private UserProfile profileObj;
	
	@Override
	public String toString() {
		return "Credentials [id=" + id + ", UserType=" + type + ", password=" + password + ", LoginStatus="
				+ loginStatus + ", user=" + profileObj + "]";
	}

	public void set_UserType(String UserType) {
		this.type = UserType;
	}
	
	public String get_UserType() {
		return type;
	}
	
	public void set_UserID(int UserID) {
		this.id = UserID;
	}
	
	public int get_UserID() {
		return id;
	}
	
	public UserProfile getUser() {
		return profileObj;
	}

	public void setUser(UserProfile user) {
		this.profileObj = user;
	}

	public void set_password(String password) {
		this.password = password;
	}
	
	public String get_password() {
		return password;
	}
	
	public void set_LoginStatus(boolean b) {
		this.loginStatus = b;
	}
	
	public boolean get_LoginStatus() {
		return loginStatus;
	}
	
	
	
}
	
	
	

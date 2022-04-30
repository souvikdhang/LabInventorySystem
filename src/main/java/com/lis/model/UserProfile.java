package com.lis.model;

import java.text.ParseException;
import java.time.LocalDate;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.stereotype.Component;


@Component
@Entity
@DynamicUpdate
@Cacheable
public class UserProfile {
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="user_sequence_2")
	@SequenceGenerator(name = "user_sequence_2", sequenceName = "user_sequence_2", allocationSize = 1, initialValue= 1)
//	@OneToOne(fetch = FetchType.EAGER, mappedBy = "UserProfile")
	
	private int user_id;
	private String name;
	private LocalDate dob;
	private String gender;
	private String address;
	private String phoneNumber;
	private String email;
		
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LocalDate getDob() {
		return dob;
	}
	public void setDob(LocalDate dob) throws ParseException {

		this.dob = dob;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone_number() {
		return phoneNumber;
	}
	public void setPhone_number(String phone_number) {
		this.phoneNumber = phone_number;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
//	@Override
//	public String toString() {
//		return "{\"user_id\":" + user_id + ", \"name\":" + name + ", \"dob\":" + dob + ", \"gender\":" + gender
//				+ ", \"address\":" + address + ", \"phone_number\":" + phoneNumber + ", \"email\":" + email + "}";
//		
//	}
	
	@Override
	public String toString() {
		return "UserProfile [user_id=" + user_id + ", name=" + name + ", dob=" + dob + ", gender=" + gender
				+ ", address=" + address + ", phoneNumber=" + phoneNumber + ", email=" + email + "]";
	}
	
		

}

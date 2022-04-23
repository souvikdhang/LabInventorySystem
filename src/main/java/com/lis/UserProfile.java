package com.lis;

import java.text.ParseException;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.springframework.stereotype.Component;


@Component
@Entity
public class UserProfile {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)

	private int user_id;
	private String name;
	private LocalDate dob;
	private String gender;
	private String address;
	private String phone_number;
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
	public void setDob(int date, int month, int year) throws ParseException {
		LocalDate dob = LocalDate.of(year,month,date);
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
		return phone_number;
	}
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public String toString() {
		return "UserProfile [user_id=" + user_id + ", name=" + name + ", dob=" + dob + ", gender=" + gender
				+ ", address=" + address + ", phone_number=" + phone_number + ", email=" + email + "]";
	}
	
	

}

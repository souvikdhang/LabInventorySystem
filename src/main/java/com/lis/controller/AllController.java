package com.lis.controller;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.transaction.Transactional;

import com.lis.dao.Credentials_repo;
import com.lis.dao.User_repo;
import com.lis.model.Credentials;
import com.lis.model.UserProfile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AllController 
{
	
	@Autowired
	Credentials_repo credentials;
	
	@Autowired
	User_repo users;
	
	@Autowired
	UserProfile currentUser;
	
	@Autowired
	Credentials currentUserCredentials;
	
	
	
	@RequestMapping("/")
		public String home() {
			return "file1.jsp";
		
		}
	
	@Transactional
	@PostMapping("/login")
	public String login(@RequestParam("userId") int uid, @RequestParam("password") String password) {
		System.out.println(users.findById(uid));
		System.out.println(credentials.existsByUserId(uid));
			if(users.existsById(uid)) {
				currentUserCredentials = credentials.getByUserId(uid);
				if(currentUserCredentials.get_password()==password) {
					currentUserCredentials.set_LoginStatus(true);
					credentials.saveAndFlush(currentUserCredentials);
					currentUser=users.getById(uid);
					return "login successful";
				}
				else return "wrong password";
			}
			else return "user not found";
	}
	
	
	@PostMapping("/addUser")
	@Transactional
		public String addUser(@RequestParam("name") String name, @RequestParam("date") String dob , @RequestParam("gender") String gender, @RequestParam("address") String address,  @RequestParam("phoneNumber") String phone, @RequestParam("emailId") String email,@RequestParam("userType")String userType) throws ParseException 
		{
			String s = "";
			DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		    System.out.println(users.existsByEmail(email));
			
		    if(currentUserCredentials.get_LoginStatus()==true) {
		    	if(currentUserCredentials.get_UserType()=="administrator") {
		    		if(!users.existsByEmail(email)) 
					{				
							UserProfile user=new UserProfile();
							Credentials userCred=new Credentials();
							
							user.setAddress(address);
						    LocalDate date = LocalDate.parse(dob,df);   
							user.setDob(date);
							user.setGender(gender);
							user.setName(name);
							user.setPhone_number(phone);
							user.setEmail(email);
							users.saveAndFlush(user);
							
							userCred.set_UserID(user.getUser_id());
							userCred.set_UserType(userType);
							String password = name + date.getYear();
							
							userCred.set_password(password);
							userCred.set_LoginStatus(false);
//							userCred.setUser(user);
							System.out.println(user);
							

							credentials.saveAndFlush(userCred);
							
							s = "Message.jsp";
					}
					else s ="userAlreadyExists.jsp";
		    	}
		    	else s = "access denied";
		    }
		    else s = "not logged in";
			return s;
		}
	
	
	@PostMapping("/deleteUser")
		public String deleteUser(@RequestParam("userId")int uid) 
	{
			String s="";
			if(currentUserCredentials.get_LoginStatus()==true) {
				if(currentUserCredentials.get_UserType()=="administrator") {
					if(credentials.existsById(uid)) {
						credentials.deleteById(uid);
						users.deleteById(uid);
						
						users.flush();
						credentials.flush();
						
						s= "deleted";
					}
					else s ="user does not exist";
				}
				else s = "access denied";
	    }
	    else s = "not logged in";
		return s;
	}
		


@PostMapping("/modifyUser")
	public String modifyUser(@RequestParam Map<String,String> requestParams) throws ParseException 
{
	
	String s="";
	if(currentUserCredentials.get_LoginStatus()==true) {
		if(currentUserCredentials.get_UserType()=="administrator") {
			int uid = Integer.parseInt(requestParams.get("userId"));
			if(users.existsById(uid)==false) {
				
				DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");  
				
				UserProfile user=users.getById(uid);
				Credentials userCred=credentials.getByUserId(uid);
				
				if(requestParams.containsKey("address"))
					user.setAddress(requestParams.get("address"));
				
				if(requestParams.containsKey("dob")) {
				LocalDate date = LocalDate.parse(requestParams.get("dob"), df); 
					user.setDob(date);
				}
				
				if(requestParams.containsKey("gender"))
					user.setGender(requestParams.get("gender"));
				
				if(requestParams.containsKey("name"))
					user.setName(requestParams.get("name"));
				
				if(requestParams.containsKey("phoneNumber"))	
					user.setPhone_number(requestParams.get("phoneNumber"));
				
				if(requestParams.containsKey("email"))
					user.setEmail(requestParams.get("email"));
				
//				userCred.setUser(user);
				
				if(requestParams.containsKey("userType"))
					userCred.set_UserType(requestParams.get("userType"));
			
				
				users.saveAndFlush(user);
				credentials.saveAndFlush(userCred);
				
				s = "updated";
			}
			else s ="user does not exist";
		}
		else s = "access denied";
	}
	else s = "not logged in";
	return s;
	
	}




@PostMapping("/addEquipment")
	public String addEquipment(@RequestParam("equipmentID") int equipmentID, @RequestParam("orgName") String orgName, @RequestParam("") )

}
		

package com.lis.controller;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

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
	
	
//	@PostMapping("/login")
//	public String login(@RequestParam("UserID") int userid, @RequestParam("Password") String password) {
//		String s = "";
//		if(credentials.existsById(userid)) {
//			Credentials cr = users.findById(userid).orElse(null);
//			if((cr.get_password()).equals(password)) {
//				if((cr.get_UserType()).equals("AD-001")) {
//					cr.set_LoginStatus("LoggedIn");
//					TableService.updateCredentials(cr, userid);
//					s = "Welcome1.jsp";
//			}
//				else if((cr.get_UserType()).equals("AD-005")) {
//					cr.set_LoginStatus("LoggedIn");
//					TableService.updateCredentials(cr, userid);
//					s = "Welcome2.jsp";
//				
//				}
//			}
//		}
//		
//		else {
//			s = "NotFound.jsp";
//		}
//		System.out.println(s);
//		return s;
//	}
	
	
	@PostMapping("/addUser")
		public String addUser(@RequestParam("Name") String Name, @RequestParam("Date") String dob , @RequestParam("Gender") String Gender, @RequestParam("Address") String address,  @RequestParam("PhoneNumber") String phone, @RequestParam("Email_Id") String Email,@RequestParam("UserType")String userType) throws ParseException 
		{
			String s = "";
			DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		    LocalDate date = LocalDate.parse(dob,df);   
		    
			System.out.println(users.existsByEmail(Email));
			
			if(!users.existsByEmail(Email)) 
			{				
					UserProfile user=new UserProfile();
					Credentials userCred=new Credentials();
					
					user.setAddress(address);
					user.setDob(date);
					user.setGender(Gender);
					user.setName(Name);
					user.setPhone_number(phone);
					user.setEmail(Email);
					userCred.set_UserType(userType);
					String password = Name + date.getYear();
					userCred.set_password(password);
					userCred.set_LoginStatus(false);
					userCred.setUser(user);
					System.out.println(user);
					
					users.saveAndFlush(user);
					credentials.saveAndFlush(userCred);
					
					s = "Message.jsp";
				}
			else s ="userAlreadyExists.jsp";
			return s;
		}
	
	
	@PostMapping("/deleteUser")
		public String deleteUser(@RequestParam("userId")int uid) 
	{
		
			credentials.deleteById(uid);
			users.deleteById(uid);
			
			users.flush();
			credentials.flush();
			
			return "deleted";
		}
		


@PostMapping("/modifyUser")
	public String modifyUser(@RequestParam Map<String,String> requestParams) throws ParseException 
{
		
	DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");  
	
	int uid = Integer.parseInt(requestParams.get("userId"));
    if(users.existsById(uid)==false)return "user not found";
    
	UserProfile user=users.getById(uid);
	Credentials userCred=credentials.getById(uid);
	
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
	
	userCred.setUser(user);
	
	if(requestParams.containsKey("userType"))
		userCred.set_UserType(requestParams.get("userType"));

	
	users.saveAndFlush(user);
	credentials.saveAndFlush(userCred);
	
	return "updated";
	
	}


@PostMapping("/addEquipment")
	public String addEquipment(@RequestParam("equipmentID") int equipmentID, @RequestParam("orgName") String orgName, @RequestParam("") )

}
		

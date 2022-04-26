package com.lis.controller;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.transaction.Transactional;

import com.lis.dao.Credentials_repo;
import com.lis.dao.User_repo;
import com.lis.dao.equipmentAvailabilityRepo;
import com.lis.dao.equipmentRepo;
import com.lis.dao.requestRepo;
import com.lis.model.Credentials;
import com.lis.model.UserProfile;
import com.lis.model.equipmentAvailability;
import com.lis.model.equipmentDetails;
import com.lis.model.requests;

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
	
	@Autowired
	equipmentRepo er;
	
	@Autowired
	requestRepo rer;
	
	@Autowired
	equipmentAvailabilityRepo ear;
	
	static int admin_signature = 147;
	
	
	@Transactional
	@PostMapping("/login")
	public String login(@RequestParam("userId") int uid, @RequestParam("password") String password) {
		System.out.println(users.getById(uid));
		System.out.println(credentials.getById(uid));
		System.out.println(password.trim());
			if(users.existsById(uid)) {
				currentUserCredentials = credentials.getById(uid);
				if(currentUserCredentials.get_password().equals(password.trim())) {
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
			
//		    if(currentUserCredentials.get_LoginStatus()==true) {
//		    	if(currentUserCredentials.get_UserType()=="administrator") {
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
							userCred.setUser(user);
//							userCred.set_UserID(user.getUser_id());
							userCred.set_UserType(userType);
							String password = name + date.getYear();
							
							userCred.set_password(password);
							userCred.set_LoginStatus(false);

							System.out.println(user);
							

							credentials.saveAndFlush(userCred);
							
							s = "userAdded";
					}
					else s ="userAlreadyExists.jsp";
//		    	}
//		    	else s = "access denied";
//		    }
//		    else s = "not logged in";
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
		
	
	@PostMapping("/viewAllUsers")
	public String viewAllUsers() 
{
		String s = "";
		List<UserProfile> ls = users.findAll();
		Iterator itr = ls.iterator();
		while(itr.hasNext()) {
			UserProfile usp = (UserProfile) itr.next();
			s += usp.toString()+"\n";
		}
		
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



@PostMapping("/userRequests")
public String userRequests(){
	String s = "";
	
	List<requests> rq = rer.findAll();
	
	Iterator itr = rq.iterator();
	
	while(itr.hasNext()) {
		requests r = (requests) itr.next();
		
		if((r.getRequestStatus()).equals("sent")) {
			s += "Request sent by user"+r.getUserID()+" with request ID "+r.getRequestID()+" and its status is "+r.getRequestStatus()+"\n";
			
		}
		
		else if((r.getRequestStatus()).equals("pending")) {
			s += "Request sent by user "+r.getUserID()+" and its status is "+r.getRequestStatus()+"\n";
		}
	}
	
	return s;
	
}


@PostMapping("/approveRequest")
public String approveRequests(@RequestParam("requestID") int requestID, @RequestParam("requestResponse") String requestResponse) {
	String s = "";
	requests r = rer.findById(requestID).orElse(null);
	if(rer.existsById(requestID)) {
		if(requestResponse.equals("accepted")) {
			s = "Request Accepted";
			r.setRequestStatus("accepted");
			rer.saveAndFlush(r);
		
			equipmentAvailability eav = ear.findById(r.getEquipmentID()).orElse(null);
			int val = eav.getAvailableamount() - 1;
			if(val > 0) {
				eav.setAvailableamount(val);
				ear.saveAndFlush(eav);
			}
		
			else {
				eav.setAvailableamount(val);
				ear.deleteById(r.getEquipmentID());
				er.deleteById(r.getEquipmentID());
			}
		
		}
	
		else if(requestResponse.equals("pending")) {
			s = "Request Pending";
			r.setRequestStatus("pending");
			rer.saveAndFlush(r);
		}
	
		else if(requestResponse.equals("Rejected")) {
			s = "Request Rejected";
			rer.deleteById(requestID);
		
		
		}
	}
	
	else {
		s = "no such request found";
	}
	
	
	return s;

}

@PostMapping("/addEquipment")
@Transactional
	public String addEquipment(@RequestParam("equipmentID") int equipmentID, @RequestParam("orgName") String orgName, @RequestParam("labName") String labName, @RequestParam("rackNumber") String rackNumber, @RequestParam("serverName") String serverName, @RequestParam("serverIPAddress") String serverIPAddress, @RequestParam("loginID") String loginID, @RequestParam("loginPassword") String loginPassword, @RequestParam("virtualMachine") int virtualMachine, @RequestParam("serverStatus") int serverStatus, @RequestParam("serverPower") int serverPower) {
		
		String s = "";
		//DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		//System.out.println(users.existsByEmail(email));
		equipmentAvailability ea = new equipmentAvailability();
			if(er.existsById(equipmentID) == false)
				{				
						equipmentDetails ed = new equipmentDetails();
						//Credentials userCred=new Credentials();
						
						
					
						ed.setEquipmentID(equipmentID);
						ed.setOrgName(orgName);
						ed.setLabName(labName);
						ed.setRackNumber(rackNumber);
						ed.setLoginID(loginID);
						ed.setLoginPassword(loginPassword);
						ed.setServerName(serverName);
						ed.setServerIPAddress(serverIPAddress);
						ed.setServerStatus(serverStatus);
						ed.setVirtualMachine(virtualMachine);
						ed.setServerPower(serverPower);
						er.saveAndFlush(ed);
						ea.setAvailableamount(1);
						ea.setEquipmentID(equipmentID);
						ear.saveAndFlush(ea);
					
						s="equipment added";
				}
			else {
					equipmentAvailability eav = ear.findById(equipmentID).orElse(null);
					int val = eav.getAvailableamount();
					eav.setAvailableamount(val+1);
					ear.saveAndFlush(eav);
					s ="equipment already exists";
				}
			
			return s;
}

@PostMapping("/deleteEquipment")
@Transactional
	public String deleteEquipment(@RequestParam("equipmentID") int equipmentID) {
		String s = "";
		equipmentAvailability ea = new equipmentAvailability();
		if(er.existsById(equipmentID)) {
			equipmentAvailability eavl = ear.findById(equipmentID).orElse(null);
			int a = eavl.getAvailableamount() - 1;
			if(a == 0) {
				ear.deleteById(equipmentID);
				er.deleteById(equipmentID);
				s = "Equipment Deleted Successfully";
				
			}
			
			else {
				eavl.setAvailableamount(a);
				ear.saveAndFlush(eavl);
				s = "Equipment Deleted Successfully";
			}
			
		}
		
		else {
			s = "Equipment doesnot exists";
		}
		
		return s;

	}


@PostMapping("/viewAllEquipments")
@Transactional
	public String viewAllEquipments() {
		String s = "";
		List<equipmentDetails> ls = er.findAll();
		Iterator itr = ls.iterator();
		while(itr.hasNext()) {
			equipmentDetails eqd = (equipmentDetails) itr.next();
			s += eqd.toString()+"\n";
		}
		
		return s;

		
	}


@PostMapping("/searchEquipments")
@Transactional
	public String searchEquipments(@RequestParam("labName") String labName, @RequestParam("orgName") String orgName, @RequestParam("equipmentID") int equipmentID) {
		String s = "";
		if(er.existsById(equipmentID)) {
			equipmentAvailability ea = ear.findById(equipmentID).orElse(null);
			
			int a = ea.getAvailableamount();
			s = "Equipment available, amount available is "+a;
		
		}
		
		return s;

		
	}

@PostMapping("/sendRequest")
@Transactional
	public String sendRequest(@RequestParam("equipmentID") int equipmentID, @RequestParam("userID") int UserID, @RequestParam("dateofRequest") String dateofRequest) {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String s = "";

		if(rer.existsByUserID(UserID) == false)
			if(users.existsById(UserID)) {
				if(er.existsById(equipmentID)) {
					requests r = new requests();
					r.setEquipmentID(equipmentID);
					r.setUserID(UserID);
					r.setRequestStatus("sent");
					LocalDate d = LocalDate.parse(dateofRequest,df);
					r.setDate(d);
					rer.saveAndFlush(r);
					requests rr = rer.findByUserID(UserID);
					s = "Request sent successfully , your request id is "+rr.getRequestID();
					}
				else {
					s = "Equipment Not Available";
				}
			
			}
		
		else {
			s = "User not found";
		}
		
		return s;
	
}


@PostMapping("/seeRequestStatus")
@Transactional
	public String seeRequestStatus(@RequestParam("user_id") int user_id, @RequestParam("password") String password, @RequestParam("requestID") int requestID) {
		String s = "";
		int dispatched_id = (requestID*admin_signature)+(admin_signature - requestID);
		if(users.existsById(user_id)) {
			Credentials cr = credentials.findById(user_id).orElse(null);
			if((cr.get_password()).equals(password)) {
				requests r = rer.findById(requestID).orElse(null);
				if((r.getRequestStatus()).equals("accepted")) {
					s = "the status is "+r.getRequestStatus()+" your uid is your requestID and your password is "+dispatched_id;
			}
				else {
					s = "the status is "+r.getRequestStatus();
				}
			
		}
		}
		
		else {
			s = "user doesn't exists";
		}
		
		return s;
}


@PostMapping("/modifyEquipment")
@Transactional
	public String modifyEquipment(@RequestParam("equipmentID") int equipmentID, @RequestParam("orgName") String orgName, @RequestParam("labName") String labName, @RequestParam("rackNumber") String rackNumber, @RequestParam("serverName") String serverName, @RequestParam("serverIPAddress") String serverIPAddress, @RequestParam("loginID") String loginID, @RequestParam("loginPassword") String loginPassword, @RequestParam("virtualMachine") int virtualMachine, @RequestParam("serverStatus") int serverStatus, @RequestParam("serverPower") int serverPower){
		String s = "";
		if(er.existsById(equipmentID)) {
			equipmentDetails ed = new equipmentDetails();
			ed.setOrgName(orgName);
			ed.setLabName(labName);
			ed.setRackNumber(rackNumber);
			ed.setServerName(serverName);
			ed.setServerIPAddress(serverIPAddress);
			ed.setLoginID(loginID);
			ed.setLoginPassword(loginPassword);
			ed.setVirtualMachine(virtualMachine);
			ed.setServerStatus(serverStatus);
			ed.setServerPower(serverPower);
			er.saveAndFlush(ed);
			s = "equipment modified successfully";
			
		}
		
		else {
			s = "equipment doesn't exists";
		}
		
	return s;
}

@PostMapping("/returnEquipment")
@Transactional
	public String returnEquipment(@RequestParam("requestID") int requestID, @RequestParam("equipmentID") int equipmentID, @RequestParam("orgName") String orgName, @RequestParam("labName") String labName, @RequestParam("rackNumber") String rackNumber, @RequestParam("serverName") String serverName, @RequestParam("serverIPAddress") String serverIPAddress, @RequestParam("loginID") String loginID, @RequestParam("loginPassword") String loginPassword, @RequestParam("virtualMachine") int virtualMachine, @RequestParam("serverStatus") int serverStatus, @RequestParam("serverPower") int serverPower) {
		String s = "";
		if(rer.existsById(requestID)) {
			requests r = rer.findById(requestID).orElse(null);
			if((r.getRequestStatus()).equals("accepted")) { 
				if(er.existsById(equipmentID)) {
					equipmentAvailability eav = ear.findById(equipmentID).orElse(null);
					int val = eav.getAvailableamount()+1;
					eav.setAvailableamount(val);
					ear.saveAndFlush(eav);
					s = "equipment returned successfully";
				}
				else {
					equipmentDetails ed = new equipmentDetails();
					equipmentAvailability eav = new equipmentAvailability();
					ed.setOrgName(orgName);
					ed.setLabName(labName);
					ed.setRackNumber(rackNumber);
					ed.setServerName(serverName);
					ed.setServerIPAddress(serverIPAddress);
					ed.setLoginID(loginID);
					ed.setLoginPassword(loginPassword);
					ed.setVirtualMachine(virtualMachine);
					ed.setServerStatus(serverStatus);
					ed.setServerPower(serverPower);
					er.saveAndFlush(ed);
					eav.setAvailableamount(1);
					eav.setEquipmentID(equipmentID);
					ear.saveAndFlush(eav);
					s = "equipment returned successfully";
				}
					
				}
			else {
				s = "No equipment alloted yet";
			}
		}
		
		else {
			
			s = "No request made";
		}
		
		return s;

}

}
	


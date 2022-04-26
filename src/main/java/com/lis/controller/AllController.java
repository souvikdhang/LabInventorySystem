package com.lis.controller;

import java.net.URI;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import com.lis.dao.Credentials_repo;
import com.lis.dao.EquipmentAvailabilityRepo;
import com.lis.dao.EquipmentRepo;
import com.lis.dao.UserRepo;
import com.lis.dao.requestRepo;
import com.lis.model.Credentials;
import com.lis.model.EquipmentAvailability;
import com.lis.model.EquipmentDetails;
import com.lis.model.UserProfile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AllController {

	@Autowired
	Credentials_repo credentials;

	@Autowired
	UserRepo users;

	@Autowired
	EquipmentRepo er;

	@Autowired
	requestRepo rer;

	@Autowired
	EquipmentAvailabilityRepo ear;

	@Autowired
	UserProfile currentUser;

	@Autowired
	Credentials currentUserCredentials;
	
	public ResponseEntity<Void> redirect(String page)
	{
		String url = "http://localhost:3000/"+page;
        System.out.println(url);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(page)).build();
    }

	@Transactional
	@PostMapping("/login")
	public String login(@RequestBody Map<String, String> body, HttpServletResponse response) 
	{
		int uid = Integer.parseInt(body.get("userId"));
		String password = body.get("password");
		
		if (users.existsById(uid)) {
			currentUserCredentials = credentials.getById(uid);
			if (currentUserCredentials.get_password().equals(password.trim())) {
				currentUserCredentials.set_LoginStatus(true);
				credentials.saveAndFlush(currentUserCredentials);
				currentUser = users.getById(uid);			
				ResponseCookie userIdCookie =ResponseCookie.from("userId",Integer.toString(uid))
			            .httpOnly(false)
			            .sameSite("None")
			            .secure(true)
			            .path("/")
			            .maxAge(Math.toIntExact(2147483647))
			            .build();
				response.addHeader("Set-Cookie", userIdCookie.toString());			
				return "loggedIn";
			} else
				return "wrongPassword";
		} else
			return "userNotFound";
	}
	

	@PostMapping("/addUser")
	@Transactional
	public String addUser(@CookieValue(name = "userId", required=false) String uidCookie, 
			@RequestParam("name") String name, @RequestParam("date") String dob,
			@RequestParam("gender") String gender, @RequestParam("address") String address,
			@RequestParam("phoneNumber") String phone, @RequestParam("emailId") String email,
			@RequestParam("userType") String userType) throws ParseException 
	{
		String s = "";
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		System.out.println(users.existsByEmail(email));
//		if(!uidCookie.equals(null)) {
//			if(credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equals("administrator")) {
				if (!users.existsByEmail(email)) {
					UserProfile user = new UserProfile();
					Credentials userCred = new Credentials();
					user.setAddress(address);
					LocalDate date = LocalDate.parse(dob, df);
					user.setDob(date);
					user.setGender(gender);
					user.setName(name);
					user.setPhone_number(phone);
					user.setEmail(email);
					users.saveAndFlush(user);
					userCred.setUser(user);
					userCred.set_UserType(userType);
					String password = name + date.getYear();
					userCred.set_password(password);
					userCred.set_LoginStatus(false);
					System.out.println(user);
					credentials.saveAndFlush(userCred);
					s = "userAdded";
				} 
				else
					s = "userAlreadyExists";
//			 }
//			 else s = "redirect:/homePage";
//		 }
//		 else s = "redirect:/";
		return s;
	}

	
	@PostMapping("/deleteUser")
	public String deleteUser(@CookieValue(name = "userId", required=false) String uidCookie, @RequestParam("userId") int uid) 
	{
		String s = "";
		if (!uidCookie.equals(null)) {
			if (credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equals( "administrator")) {
				if (credentials.existsById(uid)) {
					credentials.deleteById(uid);
					users.deleteById(uid);

					users.flush();
					credentials.flush();

					s = "userDeleted";
				} 
				else
					s = "userDoesNotExist";
			 }
			 else s = "redirect:/homePage";
		 }
		 else s = "redirect:/";
		return s;
	}

	
	@PostMapping("/modifyUser")
	public String modifyUser(@CookieValue(name = "userId", required=false) String uidCookie, 
			@RequestParam Map<String, String> requestParams) throws ParseException 
	{

		String s = "";
		if (!uidCookie.equals(null)) {
			if (credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equals( "administrator")) {
				
				int uid = Integer.parseInt(requestParams.get("userId"));
				if (users.existsById(uid) == false) {

					DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

					UserProfile user = users.getById(uid);
					Credentials userCred = credentials.getById(uid);

					if (requestParams.containsKey("address"))
						user.setAddress(requestParams.get("address"));

					if (requestParams.containsKey("dob")) {
						LocalDate date = LocalDate.parse(requestParams.get("dob"), df);
						user.setDob(date);
					}

					if (requestParams.containsKey("gender"))
						user.setGender(requestParams.get("gender"));

					if (requestParams.containsKey("name"))
						user.setName(requestParams.get("name"));

					if (requestParams.containsKey("phoneNumber"))
						user.setPhone_number(requestParams.get("phoneNumber"));

					if (requestParams.containsKey("email"))
						user.setEmail(requestParams.get("email"));

					// userCred.setUser(user);

					if (requestParams.containsKey("userType"))
						userCred.set_UserType(requestParams.get("userType"));

					users.saveAndFlush(user);
					credentials.saveAndFlush(userCred);

					s = "userUpdated";
				} else
					s = "userDoesNotExistt";
			 }
			 else s = "redirect:/homePage";
		 }
		 else s = "redirect:/";
		return s;
	}
	

	@GetMapping("/viewAllUser")
	@Transactional
	public List<UserProfile> viewAllUser(@RequestParam("userType") String userType) {
		redirect("homePage");
		List<UserProfile> allUser = new ArrayList<>();
		Iterable<Credentials> user = credentials.findAllByType(userType);

		for (Credentials u : user) {
			allUser.add(u.getUser());
		}
		return allUser;
	}
	

	@GetMapping("/getUserType")
	public String getUserType(@RequestParam("userId") int uid) {
		return credentials.getById(uid).get_UserType();
	}
	

//	 @GetMapping("/test")
//	 public void test(){
//	
//	 System.out.println( credentials.getByUserType("administrator").getUser());
//	 }

	
	@PostMapping("/addEquipment")
	@Transactional
	public String addEquipment(@CookieValue(name = "userId", required=false) String uidString,
			@RequestParam("orgName") String orgName,
			@RequestParam("labName") String labName, @RequestParam("rackNumber") String rackNumber,
			@RequestParam("serverName") String serverName, @RequestParam("serverIPAddress") String serverIPAddress,
			@RequestParam("loginID") String loginID, @RequestParam("loginPassword") String loginPassword,
			@RequestParam("virtualMachine") int virtualMachine, @RequestParam("serverStatus") int serverStatus,
			@RequestParam("serverPower") int serverPower)
	{
		String s = "";
		System.out.println(uidString);
		if (uidString!=null) {
			if (credentials.getById(Integer.parseInt(uidString)).get_UserType().equals( "administrator")) {
				
				EquipmentAvailability ea = new EquipmentAvailability();
					EquipmentDetails ed = new EquipmentDetails();
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
					ea.setDetails(ed);
					ea.setAvailableamount(1);
					ear.saveAndFlush(ea);
					s = "equipment added";				
			}
			 else redirect("/homePage");
		 }
		 else s = "redirect:/";
		return s;
	}

	
	@PostMapping("/deleteEquipment")
	@Transactional
	public String deleteEquipment(@CookieValue(name = "userId", required=false) String uidString, @RequestParam("equipmentID") int equipmentID) {
		String s = "";
		if (!uidString.equals(null)) {
			if (credentials.getById(Integer.parseInt(uidString)).get_UserType().equals( "administrator")) {
				if (er.existsById(equipmentID)) {
					EquipmentAvailability eavl = ear.findById(equipmentID).orElse(null);
					int a = eavl.getAvailableamount() - 1;
					if (a == 0) {
						ear.deleteById(equipmentID);
						er.deleteById(equipmentID);
						s = "Equipment Deleted Successfully";
		
					}
		
					else {
						eavl.setAvailableamount(a);
						ear.save(eavl);
						s = "Equipment Deleted Successfully";
					}
		
				}
		
				else
				{
					s = "Equipment doesnot exists";
				}
			 }
			 else s = "redirect:/homePage";
		 }
		 else s = "redirect:/";

		return s;

	}

	
	@PostMapping("/viewAllEquipments")
	@Transactional
	public String viewAllEquipments() {
		String s = "";
		List<EquipmentDetails> ls = er.findAll();
		Iterator<EquipmentDetails> itr = ls.iterator();
		while (itr.hasNext()) {
			EquipmentDetails eqd = (EquipmentDetails) itr.next();
			s += eqd.toString() + "\n";
		}

		return s;

	}

}

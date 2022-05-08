package com.lis.controller;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import com.lis.dao.Credentials_repo;
import com.lis.dao.EquipmentAvailabilityRepo;
import com.lis.dao.EquipmentRepo;
import com.lis.dao.RequestRepo;
import com.lis.dao.UserRepo;
import com.lis.model.Credentials;
import com.lis.model.EquipmentAvailability;
import com.lis.model.EquipmentDetails;
import com.lis.model.Requests;
import com.lis.model.UserProfile;
import com.lis.others.EquipmentAndRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
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
	EquipmentRepo equipmentRepo;
	@Autowired
	RequestRepo requestRepo;
	@Autowired
	EquipmentAvailabilityRepo equipmentAvailabilityRepo;


	static ResponseEntity<String> loginError(){
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", "/");
		return new ResponseEntity<String>("notLoggedIn", headers, HttpStatus.FORBIDDEN);
	}
	static ResponseEntity<String> accessError(){
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", "/");    
		return new ResponseEntity<String>("accessDenied", headers, HttpStatus.UNAUTHORIZED);
	}
	


	@Transactional
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpServletResponse response) {
		int uid = Integer.parseInt(body.get("userId"));
		String password = body.get("password");
		System.out.println(uid);
		users.flush();
		credentials.flush();
		System.out.println(users.existsById(uid));
		if (users.existsById(uid)) {
			Credentials currentUserCredentials = credentials.getById(uid);
			if (currentUserCredentials.get_password().equals(password.trim())) {
				currentUserCredentials.set_LoginStatus(true);
				credentials.saveAndFlush(currentUserCredentials);
				ResponseCookie userIdCookie = ResponseCookie.from("userId", Integer.toString(uid))
						
						.httpOnly(false)
						.sameSite("None")
						.path("/")
						.secure(true)
						.maxAge(Math.toIntExact(2147483647))
						.build();
				response.addHeader("Set-Cookie", userIdCookie.toString());
	
				return new ResponseEntity<String>("loggedIn",HttpStatus.OK);
			} else
				return new ResponseEntity<String>("wrongPassword",HttpStatus.UNAUTHORIZED);
		} else
			return new ResponseEntity<String>("userNotFound",HttpStatus.NOT_FOUND);
	}
	

	@PostMapping("/addUser")
	@Transactional
	public ResponseEntity<?> addUser(@CookieValue(name = "userId", required = false) String uidCookie, @RequestBody Map<String, String> body) throws ParseException 
	{
		String name = body.get("name");
		String dob= body.get("date");
		String gender= body.get("gender");
		String address= body.get("address");
		String phone= body.get("phoneNumber");
		String email= body.get("emailId");
		String userType= body.get("userType");
		 if(uidCookie==null) return loginError();
		 System.out.println(credentials.getById(Integer.parseInt(uidCookie)).get_UserType());
		 if(!credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equalsIgnoreCase("administrator")) return accessError();
		if (!users.existsByEmail(email)) {
			UserProfile user = new UserProfile();
			Credentials userCred = new Credentials();
			user.setAddress(address);
			DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			LocalDate date = LocalDate.parse(dob, df);
			user.setDob(date);
			user.setGender(gender);
			user.setName(name);
			user.setPhone_number(phone);
			user.setEmail(email);
			users.saveAndFlush(user);
			userCred.setUser(user);
			userCred.set_UserType(userType);
			String password;
			if(name.indexOf(" ")>=0)	password = name.substring(0,name.indexOf(" ")).toLowerCase() + date.getYear();
			else password = name.toLowerCase() + date.getYear();
			userCred.set_password(password);
			userCred.set_LoginStatus(false);
			System.out.println(user);
			System.out.println("userAdded");
			credentials.saveAndFlush(userCred);
			return  new ResponseEntity<Integer>(user.getUser_id(), HttpStatus.OK);
		} 
		else 
			return  new ResponseEntity<String>("userAlreadyExists", HttpStatus.CONFLICT);	
		}

	@PostMapping("/deleteUser")
	public ResponseEntity<?> deleteUser(@CookieValue(name = "userId", required = false) String uidCookie,@RequestBody Map<String, String> body) 
	{
		int uid = Integer.parseInt(body.get("userId"));
		System.out.println();
		 if(uidCookie==null) return loginError();
		 if(!credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equalsIgnoreCase("administrator")) return accessError();
			if (credentials.existsById(uid)) {
				credentials.deleteById(uid);
				users.deleteById(uid);

				users.flush();
				credentials.flush();

				return new ResponseEntity<String>("userDeleted", HttpStatus.OK);
			} 
			else 
				return new ResponseEntity<String>("userNotFound", HttpStatus.NOT_FOUND);
	}

	@PostMapping("/modifyUser")
	public ResponseEntity<?> modifyUser(@CookieValue(name = "userId", required = false) String uidCookie,
			@RequestBody Map<String, String> requestBody) throws ParseException 
	{
		if (uidCookie == null) return loginError();
		if (!credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equalsIgnoreCase("administrator")) return accessError();
		int uid = Integer.parseInt(requestBody.get("userId"));
		System.out.println(uid);
		if (users.existsById(uid) == true) {

			DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			UserProfile user = users.getById(uid);

			if (requestBody.containsKey("address"))
				user.setAddress(requestBody.get("address"));

			if (requestBody.containsKey("dob")) 
			{
				LocalDate date = LocalDate.parse(requestBody.get("dob"), df);
				System.out.println(date);
				user.setDob(date);
			}

			if (requestBody.containsKey("gender"))
				user.setGender(requestBody.get("gender"));

			if (requestBody.containsKey("name"))
				user.setName(requestBody.get("name"));

			if (requestBody.containsKey("phoneNumber"))
				user.setPhone_number(requestBody.get("phoneNumber"));

			if (requestBody.containsKey("email"))
				user.setEmail(requestBody.get("email"));
			
			users.saveAndFlush(user);

			return new ResponseEntity<Integer>(user.getUser_id(),HttpStatus.OK);
		} else
			return new ResponseEntity<String>("userNotFound",HttpStatus.NOT_FOUND);
	}
	
	@Transactional
	@PostMapping("/getUser")
	public ResponseEntity<?> getUser(@CookieValue(name = "userId", required = false) String uidCookie)
	{
		if (uidCookie == null) {
			return loginError();
			}
		System.out.println("a");
		int userId = Integer.parseInt(uidCookie);
//		if (!credentials.getById(userId).get_UserType().equalsIgnoreCase("customer")) {
//			return accessError();
//			}
		System.out.println(userId);
		if(users.existsById(userId)) {
			UserProfile user = users.getById(userId);
			System.out.println(user);
			Object obj = user;
			return new ResponseEntity<Object>(obj,HttpStatus.OK);
		}
		else {
			return new ResponseEntity<String>("userNotFound",HttpStatus.NOT_FOUND);
		}
	}

	@Transactional
	@PostMapping("/viewAllUser")
	public ResponseEntity<?> viewAllUsers(@CookieValue(name = "userId", required = false) String uidCookie) {
		
		if (uidCookie == null) {
			return loginError();
			}
		if (!credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equalsIgnoreCase("administrator")) {
			return accessError();
			}
		List<Credentials> ls = credentials.findAllByType("customer");
		List<UserProfile> userList = new ArrayList<UserProfile>();
		for (Credentials a : ls) {
			userList.add(a.getUser());
		}
		System.out.println("viewAllUser called");
		return new ResponseEntity<List<UserProfile>>(userList,HttpStatus.OK);

	}
	
	@PostMapping("/changePassword")
	public ResponseEntity<?> changePassword(@CookieValue(name = "userId", required = false) String uidCookie, @RequestBody Map<String, String> body)
	{
		if (uidCookie == null) 
		{
			return loginError();
		}
		int userId = Integer.parseInt(uidCookie);
		String oldPassword = body.get("oldPassword");
		String newPassword = body.get("newPassword");
		System.out.println(newPassword);
		if(credentials.existsById(userId)) {
			Credentials credential = credentials.getById(userId);
			if(credential.get_password().equals(oldPassword)) {
				credential.set_password(newPassword);
				credentials.saveAndFlush(credential);
				return new ResponseEntity<String>("passwordSet",HttpStatus.OK);
			}
			else return new ResponseEntity<String>("wrongpassword",HttpStatus.UNAUTHORIZED);
		}
		else return new ResponseEntity<String>("userNotFound",HttpStatus.NOT_FOUND);
	}

	@PostMapping("/addEquipment")
	@Transactional
	public ResponseEntity<?> addEquipment(@CookieValue(name = "userId", required = false) String uidCookie, @RequestBody Map<String, String> body) 
	{
		String orgName = body.get("orgName");
		String labName = body.get("labName");
		String rackNumber = body.get("rackNumber");
		String serverName = body.get("serverName");
		String serverIPAddress = body.get("serverIPAddress");
		String loginID = body.get("loginID");
		String loginPassword = body.get("loginPassword");
		int virtualMachine =  Integer.parseInt(body.get("virtualMachine"));
		int serverStatus = Integer.parseInt(body.get("serverStatus"));
		int serverPower = Integer.parseInt(body.get("serverPower"));
		
		if(uidCookie==null) return loginError();
		if(!credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equalsIgnoreCase("administrator")) return accessError();
		
		EquipmentAvailability equipmentAvailability = new EquipmentAvailability();
		EquipmentDetails equipmentDetails = new EquipmentDetails();
		
		if(!equipmentRepo.existsByRackNumber(rackNumber)) {
			equipmentDetails.setOrgName(orgName);
			equipmentDetails.setLabName(labName);
			equipmentDetails.setRackNumber(rackNumber);
			equipmentDetails.setLoginID(loginID);
			equipmentDetails.setLoginPassword(loginPassword);
			equipmentDetails.setServerName(serverName);
			equipmentDetails.setServerIPAddress(serverIPAddress);
			equipmentDetails.setServerStatus(serverStatus);
			equipmentDetails.setVirtualMachine(virtualMachine);
			equipmentDetails.setServerPower(serverPower);
			equipmentRepo.saveAndFlush(equipmentDetails);
			equipmentAvailability.setDetails(equipmentDetails);
			equipmentAvailability.setAvailableamount(1);
			equipmentAvailabilityRepo.saveAndFlush(equipmentAvailability);
			return new ResponseEntity<Integer>(equipmentDetails.getEquipmentID(),HttpStatus.OK);
		}
		else return new ResponseEntity<String>("equipmentAlreadyExists",HttpStatus.CONFLICT);

	}
	
	@PostMapping("/addExistingEquipment")
	@Transactional
	public ResponseEntity<?>addExistingEquipment(@CookieValue(name = "userId", required = false) String uidCookie,@RequestParam("equipmentID") int equipmentID){
		if(uidCookie==null) return loginError();
		if(!credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equalsIgnoreCase("administrator")) return accessError();
		if(equipmentAvailabilityRepo.existsById(equipmentID)) {
			EquipmentAvailability equipmentAvailability = equipmentAvailabilityRepo.getById(equipmentID);
			equipmentAvailability.setAvailableamount(equipmentAvailability.getAvailableamount()+1);
			equipmentAvailabilityRepo.saveAndFlush(equipmentAvailability);
			return new ResponseEntity<Integer>(equipmentAvailability.getAvailableamount(),HttpStatus.OK);
		}
		else {
			return new ResponseEntity<String>("equipmentDoesNotExists",HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/deleteEquipment")
	@Transactional
	public ResponseEntity<?> deleteEquipment(@CookieValue(name = "userId", required = false) String uidCookie,
			@RequestParam("equipmentID") int equipmentID) 
	{
		if (uidCookie == null)	return loginError();
		if (!credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equalsIgnoreCase("administrator")) return accessError();
		if (equipmentRepo.existsById(equipmentID)) 
		{
			EquipmentAvailability equipmentAvailability = equipmentAvailabilityRepo.findById(equipmentID).orElse(null);
			int available = equipmentAvailability.getAvailableamount() - 1;
			if (available <= 0) 
			{
				if (equipmentAvailability.getIssued_amount()>0) 
				{
					if(available<=0)available=0;
					equipmentAvailability.setAvailableamount(available);
					equipmentAvailabilityRepo.save(equipmentAvailability);
					return new ResponseEntity<String>("issued_cannotDelete",HttpStatus.EXPECTATION_FAILED);
				}
				else
				{
					equipmentAvailabilityRepo.deleteById(equipmentID);
					equipmentRepo.deleteById(equipmentID);
					return new ResponseEntity<String>("equipmentDeleted",HttpStatus.OK);		
				}
			} 
			else
			{
				if(available<=0)available=0;
				equipmentAvailability.setAvailableamount(available);
				equipmentAvailabilityRepo.save(equipmentAvailability);
				return new ResponseEntity<String>("equipmentReduced",HttpStatus.OK);			
			}
		}
		
		else 
		{
			return new ResponseEntity<String>("equipmentNotFound",HttpStatus.NOT_FOUND);		
		}
	}

	@PostMapping("/modifyEquipment")
	@Transactional
	public ResponseEntity<?> modifyEquipment(@CookieValue(name = "userId", required = false) String uidCookie,@RequestBody Map<String, String>  body) 
	{
		int equipmentID = Integer.parseInt(body.get("equipmentID"));
		
		if (uidCookie == null)return loginError();
		if (!credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equalsIgnoreCase("administrator")) return accessError();
		if (equipmentRepo.existsById(equipmentID))
		{
			EquipmentDetails equipmentDetails = equipmentRepo.getById(equipmentID);
			
			if(body.containsKey("orgName")) 
			{
			String orgName = body.get("orgName");
			equipmentDetails.setOrgName(orgName);
			}
			
			if(body.containsKey("labName")) 
			{
			String labName = body.get("labName");
			equipmentDetails.setLabName(labName);
			}
			
			if(body.containsKey("rackNumber")) 
			{
			String rackNumber = body.get("rackNumber");
			equipmentDetails.setRackNumber(rackNumber);
			}
			
			if(body.containsKey("serverName")) 
			{
			String serverName = body.get("serverName");
			equipmentDetails.setServerName(serverName);
			}
			
			if(body.containsKey("serverIPAddress")) 
			{
			String serverIPAddress = body.get("serverIPAddress");
			equipmentDetails.setServerIPAddress(serverIPAddress);
			}
			
			if(body.containsKey("loginID")) 
			{
			String loginID = body.get("loginID");
			equipmentDetails.setLoginID(loginID);
			}
			
			if(body.containsKey("loginPassword")) 
			{
			String loginPassword = body.get("loginPassword");
			equipmentDetails.setLoginPassword(loginPassword);
			}
			
			if(body.containsKey("virtualMachine")) 
			{
			int virtualMachine = Integer.parseInt(body.get("virtualMachine"));
			equipmentDetails.setVirtualMachine(virtualMachine);
			}
			
			if(body.containsKey("serverStatus")) 
			{
			int serverStatus = Integer.parseInt(body.get("serverStatus"));
			equipmentDetails.setServerStatus(serverStatus);
			}
			
			if(body.containsKey("serverPower")) 
			{
			int serverPower = Integer.parseInt(body.get("serverPower"));
			equipmentDetails.setServerPower(serverPower);
			}
			equipmentRepo.saveAndFlush(equipmentDetails);
			return new ResponseEntity<String>("equipmentModified",HttpStatus.OK);
		}
		else 
		{
			return new ResponseEntity<String>("equipmentNotFound",HttpStatus.NOT_FOUND);	
		}
	}

	@PostMapping("/viewAllEquipment")
	@Transactional
	public ResponseEntity<?> viewAllEquipments(@CookieValue(name = "userId", required = false) String uidCookie) {
		if (uidCookie == null)	return loginError();
		int uid = Integer.parseInt(uidCookie);
		if(credentials.getById(uid).get_UserType().equalsIgnoreCase("administrator")) {
			List<EquipmentAvailability> ls = equipmentAvailabilityRepo.findAll();
			return new ResponseEntity<List<EquipmentAvailability>>(ls,HttpStatus.OK);
		}
		else {
			List<EquipmentDetails> ls = equipmentRepo.findAll();
			return new ResponseEntity<List<EquipmentDetails>>(ls,HttpStatus.OK);
		}

	}

	@PostMapping("/searchEquipment")
	@Transactional
	public ResponseEntity<?> searchEquipments(@CookieValue(name = "userId", required = false) String uidCookie,
			@RequestParam("labName") String labName, @RequestParam("orgName") String orgName,
			@RequestParam("equipmentID") int equipmentID) 
	{
		if (uidCookie == null)	return loginError();

		if (equipmentRepo.existsById(equipmentID)) {
			EquipmentAvailability equipmentAvailability = equipmentAvailabilityRepo.findById(equipmentID).orElse(null);
			return new ResponseEntity<EquipmentAvailability>(equipmentAvailability,HttpStatus.OK);
		}
		return new ResponseEntity<String>("equipmentNotFound",HttpStatus.NOT_FOUND);
	}

	@PostMapping("/returnEquipment")
	@Transactional
	public ResponseEntity<?> returnEquipment(@CookieValue(name = "userId", required = false) String uidCookie, @RequestParam("requestID") int requestID) {
		if (uidCookie == null) return loginError();
		if (!credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equalsIgnoreCase("customer")) return accessError();
		if (requestRepo.existsById(requestID)) {
			Requests requests = requestRepo.findById(requestID).orElse(null);
			if ((requests.getRequestStatus()).equalsIgnoreCase("accepted")) {
				int equipmentID = requests.getEquipmentID();
				if (equipmentRepo.existsById(equipmentID)) {
					EquipmentAvailability eav = equipmentAvailabilityRepo.getById(equipmentID);
					int val = eav.getAvailableamount() + 1;
					eav.setAvailableamount(val);
					eav.setIssued_amount(eav.getIssued_amount()-1);
					requests.setRequestStatus("returned");
					equipmentAvailabilityRepo.saveAndFlush(eav);
					requestRepo.delete(requests);
					requestRepo.flush();
					return new ResponseEntity<String>("returned", HttpStatus.OK);
				}
				else 
				{
					return new ResponseEntity<String>("noEquipmentFound", HttpStatus.NOT_FOUND);
				}
			}
			else 
			{
				return new ResponseEntity<String>("notAlloted", HttpStatus.EXPECTATION_FAILED);
			}
		}
		else 
		{
			return new ResponseEntity<String>("requestNotFound", HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/sendRequests")
	@Transactional
	public ResponseEntity<?> sendRequest(@CookieValue(name = "userId", required = false) String uidCookie, @RequestParam("equipmentId") int equipmentID) 
	{
		if (uidCookie == null) return loginError();
		if (!credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equalsIgnoreCase("customer")) return accessError();
		int userId = Integer.parseInt(uidCookie);
		if (requestRepo.existsByUserID(userId) == false) {
			if (users.existsById(userId)) {
				if (equipmentRepo.existsById(equipmentID)) {
					Requests requests = new Requests();
					requests.setEquipmentID(equipmentID);
					requests.setUserID(userId);
					requests.setRequestStatus("sent");
					LocalDate dateOfRequest = LocalDate.now(ZoneId.of("Asia/Kolkata"));
					requests.setDate(dateOfRequest);
					requestRepo.saveAndFlush(requests);
					requests = requestRepo.findByUserID(userId);
					return new ResponseEntity<Integer>(requests.getRequestID(), HttpStatus.OK);
				} 
				else
				{
					return new ResponseEntity<String>("equipmentNotAvailable", HttpStatus.NOT_FOUND);
				}

			}

			else {
				return new ResponseEntity<String>("userNotFound", HttpStatus.NOT_FOUND);
			}
		}
		else return new ResponseEntity<String>("requestAlreadyExists", HttpStatus.CONFLICT);


	}
	
	@PostMapping("/cancelRequest")
	public ResponseEntity<?> cancelRequest(@CookieValue(name = "userId", required = false) String uidCookie){
		if (uidCookie == null) return loginError();
		int userId = Integer.parseInt(uidCookie);
		
		if (!credentials.getById(userId).get_UserType().equalsIgnoreCase("customer")) return accessError();
		
		if (requestRepo.existsByUserID(userId) == false)
		{
			return new ResponseEntity<String>("requestDoesNotExist",HttpStatus.NOT_FOUND);
		}
		else 
		{
			Requests request = requestRepo.findByUserID(userId);
			if(!request.getRequestStatus().equalsIgnoreCase("accepted")) {
				requestRepo.delete(requestRepo.findByUserID(userId));
				requestRepo.flush();
				return new ResponseEntity<String>("requestDeleted",HttpStatus.OK);
			}
			else {
				return new ResponseEntity<String>("cannotDelete",HttpStatus.CONFLICT);
			}
			

		}
	}
	
	@PostMapping("/allRequestByUser")
	public ResponseEntity<?> allRequestByUser(@CookieValue(name = "userId", required = false) String uidCookie){
		if (uidCookie == null) return loginError();
		int userId = Integer.parseInt(uidCookie);
		if (!credentials.getById(userId).get_UserType().equalsIgnoreCase("customer")) return accessError();
		if (requestRepo.existsByUserID(userId) == false) 
		{
			return new ResponseEntity<String>("requestDoesNotExist",HttpStatus.NOT_FOUND);
		}
		else 
		{
			EquipmentAndRequest equipmentAndRequest = new EquipmentAndRequest();
			Requests request = requestRepo.findByUserID(userId);
			equipmentAndRequest.setRequest(request);
			System.out.println(equipmentAndRequest);
			if(requestRepo.findByUserID(userId).getRequestStatus().equalsIgnoreCase("accepted")) {
				equipmentAndRequest.setEquipmentDetails(equipmentRepo.findById(request.getEquipmentID()).get());
				System.out.println(equipmentAndRequest);
			}
			return new ResponseEntity<EquipmentAndRequest>(equipmentAndRequest,HttpStatus.OK);
		}
	}

//	@PostMapping("/seeRequestStatus")
//	@Transactional
//	public String seeRequestStatus(@CookieValue(name = "userId", required = false) String uidCookie, @RequestParam("user_id") int user_id, @RequestParam("password") String password,
//			@RequestParam("requestID") int requestID) {
//		String s = "";
//		int dispatched_id = (requestID * admin_signature) + (admin_signature - requestID);
//
//		if (users.existsById(user_id)) {
//			Credentials cr = credentials.findById(user_id).orElse(null);
//			if ((cr.get_password()).equalsIgnoreCase(password)) {
//				Requests requests = requestRepo.findById(requestID).orElse(null);
//				if ((requests.getRequestStatus()).equals("accepted")) {
//					s = "the status is " + requests.getRequestStatus()
//							+ " your uid is your requestID and your password is "
//							+ dispatched_id;
//				} else {
//					s = "the status is " + requests.getRequestStatus();
//				}
//
//			}
//		}
//
//		else {
//			s = "user doesn't exists";
//		}
//
//		return s;
//	}

	@PostMapping("/viewRequests")
	public ResponseEntity<?> viewUserRequests(@CookieValue(name = "userId", required = false) String uidCookie) {
		if (uidCookie == null)return loginError();
		if (!credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equals("administrator"))	return accessError();
		
		List<Requests> rq1 = requestRepo.findAllByRequestStatus("pending");
		List<Requests> rq2 = requestRepo.findAllByRequestStatus("sent");
		List<Requests> newList = new ArrayList<Requests>(rq1);
		newList.addAll(rq2);
		return ResponseEntity.ok().body(newList);

	}

	@PostMapping("/approveRequests")
	public ResponseEntity<?> approveRequests(@CookieValue(name = "userId", required = false) String uidCookie,
			@RequestParam  int requestID,
			@RequestParam String requestResponse) 
	{
		if (uidCookie == null)return loginError();
		if (!credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equalsIgnoreCase("administrator"))return accessError();
		
		System.out.println(requestID);
		System.out.println(requestResponse);
		
		if (requestRepo.existsById(requestID)) {
			Requests requests = requestRepo.getById(requestID);
			EquipmentAvailability eav = equipmentAvailabilityRepo.getById(requests.getEquipmentID());

			if (requestResponse.equalsIgnoreCase("accepted")) {
				int available = eav.getAvailableamount();
				int issued = eav.getIssued_amount();
				if(available>0) 
				{ 
					requests.setRequestStatus("accepted");
					requestRepo.saveAndFlush(requests);
					eav.setAvailableamount(available-1);
					eav.setIssued_amount(issued+1);
					equipmentAvailabilityRepo.saveAndFlush(eav);
					requestRepo.saveAndFlush(requests);
					return new ResponseEntity<String>("requestAccepted",HttpStatus.OK);
				}
				else 
				{
					return new ResponseEntity<String>("notAvailableToIssue",HttpStatus.CONFLICT);

				}
			}
			else if (requestResponse.equalsIgnoreCase("pending")) {

				requests.setRequestStatus("pending");
				requestRepo.saveAndFlush(requests);
				return new ResponseEntity<String>("requestPending",HttpStatus.OK);
			}
			else if (requestResponse.equalsIgnoreCase("rejected")) {
				requestRepo.delete(requests);
				requestRepo.flush();
				return new ResponseEntity<String>("requestRejected",HttpStatus.OK);
			}
			
		}return new ResponseEntity<String>("noRequestFound",HttpStatus.NOT_FOUND);
	}

}

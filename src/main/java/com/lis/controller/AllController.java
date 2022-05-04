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
//	@Autowired
//	UserProfile currentUser;
//	@Autowired
//	Credentials currentUserCredentials;
//	@Autowired
//	EquipmentAvailability equipmentAvailability;
//	@Autowired
//	EquipmentDetails equipmentDetails;
//	@Autowired
//	Requests requests;

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
	
	static int admin_signature = 147;


	@Transactional
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpServletResponse response) {
		int uid = Integer.parseInt(body.get("userId"));
		String password = body.get("password");
		users.flush();
		credentials.flush();
		if (users.existsById(uid)) {
			Credentials currentUserCredentials = credentials.getById(uid);
			if (currentUserCredentials.get_password().equals(password.trim())) {
				currentUserCredentials.set_LoginStatus(true);
				credentials.saveAndFlush(currentUserCredentials);
//				UserProfile currentUser = users.getById(uid);
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
//		 if(!uidCookie.equals(null)) return loginError();
//		 if(credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equals("administrator")) return accessError();
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
			credentials.saveAndFlush(userCred);
			return  new ResponseEntity<Integer>(user.getUser_id(), HttpStatus.OK);
		} 
		else 
			return  new ResponseEntity<String>("userAlreadyExists", HttpStatus.CONFLICT);	}

	@PostMapping("/deleteUser")
	public ResponseEntity<?> deleteUser(@CookieValue(name = "userId", required = false) String uidCookie,@RequestBody Map<String, String> body) 
	{
		int uid = Integer.parseInt(body.get("userId"));
		System.out.println();
		 if(uidCookie==null) return loginError();
		 if(!credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equals("administrator")) return accessError();
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
		if (!credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equals("administrator")) return accessError();
		int uid = Integer.parseInt(requestBody.get("userId"));
		System.out.println(uid);
		if (users.existsById(uid) == true) {

			DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			UserProfile user = users.getById(uid);
			// Credentials userCred = credentials.getById(uid);

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
	@PostMapping("/viewAllUser")
	public ResponseEntity<?> viewAllUsers(@CookieValue(name = "userId", required = false) String uidCookie) {
		
		if (uidCookie == null) {
			return loginError();
			}
		if (!credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equals("administrator")) {
			return accessError();
			}
//		String s = "";
		List<Credentials> ls = credentials.findAllByType("customer");
		List<UserProfile> userList = new ArrayList<UserProfile>();
		for (Credentials a : ls) {
			userList.add(a.getUser());
		}
		return new ResponseEntity<List<UserProfile>>(userList,HttpStatus.OK);

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
		if(!credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equals("administrator")) return accessError();
		
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

	@PostMapping("/deleteEquipment")
	@Transactional
	public ResponseEntity<?> deleteEquipment(@CookieValue(name = "userId", required = false) String uidCookie,
			@RequestParam("equipmentID") int equipmentID) 
	{
		if (uidCookie == null)	return loginError();
		if (!credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equals("administrator")) return accessError();
		if (equipmentRepo.existsById(equipmentID)) 
		{
			EquipmentAvailability equipmentAvailability = equipmentAvailabilityRepo.findById(equipmentID).orElse(null);
			int available = equipmentAvailability.getAvailableamount() - 1;
			if (available == 0) 
			{
				if (equipmentAvailability.getIssued_amount()>0) 
				{
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
		if (!credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equals("administrator")) return accessError();
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
//		if (uidCookie == null)	return loginError();
		int uid = Integer.parseInt(uidCookie);
		if(credentials.getById(uid).get_UserType().equalsIgnoreCase("administrator")) {
			List<EquipmentAvailability> ls = equipmentAvailabilityRepo.findAll();											//different return objects for user and admin, include issued for admin
			return new ResponseEntity<List<EquipmentAvailability>>(ls,HttpStatus.OK);
		}
		else {
			List<EquipmentDetails> ls = equipmentRepo.findAll();														//different return objects for user and admin, include issued for admin
			return new ResponseEntity<List<EquipmentDetails>>(ls,HttpStatus.OK);
		}

	}

	@PostMapping("/searchEquipment")
	@Transactional
	public ResponseEntity<?> searchEquipments(@CookieValue(name = "userId", required = false) String uidCookie,
			@RequestParam("labName") String labName, @RequestParam("orgName") String orgName,
			@RequestParam("equipmentID") int equipmentID) 
	{
//		if (uidCookie == null)	return loginError();

		if (equipmentRepo.existsById(equipmentID)) {
			EquipmentAvailability equipmentAvailability = equipmentAvailabilityRepo.findById(equipmentID).orElse(null);
			return new ResponseEntity<EquipmentAvailability>(equipmentAvailability,HttpStatus.OK);
		}
		return new ResponseEntity<String>("equipmentNotFound",HttpStatus.NOT_FOUND);
	}

	@PostMapping("/returnEquipment")
	@Transactional
	public ResponseEntity<?> returnEquipment(@CookieValue(name = "userId", required = false) String uidCookie, @RequestParam("requestID") int requestID) {
//		if (uidCookie == null) return loginError();
//		if (!credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equals("customer")) return accessError();
		if (requestRepo.existsById(requestID)) {
			Requests requests = requestRepo.findById(requestID).orElse(null);
			if ((requests.getRequestStatus()).equals("accepted")) {
				int equipmentID = requests.getEquipmentID();
				if (equipmentRepo.existsById(equipmentID)) {
					EquipmentAvailability eav = equipmentAvailabilityRepo.getById(equipmentID);
					int val = eav.getAvailableamount() + 1;
					eav.setAvailableamount(val);
					eav.setIssued_amount(eav.getIssued_amount()-1);
					requests.setRequestStatus("returned");
					equipmentAvailabilityRepo.saveAndFlush(eav);
					requestRepo.saveAndFlush(requests);
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
//		if (uidCookie == null) return loginError();
//		if (!credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equals("customer")) return AccessError();
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
		
		if (!credentials.getById(userId).get_UserType().equals("customer")) return accessError();
		
		if (requestRepo.existsByUserID(userId) == false)
		{
			return new ResponseEntity<String>("requestDoesNotExist",HttpStatus.NOT_FOUND);
		}
		else 
		{
			requestRepo.delete(requestRepo.findByUserID(userId));
			requestRepo.flush();
			return new ResponseEntity<String>("requestDeleted",HttpStatus.OK);
		}
	}
	
	@PostMapping("/allRequestByUser")
	public ResponseEntity<?> allRequestByUser(@CookieValue(name = "userId", required = false) String uidCookie){
		if (uidCookie == null) return loginError();
		int userId = Integer.parseInt(uidCookie);
		if (!credentials.getById(userId).get_UserType().equals("customer")) return accessError();
		if (requestRepo.existsByUserID(userId) == false) 
		{
			return new ResponseEntity<String>("requestDoesNotExist",HttpStatus.NOT_FOUND);
		}
		else 
		{
			
			return new ResponseEntity<Requests>(requestRepo.findByUserID(userId),HttpStatus.OK);
		}
	}

	@PostMapping("/seeRequestStatus")
	@Transactional
	public String seeRequestStatus(@CookieValue(name = "userId", required = false) String uidCookie, @RequestParam("user_id") int user_id, @RequestParam("password") String password,
			@RequestParam("requestID") int requestID) {
		String s = "";
		int dispatched_id = (requestID * admin_signature) + (admin_signature - requestID);

		if (users.existsById(user_id)) {
			Credentials cr = credentials.findById(user_id).orElse(null);
			if ((cr.get_password()).equals(password)) {
				Requests requests = requestRepo.findById(requestID).orElse(null);
				if ((requests.getRequestStatus()).equals("accepted")) {
					s = "the status is " + requests.getRequestStatus()
							+ " your uid is your requestID and your password is "
							+ dispatched_id;
				} else {
					s = "the status is " + requests.getRequestStatus();
				}

			}
		}

		else {
			s = "user doesn't exists";
		}

		return s;
	}

	@PostMapping("/viewRequests")
	public ResponseEntity<?> viewUserRequests(@CookieValue(name = "userId", required = false) String uidCookie) {
//		if (uidCookie == null)
//			return loginPage;
//		if (!credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equals("administrator"))
//			return homePage;
//		String s = "";
		
		List<Requests> rq = requestRepo.findAll();
		return ResponseEntity.ok().body(rq);

	}

	@PostMapping("/approveRequests")
	public String approveRequests(@CookieValue(name = "userId", required = false) String uidCookie,
			@RequestBody  int requestID,
			@RequestBody String requestResponse) 
	{
//		if (uidCookie == null)return loginPage;
//		if (!credentials.getById(Integer.parseInt(uidCookie)).get_UserType().equals("administrator"))return homePage;
		
		System.out.println(requestID);
		System.out.println(requestResponse);
		
		String s = "";
		if (requestRepo.existsById(requestID)) {
			Requests requests = requestRepo.getById(requestID);
			EquipmentAvailability eav = equipmentAvailabilityRepo.getById(requests.getEquipmentID());

			if (requestResponse.equals("accepted")) {
				int available = eav.getAvailableamount();
				int issued = eav.getIssued_amount();
				if(available>0) 
				{ 
					requests.setRequestStatus("accepted");
					requestRepo.saveAndFlush(requests);
					eav.setAvailableamount(available-1);
					eav.setIssued_amount(issued+1);
					s = "requestAccepted";
				}
				else 
				{
					return "notAvailableToIssue";
				}
			}
			else if (requestResponse.equals("pending")) {
				s = "requestPending";
				requests.setRequestStatus("pending");
				requestRepo.saveAndFlush(requests);
			}
			else if (requestResponse.equals("Rejected")) {
				s = "requestRejected";
				requestRepo.deleteById(requestID);
			}
			s = "noRequestFound";
		}
		return s;
	}

}

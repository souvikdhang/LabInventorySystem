package com.lis.controller;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

import org.springframework.beans.factory.annotation.Autowired;
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
	User_repo users;

	@Autowired
	equipmentRepo er;

	@Autowired
	requestRepo rer;

	@Autowired
	equipmentAvailabilityRepo ear;

	@Autowired
	UserProfile currentUser;

	@Autowired
	Credentials currentUserCredentials;

	@Transactional
	@PostMapping("/login")
	public String login(@RequestBody Map<String, String> body) {

		// System.out.println(Integer.parseInt(body.get("userId")));
		int uid = Integer.parseInt(body.get("userId"));
		String password = body.get("password");
		// System.out.println(users.getById(uid));
		// System.out.println(credentials.getById(uid));
		// System.out.println(password.trim());
		if (users.existsById(uid)) {
			currentUserCredentials = credentials.getById(uid);
			if (currentUserCredentials.get_password().equals(password.trim())) {
				currentUserCredentials.set_LoginStatus(true);
				credentials.saveAndFlush(currentUserCredentials);
				currentUser = users.getById(uid);
				return "loggedIn";
			} else
				return "wrongPassword";
		} else
			return "userNotFound";
	}

	@PostMapping("/addUser")
	@Transactional
	public String addUser(@RequestParam("name") String name, @RequestParam("date") String dob,
			@RequestParam("gender") String gender, @RequestParam("address") String address,
			@RequestParam("phoneNumber") String phone, @RequestParam("emailId") String email,
			@RequestParam("userType") String userType) throws ParseException {
		String s = "";
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		System.out.println(users.existsByEmail(email));

		// if(currentUserCredentials.get_LoginStatus()==true) {
		// if(currentUserCredentials.get_UserType()=="administrator") {
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
			// userCred.set_UserID(user.getUser_id());
			userCred.set_UserType(userType);
			String password = name + date.getYear();

			userCred.set_password(password);
			userCred.set_LoginStatus(false);

			System.out.println(user);

			credentials.saveAndFlush(userCred);

			s = "added";
		} else
			s = "userAlreadyExists.jsp";
		// }
		// else s = "access denied";
		// }
		// else s = "not logged in";
		return s;
	}

	@PostMapping("/deleteUser")
	public String deleteUser(@RequestParam("userId") int uid) {
		String s = "";
		if (currentUserCredentials.get_LoginStatus() == true) {
			if (currentUserCredentials.get_UserType() == "administrator") {
				if (credentials.existsById(uid)) {
					credentials.deleteById(uid);
					users.deleteById(uid);

					users.flush();
					credentials.flush();

					s = "deleted";
				} else
					s = "userDoesNotExist";
			} else
				s = "accessDenied";
		} else
			s = "notLoggedIn";
		return s;
	}

	
	@PostMapping("/modifyUser")
	public String modifyUser(@RequestParam Map<String, String> requestParams) throws ParseException {

		String s = "";
		if (currentUserCredentials.get_LoginStatus() == true) {
			if (currentUserCredentials.get_UserType() == "administrator") {
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

					s = "updated";
				} else
					s = "user does not exist";
			} else
				s = "access denied";
		} else
			s = "not logged in";
		return s;

	}
	

	@GetMapping("/viewAllUser")
	@Transactional
	public List<UserProfile> viewAllUser(@RequestParam("userType") String userType) {
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

	// @GetMapping("/test")
	// public void test(){
	//
	// System.out.println( credentials.getByUserType("administrator").getUser());
	// }

	@PostMapping("/addEquipment")
	@Transactional
	public String addEquipment(@RequestParam("equipmentID") int equipmentID, @RequestParam("orgName") String orgName,
			@RequestParam("labName") String labName, @RequestParam("rackNumber") String rackNumber,
			@RequestParam("serverName") String serverName, @RequestParam("serverIPAddress") String serverIPAddress,
			@RequestParam("loginID") String loginID, @RequestParam("loginPassword") String loginPassword,
			@RequestParam("virtualMachine") int virtualMachine, @RequestParam("serverStatus") int serverStatus,
			@RequestParam("serverPower") int serverPower) {

		String s = "";
		// DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		// System.out.println(users.existsByEmail(email));
		equipmentAvailability ea = new equipmentAvailability();
		if (er.existsById(equipmentID) == false) {
			equipmentDetails ed = new equipmentDetails();
			// Credentials userCred=new Credentials();
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

			s = "equipment added";
		} else {
			equipmentAvailability eav = ear.findById(equipmentID).orElse(null);
			int val = eav.getAvailableamount();
			eav.setAvailableamount(val + 1);
			ear.save(eav);
			s = "equipment already exists";
		}

		return s;
	}

	@PostMapping("/deleteEquipment")
	@Transactional
	public String deleteEquipment(@RequestParam("equipmentID") int equipmentID) {
		String s = "";
		if (er.existsById(equipmentID)) {
			equipmentAvailability eavl = ear.findById(equipmentID).orElse(null);
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
		Iterator<equipmentDetails> itr = ls.iterator();
		while (itr.hasNext()) {
			equipmentDetails eqd = (equipmentDetails) itr.next();
			s += eqd.toString() + "\n";
		}

		return s;

	}

}

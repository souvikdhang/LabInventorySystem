package com.lis.controller;

import javax.servlet.http.HttpServletResponse;

import com.lis.dao.Credentials_repo;
import com.lis.dao.EquipmentAvailabilityRepo;
import com.lis.dao.RequestRepo;
import com.lis.dao.UserRepo;
import com.lis.model.Credentials;
import com.lis.model.EquipmentAvailability;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	@Autowired
	Credentials_repo credentials;

	@Autowired
	UserRepo users;

	@Autowired
	EquipmentAvailabilityRepo er;

	@Autowired
	RequestRepo rer;

	@Autowired
	EquipmentAvailability ear;

	@RequestMapping("/")
	public String localHost(@CookieValue(name = "userId", required = false) String uidString) {
		if (uidString == null || uidString == "") {
			return "Final_Frontend/login.html";
		} else {
			String url = "redirect:/homePage";
			return url;
		}
	}

	@GetMapping("/homePage")
	public String homePage(@CookieValue(name = "userId", required = false) String uidString) {

		if (uidString == null) {
			return "redirect:/";
		} else {
			if (credentials.getById(Integer.parseInt(uidString)).get_UserType().equalsIgnoreCase("administrator")) {
				System.out.println("admin");
				return "Final_Frontend/adminHome.html";
			} else {
				System.out.println("customer");
				return "Final_Frontend/userHome.html";
			}
		}

	}

	@GetMapping("/newEquipment")
	public String newEquipmentPath(@CookieValue(name = "userId", required = false) String uidString) {
		System.out.println(uidString);

		if (uidString == null) {
			return "redirect:/";
		} else {
			if (credentials.getById(Integer.parseInt(uidString)).get_UserType().equalsIgnoreCase("administrator")) {
				return "Final_Frontend/registerEquipment.html";
			} else {
				return "redirect:/homePage";
			}

		}
	}

	@GetMapping("/logout")
	public void logout(@CookieValue(name = "userId", required = false) String uidString, HttpServletResponse response) throws InterruptedException {
		ResponseCookie userIdCookie = ResponseCookie.from("userId", null)
				.httpOnly(false)
				.sameSite("None")
				.path("/")
				.secure(true)
				.maxAge(1)
				.build();
		response.addHeader("Set-Cookie", userIdCookie.toString());
		Credentials credential = credentials.getById(Integer.parseInt(uidString));
		credential.set_LoginStatus(false);
		credentials.saveAndFlush(credential);

	}

	 @GetMapping("/manageUsers")
	 public String manageUsers(@CookieValue(name = "userId", required = false)
	 String uidString) {
		 if (uidString == null) {
		 return "redirect:/";
		 }
		 if (credentials.getById(Integer.parseInt(uidString)).get_UserType().equalsIgnoreCase("administrator"))
		 {
		 return "Final_Frontend/manageUsers.html";
		 } else {
		 return "redirect:/homePage";
		 }
	 }

	// @GetMapping("/manageUserRequests")
	// public String manageUserRequests(@CookieValue(name = "userId", required =
	// false) String uidString) {
	// if (uidString == null) {
	// return "redirect:/";
	// }
	// if
	// (credentials.getById(Integer.parseInt(uidString)).get_UserType().equalsIgnoreCase("administrator"))
	// {
	// return "Final_Frontend/manageUserRequests.html";
	// } else {
	// return "redirect:/homePage";
	// }
	// }

	// @GetMapping("/manageLabEquipments")
	// public String manageLabEquipments(@CookieValue(name = "userId", required =
	// false) String uidString) {
	// if (uidString == null) {
	// return "redirect:/";
	// }
	// if
	// (credentials.getById(Integer.parseInt(uidString)).get_UserType().equalsIgnoreCase("administrator"))
	// {
	// return "Final_Frontend/manageLabEquipments.html";
	// } else {
	// return "redirect:/homePage";
	// }
	// }

	// @GetMapping("/sendLabEquipmentRequests")
	// public String sendLabEquipmentRequests(@CookieValue(name = "userId", required
	// = false) String uidString) {
	// if (uidString == null) {
	// return "redirect:/";
	// }
	// if
	// (credentials.getById(Integer.parseInt(uidString)).get_UserType().equalsIgnoreCase("administrator"))
	// {
	// return "Final_Frontend/sendLabEquipmentRequests.html";
	// } else {
	// return "redirect:/homePage";
	// }
	// }

	// @GetMapping("/searchLabEquipments")
	// public String searchLabEquipments(@CookieValue(name = "userId", required =
	// false) String uidString) {
	// if (uidString == null) {
	// return "redirect:/";
	// }
	// if
	// (credentials.getById(Integer.parseInt(uidString)).get_UserType().equalsIgnoreCase("administrator"))
	// {
	// return "Final_Frontend/searchLabEquipments.html";
	// } else {
	// return "redirect:/homePage";
	// }
	// }

	// @GetMapping("/returnLabEquipments")
	// public String returnLabEquipments(@CookieValue(name = "userId", required =
	// false) String uidString) {
	// if (uidString == null) {
	// return "redirect:/";
	// }
	// if
	// (credentials.getById(Integer.parseInt(uidString)).get_UserType().equalsIgnoreCase("administrator"))
	// {
	// return "Final_Frontend/returnLabEquipments.html";
	// } else {
	// return "redirect:/homePage";
	// }
	// }

}

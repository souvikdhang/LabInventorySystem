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
		System.out.println(uidString);

		if (uidString == null) {
			return "redirect:/";
		}
		if (credentials.getById(Integer.parseInt(uidString)).get_UserType().equalsIgnoreCase("administrator")) {
			System.out.println(credentials.getById(Integer.parseInt(uidString)).get_UserType());
			return "redirect:/adminHome";
		} else if (credentials.getById(Integer.parseInt(uidString)).get_UserType().equalsIgnoreCase("customer")) {
			System.out.println("customer");
			return "redirect:/userHome";
		}

		return "redirect:/";

	}

	@GetMapping("/adminHome")
	public String adminHome(@CookieValue(name = "userId", required = false) String uidString) {
		System.out.println(uidString);

		if (!credentials.getById(Integer.parseInt(uidString)).get_UserType().equalsIgnoreCase("administrator"))
			return "redirect:/";
		return "Final_Frontend/admin/adminHome.html";

	}

	@GetMapping("/userHome")
	public String userHome(@CookieValue(name = "userId", required = false) String uidString) {
		System.out.println(uidString);
		if (credentials.getById(Integer.parseInt(uidString)).get_UserType().equalsIgnoreCase("administrator"))
			return "redirect:/";
		return "Final_Frontend/user/userHome.html";

	}

	@GetMapping("/newEquipment")
	public String newEquipmentPage(@CookieValue(name = "userId", required = false) String uidString) {
		System.out.println(uidString);

		if (uidString == null) {
			return "redirect:/";
		} else {
			if (credentials.getById(Integer.parseInt(uidString)).get_UserType().equalsIgnoreCase("administrator")) {
				return "Final_Frontend/admin/registerEquipment.html";
			} else {
				return "redirect:/homePage";
			}

		}
	}

	@GetMapping("/newUser")
	public String newUserPage(@CookieValue(name = "userId", required = false) String uidString) {
		System.out.println(uidString);

		if (uidString == null) {
			return "redirect:/";
		} else {
			if (credentials.getById(Integer.parseInt(uidString)).get_UserType().equalsIgnoreCase("administrator")) {
				return "Final_Frontend/admin/registerUser.html";
			} else {
				return "redirect:/homePage";
			}

		}
	}

	@GetMapping("/logout")
	public void logout(@CookieValue(name = "userId", required = false) String uidString, HttpServletResponse response)
			throws InterruptedException {
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
	public String manageUsersPage(@CookieValue(name = "userId", required = false) String uidString) {
		if (uidString == null) {
			return "redirect:/";
		}
		if (credentials.getById(Integer.parseInt(uidString)).get_UserType().equalsIgnoreCase("administrator")) {
			return "Final_Frontend/admin/manageUsers.html";
		} else {
			return "redirect:/homePage";
		}
	}

	@GetMapping("/manageEquipmentPage")
	public String manageEquipmentPage(@CookieValue(name = "userId", required = false) String uidString) {
		if (uidString == null) {
			return "redirect:/";
		}

		int userId = Integer.parseInt(uidString);
		if (credentials.getById(userId).get_UserType().equalsIgnoreCase("administrator")) {
			System.out.println("admin");
			return "redirect:/manageEquipmentAdminPage";
		} else if (credentials.getById(userId).get_UserType().equalsIgnoreCase("customer")) {
			System.out.println("customer");
			return "redirect:/manageEquipmentUserPage";
		}
		return "redirect:/";
	}

	@GetMapping("/manageEquipmentAdminPage")
	public String manageEquipmentAdminPage(@CookieValue(name = "userId", required = false) String uidString) {
		System.out.println(uidString);
		if (!credentials.getById(Integer.parseInt(uidString)).get_UserType().equalsIgnoreCase("administrator"))
			return "redirect:/";
		return "Final_Frontend/admin/manageEquipmentAdmin.html";

	}

	@GetMapping("/manageEquipmentUserPage")
	public String manageEquipmentUserPage(@CookieValue(name = "userId", required = false) String uidString) {
		System.out.println(uidString);
		if (credentials.getById(Integer.parseInt(uidString)).get_UserType().equalsIgnoreCase("administrator"))
			return "redirect:/";
		return "Final_Frontend/user/manageEquipmentUser.html";

	}

	@GetMapping("/manageUserRequestsPage")
	public String manageUserRequestsPage(@CookieValue(name = "userId", required = false) String uidString) {
		if (uidString == null) {
			return "redirect:/";
		}
		if (credentials.getById(Integer.parseInt(uidString)).get_UserType().equalsIgnoreCase("administrator")) {
			return "Final_Frontend/admin/manageRequestsAdmin.html";
		} else {
			return "redirect:/homePage";
		}
	}

	@GetMapping("/manageRequestsPage")
	public String manageRequestsPage(@CookieValue(name = "userId", required = false) String uidString) {
		if (uidString == null) {
			return "redirect:/";
		}
		if (credentials.getById(Integer.parseInt(uidString)).get_UserType().equalsIgnoreCase("customer")) {
			return "Final_Frontend/user/returnEquipment.html";
		} else {
			return "redirect:/homePage";
		}
	}

	@GetMapping("/increaseEquipment")
	public String increaseEquipment(@CookieValue(name = "userId", required = false) String uidString) {
		if (uidString == null) {
			return "redirect:/";
		}
		if (credentials.getById(Integer.parseInt(uidString)).get_UserType().equalsIgnoreCase("administrator")) {
			return "Final_Frontend/admin/increaseEquipment.html";
		} else {
			return "redirect:/homePage";
		}
	}

	@GetMapping("/modifyEquipmentPage")
	public String modifyEquipmentPage(@CookieValue(name = "userId", required = false) String uidString) {
		if (uidString == null) {
			return "redirect:/";
		}
		if (credentials.getById(Integer.parseInt(uidString)).get_UserType().equalsIgnoreCase("administrator")) {
			return "Final_Frontend/admin/modifyEquipment.html";
		} else {
			return "redirect:/homePage";
		}
	}

}

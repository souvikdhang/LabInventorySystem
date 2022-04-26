package com.lis.controller;

import javax.servlet.http.HttpServletResponse;

import com.lis.dao.Credentials_repo;
import com.lis.dao.EquipmentAvailabilityRepo;
import com.lis.dao.UserRepo;
import com.lis.dao.requestRepo;
import com.lis.model.EquipmentAvailability;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController
{
	
	@Autowired
	Credentials_repo credentials;

	@Autowired
	UserRepo users;

	@Autowired
	EquipmentAvailabilityRepo er;

	@Autowired
	requestRepo rer;

	@Autowired
	EquipmentAvailability ear;
	
		
	@RequestMapping("/")
	public String localHost(@CookieValue(name = "userId", required=false) String uidString) 
	{
		if(uidString==null) 
		{
			return "Final_Frontend/login.html";
		}
		else 
		{
			String url = "redirect:/homePage";
			return url;
		}
	}
	
	
	@GetMapping("/homePage")
	public String homePage(@CookieValue(name = "userId", required=false) String uidString) 
	{
		System.out.println(uidString+"dfd");
		
		if(uidString==null) 
		{
			return "redirect:/";
		}
		else 
		{
			if(credentials.getById(Integer.parseInt(uidString)).get_UserType().equalsIgnoreCase("administrator"))
			{
				return "Final_Frontend/adminHome.html";
			}
			else
			{
				return "Final_Frontend/userHome.html";
			}
		}
		

	}
	
	@GetMapping("/newEquipment")
	public String newEquipmentPath(@CookieValue(name = "userId", required=false) String uidString) 
	{
		System.out.println(uidString);
		
		if(uidString==null) 
		{
			return "redirect:/";
		}
		else 
		{	
			if(credentials.getById(Integer.parseInt(uidString)).get_UserType().equalsIgnoreCase("administrator"))
			{
				return "Final_Frontend/registerEquipment.html";
			}
			else
			{
				return  "redirect:/homePage";
			}
			
		}		
	}
	
	@GetMapping("/logout")
	public String logout(HttpServletResponse response) throws InterruptedException {
		ResponseCookie userIdCookie =ResponseCookie.from("userId",null)
	            .httpOnly(false)
	            .sameSite("None")
	            .secure(true)
	            .path("/")
	            .maxAge(Math.toIntExact(1))
	            .build();
		response.addHeader("Set-Cookie", userIdCookie.toString());
		Thread.sleep(500);
		return "redirect:/";
	}
	
}

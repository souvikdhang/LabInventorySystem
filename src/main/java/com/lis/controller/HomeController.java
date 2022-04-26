package com.lis.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.lis.dao.Credentials_repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController
{
	
	@Autowired
	Credentials_repo credentials;
		
	@RequestMapping("/")
	public String localHost(@CookieValue(name = "userId", required=false) String uid, @CookieValue(name = "userType", required=false) String userType) 
	{
		if(uid==null||userType==null) 
		{
			return "Final_Frontend/login.html";
		}
		else 
		{
			String url = "redirect:/homePage?userId="+uid+"&userType="+userType;
			return url;
		}
	}
	
	
	@GetMapping("/homePage")
	public String homePage(@RequestParam("userId") int uid, @RequestParam("userType") String userType,  HttpServletResponse response) 
	{
		Cookie userIdCookie = new Cookie("userId",Integer.toString(uid));
		userIdCookie.setMaxAge(2147483647);
		Cookie userTypeCookie = new Cookie("userType",userType);
		userTypeCookie.setMaxAge(2147483647);
		response.addCookie(userTypeCookie);
		response.addCookie(userIdCookie);
		
		if(userType.equalsIgnoreCase("administrator"))
		{
			return "Final_Frontend/adminHome.html";
		}
		else
		{
			return "Final_Frontend/userHome.html";
		}
	}
	

	

	
}

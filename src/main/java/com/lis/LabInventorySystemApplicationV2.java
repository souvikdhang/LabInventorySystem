package com.lis;

import java.text.ParseException;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication

public class LabInventorySystemApplicationV2 {
	

//	private UserProfile user ;
	

	public static void main(String[] args) throws ParseException {
		SpringApplication.run(LabInventorySystemApplicationV2.class, args);
	}
	

//	public ModelAndView servehtml() {
//	    ModelAndView modelAndView = new ModelAndView();
//	    modelAndView.setViewName("demo2.html");
//	    return modelAndView;
//	}
//		
//	
//
//	public UserProfile printall() throws ParseException {
//		
//
//		
//		user.setAddress("22/1,sri ram dhang road");
//		user.setDob(13,01,2001);
//		user.setGender("male");
//		user.setName("souvik dhang");
//		user.setPhone_number("6290720902");
//		user.setEmail("souvik.dhang@gmail.com");
//		
//		System.out.println(user);
//		
//		Configuration con = new Configuration().configure().addAnnotatedClass(UserProfile.class);
//		ServiceRegistry reg =(ServiceRegistry) new StandardServiceRegistryBuilder().applySettings(con.getProperties()).build();
//		SessionFactory sf = con.buildSessionFactory(reg);
//		Session session = sf.openSession();
//		Transaction tx = session.beginTransaction();
//		session.save(user);
//		tx.commit();
//		
//		
//		return user;
//	}

}

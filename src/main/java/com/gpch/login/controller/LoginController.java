package com.gpch.login.controller;

import com.gpch.login.model.OTPModel;
import com.gpch.login.model.User;
import com.gpch.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @RequestMapping(value={"/", "/login"}, method = RequestMethod.GET)
    public ModelAndView login(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @RequestMapping(value="/admin/home", method = RequestMethod.GET)
    public ModelAndView home(){
    	
//        System.out.println("bdata");

        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUsername(auth.getName());
        
        String result = user.getOther();
        
        boolean trained = true;
        
        if (result != null && result.indexOf("Trained : false") > 10) {
        	trained = false;
        }

        if (!trained && !user.getUsername().startsWith("tr")) {
        	OTPModel otp = new OTPModel();
	        modelAndView.addObject("googleauth", "OTP (Google Authenticator)");
	        modelAndView.addObject("message", "Your score is too low, or you are in training -\n\n" + user.getName() + " " + user.getLastName() + 
	        		" (" + user.getUsername() + ")");
	        modelAndView.addObject("message2", "This is your BehavioSec score - " + result);
	        modelAndView.addObject("message3", "Enter your code to continue login:");
	        modelAndView.addObject("OTPModel", otp);
	        modelAndView.setViewName("admin/loginOTP");        
        } else {
	        modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName() + 
	        		" (" + user.getUsername() + ")");
	        modelAndView.addObject("adminMessage", "This is your BehavioSec score:<br><br>" + result);
	        modelAndView.setViewName("admin/home");
        }
        return modelAndView;
    }
}

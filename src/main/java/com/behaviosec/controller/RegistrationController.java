package com.gpch.login.controller;

import java.io.UnsupportedEncodingException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.behaviosec.login.utils.Utils;
import com.gpch.login.model.User;
import com.gpch.login.service.UserService;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

	private static Utils utils = new Utils();

    @RequestMapping(value="/registration", method = RequestMethod.GET)
    public ModelAndView registration(){
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("registration");
        return modelAndView;
    }
    
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
    	    	
    	String bdata = user.getOther();
        System.out.println(" bdata " + bdata);
        user.setOther("");
  
        String userAgent = "";
        if (bdata != null && bdata.trim().length() > 0) {
        	int indexOf = bdata.indexOf("::");
        	if (indexOf > 0) {
        		userAgent = bdata.substring(0, indexOf);
        		bdata = bdata.substring(indexOf + 2);
        	}
        }
		String timingData = bdata;
		String userName = user.getUsername();
		String ip = "1.1.1.1";
		
		String result = utils.checkData(userName, userAgent, ip, timingData);
		
		user.setOther("result " + result);

        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByUsername(user.getUsername());
        if (userExists != null) {
            bindingResult.rejectValue("username", "error.user",
                            "There is already a user registered with the username provided");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("registration");
        } else {
        	
            userService.saveUser(user);
            String QRUrl = "";
            
            try { 
            	QRUrl = userService.generateQRUrl(user);
            } catch (UnsupportedEncodingException uee) {
            	uee.printStackTrace();
            }
            modelAndView.addObject("successMessage", "Scan this Barcode using Google Authenticator app on your phone to use it later for login");
            modelAndView.addObject("qrurl", QRUrl);
            modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getUsername() + ")<br><br>" + result);
            modelAndView.setViewName("registrationSuccess");

        }
        return modelAndView;
    }
}

package com.behaviosec.controller;

import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

import com.behaviosec.model.OTPModel;
import com.behaviosec.model.User;
import com.behaviosec.service.UserService;

@Controller
public class OTPController {

    @Autowired
    private UserService userService;

	/*
    @RequestMapping(value={"/home/loginOTP"}, method = RequestMethod.GET)
    public ModelAndView loginOTP(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home/loginOTP");
        return modelAndView;
    }

    @RequestMapping(value = "/home/loginOTP", method = RequestMethod.POST)
    public ModelAndView validateOTP() {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("successMessage", "User confirmed successfully");
        modelAndView.setViewName("home/loginOTP");


        return modelAndView;
    }



*/
/*
    @GetMapping("/home/loginOTP")
    public ModelAndView loginOTP(Model model){
    	
    	model.addAttribute("OTPModel", new OTPModel());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/loginOTP");
        return modelAndView;
    }
*/
    @PostMapping("/home/loginOTP")
    public ModelAndView validateOTP(@RequestHeader(value = "referer", required = false) final String referer, @ModelAttribute OTPModel otpModel) {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUsername(auth.getName());
        
        String type = otpModel.getType();
        String result = null;
        
        user.getOther();
        
        if ("otp".equals(type)) {
        	
        }
        String secret = user.getSecret();
        
        boolean passed = false;
        
        String totpKey = otpModel.getOtpValue();
        if (totpKey != null) {
        	Totp totp = new Totp(secret);
            if (totp.verify(totpKey.toString())) {
            	passed = true;
            } else {
            	System.out.println("secret " + secret);
            	System.out.println("totpKey" + totpKey);
            }
        }
        
        if (!passed) {
        	OTPModel otp = new OTPModel();
	        modelAndView.addObject("googleauth", "OTP (Google Authenticator)");
	        modelAndView.addObject("message", "Your score is too low, or you are in training -\n\n" + user.getName() + " " + user.getLastName() + 
	        		" (" + user.getUsername() + ")" + "\n\nThis is your BehavioSec score:\n\nEnter your code:" + result);
	        modelAndView.addObject("OTPModel", otp);
	        modelAndView.addObject("OTPModel", otp);
	        modelAndView.setViewName("home/loginOTP");        
        } else {
	        modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName() + 
	        		" (" + user.getUsername() + ")");
	        modelAndView.addObject("adminMessage", "This is your BehavioSec score:<br><br>" + result);
	        modelAndView.setViewName("home/homepage");
        }
        return modelAndView;
  
        
        
        
        
        
        
        
//        modelAndView.addObject("successMessage", "User confirmed successfully");
//        modelAndView.setViewName("home/loginOTP");


//        return modelAndView;
    }
}
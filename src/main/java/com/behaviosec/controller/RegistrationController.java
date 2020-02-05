package com.behaviosec.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.behaviosec.handler.SimpleAuthenticationSuccessHandler;
import com.behaviosec.entities.Report;
import com.behaviosec.entities.Response;
import com.behaviosec.model.Registration;
import com.behaviosec.model.User;
import com.behaviosec.service.RegistrationService;
import com.behaviosec.service.UserService;
import com.behaviosec.utils.Helper;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;

@Controller
public class RegistrationController {
	@Value( "${behaviosec.behaviosecurl}" )
	@NonNull public String behaviosecurl;
	@Value( "${behaviosec.tenantId}" )
	public String tenantId;

	private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
	private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserService userService;
    
    @Autowired
    private RegistrationService registrationService;


    @RequestMapping(value="/registration", method = RequestMethod.GET)
    public ModelAndView registration(){
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("registration");
        return modelAndView;
    }
    
    @RequestMapping(value="/registrationga", method = RequestMethod.GET)
    public ModelAndView registrationga(){
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("registrationga");
        return modelAndView;
    }
    
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView createNewUser(HttpServletRequest request, @Valid User user, BindingResult bindingResult) {
    	return createNewUser(request, user, bindingResult, "");
    }
    
    @RequestMapping(value = "/registrationga", method = RequestMethod.POST)
    public ModelAndView createNewUserGA(HttpServletRequest request, @Valid User user, BindingResult bindingResult) {
    	return createNewUser(request, user, bindingResult, "ga");
    }
    
    @RequestMapping(value="/authorizationregistration", method = RequestMethod.GET)
    public ModelAndView authorizationRegistration(){
        ModelAndView modelAndView = new ModelAndView();
        Registration registration = new Registration();
        registration.setKeysecret(UUID.randomUUID().toString());
        registration.setMax(10);
        modelAndView.addObject("registration", registration);
        modelAndView.setViewName("secretregistration");
        return modelAndView;
    }
    
    @RequestMapping(value = "/authorizationregistration", method = RequestMethod.POST)
    public ModelAndView createNewAuthorization(HttpServletRequest request, @Valid Registration registration, BindingResult bindingResult) {
    	//return createNewUser(request, user, bindingResult, "");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("secretregistration");

        Registration registrationExists = registrationService.findRegistrationByName(registration.getName());
        if (registrationExists != null) {
            bindingResult.rejectValue("name", "error.user",
                            "There is already a user registered with the username provided");
        } else if (!bindingResult.hasErrors()) {
        	registration.setOther("");
        	registrationService.saveRegistration(registration);
	        modelAndView.addObject("registration", registration);
	        modelAndView.setViewName("secretregistration");
        }
        return modelAndView;

    }
    

    public ModelAndView createNewUser(HttpServletRequest request, @Valid User user, BindingResult bindingResult, String type) {
		String report = null;
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByUsername(user.getUsername());
        if (userExists != null) {
            bindingResult.rejectValue("username", "error.user",
                            "There is already a user registered with the username provided");
        } else if (bindingResult.hasErrors()) {
            modelAndView.setViewName("registration" + type);
        } else {
        	
	    	String bdata = user.getOther();
	        System.out.println(" bdata " + bdata);
	        user.setOther("");
	  
	        if (bdata == null) {
	        	bdata = request.getParameter("bdata");
	        }
	        String userAgent = "";
	        if (bdata != null && bdata.trim().length() > 0) {
	        	int indexOf = bdata.indexOf("::");
	        	if (indexOf > 0) {
	        		userAgent = bdata.substring(0, indexOf);
	        		bdata = bdata.substring(indexOf + 2);
	        	} else {
	        		userAgent = request.getHeader("User-Agent");
	        	}
	        }
			String timingData = bdata;
			String userName = user.getUsername();
	        String clientIp = Helper.getClientIpAddress(request);
			
	//		String result = utils.checkData(userName, userAgent, clientIp, timingData);
			Response r = SimpleAuthenticationSuccessHandler.getResponse(behaviosecurl, 
					tenantId, clientIp, userAgent, userName, timingData, log);
			if(r.hasReport()){
	        	report = r.getReponseString();
	        	userService.updateUser(user, report);
	        }
			
			user.setOther("result " + report);
            userService.saveUser(user);
            String QRUrl = "";
            
            try { 
            	QRUrl = userService.generateQRUrl(user);
            } catch (UnsupportedEncodingException uee) {
            	uee.printStackTrace();
            }
            String successMessage = "You may be prompted for your PingID later for login";
            
            if (type.equals("ga")) {
            	successMessage = "Scan this Barcode using Google Authenticator app on your phone to use it later for login";
            }
            
            
    		Report bhsReport = null;
    		try {
    			bhsReport = objectMapper.readValue(
    					report,
    					Report.class
    			);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		
    		
    		if (bhsReport != null) {
    			modelAndView.addObject("score", "<b>Behavioral Score: </b>" + ((int) bhsReport.getScore()));
    			modelAndView.addObject("risk", "<b>Risk: </b>" + ((int) bhsReport.getRisk()));
    			modelAndView.addObject("deviceChange", "<b>Device Changed: </b>" + bhsReport.isDeviceChanged());
    			modelAndView.addObject("ipChange", "<b>Ip Changed: </b>" + bhsReport.isIpChanged());
    			modelAndView.addObject("bot", "<b>Bot: </b>" + bhsReport.isBot());
    			modelAndView.addObject("replay", "<b>Replay Attack: </b>" + bhsReport.isReplay());
    			modelAndView.addObject("remoteAccess", "<b>Remote Access: </b>" + bhsReport.isRemoteAccess());
    		}
            
            modelAndView.addObject("successMessage", successMessage);
            modelAndView.addObject("qrurl", QRUrl);
            modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getUsername() + ")");
            modelAndView.setViewName("registration" + type + "Success");

        }
        return modelAndView;
    }
}

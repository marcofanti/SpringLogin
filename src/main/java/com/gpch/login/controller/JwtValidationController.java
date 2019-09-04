package com.gpch.login.controller;

import com.gpch.login.model.User;
import com.gpch.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class JwtValidationController {

    @Autowired
    private UserService userService;

    @RequestMapping(value="/jwtvalidation", method = RequestMethod.GET)
    public String jwtValidation(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        
        if (user == null) {
        	return("/error");
        } else {
	        return "/success";
        }
    }
}
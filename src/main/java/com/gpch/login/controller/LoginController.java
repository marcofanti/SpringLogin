package com.gpch.login.controller;

import java.io.UnsupportedEncodingException;

import javax.validation.Valid;

import com.gpch.login.model.User;
import com.gpch.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        
        String QRUrl = "";
        
        try { 
        	QRUrl = userService.generateQRUrl(user);
        } catch (UnsupportedEncodingException uee) {
        	uee.printStackTrace();
        }
        modelAndView.addObject("qrurl", QRUrl);

        modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        modelAndView.addObject("adminMessage", "Scan this Barcode using Google Authenticator app on your phone to use it later in login");
        modelAndView.addObject("QRUrl", QRUrl);
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }
}

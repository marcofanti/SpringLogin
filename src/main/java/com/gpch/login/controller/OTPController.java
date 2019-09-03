package com.gpch.login.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.gpch.login.model.OTPModel;
import com.gpch.login.model.User;
import com.gpch.login.service.UserService;

@Controller
public class OTPController {

    @Autowired
    private UserService userService;

	/*
    @RequestMapping(value={"/admin/loginOTP"}, method = RequestMethod.GET)
    public ModelAndView loginOTP(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/loginOTP");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/loginOTP", method = RequestMethod.POST)
    public ModelAndView validateOTP() {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("successMessage", "User confirmed successfully");
        modelAndView.setViewName("admin/loginOTP");


        return modelAndView;
    }



*/

    @GetMapping("/admin/loginOTP")
    public ModelAndView loginOTP(Model model){
    	model.addAttribute("OTPModel", new OTPModel());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/loginOTP");
        return modelAndView;
    }

    @PostMapping("/admin/loginOTP")
    public ModelAndView validateOTP(@ModelAttribute OTPModel otpModel) {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("successMessage", "User confirmed successfully");
        modelAndView.setViewName("admin/loginOTP");


        return modelAndView;
    }
}
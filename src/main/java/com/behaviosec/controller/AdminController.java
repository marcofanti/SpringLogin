package com.behaviosec.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

import com.behaviosec.model.OTPModel;
import com.behaviosec.model.User;
import com.behaviosec.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;

@Controller
public class AdminController {
	@Value("${behaviosec.behaviosecurl}")
	@NonNull
	public String behaviosecurl;
	@Value("${behaviosec.tenantId}")
	public String tenantId;
	@Value("${behaviosec.maxtries:2}")
	public String maxTries;

	private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private UserService userService;

	@GetMapping("/admin/makeadmin")
	public ModelAndView makeadminget(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value = "referer", required = false) final String referer,
			@ModelAttribute OTPModel otpModel) {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = new User();
        modelAndView.addObject("user", user);
	    modelAndView.setViewName("admin/makeadmin");

		return modelAndView;
	}	

	@PostMapping("/admin/makeadmin")
	public ModelAndView makeadminpost(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value = "referer", required = false) final String referer,
			@ModelAttribute OTPModel otpModel) {
		ModelAndView modelAndView = new ModelAndView();
		//User user = userService.findUserByUsername(auth.getName());


	    modelAndView.setViewName("admin/makeadmin");
		return modelAndView;
	}	
}
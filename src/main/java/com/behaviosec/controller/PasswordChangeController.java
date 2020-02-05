package com.behaviosec.controller;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.behaviosec.handler.SimpleAuthenticationSuccessHandler;
import com.behaviosec.entities.Report;
import com.behaviosec.entities.Response;
import com.behaviosec.model.PasswordChangeModel;
import com.behaviosec.model.User;
import com.behaviosec.service.UserService;
import com.behaviosec.utils.Helper;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;

@Controller
public class PasswordChangeController {
	@Value( "${behaviosec.behaviosecurl}" )
	@NonNull public String behaviosecurl;
	@Value( "${behaviosec.tenantId}" )
	public String tenantId;

	private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass().getName());
	private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserService userService;
        
	@GetMapping("/home/passwordChange")
	public ModelAndView passwordChange() {
		ModelAndView modelAndView = new ModelAndView();

		modelAndView.addObject("adminMessage", "");
		modelAndView.addObject("passwordChange", new PasswordChangeModel());
		modelAndView.setViewName("/home/passwordChange");
		return modelAndView;
	}

	@PostMapping("/home/passwordChange")
	public ModelAndView passwordChangeSubmit(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute PasswordChangeModel passwordChange, Authentication authentication) {

		
		

		String userName = authentication.getName();
		/*
		System.out.println("\nAttributes\n");
		Iterator<String> itr = request.getAttributeNames().asIterator();

		while (itr.hasNext()) {
			Object attribute = itr.next();
			System.out.println(attribute + " " + request.getAttribute(attribute.toString()));
		}
		System.out.println("\nHeaders\n");

		itr = request.getHeaderNames().asIterator();

		String userAgent = "";

		while (itr.hasNext()) {
			Object header = itr.next();
			String headerValue = request.getHeader(header.toString());
			System.out.println(header + " " + headerValue);
			if (header.equals("user-agent")) {
				userAgent = headerValue;
			}
		}
		System.out.println("\nParameters\n");

		itr = request.getParameterNames().asIterator();

		String timingData = "";
		String clientIp = Helper.getClientIpAddress(request);

		while (itr.hasNext()) {
			Object parameters = itr.next();
			if (parameters.equals("bdata") || parameters.equals("other")) {
				timingData = request.getParameter(parameters.toString());
			} else if (parameters.equals("username")) {
				userName = request.getParameter(parameters.toString());
			}
			log.info(parameters + " " + request.getParameter(parameters.toString()));
		}

		Response r = SimpleAuthenticationSuccessHandler.getResponse(behaviosecurl, 
				tenantId, clientIp, userAgent, userName, timingData, log);
 */
		
		Response r = SimpleAuthenticationSuccessHandler.callGetResponse(request, userName, behaviosecurl, tenantId, null, log);
		
		ModelAndView modelAndView = new ModelAndView();

		if (r.hasReport()) {

			String result = r.getReponseString();

			Report bhsReport = null;
			try {
				bhsReport = objectMapper.readValue(result, Report.class);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if ((int) bhsReport.getScore() > 90) {
				User user = userService.findUserByUsername(userName);
		        
		        if(r != null && r.hasReport()){
		        	String report = r.getReponseString();
		        	userService.updateUser(user, report);
		        }
		        
				modelAndView.addObject("adminMessage", "Password changed");
				modelAndView.addObject("passwordChange", new PasswordChangeModel());
				modelAndView.setViewName("/home/homepage");
				return modelAndView;

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

		}
		
		modelAndView.addObject("adminMessage", "Password changed");
		modelAndView.addObject("passwordChange", new PasswordChangeModel());
		modelAndView.setViewName("/home/passwordChange");
		return modelAndView;
	}

}

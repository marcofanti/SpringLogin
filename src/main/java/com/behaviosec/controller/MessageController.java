package com.behaviosec.controller;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.behaviosec.model.MessageModel;
import com.behaviosec.utils.Helper;
import com.behaviosec.config.Constants;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;

@Controller
public class MessageController {
	@Value( "${behaviosec.behaviosecurl}" )
	@NonNull public String behaviosecurl;
	@Value( "${behaviosec.tenantId}" )
	public String tenantId;

	private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass().getName());
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@GetMapping("/home/message")
	public ModelAndView registration() {
		ModelAndView modelAndView = new ModelAndView();

		modelAndView.addObject("adminMessage", "");
		modelAndView.addObject("message", new MessageModel());
		modelAndView.setViewName("/home/message");
		return modelAndView;
	}

	@PostMapping("/home/message")
	public ModelAndView messageSubmit(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute MessageModel message, Authentication authentication) {

		String userName = authentication.getName();
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

		ModelAndView modelAndView = new ModelAndView();

		Response r = SimpleAuthenticationSuccessHandler.getResponse(behaviosecurl, 
				tenantId, clientIp, userAgent, userName, timingData, log);

		if (r.hasReport()) {

			String result = r.getReport().toString();

			Report bhsReport = null;
			try {
				bhsReport = objectMapper.readValue(result, Report.class);
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

		}
		modelAndView.addObject("adminMessage", "The new message is " + message.getContent());
		modelAndView.addObject("message", new MessageModel());
		modelAndView.setViewName("/home/message");
		return modelAndView;
	}

}

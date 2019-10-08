package com.gpch.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.gpch.login.model.MessageModel;

@Controller
public class MessageController {

	@GetMapping("/admin/message")
	public ModelAndView registration(){
        ModelAndView modelAndView = new ModelAndView();
        
        modelAndView.addObject("message", new MessageModel());
        modelAndView.setViewName("/admin/message");
		return modelAndView;
	}

	@PostMapping("/admin/message")
	public ModelAndView messageSubmit(@ModelAttribute MessageModel message) {
		System.out.println("message " + message.getContent());
        ModelAndView modelAndView = new ModelAndView();
        
        modelAndView.addObject("message", new MessageModel());
        modelAndView.setViewName("/admin/message");
		return modelAndView;
	}

}

package com.gpch.login.controller;

import java.io.UnsupportedEncodingException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.gpch.login.model.User;
import com.gpch.login.service.UserService;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @RequestMapping(value="/registration", method = RequestMethod.GET)
    public ModelAndView registration(){
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("registration");
        return modelAndView;
    }

/*    
    @RequestMapping(value = "/registrationConfirm", method = RequestMethod.GET)
    public String confirmRegistration(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token);
        if(result.equals("valid")) {
            User user = userService.getUser(token);
            if (user.isUsing2FA()) {
            	String QRUrl = userService.generateQRUrl(user);
            	System.out.println("QRUrl = " + QRUrl);
                model.addAttribute("qr", QRUrl);
                return "redirect:/qrcode.html?lang=" + locale.getLanguage();
            }
             
            model.addAttribute(
              "message", messages.getMessage("message.accountVerified", null, locale));
            return "redirect:/login?lang=" + locale.getLanguage();
        }
    }
 
    */
    
    
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());
        if (userExists != null) {
            bindingResult.rejectValue("email", "error.user",
                            "There is already a user registered with the email provided");
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
            modelAndView.addObject("successMessage", "User has been registered successfully");
            modelAndView.addObject("qrurl", QRUrl);
            modelAndView.addObject("user", new User());
            modelAndView.setViewName("registration");

        }
        return modelAndView;
    }
}

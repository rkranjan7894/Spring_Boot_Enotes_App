package com.spring.Enotes.controller;

import com.spring.Enotes.entity.User;
import com.spring.Enotes.repository.UserRepository;
import com.spring.Enotes.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class HomeController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepo;
    @ModelAttribute
    public void commonUser(Principal p, Model m){
        if(p!=null) {
            String email = p.getName();
            User user = userRepo.findByEmail(email);
            m.addAttribute("user", user);
        }
    }
    @GetMapping("/")
    public String index(){
        return "index";
    }
    @GetMapping("/register")
    public String register(){
        return "register";
    }
    @GetMapping("/signin")
    public String login(){
        return "login";
    }
    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute User user, HttpSession session, Model m, HttpServletRequest request){
        String url=request.getRequestURL().toString();
        url=url.replace(request.getServletPath(),"");
        boolean f=userService.existEmailCheck(user.getEmail());
        if (f){
               session.setAttribute("msg","Email already exist");
        }else {
            User saveUser = userService.saveUser(user,url);
            if (saveUser!=null){
                session.setAttribute("msg","Register success!");

            }else {
                session.setAttribute("msg","Something wrong on server");

            }
        }
        return "redirect:/register";
    }
    @GetMapping("/verify")
    public String verifyAccount(@Param("code")String code, Model m){
        boolean f=userService.verifyAccount(code);
        if (f){
            m.addAttribute("msg","Successfully your account is verified");
        }else{
            m.addAttribute("msg","may be your verification code is incorrect or already verified");
        }


        return "message";
    }



}

package com.foodrescue.controller;
import com.foodrescue.service.UserService; import java.security.Principal; import org.springframework.stereotype.Controller; import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.mvc.support.RedirectAttributes;
/** Profile & Settings for every role (donor, NGO, admin). */
@Controller @RequestMapping("/profile")
public class ProfileController {
  private final UserService users; public ProfileController(UserService u){users=u;}
  @GetMapping public String view(Model m,Principal p){ m.addAttribute("user",users.byEmail(p.getName())); return "profile"; }
  @PostMapping public String save(@RequestParam String name,@RequestParam String phone,@RequestParam String location,@RequestParam(required=false) String orgName,Principal p,RedirectAttributes ra){
    if(name.isBlank()||phone.isBlank()||location.isBlank()) ra.addFlashAttribute("error","Name, phone and location are required.");
    else{ users.updateProfile(p.getName(),name,phone,location,orgName); ra.addFlashAttribute("success","Profile updated."); }
    return "redirect:/profile"; }
  @PostMapping("/password") public String password(@RequestParam String current,@RequestParam String next,Principal p,RedirectAttributes ra){
    try{ users.changePassword(p.getName(),current,next); ra.addFlashAttribute("success","Password changed."); }
    catch(IllegalArgumentException e){ ra.addFlashAttribute("error",e.getMessage()); } return "redirect:/profile"; }
}

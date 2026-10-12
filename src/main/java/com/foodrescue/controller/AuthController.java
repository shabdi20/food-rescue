package com.foodrescue.controller;
import com.foodrescue.dto.RegisterForm; import com.foodrescue.service.UserService; import jakarta.validation.Valid;
import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
public class AuthController {
  private final UserService users; public AuthController(UserService u){users=u;}
  @GetMapping("/") public String home(){ return "index"; }
  @GetMapping("/about") public String about(){ return "about"; }
  @GetMapping("/how-it-works") public String how(){ return "how-it-works"; }
  @GetMapping("/impact") public String impact(){ return "impact"; }
  @GetMapping("/login") public String login(){ return "login"; }
  @GetMapping("/register") public String reg(Model m){ m.addAttribute("form",new RegisterForm()); return "register"; }
  @PostMapping("/register") public String doReg(@Valid @ModelAttribute("form") RegisterForm f, BindingResult br, RedirectAttributes ra){
    if(!f.getPassword().equals(f.getConfirmPassword())) br.rejectValue("confirmPassword",null,"Passwords do not match");
    if(br.hasErrors()) return "register";
    try{ users.register(f); }catch(IllegalArgumentException e){ br.rejectValue("email",null,e.getMessage()); return "register"; }
    ra.addFlashAttribute("success","Account created. Please sign in."); return "redirect:/login"; }
}

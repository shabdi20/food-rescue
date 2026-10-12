package com.foodrescue.controller;
import com.foodrescue.entity.*; import com.foodrescue.repository.*; import com.foodrescue.service.*;
import java.security.Principal; import org.springframework.stereotype.Controller; import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller @RequestMapping("/organization")
public class OrganizationController {
  private final UserService users; private final DonationService ds; private final OrganizationRepository orgs; private final PickupRepository pickups;
  public OrganizationController(UserService u,DonationService d,OrganizationRepository o,PickupRepository p){users=u;ds=d;orgs=o;pickups=p;}
  @GetMapping("/dashboard") public String dash(@RequestParam(required=false) String q,Model m,Principal p){
    User u=users.byEmail(p.getName()); m.addAttribute("user",u); m.addAttribute("food",ds.available(q)); m.addAttribute("q",q);
    m.addAttribute("pickups",pickups.findByOrganization(orgs.findByUser(u).orElseThrow())); return "organization/dashboard"; }
  @PostMapping("/food/{id}/claim") public String claim(@PathVariable Long id,Principal p,RedirectAttributes ra){
    try{ ds.claim(id,users.byEmail(p.getName())); ra.addFlashAttribute("success","Food claimed. Pickup created."); }
    catch(IllegalStateException|IllegalArgumentException e){ ra.addFlashAttribute("error",e.getMessage()); }
    return "redirect:/organization/dashboard"; }
  @PostMapping("/pickups/{id}/advance") public String advance(@PathVariable Long id){ ds.advance(id); return "redirect:/organization/dashboard"; }
  @GetMapping("/food/{id}") public String details(@PathVariable Long id,Model m,Principal p){ m.addAttribute("user",users.byEmail(p.getName())); m.addAttribute("d",ds.find(id)); return "organization/food-details"; }
}

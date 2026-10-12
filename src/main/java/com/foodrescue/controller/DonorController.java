package com.foodrescue.controller;
import com.foodrescue.dto.DonationForm; import com.foodrescue.entity.*; import com.foodrescue.repository.*; import com.foodrescue.service.*;
import jakarta.validation.Valid; import java.security.Principal; import org.springframework.stereotype.Controller; import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller @RequestMapping("/donor")
public class DonorController {
  private final UserService users; private final DonationService ds; private final DonationRepository repo; private final PickupRepository pickups;
  public DonorController(UserService u,DonationService d,DonationRepository r,PickupRepository p){users=u;ds=d;repo=r;pickups=p;}
  @GetMapping("/dashboard") public String dash(Model m,Principal p){ User u=users.byEmail(p.getName()); var list=repo.findByDonorOrderByCreatedAtDesc(u);
    m.addAttribute("user",u); m.addAttribute("donations",list); m.addAttribute("requests",pickups.findByDonationDonor(u));
    m.addAttribute("active",list.stream().filter(d->d.getStatus()==Status.AVAILABLE).count());
    m.addAttribute("done",list.stream().filter(d->d.getStatus()==Status.COMPLETED).count()); return "donor/dashboard"; }
  @GetMapping("/donations") public String all(@RequestParam(required=false) String status,@RequestParam(required=false) String q,Model m,Principal p){
    User u=users.byEmail(p.getName()); m.addAttribute("user",u); m.addAttribute("status",status); m.addAttribute("q",q);
    m.addAttribute("donations",repo.findByDonorOrderByCreatedAtDesc(u).stream()
      .filter(d->status==null||status.isBlank()||d.getStatus().name().equals(status))
      .filter(d->q==null||d.getFoodName().toLowerCase().contains(q.toLowerCase())).toList()); return "donor/donations"; }
  @GetMapping("/donations/new") public String form(Model m,Principal p){ m.addAttribute("user",users.byEmail(p.getName())); m.addAttribute("form",new DonationForm()); return "donor/add-donation"; }
  @PostMapping("/donations") public String save(@Valid @ModelAttribute("form") DonationForm f,BindingResult br,@RequestParam(required=false) String draft,Principal p,Model m,RedirectAttributes ra){
    User u=users.byEmail(p.getName());
    if(!br.hasErrors()){ try{ ds.create(u,f,draft!=null); ra.addFlashAttribute("success",draft!=null?"Draft saved.":"Donation published successfully."); return "redirect:/donor/donations"; }
      catch(IllegalArgumentException e){ br.rejectValue("image",null,e.getMessage()); } }
    m.addAttribute("user",u); return "donor/add-donation"; }
  @GetMapping("/donations/{id}") public String details(@PathVariable Long id,Model m,Principal p){ User u=users.byEmail(p.getName());
    m.addAttribute("user",u); m.addAttribute("d",ds.owned(id,u)); m.addAttribute("requests",pickups.findByDonationDonor(u).stream().filter(r->r.getDonation().getId().equals(id)).toList()); return "donor/donation-details"; }
  @PostMapping("/donations/{id}/publish") public String publish(@PathVariable Long id,Principal p,RedirectAttributes ra){
    ds.publish(id,users.byEmail(p.getName())); ra.addFlashAttribute("success","Donation published."); return "redirect:/donor/donations"; }
  @GetMapping("/requests") public String requests(@RequestParam(required=false) String status,Model m,Principal p){ User u=users.byEmail(p.getName());
    m.addAttribute("user",u); m.addAttribute("status",status);
    m.addAttribute("requests",pickups.findByDonationDonor(u).stream().filter(r->status==null||status.isBlank()||r.getStatus().name().equals(status)).toList()); return "donor/requests"; }
  @PostMapping("/requests/{id}/accept") public String accept(@PathVariable Long id,Principal p,RedirectAttributes ra){
    ds.accept(id,users.byEmail(p.getName())); ra.addFlashAttribute("success","Request accepted. Pickup scheduled."); return "redirect:/donor/requests"; }
  @PostMapping("/requests/{id}/reject") public String reject(@PathVariable Long id,Principal p,RedirectAttributes ra){
    ds.reject(id,users.byEmail(p.getName())); ra.addFlashAttribute("success","Request rejected. Food is available again."); return "redirect:/donor/requests"; }
}

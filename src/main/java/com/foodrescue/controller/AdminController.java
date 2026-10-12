package com.foodrescue.controller;
import com.foodrescue.entity.*; import com.foodrescue.repository.*;
import java.time.YearMonth; import java.time.format.TextStyle; import java.time.temporal.ChronoUnit; import java.util.*;
import org.springframework.data.domain.Sort; import org.springframework.http.*; import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.web.bind.annotation.*;
@Controller @RequestMapping("/admin")
public class AdminController {
  private final UserRepository users; private final DonationRepository donations; private final PickupRepository pickups;
  public AdminController(UserRepository u,DonationRepository d,PickupRepository p){users=u;donations=d;pickups=p;}
  /** Fills the model with the numbers and chart data used by dashboard and reports. */
  private void stats(Model m){
    var all=donations.findAll(Sort.by(Sort.Direction.DESC,"createdAt")); long total=all.size();
    long done=all.stream().filter(d->d.getStatus()==Status.COMPLETED).count(), pend=all.stream().filter(d->d.getStatus()==Status.AVAILABLE).count();
    int a=total==0?0:(int)(done*100/total), b=total==0?0:(int)((done+pend)*100/total);
    m.addAttribute("donutStyle",total==0?"#cfd8dc":"conic-gradient(#2e7d32 0 "+a+"%,#f4a261 "+a+"% "+b+"%,#cfd8dc "+b+"% 100%)");
    m.addAttribute("total",total); m.addAttribute("completed",done); m.addAttribute("pending",pend);
    m.addAttribute("rescued",all.stream().filter(d->d.getStatus()==Status.COMPLETED).mapToInt(Donation::getQuantity).sum());
    long u=users.count(), dn=users.countByRole(Role.DONOR), ng=users.countByRole(Role.ORGANIZATION);
    m.addAttribute("totalUsers",u); m.addAttribute("donors",dn); m.addAttribute("ngos",ng);
    m.addAttribute("pctDonors",u==0?0:dn*100/u); m.addAttribute("pctNgos",u==0?0:ng*100/u); m.addAttribute("pctAdmins",u==0?0:(u-dn-ng)*100/u);
    m.addAttribute("recent",all.stream().limit(4).toList());
    YearMonth now=YearMonth.now(); int[] c=new int[6];
    for(Donation d:all){ long diff=ChronoUnit.MONTHS.between(YearMonth.from(d.getCreatedAt()),now); if(diff>=0&&diff<6) c[5-(int)diff]++; }
    int max=Math.max(1,Arrays.stream(c).max().getAsInt()); StringBuilder pts=new StringBuilder(); List<String> labels=new ArrayList<>();
    for(int i=0;i<6;i++){ pts.append(10+i*56).append(',').append(String.format(Locale.ROOT,"%.1f",90-c[i]*75.0/max)).append(' ');
      labels.add(now.minusMonths(5-i).getMonth().getDisplayName(TextStyle.SHORT,Locale.ENGLISH)); }
    m.addAttribute("linePts",pts.toString().trim()); m.addAttribute("areaPts",pts+"290,90 10,90"); m.addAttribute("monthLabels",labels); }
  @GetMapping("/dashboard") public String dash(Model m){ stats(m); m.addAttribute("totalDonations",donations.count()); return "admin/dashboard"; }
  @GetMapping("/reports") public String reports(Model m){ stats(m); return "admin/reports"; }
  @GetMapping("/users") public String users(@RequestParam(required=false) String q,Model m){ m.addAttribute("q",q);
    m.addAttribute("users",users.findAll().stream().filter(u->q==null||(u.getName()+u.getEmail()).toLowerCase().contains(q.toLowerCase())).toList()); return "admin/users"; }
  @GetMapping("/donations") public String donations(@RequestParam(required=false) String status,@RequestParam(required=false) String q,Model m){
    m.addAttribute("status",status); m.addAttribute("q",q);
    m.addAttribute("donations",donations.findAll().stream().filter(d->status==null||status.isBlank()||d.getStatus().name().equals(status))
      .filter(d->q==null||d.getFoodName().toLowerCase().contains(q.toLowerCase())).toList()); return "admin/donations"; }
  @GetMapping("/requests") public String requests(@RequestParam(required=false) String q,Model m){ m.addAttribute("q",q);
    m.addAttribute("pickups",pickups.findAll().stream().filter(p->q==null||p.getDonation().getFoodName().toLowerCase().contains(q.toLowerCase())).toList()); return "admin/requests"; }
  @GetMapping("/export") @ResponseBody public ResponseEntity<String> export(){
    StringBuilder sb=new StringBuilder("id,food,donor,quantity,unit,location,status\n");
    for(Donation d:donations.findAll()) sb.append(d.getId()).append(',').append(csv(d.getFoodName())).append(',').append(csv(d.getDonor().getOrgName()))
      .append(',').append(d.getQuantity()).append(',').append(csv(d.getUnit())).append(',').append(csv(d.getPickupLocation())).append(',').append(d.getStatus()).append('\n');
    return ResponseEntity.ok().header("Content-Disposition","attachment; filename=donations.csv").contentType(MediaType.parseMediaType("text/csv")).body(sb.toString()); }
  private String csv(String s){ return "\""+(s==null?"":s.replace("\"","\"\""))+"\""; }
}

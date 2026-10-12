package com.foodrescue.service;
import com.foodrescue.dto.RegisterForm; import com.foodrescue.entity.*; import com.foodrescue.repository.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException; import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
@Service
public class UserService implements UserDetailsService {
  private final UserRepository users; private final OrganizationRepository orgs; private final PasswordEncoder enc;
  public UserService(UserRepository u, OrganizationRepository o, PasswordEncoder e){users=u;orgs=o;enc=e;}
  @Transactional public User register(RegisterForm f){
    if(users.existsByEmail(f.getEmail())) throw new IllegalArgumentException("An account with this email already exists");
    User u=new User(); u.setName(f.getName()); u.setEmail(f.getEmail()); u.setPhone(f.getPhone());
    u.setLocation(f.getLocation()); u.setOrgName(f.getOrgName()); u.setPassword(enc.encode(f.getPassword()));
    u.setRole(Role.valueOf(f.getRole())); users.save(u);
    if(u.getRole()==Role.ORGANIZATION){ Organization o=new Organization(); o.setUser(u);
      o.setOrganizationName(f.getOrgName()); o.setAddress(f.getLocation()); o.setContactNumber(f.getPhone()); orgs.save(o);}
    return u; }
  public User byEmail(String e){ return users.findByEmail(e).orElseThrow(); }
  @Transactional public void updateProfile(String email,String name,String phone,String location,String orgName){
    User u=byEmail(email); u.setName(name); u.setPhone(phone); u.setLocation(location); u.setOrgName(orgName); users.save(u);
    orgs.findByUser(u).ifPresent(o->{ o.setOrganizationName(orgName); o.setContactNumber(phone); o.setAddress(location); orgs.save(o); }); }
  @Transactional public void changePassword(String email,String current,String next){
    User u=byEmail(email); if(!enc.matches(current,u.getPassword())) throw new IllegalArgumentException("Current password is incorrect");
    if(next==null||next.length()<8) throw new IllegalArgumentException("New password must be at least 8 characters");
    u.setPassword(enc.encode(next)); users.save(u); }
  @Override public UserDetails loadUserByUsername(String email){
    User u=users.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("Not found"));
    return org.springframework.security.core.userdetails.User.withUsername(u.getEmail()).password(u.getPassword()).roles(u.getRole().name()).build(); }
}

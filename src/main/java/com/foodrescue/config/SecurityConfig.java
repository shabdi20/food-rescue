package com.foodrescue.config;
import org.springframework.context.annotation.*; import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class SecurityConfig {
  @Bean PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }
  @Bean SecurityFilterChain chain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(a->a
        .requestMatchers("/","/login","/register","/css/**","/js/**","/images/**","/uploads/**","/about","/how-it-works","/impact").permitAll()
        .requestMatchers("/donor/**").hasRole("DONOR")
        .requestMatchers("/organization/**").hasRole("ORGANIZATION")
        .requestMatchers("/admin/**").hasRole("ADMIN").anyRequest().authenticated())
      .formLogin(f->f.loginPage("/login").usernameParameter("email")
        .successHandler((req,res,auth)->{ String r=auth.getAuthorities().iterator().next().getAuthority();
          res.sendRedirect(switch(r){ case "ROLE_ADMIN"->"/admin/dashboard"; case "ROLE_ORGANIZATION"->"/organization/dashboard"; default->"/donor/dashboard"; }); })
        .failureUrl("/login?error"))
      .logout(l->l.logoutSuccessUrl("/")).exceptionHandling(e->e.accessDeniedPage("/login?denied"));
    return http.build(); }
}

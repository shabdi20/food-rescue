package com.foodrescue.config;
import com.foodrescue.dto.DonationForm;
import com.foodrescue.dto.RegisterForm;
import com.foodrescue.entity.Donation;
import com.foodrescue.entity.Role;
import com.foodrescue.entity.User; import com.foodrescue.repository.*; import com.foodrescue.service.*;
import java.time.LocalDateTime; import org.springframework.boot.CommandLineRunner; import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
/** Sample data inserted once when the users table is empty. Every password: Password@123 */
@Configuration
public class DataSeeder {
  @Bean CommandLineRunner seed(UserRepository users, UserService us, DonationService ds, DonationRepository dr, PasswordEncoder enc){ return a->{
    if(users.count()>0) return;
    User admin=new User(); admin.setName("Aarav Desai"); admin.setEmail("admin@foodrescue.org"); admin.setPassword(enc.encode("Password@123"));
    admin.setRole(Role.ADMIN); admin.setPhone("+91 98765 43210"); admin.setLocation("Bengaluru"); users.save(admin);
    User donor=us.register(form("Ananya Gupta","donor@foodrescue.org","DONOR","Green Table Café"));
    User ngo=us.register(form("Hope Community","ngo@foodrescue.org","ORGANIZATION","Hope Community Kitchen"));
    String[][] items={{"Fresh vegetable meals","24","Boxes","Koramangala"},{"Rice & lentil portions","18","Trays","Indiranagar"},{"Bakery assortment","12","kg","HSR Layout"},{"Bananas and apples","30","kg","Jayanagar"}};
    for(String[] i:items){ DonationForm f=new DonationForm(); f.setFoodName(i[0]); f.setQuantity(Integer.parseInt(i[1])); f.setUnit(i[2]);
      f.setPickupLocation(i[3]); f.setBestBefore(LocalDateTime.now().plusHours(8)); f.setDescription("Freshly prepared today."); ds.create(donor,f); }
    var all=dr.findAll(); ds.claim(all.get(1).getId(),ngo); var p=ds.claim(all.get(3).getId(),ngo); ds.advance(p.getId()); ds.advance(p.getId());
  };}
  private RegisterForm form(String n,String e,String r,String org){ RegisterForm f=new RegisterForm(); f.setName(n); f.setEmail(e); f.setRole(r);
    f.setOrgName(org); f.setPassword("Password@123"); f.setConfirmPassword("Password@123"); f.setPhone("+91 98765 43210"); f.setLocation("Bengaluru"); return f; }
}

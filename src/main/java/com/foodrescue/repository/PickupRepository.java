package com.foodrescue.repository;
import com.foodrescue.entity.*; import java.util.List; import org.springframework.data.jpa.repository.JpaRepository;
public interface PickupRepository extends JpaRepository<Pickup,Long>{ List<Pickup> findByOrganization(Organization o); List<Pickup> findByDonationDonor(User d);}

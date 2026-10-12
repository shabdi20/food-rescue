package com.foodrescue.repository;
import com.foodrescue.entity.*; import java.util.List; import org.springframework.data.jpa.repository.JpaRepository;
public interface DonationRepository extends JpaRepository<Donation,Long>{
  List<Donation> findByDonorOrderByCreatedAtDesc(User d);
  List<Donation> findByStatusAndFoodNameContainingIgnoreCase(Status s,String q);
  long countByStatus(Status s);}

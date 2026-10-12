package com.foodrescue.repository;
import com.foodrescue.entity.*; import java.util.Optional; import org.springframework.data.jpa.repository.JpaRepository;
public interface OrganizationRepository extends JpaRepository<Organization,Long>{ Optional<Organization> findByUser(User u);}

package com.foodrescue.repository;
import com.foodrescue.entity.*; import java.util.Optional; import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User,Long>{ Optional<User> findByEmail(String e); boolean existsByEmail(String e); long countByRole(Role r);}

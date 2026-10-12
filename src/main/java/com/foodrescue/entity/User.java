package com.foodrescue.entity;
import jakarta.persistence.*; import java.time.LocalDateTime; import lombok.*;
@Entity @Table(name="users") @Getter @Setter @NoArgsConstructor
public class User {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  private String name;
  @Column(unique=true,nullable=false) private String email;
  private String password; // BCrypt hash
  @Enumerated(EnumType.STRING) private Role role;
  private String phone, location, orgName;
  private LocalDateTime createdAt = LocalDateTime.now();
}

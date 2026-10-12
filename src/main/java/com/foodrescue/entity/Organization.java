package com.foodrescue.entity;
import jakarta.persistence.*; import lombok.*;
@Entity @Getter @Setter @NoArgsConstructor
public class Organization {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @OneToOne private User user;
  private String organizationName, address, contactNumber;
  private String verificationStatus = "VERIFIED";
}

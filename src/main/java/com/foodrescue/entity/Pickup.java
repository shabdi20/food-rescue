package com.foodrescue.entity;
import jakarta.persistence.*; import java.time.LocalDateTime; import lombok.*;
@Entity @Getter @Setter @NoArgsConstructor
public class Pickup {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional=false) private Donation donation;
  @ManyToOne(optional=false) private Organization organization;
  private LocalDateTime pickupDate = LocalDateTime.now().plusHours(2);
  @Enumerated(EnumType.STRING) private Status status = Status.CLAIMED;
}

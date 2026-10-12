package com.foodrescue.entity;
import jakarta.persistence.*; import java.time.LocalDateTime; import lombok.*;
@Entity @Getter @Setter @NoArgsConstructor
public class Donation {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional=false) private User donor;
  private String contactPerson, contactNumber, fullAddress, imagePath;
  private String foodName, category, unit, pickupLocation;
  private int quantity;
  @Column(length=1000) private String description;
  private LocalDateTime bestBefore;
  @Enumerated(EnumType.STRING) private Status status = Status.AVAILABLE;
  private LocalDateTime createdAt = LocalDateTime.now();
}

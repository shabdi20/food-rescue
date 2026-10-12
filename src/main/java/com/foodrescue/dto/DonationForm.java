package com.foodrescue.dto;
import jakarta.validation.constraints.*; import java.time.LocalDateTime; import lombok.*; import org.springframework.web.multipart.MultipartFile;
import org.springframework.format.annotation.DateTimeFormat;
@Getter @Setter
public class DonationForm {
  @NotBlank private String foodName;
  @NotBlank private String category = "Prepared meals";
  @Min(value=1,message="Quantity must be at least 1") private int quantity;
  @NotBlank private String unit = "Boxes";
  private String description;
  @NotNull @Future(message="Deadline must be in the future") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) private LocalDateTime bestBefore;
  @NotBlank private String pickupLocation;
  private String contactPerson, contactNumber, fullAddress; private MultipartFile image;
}

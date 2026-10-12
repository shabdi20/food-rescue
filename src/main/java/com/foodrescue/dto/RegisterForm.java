package com.foodrescue.dto;
import jakarta.validation.constraints.*; import lombok.*;
@Getter @Setter
public class RegisterForm {
  @NotBlank private String name;
  @NotBlank @Email private String email;
  @NotBlank @Size(min=8,message="At least 8 characters") private String password;
  @NotBlank private String confirmPassword;
  @NotBlank private String phone;
  @NotBlank private String location;
  private String orgName;
  @NotBlank private String role = "DONOR";
  @AssertTrue(message="You must accept the terms") private boolean terms;
}

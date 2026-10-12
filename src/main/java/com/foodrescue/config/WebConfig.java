package com.foodrescue.config;
import org.springframework.context.annotation.Configuration; import org.springframework.web.servlet.config.annotation.*;
/** Serves uploaded food images from the ./uploads folder. */
@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override public void addResourceHandlers(ResourceHandlerRegistry r){ r.addResourceHandler("/uploads/**").addResourceLocations("file:uploads/"); }
}

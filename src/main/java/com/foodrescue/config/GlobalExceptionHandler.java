package com.foodrescue.config;
import org.springframework.dao.DataAccessException; import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException; import org.springframework.web.servlet.ModelAndView;
/** Turns exceptions into a friendly error page (never a stack trace). */
@ControllerAdvice
public class GlobalExceptionHandler {
  private ModelAndView page(HttpStatus s,String msg){ ModelAndView mv=new ModelAndView("error"); mv.setStatus(s); mv.addObject("status",s.value()); mv.addObject("message",msg); return mv; }
  @ExceptionHandler({IllegalArgumentException.class,IllegalStateException.class}) ModelAndView bad(RuntimeException e){ return page(HttpStatus.BAD_REQUEST,e.getMessage()); }
  @ExceptionHandler(SecurityException.class) ModelAndView denied(SecurityException e){ return page(HttpStatus.FORBIDDEN,"You are not allowed to do that."); }
  @ExceptionHandler(MaxUploadSizeExceededException.class) ModelAndView big(){ return page(HttpStatus.BAD_REQUEST,"Image must be 5 MB or smaller."); }
  @ExceptionHandler(DataAccessException.class) ModelAndView db(){ return page(HttpStatus.INTERNAL_SERVER_ERROR,"A database problem occurred. Please try again shortly."); }
}

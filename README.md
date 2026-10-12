# Food Rescue (Spring Boot + Thymeleaf + MySQL)
Run: set DB password in `application.properties`, then `mvn spring-boot:run`, open http://localhost:8080
Seed logins (password `Password@123`): admin@foodrescue.org, donor@foodrescue.org, ngo@foodrescue.org
Flow: Controller -> Service -> Repository -> MySQL. Status: AVAILABLE -> CLAIMED -> PICKUP_SCHEDULED -> COMPLETED.
NOTE: schema changed (DRAFT status, contact/image columns). If you ran an earlier version, `DROP DATABASE food_rescue;` once before starting.
Optional: put a photo at src/main/resources/static/images/hero.jpg for the landing/about image. Uploaded food images are stored in ./uploads.

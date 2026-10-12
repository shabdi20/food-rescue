package com.foodrescue.service;
import com.foodrescue.dto.DonationForm; import com.foodrescue.entity.*; import com.foodrescue.repository.*;
import java.io.IOException; import java.nio.file.*; import java.util.*;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import org.springframework.web.multipart.MultipartFile;
@Service
public class DonationService {
  private final DonationRepository donations; private final PickupRepository pickups; private final OrganizationRepository orgs;
  public DonationService(DonationRepository d, PickupRepository p, OrganizationRepository o){donations=d;pickups=p;orgs=o;}
  public Donation create(User donor, DonationForm f){ return create(donor,f,false); }
  /** draft=true saves as DRAFT (hidden from NGOs) instead of AVAILABLE. */
  public Donation create(User donor, DonationForm f, boolean draft){
    Donation d=new Donation(); d.setDonor(donor); d.setFoodName(f.getFoodName()); d.setCategory(f.getCategory());
    d.setQuantity(f.getQuantity()); d.setUnit(f.getUnit()); d.setDescription(f.getDescription());
    d.setBestBefore(f.getBestBefore()); d.setPickupLocation(f.getPickupLocation()); d.setFullAddress(f.getFullAddress());
    d.setContactPerson(f.getContactPerson()); d.setContactNumber(f.getContactNumber());
    d.setStatus(draft?Status.DRAFT:Status.AVAILABLE); d.setImagePath(saveImage(f.getImage())); return donations.save(d); }
  private String saveImage(MultipartFile file){
    if(file==null||file.isEmpty()) return null; String ct=file.getContentType();
    if(!"image/png".equals(ct)&&!"image/jpeg".equals(ct)) throw new IllegalArgumentException("Only PNG or JPG images are allowed");
    String name=UUID.randomUUID()+("image/png".equals(ct)?".png":".jpg");
    try{ Path dir=Path.of("uploads"); Files.createDirectories(dir); file.transferTo(dir.resolve(name).toAbsolutePath()); }
    catch(IOException e){ throw new IllegalStateException("Could not store the image"); } return name; }
  public Donation find(Long id){ return donations.findById(id).orElseThrow(()->new IllegalArgumentException("Donation not found")); }
  public Donation owned(Long id, User donor){ Donation d=find(id);
    if(!d.getDonor().getId().equals(donor.getId())) throw new SecurityException("You cannot access this donation"); return d; }
  public List<Donation> available(String q){ return donations.findByStatusAndFoodNameContainingIgnoreCase(Status.AVAILABLE, q==null?"":q); }
  @Transactional public void publish(Long id, User donor){ Donation d=owned(id,donor);
    if(d.getStatus()!=Status.DRAFT) throw new IllegalStateException("Only drafts can be published"); d.setStatus(Status.AVAILABLE); donations.save(d); }
  /** AVAILABLE -> CLAIMED; creates the Pickup linking donation and organization. */
  @Transactional public Pickup claim(Long donationId, User orgUser){
    Donation d=find(donationId);
    if(d.getStatus()!=Status.AVAILABLE) throw new IllegalStateException("This food has already been claimed");
    Organization o=orgs.findByUser(orgUser).orElseThrow(); d.setStatus(Status.CLAIMED); donations.save(d);
    Pickup p=new Pickup(); p.setDonation(d); p.setOrganization(o); return pickups.save(p); }
  /** CLAIMED -> PICKUP_SCHEDULED -> COMPLETED, keeping donation and pickup in sync. */
  @Transactional public void advance(Long pickupId){
    Pickup p=pickups.findById(pickupId).orElseThrow(()->new IllegalArgumentException("Pickup not found"));
    Status next=switch(p.getStatus()){ case CLAIMED->Status.PICKUP_SCHEDULED; case PICKUP_SCHEDULED->Status.COMPLETED; default->p.getStatus(); };
    p.setStatus(next); p.getDonation().setStatus(next); pickups.save(p); donations.save(p.getDonation()); }
  private Pickup ownedPickup(Long id, User donor){ Pickup p=pickups.findById(id).orElseThrow(()->new IllegalArgumentException("Request not found"));
    if(!p.getDonation().getDonor().getId().equals(donor.getId())) throw new SecurityException("Not your request"); return p; }
  /** Donor accepts a pending request: CLAIMED -> PICKUP_SCHEDULED. */
  @Transactional public void accept(Long pickupId, User donor){ if(ownedPickup(pickupId,donor).getStatus()==Status.CLAIMED) advance(pickupId); }
  /** Donor rejects a pending request: donation goes back to AVAILABLE and the pickup is removed. */
  @Transactional public void reject(Long pickupId, User donor){ Pickup p=ownedPickup(pickupId,donor);
    if(p.getStatus()!=Status.CLAIMED) throw new IllegalStateException("Only pending requests can be rejected");
    p.getDonation().setStatus(Status.AVAILABLE); donations.save(p.getDonation()); pickups.delete(p); }
}

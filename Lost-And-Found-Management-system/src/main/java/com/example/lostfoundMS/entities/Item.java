package com.example.lostfoundMS.entities;

import com.example.lostfoundMS.entities.enums.CustodyStatus;
import com.example.lostfoundMS.entities.enums.ItemModerationStatus;
import com.example.lostfoundMS.entities.enums.ItemStatus;
import com.example.lostfoundMS.entities.enums.ItemType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Item name is required")
    @Size(min = 2, max = 150, message = "Item name must be between 2 and 150 characters")
    @Column(nullable = false)
    private String name;

    // Public-facing description shown on the Bulletin Board.
    // Never put identifying/sensitive details here (see privateDetails below).
    @NotBlank(message = "Description is required")
    @Size(min = 1, max = 3000, message = "Item description must be between 1 and 3000 characters")
    @Column(nullable = false, length = 3000)
    private String description;

    // Privacy Guard: sensitive/identifying detail used ONLY to verify a claimant.
    // Never rendered on the public bulletin board.
    @Size(max = 1000, message = "Private detail must be under 1000 characters")
    @Column(length = 1000)
    private String privateDetails;

    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Enter a valid phone number for guest contact"
    )
    private String guestPhone;

    @NotBlank(message = "Category is required")
    @Column(nullable = false)
    private String category;

    @NotNull(message = "Item type (LOST or FOUND) is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType type;

    private String imageUrl;      // Cloudinary secure_url
    private String imagePublicId; // Cloudinary public_id, needed to delete the asset later

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemStatus itemStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemModerationStatus moderationStatus;

    // Only meaningful for FOUND items: where the physical item currently sits
    @Enumerated(EnumType.STRING)
    private CustodyStatus custodyStatus;

    @Column(unique = true)
    private String referenceCode;

    @NotBlank(message = "Location is required")
    @Column(nullable = false)
    private String locationTag;

    @NotNull(message = "Date reported is required")
    @Column(nullable = false)
    private LocalDate dateReported;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // An item can appear as the "lost" side of many candidate matches
    @OneToMany(mappedBy = "lostItem")
    private List<Match> matchesAsLostItem;

    // ...or as the "found" side of many candidate matches
    @OneToMany(mappedBy = "foundItem")
    private List<Match> matchesAsFoundItem;

    public Item() {
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();

        if (this.itemStatus == null) {
            this.itemStatus = ItemStatus.ACTIVE;
        }
        if (this.moderationStatus == null) {
            this.moderationStatus = ItemModerationStatus.PENDING;
        }
        if (this.type == ItemType.FOUND && this.custodyStatus == null) {
            this.custodyStatus = CustodyStatus.WITH_FINDER;
        }

        String prefix = (this.type == ItemType.FOUND) ? "FND" : "LST";
        String year = String.valueOf(LocalDateTime.now().getYear());
        String random = String.format("%05d", (int) (Math.random() * 99999));
        this.referenceCode = prefix + "-" + year + "-" + random;
    }

    // ---------- Getters / setters ----------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrivateDetails() {
        return privateDetails;
    }

    public void setPrivateDetails(String privateDetails) {
        this.privateDetails = privateDetails;
    }

    public String getGuestPhone() {
        return guestPhone;
    }

    public void setGuestPhone(String guestPhone) {
        this.guestPhone = guestPhone;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImagePublicId() {
        return imagePublicId;
    }

    public void setImagePublicId(String imagePublicId) {
        this.imagePublicId = imagePublicId;
    }

    public ItemStatus getStatus() {
        return itemStatus;
    }

    public void setStatus(ItemStatus status) {
        this.itemStatus = status;
    }

    public ItemModerationStatus getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(ItemModerationStatus moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public CustodyStatus getCustodyStatus() {
        return custodyStatus;
    }

    public void setCustodyStatus(CustodyStatus custodyStatus) {
        this.custodyStatus = custodyStatus;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public String getLocationTag() {
        return locationTag;
    }

    public void setLocationTag(String locationTag) {
        this.locationTag = locationTag;
    }

    public LocalDate getDateReported() {
        return dateReported;
    }

    public void setDateReported(LocalDate dateReported) {
        this.dateReported = dateReported;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Match> getMatchesAsLostItem() {
        return matchesAsLostItem;
    }

    public void setMatchesAsLostItem(List<Match> matchesAsLostItem) {
        this.matchesAsLostItem = matchesAsLostItem;
    }

    public List<Match> getMatchesAsFoundItem() {
        return matchesAsFoundItem;
    }

    public void setMatchesAsFoundItem(List<Match> matchesAsFoundItem) {
        this.matchesAsFoundItem = matchesAsFoundItem;
    }
}

package com.example.lostfoundMS.entities.dto;

import com.example.lostfoundMS.entities.enums.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public class ReportItemRequest {

        @NotNull(message = "Specify whether this is a LOST or FOUND item")
        private ItemType type;

        @NotBlank(message = "Item name is required")
        @Size(min = 2, max = 150)
        private String name;

        @NotBlank(message = "Category is required")
        private String category;

        @NotBlank(message = "Description is required")
        @Size(min = 1, max = 3000)
        private String description;

        @Size(max = 1000)
        private String privateDetails;

        @NotBlank(message = "Location is required")
        private String locationTag;

        @NotNull(message = "Date is required")
        private LocalDate dateReported;

        private String guestPhone;

        private MultipartFile photo;

        public ItemType getType() { return type; }
        public void setType(ItemType type) { this.type = type; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getPrivateDetails() { return privateDetails; }
        public void setPrivateDetails(String privateDetails) { this.privateDetails = privateDetails; }

        public String getLocationTag() { return locationTag; }
        public void setLocationTag(String locationTag) { this.locationTag = locationTag; }

        public LocalDate getDateReported() { return dateReported; }
        public void setDateReported(LocalDate dateReported) { this.dateReported = dateReported; }

        public String getGuestPhone() { return guestPhone; }
        public void setGuestPhone(String guestPhone) { this.guestPhone = guestPhone; }

        public MultipartFile getPhoto() { return photo; }
        public void setPhoto(MultipartFile photo) { this.photo = photo; }

}

package com.example.lostfoundMS.services;

import com.example.lostfoundMS.entities.Item;
import com.example.lostfoundMS.entities.dto.ReportItemRequest;
import com.example.lostfoundMS.entities.enums.ItemModerationStatus;
import com.example.lostfoundMS.entities.enums.ItemStatus;
import com.example.lostfoundMS.entities.enums.ItemType;
import com.example.lostfoundMS.entities.User;
import com.example.lostfoundMS.repo.ItemRepository;
import com.example.lostfoundMS.utils.CloudinaryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final CloudinaryService cloudinaryService;

    public ItemService(ItemRepository itemRepository, CloudinaryService cloudinaryService) {
        this.itemRepository = itemRepository;
        this.cloudinaryService = cloudinaryService;
    }

    // --- Admin Stats and Dashboard Query Additions ---

    public long countAll() {
        return itemRepository.count();
    }

    public long countByStatus(ItemStatus status) {
        return itemRepository.countByStatus(status);
    }

    public long countByType(ItemType type) {
        return itemRepository.countByType(type);
    }

    public long countByModerationStatus(ItemModerationStatus status) {
        return itemRepository.countByModerationStatus(status);
    }

    public List<Item> findRecent(int limit) {
        // Fetches the most recently reported items sorted descending
        return itemRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "dateReported"))
        ).getContent();
    }

    // --- Original Services ---

    public Item reportItem(ReportItemRequest request, User reporter) throws IOException {
        if (request.getType() == ItemType.FOUND
                && (request.getPhoto() == null || request.getPhoto().isEmpty())) {
            throw new IllegalArgumentException("A photo is required when reporting a found item");
        }

        Item item = new Item();
        item.setUser(reporter);
        item.setType(request.getType());
        item.setName(request.getName());
        item.setCategory(request.getCategory());
        item.setDescription(request.getDescription());
        item.setPrivateDetails(request.getPrivateDetails());
        item.setLocationTag(request.getLocationTag());
        item.setDateReported(request.getDateReported());
        item.setGuestPhone(request.getGuestPhone());

        if(request.getPhoto() != null && !request.getPhoto().isEmpty()) {
            CloudinaryService.UploadResult uploadResult = cloudinaryService.upload(request.getPhoto());
            item.setImageUrl(uploadResult.url());
            item.setImagePublicId(uploadResult.publicId());
        }

        return itemRepository.save(item);
    }

    public List<Item> getPublicBoard() {
        return itemRepository.findByModerationStatusAndItemStatus(
                ItemModerationStatus.APPROVED, ItemStatus.ACTIVE
        );
    }

    public List<Item> getPendingModeration() {
        return itemRepository.findByModerationStatus(ItemModerationStatus.PENDING);
    }

    public Item approveItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        item.setModerationStatus(ItemModerationStatus.APPROVED);
        return itemRepository.save(item);
    }

    public void rejectItem(Long itemId) throws IOException {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        if (item.getImagePublicId() != null) {
            cloudinaryService.delete(item.getImagePublicId());
        }

        item.setModerationStatus(ItemModerationStatus.REJECTED);
        itemRepository.save(item);
    }

    public List<Item> getUserItems(Long userId) {
        return itemRepository.findByUserId(userId);
    }
}
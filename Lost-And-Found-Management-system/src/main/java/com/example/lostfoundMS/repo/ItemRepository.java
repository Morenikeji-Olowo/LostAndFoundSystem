package com.example.lostfoundMS.repo;

import com.example.lostfoundMS.entities.Item;
import com.example.lostfoundMS.entities.enums.ItemModerationStatus;
import com.example.lostfoundMS.entities.enums.ItemStatus;
import com.example.lostfoundMS.entities.enums.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByReferenceCode(String referenceCode);

    // Public bulletin board: only approved + active items
    List<Item> findByModerationStatusAndItemStatus(
        ItemModerationStatus moderationStatus, ItemStatus itemStatus
    );

    // Admin moderation queue
    List<Item> findByModerationStatus(ItemModerationStatus moderationStatus);

    // Candidate pool for the matching job: opposite type, same category, still active
    List<Item> findByTypeAndCategoryAndItemStatus(
        ItemType type, String category, ItemStatus itemStatus
    );

    // A user's own reported items, for their dashboard
    List<Item> findByUserId(Long userId);

    // Auto-archive job: active items older than a cutoff date
    List<Item> findByItemStatusAndDateReportedBefore(ItemStatus itemStatus, LocalDate cutoff);
}

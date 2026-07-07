package com.example.lostfoundMS.repo;

import com.example.lostfoundMS.entities.Item;
import com.example.lostfoundMS.entities.enums.ItemStatus;
import com.example.lostfoundMS.entities.enums.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE i.type = :type")
    List<Item> findByType(@Param("type")ItemType type);

    @Query("SELECT i FROM Item i WHERE i.user.id = :userId")
    List<Item> findByUserId(@Param("userId") Long userId);

    @Query("SELECT i FROM Item i WHERE i.type = :type AND i.itemStatus = :status")
    List<Item> findByTypeAndStatus(@Param("type")ItemType type, @Param("status")ItemStatus status);

    @Query("SELECT i FROM Item i WHERE i.type = :type AND " +
            "(LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Item> searchByKeyword(@Param("type") ItemType type,
                               @Param("keyword") String keyword);

    @Query("SELECT i FROM Item i ORDER BY i.createdAt DESC LIMIT 4")
    List<Item> getTop4Items ();
}
package com.example.lostfoundMS.repo;

import com.example.lostfoundMS.entities.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
//    List<Claim> findByItemId(Long itemId);
    @Query("SELECT c FROM Claim c WHERE c.user.id = :userId")
    List<Claim> findClaimByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Claim c WHERE c.item.id = :itemId")
    List<Claim> findByItemId(@Param("itemId") Long itemId);

    @Query("SELECT c FROM Claim c WHERE c.user.id = :userId AND c.item.id = :itemId")
    Optional<Claim> findByUserIdAndItemId(@Param("userId") Long userId, @Param("itemId") Long itemId);

}
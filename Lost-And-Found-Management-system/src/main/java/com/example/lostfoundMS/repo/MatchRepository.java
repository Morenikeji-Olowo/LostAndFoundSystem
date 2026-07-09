package com.example.lostfoundMS.repo;

import com.example.lostfoundMS.entities.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    // All candidate matches for a given lost item, e.g. to show "possible matches" on its page
    List<Match> findByLostItemId(Long lostItemId);

    // All candidate matches for a given found item
    List<Match> findByFoundItemId(Long foundItemId);

    // Matches not yet notified — used by the notification job
    List<Match> findByNotifiedFalse();

    // Guard against creating the same match twice when the matching job re-runs
    boolean existsByLostItemIdAndFoundItemId(Long lostItemId, Long foundItemId);
}

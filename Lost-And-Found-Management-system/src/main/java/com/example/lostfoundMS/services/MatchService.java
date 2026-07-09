package com.example.lostfoundMS.services;

import com.example.lostfoundMS.entities.Item;
import com.example.lostfoundMS.entities.Match;
import com.example.lostfoundMS.entities.enums.ItemStatus;
import com.example.lostfoundMS.entities.enums.ItemType;
import com.example.lostfoundMS.repo.ItemRepository;
import com.example.lostfoundMS.repo.MatchRepository;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MatchService {
    private static final double CATEGORY_WEIGHT = 0.4;
    private static final double LOCATION_WEIGHT = 0.3;
    private static final double KEYWORD_WEIGHT = 0.3;
    private static final double TIME_WEIGHT = 0.15;
    private static final double MATCH_THRESHOLD = 0.5;

    private final ItemRepository itemRepository;
    private final MatchRepository  matchRepository;

    public MatchService(ItemRepository itemRepository, MatchRepository matchRepository) {
        this.itemRepository = itemRepository;
        this.matchRepository = matchRepository;
    }

    public List<Match> findMatchesFor(Item newItem) {
        ItemType oppositeType = (newItem.getType() == ItemType.FOUND) ? ItemType.LOST : ItemType.FOUND;

        List<Item> candidates = itemRepository.findByTypeAndCategoryAndItemStatus(
                oppositeType, newItem.getCategory(), ItemStatus.ACTIVE
        );
        return candidates.stream()
                .map(candidate -> scoreAndBuildMatch(newItem, candidate))
                .filter(match -> match.getSimilarityScore() >= MATCH_THRESHOLD)
                .filter(match -> !alreadyExists(match))
                .map(matchRepository::save)
                .collect(Collectors.toList());
    }

    private Match scoreAndBuildMatch(Item newItem, Item candidate) {
        double score = 0.0;

        score += CATEGORY_WEIGHT;

        if (candidate.getLocationTag().equalsIgnoreCase(newItem.getLocationTag())) {
            score += LOCATION_WEIGHT;
        }

        double keywordOverlap = keywordOverlapScore(newItem.getDescription(), candidate.getDescription());
        score += keywordOverlap * KEYWORD_WEIGHT;

        double timeProximity = timeProximityScore(newItem, candidate);
        score += timeProximity * TIME_WEIGHT;

        Item lostItem = (newItem.getType() == ItemType.LOST) ? newItem : candidate;
        Item foundItem = (newItem.getType() == ItemType.FOUND) ? newItem : candidate;

        return new Match(lostItem, foundItem, score);
    }

    private double keywordOverlapScore(String descriptionA, String descriptionB) {
        Set<String> wordsA = toWordSet(descriptionA);
        Set<String> wordsB = toWordSet(descriptionB);

        if (wordsA.isEmpty() || wordsB.isEmpty()) {
            return 0.0;
        }

        long sharedWords = wordsA.stream().filter(wordsB::contains).count();
        int smallerSetSize = Math.min(wordsA.size(), wordsB.size());

        return (double) sharedWords / smallerSetSize;
    }

    private Set<String> toWordSet(String text) {
        return Arrays.stream(text.toLowerCase().split("[^a-zA-Z]+"))
                .filter(word -> word.length() > 2) // skip "a", "an", "on", "of" etc.
                .collect(Collectors.toSet());
    }

    private double timeProximityScore(Item newItem, Item candidate) {
        Item lostItem = (newItem.getType() == ItemType.LOST) ? newItem : candidate;
        Item foundItem = (newItem.getType() == ItemType.FOUND) ? newItem : candidate;

        long daysBetween = ChronoUnit.DAYS.between(
                lostItem.getDateReported(), foundItem.getDateReported()
        );

        if (daysBetween < -2) {
            return 0.0;
        }

        // Same day up to 3 days apart - strong signal, full score
        if (daysBetween <= 3) {
            return 1.0;
        }

        // Linear decay from day 3 to day 14 - beyond 2 weeks apart, basically no signal
        if (daysBetween <= 14) {
            return 1.0 - ((daysBetween - 3) / 11.0);
        }

        return 0.0;
    }

    private boolean alreadyExists(Match match) {
        return matchRepository.existsByLostItemIdAndFoundItemId(
                match.getLostItem().getId(), match.getFoundItem().getId()
        );
    }
}

package com.example.lostfoundMS.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "item_matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull(message = "A match must reference a lost item")
    @ManyToOne
    @JoinColumn(name = "lost_item_id", nullable = false)
    private Item lostItem;

    @NotNull(message = "A match must reference a found item")
    @ManyToOne
    @JoinColumn(name = "found_item_id", nullable = false)
    private Item foundItem;

    @NotNull(message = "Similarity score is required")
    @DecimalMin(value = "0.0", message = "Similarity score cannot be negative")
    @DecimalMax(value = "1.0", message = "Similarity score cannot exceed 1.0")
    @Column(nullable = false)
    private double similarityScore; // 0.0 to 1.0

    @Column(nullable = false)
    private boolean notified;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Match() {
    }

    public Match(Item lostItem, Item foundItem, double similarityScore) {
        this.lostItem = lostItem;
        this.foundItem = foundItem;
        this.similarityScore = similarityScore;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // ---------- Getters / setters ----------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Item getLostItem() {
        return lostItem;
    }

    public void setLostItem(Item lostItem) {
        this.lostItem = lostItem;
    }

    public Item getFoundItem() {
        return foundItem;
    }

    public void setFoundItem(Item foundItem) {
        this.foundItem = foundItem;
    }

    public double getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(double similarityScore) {
        this.similarityScore = similarityScore;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

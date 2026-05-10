package com.example.lostfoundMS.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "item_matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lost item
    @ManyToOne
    @JoinColumn(name = "lost_item_id")
    private Item lostItem;

    // Found item
    @ManyToOne
    @JoinColumn(name = "found_item_id")
    private Item foundItem;

    private double similarityScore; // 0.0 to 1.0

    public Match() {}

    public Match(Item lostItem, Item foundItem, double similarityScore) {
        this.lostItem = lostItem;
        this.foundItem = foundItem;
        this.similarityScore = similarityScore;
    }

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
}
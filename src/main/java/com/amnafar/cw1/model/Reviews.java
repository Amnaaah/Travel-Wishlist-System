package com.amnafar.cw1.model;

import jakarta.persistence.*;

@Entity
public class Reviews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String reviewText;
    private int ratedStars;

    // Review belongs to a place
    @ManyToOne
    @JoinColumn(name = "place_id")
    private Places place;

    // Review belongs to a user
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Reviews() {}

    // Getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }

    public int getRatedStars() { return ratedStars; }
    public void setRatedStars(int ratedStars) { this.ratedStars = ratedStars; }

    public Places getPlace() { return place; }
    public void setPlace(Places place) { this.place = place; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}

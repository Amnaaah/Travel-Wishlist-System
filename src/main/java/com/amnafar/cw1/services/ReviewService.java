package com.amnafar.cw1.services;

import com.amnafar.cw1.model.Places;
import com.amnafar.cw1.model.Reviews;
import com.amnafar.cw1.model.User;
import com.amnafar.cw1.repository.PlacesRepository;
import com.amnafar.cw1.repository.ReviewsRepository;
import com.amnafar.cw1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewsRepository reviewsRepository;

    @Autowired
    private PlacesRepository placesRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private PlaceService placeService;

     // Fetch review using ID
    public Reviews getReviewById(int id) {
        return reviewsRepository.findById(id).orElse(null);
    }

    // Save a new review to the database
    public Reviews createReview(int placeId, int userId, String text, int rating) {
        Reviews review = new Reviews();

        review.setReviewText(text);
        review.setRatedStars(rating);

        User user = userService.getUserById(userId);
        Places place = placeService.getPlaceById(placeId);

        review.setUser(user);
        review.setPlace(place);

        // Save & return
        return reviewsRepository.save(review);
    }

    // Update an existing review identified by ID
    public Reviews updateReview(int id, Reviews review) {
        Reviews existing = reviewsRepository.findById(id).orElse(null);
        if (existing != null) { // Update only the text and star rating fields
            existing.setReviewText(review.getReviewText());
            existing.setRatedStars(review.getRatedStars());
            return reviewsRepository.save(existing);
        }
        return null;
    }

    // Get list of all reviews for a specific place by placeId
    public List<Reviews> getReviewsForPlace(int placeId) {
        return reviewsRepository.findByPlaceId(placeId);
    }

    public List<Reviews> getReviewsByUser(int userId) {
        return reviewsRepository.findByUserId(userId);
    }



    // Search places by name (exact or partial) and get all reviews for those places
    public List<Reviews> searchReviewsByPlaceName(String partialName) {
        // Find places whose name contains the partial string (case-insensitive)
        List<Places> places = placeService.getPlacesByPartialName(partialName);

        if (places.isEmpty()) {
            return List.of(); // No places found
        }

        // Extract place IDs as Integer list
        List<Integer> placeIds = places.stream()
                .map(Places::getId)
                .toList();

        // Use Spring Data method findByPlaceIdIn to get reviews
        return reviewsRepository.findByPlaceIdIn(placeIds);
    }

    public List<Reviews> getReviewsByPlaceName(String name) {
        List<Places> places = placesRepository.findByNameIgnoreCase(name);
        List<Integer> placeIds = places.stream()
                .map(Places::getId)
                .collect(Collectors.toList());
        return reviewsRepository.findByPlaceIdIn(placeIds);
    }



    //    // Delete review using ID, returns true if successful
//    public boolean deleteReview(int id) {
//        Reviews existing = reviewsRepository.findById(id).orElse(null);
//        if (existing != null) { // Delete review
//            reviewsRepository.delete(existing);
//            return true;
//        }
//        return false;
//    }


}

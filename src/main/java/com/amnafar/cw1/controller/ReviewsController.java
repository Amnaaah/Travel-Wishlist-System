package com.amnafar.cw1.controller;

// Importing model classes and services
import com.amnafar.cw1.model.Places;
import com.amnafar.cw1.model.Reviews;
import com.amnafar.cw1.model.User;
import com.amnafar.cw1.services.PlaceService;
import com.amnafar.cw1.services.ReviewService;
import com.amnafar.cw1.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/reviews")

@Tag(name = "Reviews Controller", description = "API endpoints for managing reviews")
public class ReviewsController {
    @Autowired
    private ReviewService reviewService;

    //to fetch place info when linking reviews
    @Autowired
    private PlaceService placeService;

    //to fetch user info when linking reviews to users
    @Autowired
    private UserService userService;

    //POST create a new review
    @Operation(
            summary = "Create a new review",
            description = "Create a new review for a place by a user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reviewed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reviews.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid place ID or user ID")
    })
    @PostMapping
    public ResponseEntity<Reviews> createReview(
            @Parameter(description = "ID of the place being reviewed", required = true)
            @RequestParam int placeId,
            @Parameter(description = "ID of the user writing the review", required = true)
            @RequestParam int userId,
            @Parameter(description = "Review data", required = true)
            @RequestBody Reviews review) {

        //gets the place and user objects by their IDs
        Places place = placeService.getPlaceById(placeId);
        User user = userService.getUserById(userId);

        // If the place or user is not found, return HTTP 400 Bad Request
        if (place == null || user == null) return ResponseEntity.badRequest().build();

        //ad this place and user(linking)
        review.setPlace(place);
        review.setUser(user);

        // Calls service to save the review
        Reviews created = reviewService.createReview(
                placeId,
                userId,
                review.getReviewText(),
                review.getRatedStars()
        );


        return ResponseEntity.ok(created);
    }

    //GET reviews by user
    @Operation(
            summary = "Get reviews by user",
            description = "Retrieve all reviews created by a specific user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved reviews by the user",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reviews.class))
            ),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{userId}")
    public List<Reviews> getReviewsByUser(
            @Parameter(description = "ID of the user to get reviews for", required = true)
            @PathVariable int userId) {

        return reviewService.getReviewsByUser(userId);
    }


    // GET search reviews by partial place name
    @Operation(
            summary = "Search reviews by partial place name",
            description = "Find all reviews for places whose name contains the given text (case-insensitive)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved matching reviews",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reviews.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid or missing name parameter")
    })
    @GetMapping("/search")
    public ResponseEntity<List<Reviews>> searchReviewsByPlaceName(
            @Parameter(description = "Partial place name to search for", required = true)
            @RequestParam String name) {

        // Validate input
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Call service to get reviews
        List<Reviews> reviews = reviewService.searchReviewsByPlaceName(name.trim());

        return ResponseEntity.ok(reviews);
    }

    //GET reviews for a place
//    @Operation(
//            summary = "Get reviews for a place",
//            description = "Retrieve all reviews for a specific place"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(
//                    responseCode = "200",
//                    description = "Successfully retrieved reviews for the place",
//                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reviews.class))
//            ),
//            @ApiResponse(responseCode = "404", description = "Place not found")
//    })
//    @GetMapping("/place/{placeId}")
//    public List<Reviews> getReviewsForPlace(
//            @Parameter(description = "Name of the place to get reviews for", required = true)
//            @PathVariable int placeId) {
//        return reviewService.getReviewsForPlace(placeId);
//    }



//    //DELETE a review by ID
//    @Operation(
//            summary = "Delete a review",
//            description = "Delete a review by its ID"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
//            @ApiResponse(responseCode = "404", description = "Review not found")
//    })
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteReview(
//            @Parameter(description = "ID of the review to delete", required = true)
//            @PathVariable int id) {
//
//        // Calls service to delete
//        boolean deleted = reviewService.deleteReview(id);
//
//        // If deleted, return HTTP 204, else 404 Not Found
//        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
//    }



}

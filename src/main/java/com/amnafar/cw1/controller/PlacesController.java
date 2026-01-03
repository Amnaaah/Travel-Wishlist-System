package com.amnafar.cw1.controller;

import com.amnafar.cw1.model.Places;
import com.amnafar.cw1.model.Reviews;
import com.amnafar.cw1.model.User;
import com.amnafar.cw1.services.PlaceService;
import com.amnafar.cw1.services.ReviewService;
import com.amnafar.cw1.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Base64;
import java.util.List;

// to allow request from frontend
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/places")

// Swagger annotation to group this controller in API docs
@Tag(name = "Places Controller", description = "API endpoints for managing places")
public class PlacesController {

    @Autowired
    private PlaceService placeService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewService reviewService;

    //GET all places for a user
    @Operation(
            summary = "Get all places for a user",
            description = "Retrieve all places associated with a specific user ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved places",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Places.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid user ID")
    })
    @GetMapping
    public List<Places> getAllPlacesForUser(
            @Parameter(description = "User ID to get places for", required = true)
            @RequestParam int userId) {
        return placeService.getAllPlacesForUser(userId); //get all places for the logged-in user
    }

    // POST add a new place
    @Operation(
            summary = "Add a new place",
            description = "Create a new place entry for a user with image upload"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Place added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID or request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<String> addPlace(
            @Parameter(description = "User ID who adds the place", required = true)
            @RequestParam int userId,
            @Parameter(description = "Name of the place", required = true)
            @RequestParam String name,
            @Parameter(description = "Country where the place is located", required = true)
            @RequestParam String country,
            @Parameter(description = "City where the place is located", required = true)
            @RequestParam String city,
            @Parameter(description = "Priority level of the trip", required = true)
            @RequestParam String priority,
            @Parameter(description = "Notes about the place", required = true)
            @RequestParam String note,
            @Parameter(description = "Whether the place has been visited", required = true)
            @RequestParam boolean visited,
            @Parameter(description = "Image file for the place", required = true)
            @RequestParam MultipartFile imageFile) {
        try {
            // Fetches the user by ID to associate with this place
            User user = userService.getUserById(userId);
            if (user == null) return ResponseEntity.badRequest().body("Invalid user ID");

            // Creates a new Places object and sets its properties
            Places place = new Places();
            place.setName(name);
            place.setCountry(country);
            place.setCity(city);
            place.setPriority(priority);
            place.setNote(note);
            place.setVisited(visited);

            // Converts uploaded image to Base64 string
            byte[] imageBytes = imageFile.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            place.setImage(base64Image);

            // Adds the User as the foreign key
            place.setUser(user);

            // Saves the new place using the service
            placeService.addPlace(place);
            return ResponseEntity.ok("Saved");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    //DELETE a place by ID
    @Operation(
            summary = "Delete a place",
            description = "Remove a place using its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Place deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Place not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlace(
            @Parameter(description = "ID of the place to delete", required = true)
            @PathVariable int id) {
        // Calls service to delete place by ID and returns the HTTP status
        boolean deleted = placeService.deletePlace(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    //GET filter places
    @Operation(
            summary = "Filter places",
            description = "Filter places by various criteria (visited status, city, country, priority)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully filtered places",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Places.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters")
    })
    @GetMapping("/filter")
    public List<Places> filterPlaces(
            @Parameter(description = "User ID to filter places for", required = true)
            @RequestParam int userId,
            @Parameter(description = "Filter by visited status")
            @RequestParam(required = false) Boolean visited,
            @Parameter(description = "Filter by city name")
            @RequestParam(required = false) String city,
            @Parameter(description = "Filter by country name")
            @RequestParam(required = false) String country,
            @Parameter(description = "Filter by priority level")
            @RequestParam(required = false) String priority) {

        // Depending on which parameter is provided, filter places accordingly
        if (visited != null) return placeService.getPlacesByVisited(userId, visited);
        if (city != null) return placeService.getPlacesByCity(userId, city);
        if (country != null) return placeService.getPlacesByCountry(userId, country);
        if (priority != null) return placeService.getPlacesByPriority(userId, priority);

        // If no filter then return all places
        return placeService.getAllPlacesForUser(userId);
    }

    //PUT update a place
    @Operation(
            summary = "Update a place",
            description = "Update an existing place with new details"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Place updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "404", description = "Place not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}") // Maps HTTP PUT requests to /api/places/{id}
    public ResponseEntity<String> updatePlace(
            @Parameter(description = "ID of the place to update", required = true)
            @PathVariable int id,
            @Parameter(description = "User ID who makes the changes", required = true)
            @RequestParam int userId,
            @Parameter(description = "Updated name", required = true)
            @RequestParam String name,
            @Parameter(description = "Updated country")
            @RequestParam(required = false) String country,
            @Parameter(description = "Updated city")
            @RequestParam(required = false) String city,
            @Parameter(description = "Updated priority level")
            @RequestParam(required = false) String priority,
            @Parameter(description = "Updated rating")
            @RequestParam(required = false) Integer rating,
            @Parameter(description = "Updated notes")
            @RequestParam(required = false) String note,
            @Parameter(description = "Updated visited status")
            @RequestParam(required = false) Boolean visited,
            @Parameter(description = "Updated image file")
            @RequestParam(required = false) MultipartFile imageFile
    ) {
        try {
            // Calls service to update
            placeService.updatePlace(id, userId, name, country, city, priority, rating, note, visited, imageFile);
            return ResponseEntity.ok("Place updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }



    @Operation(
            summary = "Get all reviews for a specific place",
            description = "Retrieves a list of all reviews associated with the given place ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved reviews")
    })
    @GetMapping("/{placeId}/reviews")
    public List<Reviews> getReviewsForPlace(
            @Parameter(description = "ID of the place whose reviews are to be retrieved", required = true)
            @PathVariable int placeId) {
        return reviewService.getReviewsForPlace(placeId);
    }


    @Operation(
            summary = "Add a new review for a specific place",
            description = "Creates a new review by a user for a place, with review text and rating."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review successfully added"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Place or user not found")
    })
    @PostMapping("/{placeId}/reviews")
    public ResponseEntity<?> addReview(
            @Parameter(description = "ID of the place to add the review to", required = true)
            @PathVariable int placeId,

            @Parameter(description = "ID of the user adding the review", required = true)
            @RequestParam int userId,

            @Parameter(description = "Text content of the review", required = true)
            @RequestParam String text,

            @Parameter(description = "Numeric rating for the review (e.g., 1–5)", required = true)
            @RequestParam int rating) {
        reviewService.createReview(placeId, userId, text, rating);
        return ResponseEntity.ok("Review added");
    }


}

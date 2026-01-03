package com.amnafar.cw1.controller;
import com.amnafar.cw1.model.User;
import com.amnafar.cw1.services.PlaceService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "API endpoints for user management and authentication")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PlaceService placeService;

    //POST register new user
    @Operation(
            summary = "Register a new user",
            description = "Create a new user account in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid user data or username already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<User> register(
            @Parameter(description = "User registration data", required = true)
            @RequestBody User user) {
        //Call service to add a new user
        User created = userService.addUser(user);
        // Return created user as response
        return ResponseEntity.ok(created);
    }

    //POST user login
    @Operation(
            summary = "User login",
            description = "Authenticate user with username and password"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<User> login(
            @Parameter(description = "User login credentials (username and password)", required = true)
            @RequestBody User loginData) {
        // Authenticate using username and password
        User found = userService.login(loginData.getUsername(), loginData.getPassword());
        // If found return user, else return 401 Unauthorized
        return found != null ? ResponseEntity.ok(found) : ResponseEntity.status(401).build();
    }

    //GET user by ID
    @Operation(
            summary = "Get user by ID",
            description = "Retrieve a specific user by their ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User found successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(
            @Parameter(description = "ID of the user to retrieve", required = true)
            @PathVariable int id) {
        // Find user by ID
        User user = userService.getUserById(id);
        // If found return 200 OK, else 404 Not Found
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    //PUT update user
    @Operation(
            summary = "Update user",
            description = "Update an existing user's information"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid user data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<User> update(
            @Parameter(description = "ID of the user to update", required = true)
            @PathVariable int id,
            @Parameter(description = "Updated user data", required = true)
            @RequestBody User user) {
        // Update user info using ID
        User updated = userService.updateUser(id, user);
        // Return updated user if successful, else 404
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    //DELETE user by ID
    @Operation(
            summary = "Delete user",
            description = "Delete a user account from the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the user to delete", required = true)
            @PathVariable int id) {
        // Delete user using ID
        boolean deleted = userService.deleteUser(id);
        // If deleted, return 204 No Content else 404 Not Found
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    //GET user stats
    @Operation(
            summary = "Get user statistics",
            description = "Retrieve statistics for a user including wishlist count, travelled places count, and total countries count"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved user statistics",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object", example = "{\"wishlistCount\": 5, \"travelledCount\": 12, \"countriesCount\": 8}"))
            ),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/stats/{userId}")
    public Map<String, Long> getUserStats(
            @Parameter(description = "ID of the user to get statistics for", required = true)
            @PathVariable int userId) {
        // Count places from wishlist
        Long wishlistCount = placeService.getPlacesByVisited(userId, false).stream().count();
        // Count places the user has travelled
        Long travelledCount = placeService.getPlacesByVisited(userId, true).stream().count();
        // Count all countries
        Long countriesCount = placeService.countDistinctCountries();

        // Create a map to hold stats
        Map<String, Long> stats = new HashMap<>();
        stats.put("wishlistCount", wishlistCount);
        stats.put("travelledCount", travelledCount);
        stats.put("countriesCount", countriesCount);

        // Return the stats as JSON
        return stats;
    }

    //    //GET all users
//    @Operation(
//            summary = "Get all users",
//            description = "Retrieve a list of all registered users"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(
//                    responseCode = "200",
//                    description = "Successfully retrieved all users",
//                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
//            )
//    })
//    @GetMapping
//    public List<User> getAllUsers() {
//        // Get a list of all users
//        return userService.getAllUsers();
//    }

}

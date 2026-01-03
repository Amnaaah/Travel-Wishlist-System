package com.amnafar.cw1.services;
import com.amnafar.cw1.model.Places;
import com.amnafar.cw1.repository.PlacesRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;

@Service
public class PlaceService {

    @Autowired
    private PlacesRepository placesRepository;

    // Finds all places linked to the user ID
    public List<Places> getAllPlacesForUser(int userId) {

        return placesRepository.findByUserId(userId);
    }

    // Fetches place by ID
    public Places getPlaceById(int id) {
        return placesRepository.findById(id).orElse(null);
    }

    // Saves new place to the DB
    public Places addPlace(Places place) {
        return placesRepository.save(place);
    }

    // Find existing place by ID
    public Places updatePlace(int id, Places updatedPlace) {
        Places existing = placesRepository.findById(id).orElse(null);
        if (existing != null) { // Update fields
            existing.setName(updatedPlace.getName());
            existing.setCountry(updatedPlace.getCountry());
            existing.setCity(updatedPlace.getCity());
            existing.setPriority(updatedPlace.getPriority());
            existing.setNote(updatedPlace.getNote());
            existing.setVisited(updatedPlace.isVisited());
            existing.setImage(updatedPlace.getImage());
            return placesRepository.save(existing); // Save changes
        }
        return null;
    }

    // Find place first & delete if found
    public boolean deletePlace(int id) {

        Places existing = placesRepository.findById(id).orElse(null);
        if (existing != null) {

            placesRepository.delete(existing);
            return true;
        }
        return false;
    }

    //filter by visited status
    public List<Places> getPlacesByVisited(int userId, boolean visited) {
        return placesRepository.findByUserIdAndVisited(userId, visited);
    }

    // filter by city
    public List<Places> getPlacesByCity(int userId, String city) {
        return placesRepository.findByUserIdAndCity(userId, city);
    }

    // filter by country
    public List<Places> getPlacesByCountry(int userId, String country) {
        return placesRepository.findByUserIdAndCountry(userId, country);
    }

    // filter by country
    public List<Places> getPlacesByPriority(int userId, String priority) {
        return placesRepository.findByUserIdAndPriority(userId, priority);
    }

    // count total visited
    public Long countByVisited(boolean visited) {
        return placesRepository.countByVisited(visited);
    }

    // count total dif countries
    public Long countDistinctCountries() {
        return placesRepository.countDistinctCountry();
    }

    // update with file upload
    @Transactional // Ensures changes are saved in one transaction
    public void updatePlace(int id, int userId, String name, String country, String city, String priority,
                            Integer rating, String note, Boolean visited, MultipartFile imageFile) throws Exception {

        // Find place by ID and throw exception if not found
        Places place = placesRepository.findById(id)
                .orElseThrow(() -> new Exception("Place not found"));

        //making sure that the logged-in user added this place
        if (place.getUser().getId() != userId) {
            throw new Exception("Unauthorized: cannot edit someone else's place");
        }

        // Update fields
        place.setName(name);
        place.setCountry(country);
        place.setCity(city);
        place.setPriority(priority);
        place.setNote(note);
        place.setVisited(visited != null ? visited : false);

        // If a new image is uploaded, convert to Base64 and set
        if (imageFile != null && !imageFile.isEmpty()) {
            byte[] imageBytes = imageFile.getBytes();
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            place.setImage(base64);
        }

        // Save the updated place
        placesRepository.save(place);
    }

    public List<Places> getPlacesByPartialName(String partialName) {
        return placesRepository.findByNameContainingIgnoreCase(partialName);
    }


}

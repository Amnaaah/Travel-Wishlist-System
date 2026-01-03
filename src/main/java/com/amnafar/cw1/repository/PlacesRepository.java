package com.amnafar.cw1.repository;

import com.amnafar.cw1.model.Places;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlacesRepository extends JpaRepository<Places, Integer> {
    // Count for stats
    Long countByVisited(boolean visited);

    @Query("SELECT COUNT(DISTINCT p.country) FROM Places p")
    Long countDistinctCountry();

    // Filter places by user
    List<Places> findByUserId(int userId);

    // Filter places for a user by visited
    List<Places> findByUserIdAndVisited(int userId, boolean visited);

    List<Places> findByUserIdAndCity(int userId, String city);

    List<Places> findByUserIdAndCountry(int userId, String country);

    List<Places> findByUserIdAndPriority(int user_Id, String priority);

    List<Places> findByNameContainingIgnoreCase(String name);

    List<Places> findByNameIgnoreCase(String name);

}


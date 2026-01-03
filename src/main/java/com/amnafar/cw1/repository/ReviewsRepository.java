package com.amnafar.cw1.repository;

import com.amnafar.cw1.model.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewsRepository extends JpaRepository<Reviews, Integer> {
    // Get all reviews for a place
    List<Reviews> findByPlaceId(int placeId);

    // Get all reviews by user
    List<Reviews> findByUserId(int userId);

    List<Reviews> findByPlaceIdIn(List<Integer> placeIds);


}

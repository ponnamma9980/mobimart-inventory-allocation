package com.mobimart.repository;

import com.mobimart.model.AllocationRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllocationRecommendationRepository
        extends JpaRepository<AllocationRecommendation, Long> {
}
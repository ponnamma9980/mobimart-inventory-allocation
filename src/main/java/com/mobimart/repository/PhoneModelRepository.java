package com.mobimart.repository;

import com.mobimart.model.PhoneModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneModelRepository extends JpaRepository<PhoneModel, Long> {
}
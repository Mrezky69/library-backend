package com.school.library.rent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.school.library.rent.model.Rent;

@Repository
public interface RentRepository extends JpaRepository<Rent, Long> {
}
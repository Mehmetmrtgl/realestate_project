package com.example.demo.repositories;

import com.example.demo.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image,Long> {
    List<Image> findAllByPropertyId(Long propertyId);
}

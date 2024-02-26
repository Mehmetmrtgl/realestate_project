package com.example.demo.services;

import com.example.demo.entities.Client;
import com.example.demo.entities.Feature;
import com.example.demo.entities.Property;
import com.example.demo.entities.UserType;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repositories.FeatureRepository;
import com.example.demo.repositories.PropertyRepository;
import com.example.demo.requests.PropertyCreateRequest;
import com.example.demo.requests.PropertyUpdateRequest;
import com.example.demo.responses.FeatureResponse;
import com.example.demo.responses.PropertyResponse;
import com.example.demo.responses.ClientResponse;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PropertyService {

    private final DealerService dealerService;
    private final PropertyRepository propertyRepository;

    private final FeatureRepository featureRepository;

    @Autowired
    public PropertyService(DealerService dealerService,
                           PropertyRepository propertyRepository,
                           FeatureRepository featureRepository) {
        this.dealerService = dealerService;
        this.propertyRepository = propertyRepository;
        this.featureRepository = featureRepository;

    }

    public Property createOneProperty(PropertyCreateRequest newPostRequest) {

        Client client = dealerService.getOneDealerById(newPostRequest.getClientId());
        if (client == null || client.getUserType() != UserType.DEALER) {
            return null;
        }
        if(client == null)
            return null;

        final Feature savedFeature;

        Feature feature = Feature.builder()
                .bathrooms(newPostRequest.getBathrooms())
                .lounges(newPostRequest.getLounges())
                .storeys(newPostRequest.getStoreys())
                .bedrooms(newPostRequest.getBedrooms())
                .build();
        savedFeature = this.featureRepository.save(feature);

        Property property = Property.builder()
                .location(newPostRequest.getLocation())
                .description(newPostRequest.getDescription())
                .numberOfRooms(newPostRequest.getNumberOfRooms())
                .propertyType(newPostRequest.getPropertyType())
                .feature(savedFeature) //saved features
                .status(newPostRequest.getStatus())
                .price(newPostRequest.getPrice())
                .propertyOwnerName(newPostRequest.getPropertyOwnerName())
                .client(client)
                .build();


        return this.propertyRepository.save(property);

    }


    public List<PropertyResponse> getAllProperties() {
        List<Property> properties;

        properties = propertyRepository.findAll();


        return properties.stream()
                .map(this::mapToPropertyResponse)
                .collect(Collectors.toList());
    }

    private PropertyResponse mapToPropertyResponse(Property property) {
        Feature feature = property.getFeature();
        Client client = property.getClient();

        FeatureResponse featureResponse = FeatureResponse.builder()
                .bathrooms(feature.getBathrooms())
                .bedrooms(feature.getBedrooms())
                .lounges(feature.getLounges())
                .storeys(feature.getStoreys())
                .build();

        ClientResponse clientResponse = ClientResponse.builder()
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .mobileNumber(client.getMobileNumber())
                .email(client.getEmail())
                .userType(client.getUserType())
                .build();

        return PropertyResponse.builder()
                .id(property.getId())
                .propertyOwnerName(property.getPropertyOwnerName())
                .location(property.getLocation())
                .description(property.getDescription())
                .numberOfRooms(property.getNumberOfRooms())
                .propertyType(property.getPropertyType())
                .status(property.getStatus())
                .price(property.getPrice())
                .feature(featureResponse)
                .client(clientResponse)
                .build();
    }

    public PropertyResponse getOnePropertyById(Long propertyId) {
        Optional<Property> propertyOptional = propertyRepository.findById(propertyId);
        if (propertyOptional.isPresent()) {
            Property property = propertyOptional.get();
            return mapToPropertyResponse(property);
        } else {
            throw new NotFoundException("Property not found with id: " + propertyId);
        }
    }


    public Property updateOnePropertyById(Long propertyId, PropertyUpdateRequest updateProperty) {
        Property dbProperty = this.propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        // Update Field if not blank
        if (updateProperty.getPropertyType() != null && !updateProperty.getPropertyType().isBlank()) {
            dbProperty.setPropertyType(updateProperty.getPropertyType());
        }
        if (updateProperty.getPropertyOwnerName() != null && !updateProperty.getPropertyOwnerName().isBlank()) {
            dbProperty.setPropertyOwnerName(updateProperty.getPropertyOwnerName());
        }
        if (updateProperty.getLocation() != null && !updateProperty.getLocation().isBlank()) {
            dbProperty.setLocation(updateProperty.getLocation());
        }
        if (updateProperty.getStatus() != null && !updateProperty.getStatus().isBlank()) {
            dbProperty.setStatus(updateProperty.getStatus());
        }
        if (updateProperty.getDescription() != null && !updateProperty.getDescription().isBlank()) {
            dbProperty.setDescription(updateProperty.getDescription());
        }
        if (updateProperty.getPrice() != null) {
            dbProperty.setPrice(updateProperty.getPrice());
        }
        if (updateProperty.getNumberOfRooms() != null) {
            dbProperty.setNumberOfRooms(updateProperty.getNumberOfRooms());
        }
        // Feature update
        Feature feature = dbProperty.getFeature();
        if (feature != null) {
            if (updateProperty.getBathrooms() != null) {
                feature.setBathrooms(updateProperty.getBathrooms());
            }
            if (updateProperty.getBedrooms() != null) {
                feature.setBedrooms(updateProperty.getBedrooms());
            }
            if (updateProperty.getLounges() != null) {
                feature.setLounges(updateProperty.getLounges());
            }
            if (updateProperty.getStoreys() != null) {
                feature.setStoreys(updateProperty.getStoreys());
            }
        }

        // Update images

        return this.propertyRepository.save(dbProperty);
    }


    public void deleteOnePostById(Long propertyId) {
        this.propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        this.propertyRepository.deleteById(propertyId);
    }

    public List<PropertyResponse> getAllPropertiesByClientId(Long clientId) {
        List<Property> properties = propertyRepository.findByClientId(clientId);
        if (properties.isEmpty()) {
            throw new EntityNotFoundException("Properties not found for dealerId: " + clientId);
        }
        return properties.stream()
                .map(this::mapToPropertyResponse)
                .collect(Collectors.toList());
    }




}


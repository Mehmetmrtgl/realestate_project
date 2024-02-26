package com.example.demo.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertyCreateRequest {
    private  Long clientId;
    private  String description;
    private  Integer numberOfRooms;
    private  String Status;
    private  Long price;
    private  String location;
    private  String propertyOwnerName;
    private  String propertyType;
    private  Integer bathrooms;
    private  Integer bedrooms;
    private  Integer lounges;
    private  Integer storeys;
}
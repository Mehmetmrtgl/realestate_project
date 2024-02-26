package com.example.demo.controller;
import com.example.demo.entities.Client;
import com.example.demo.entities.Property;
import com.example.demo.responses.PropertyResponse;
import com.example.demo.services.CustomerService;
import com.example.demo.services.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService customerService;
    private final PropertyService propertyService;
    @Autowired
    public CustomerController(CustomerService customerService,
                              PropertyService propertyService) {
        this.customerService = customerService;
        this.propertyService = propertyService;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> createClient(@RequestBody Client newClient) {
        Client client = customerService.saveOneClient(newClient);
        if(client != null)
            return new ResponseEntity<>(HttpStatus.CREATED);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/properties")
    public List<PropertyResponse> getAllProperties() {
        return propertyService.getAllProperties();
    }


    @GetMapping("/property/{propertyId}")
    public PropertyResponse getOnePropertyById(@PathVariable Long propertyId) {
        return propertyService.getOnePropertyById(propertyId);
    }



}

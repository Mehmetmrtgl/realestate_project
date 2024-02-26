package com.example.demo.controller;

import com.example.demo.entities.Client;
import com.example.demo.entities.Image;
import com.example.demo.entities.Property;
import com.example.demo.requests.PropertyCreateRequest;
import com.example.demo.requests.PropertyUpdateRequest;
import com.example.demo.responses.ImageResponse;
import com.example.demo.responses.PropertyResponse;
import com.example.demo.services.DealerService;
import com.example.demo.services.ImageService;
import com.example.demo.services.PropertyService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/dealers")
public class DealerController {
    private final DealerService dealerService;
    private  final PropertyService propertyService;
    private  final ImageService imageService;

    @Autowired
    public DealerController(DealerService dealerService,
                            PropertyService propertyService,
                            ImageService imageService) {
        this.dealerService = dealerService;
        this.propertyService = propertyService;
        this.imageService = imageService;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> createClient(@RequestBody Client newClient) {
        Client client = dealerService.saveOneClient(newClient);
        if(client != null)
            return new ResponseEntity<>(HttpStatus.CREATED);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/properties/{clientId}")
    public List<PropertyResponse> getAllPropertiesByUserId(@PathVariable Long clientId) {
        return propertyService.getAllPropertiesByClientId(clientId);
    }

    @GetMapping("/property/{propertyId}")
    public PropertyResponse getOnePropertyById(@PathVariable Long propertyId) {
        return propertyService.getOnePropertyById(propertyId);
    }

    @PostMapping("/property/add")
    public Property createOnePost(@RequestBody PropertyCreateRequest newPostRequest) {
        return propertyService.createOneProperty(newPostRequest);
    }


    @PutMapping("/property/{propertyId}")
    public Property updateOneProperty(@PathVariable Long propertyId, @RequestBody PropertyUpdateRequest updateProperty) {
        return propertyService.updateOnePropertyById(propertyId, updateProperty);
    }

    @DeleteMapping("/property/{propertyId}")
    public void deleteOneProperty(@PathVariable Long propertyId) {
        propertyService.deleteOnePostById(propertyId);
    }

    @PostMapping("/image/add/{propertyId}")
    public List<Image> addImageToProperty(@RequestParam MultipartFile[] images, @PathVariable Long propertyId){
        return imageService.addImagesToProperty(images, propertyId);
    }


    @DeleteMapping("property/image/{imageId}")
    public ResponseEntity<String> deleteImageById(@PathVariable Long imageId){
        return ResponseEntity.accepted().body(this.imageService.deleteImageById(imageId));
    }

    @Transactional
    @GetMapping("/property/images/{propertyId}")
    public ResponseEntity<byte[]> fetchAllImagesByPropertyId(@PathVariable Long propertyId){
        List<ImageResponse> images = this.imageService.fetchAllImagesByPropertyId(propertyId);
        List<byte[]> imagesByte = new ArrayList<>();

        images.forEach(image -> imagesByte.add(
                image.getImage()
        ));
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/jpg"))
                .body(imagesByte.get(0));
    }

}

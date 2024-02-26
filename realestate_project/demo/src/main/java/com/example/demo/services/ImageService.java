package com.example.demo.services;

import com.example.demo.entities.Image;
import com.example.demo.entities.Property;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repositories.ImageRepository;
import com.example.demo.repositories.PropertyRepository;
import com.example.demo.responses.ImageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
public class ImageService {
    private final ImageRepository imageRepository;
    private final PropertyRepository propertyRepository;
    @Autowired
    public ImageService(ImageRepository imageRepository,
                        PropertyRepository propertyRepository) {
        this.imageRepository = imageRepository;
        this.propertyRepository = propertyRepository;
    }
    private void multipartFileToImageList(MultipartFile[] imagesRequest, List<Image> images) {
        Arrays.asList(imagesRequest).forEach(
                imageRequest -> {
                    try {
                        Image image = Image.builder()
                                .image(compressImage(imageRequest.getBytes()))
                                .name(imageRequest.getOriginalFilename())
                                .type(imageRequest.getContentType())
                                .property(null)
                                .build();
                        images.add(imageRepository.save(image));
                    } catch (IOException e) {
                        throw new RuntimeException("Could not save image: " + e);
                    }
                }
        );
    }
    public List<Image> addImagesToProperty(MultipartFile[] imagesRequest, Long propertyId){
        List<Image> images = new ArrayList<>();
        Property dbProperty = this.propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        multipartFileToImageList(imagesRequest, images);

        //set images property id to saved property
        try{
            images.forEach(image -> image.setProperty(dbProperty));
            this.imageRepository.saveAll(images);
        }catch (Exception e){
            throw new RuntimeException("Could not save image: " + e);
        }

        return images;
    }

    public static byte[] compressImage(byte[] image){
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(image);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(image.length);
        byte[] tmp = new byte[4*1024];

        while(!deflater.finished()){
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }

        try{
            outputStream.close();
        }catch (IOException e){
            throw new RuntimeException("Can't compress image");
        }
        return outputStream.toByteArray();
    }

    public static byte[] decompressImage(byte[] image){
        Inflater inflater = new Inflater();
        inflater.setInput(image);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] tmp = new byte[4*1024];

        try{
            while(!inflater.finished()){
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        }catch (Exception e){
            throw new RuntimeException("Can't decompress image");
        }
        return outputStream.toByteArray();
    }

    public String deleteImageById(Long imageId){
        this.imageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find images"));
        this.imageRepository.deleteById(imageId);
        return "Image deleted successfully";
    }

    public List<ImageResponse> fetchAllImagesByPropertyId(Long propertyId) {
        return getImageByPropertyId(propertyId, this.imageRepository);
    }

    public static List<ImageResponse> getImageByPropertyId(Long propertyId, ImageRepository imageRepository) {
        List<ImageResponse> imageResponses = new ArrayList<>();
        List<Image> images = imageRepository.findAllByPropertyId(propertyId);

        images.forEach(
                image -> imageResponses.add(
                        ImageResponse.builder()
                                .id(image.getId())
                                .type(image.getType())
                                .name(image.getName())
                                .image(decompressImage(image.getImage()))
                                .build()
                )
        );

        return imageResponses;
    }


}

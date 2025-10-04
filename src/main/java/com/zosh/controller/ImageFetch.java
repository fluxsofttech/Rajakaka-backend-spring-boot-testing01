package com.zosh.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.zosh.service.impl.ImageFetchService;
@RestController
@RequestMapping("/adminrajakaka")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ImageFetch {
	 @Autowired
	    private ImageFetchService service;

	    @GetMapping("/image-urls")
	    public List<ImageFetchService.ImageDTO> getAllImages() {
	        List<ImageFetchService.ImageDTO> images = service.getAllImages();
	        System.out.println("📡 Returning " + images.size() + " images");
	        return images;
	    }

	    @GetMapping("/image-urls/{productId}")
	    public List<String> getImagesByProduct(@PathVariable Long productId) {
	        return service.getImagesByProductId(productId);
	    }

}

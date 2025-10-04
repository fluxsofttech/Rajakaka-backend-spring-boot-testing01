package com.zosh.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zosh.repository.ProductImageRepository;

@Service
public class ImageFetchService   {
	
    @Autowired
    private ProductImageRepository repo;

    public List<ImageDTO> getAllImages() {
        List<Object[]> rows = repo.findAllImages();
        return rows.stream()
                .map(r -> new ImageDTO(((Number) r[0]).longValue(), (String) r[1]))
                .toList();
    }

    public List<String> getImagesByProductId(Long productId) {
        return repo.findImagesByProductId(productId);
    }

    // ✅ Simple DTO
    public record ImageDTO(Long productId, String imageUrl) {}

}

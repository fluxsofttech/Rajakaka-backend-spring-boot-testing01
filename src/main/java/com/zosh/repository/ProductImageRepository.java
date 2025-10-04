package com.zosh.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductImageRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Object[]> findAllImages() {
        return jdbcTemplate.query(
            "SELECT product_id, images FROM product_images",
            (rs, rowNum) -> new Object[]{rs.getLong("product_id"), rs.getString("images")}
        );
    }

    public List<String> findImagesByProductId(Long productId) {
        return jdbcTemplate.queryForList(
            "SELECT images FROM product_images WHERE product_id = ?",
            new Object[]{productId},
            String.class
        );
    }
}

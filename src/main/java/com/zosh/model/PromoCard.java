package com.zosh.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;       // e.g. "Bestselling TVs"
    private String subtitle;    // e.g. "Starting at"
    private String price;       // e.g. "₹5,311*"
    private String note;        // e.g. "Extra Exchange Benefits | Easy EMI"
    private String imageUrl;    // e.g. "https://images.pexels.com/..."
}

package com.zosh.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private String productCategory;
    private String complaintCategory;
    private String message;
    private String email;
    private String phone;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus status = ComplaintStatus.OPEN;  // ✅ Enum instead of String

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    private User user;
}

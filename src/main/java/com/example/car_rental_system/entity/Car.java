package com.example.car_rental_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;
    private String model;
    private String licensePlate;
    private Integer year;
    private String color;
    private Integer seats;
    private String transmission;
    private String fuelType;

    @Column(precision = 10, scale = 2)
    private BigDecimal pricePerDay;

    private boolean available;

    @Lob
    private String description;

    @ElementCollection
    private List<String> features = new ArrayList<>();

    @ElementCollection
    private List<String> imageUrls = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Lessor owner;

    @OneToMany(mappedBy = "car")
    private List<Booking> bookings = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}
package com.example.car_rental_system.model.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarResponse {
    private Long id;
    private String brand;
    private String model;
    private String licensePlate;
    private Integer year;
    private String color;
    private Integer seats;
    private String transmission;
    private String fuelType;
    private BigDecimal pricePerDay;
    private boolean available;
    private String description;
    private List<String> features;
    private List<String> imageUrls;
    private Long ownerId;
    private String ownerName;
    private Date createdAt;
}
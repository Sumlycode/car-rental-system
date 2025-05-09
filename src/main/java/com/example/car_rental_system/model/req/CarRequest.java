package com.example.car_rental_system.model.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarRequest {
    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    @NotBlank
    private String licensePlate;

    @NotNull
    private Integer year;

    @NotBlank
    private String color;

    @NotNull
    private Integer seats;

    @NotBlank
    private String transmission;

    @NotBlank
    private String fuelType;

    @NotNull
    @Positive
    private BigDecimal pricePerDay;

    private String description;
    private List<String> features;
    private List<String> imageUrls;
}
package com.example.car_rental_system.model.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class JwtResponse {
    private String token;
    private Date expirationDate;


    public JwtResponse(String token, Date expirationDate) {
        this.token = token;
        this.expirationDate = expirationDate;
    }
}
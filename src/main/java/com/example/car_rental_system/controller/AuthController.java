package com.example.car_rental_system.controller;

import com.example.car_rental_system.entity.User;
import com.example.car_rental_system.model.req.LoginRequest;
import com.example.car_rental_system.model.req.RegisterRequest;
import com.example.car_rental_system.model.res.JwtResponse;
import com.example.car_rental_system.service.AuthService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws BadRequestException {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) throws BadRequestException {
        User user = authService.registerUser(registerRequest);
        return ResponseEntity.ok("User registered successfully! Please wait for admin verification.");
    }
}
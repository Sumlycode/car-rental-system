package com.example.car_rental_system.controller;

import com.example.car_rental_system.entity.User;
import com.example.car_rental_system.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/verify/{id}")
    public ResponseEntity<?> verifyUser(@PathVariable Long id) {
        User user = adminService.verifyUser(id);
        return new ResponseEntity<>("User verified successfully: " + user, HttpStatus.OK);
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<?> rejectUser(@PathVariable Long id) {
        User user = adminService.unverifyUser(id);
        return new ResponseEntity<>("User rejected successfully: " + user, HttpStatus.OK);
    }
}

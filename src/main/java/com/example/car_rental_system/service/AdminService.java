package com.example.car_rental_system.service;

import com.example.car_rental_system.entity.Lessor;
import com.example.car_rental_system.entity.Renter;
import com.example.car_rental_system.entity.User;
import com.example.car_rental_system.exception.ResourceNotFoundException;
import com.example.car_rental_system.repository.LessorRepository;
import com.example.car_rental_system.repository.RenterRepository;
import com.example.car_rental_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RenterRepository renterRepository;

    @Autowired
    private LessorRepository lessorRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<Renter> getAllRenters() {
        return renterRepository.findAll();
    }

    public List<Lessor> getAllLessors() {
        return lessorRepository.findAll();
    }

    public User verifyUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setVerified(true);
        return userRepository.save(user);
    }

    public User unverifyUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setVerified(false);
        return userRepository.save(user);
    }
}
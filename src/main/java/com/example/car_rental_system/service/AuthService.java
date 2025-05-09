package com.example.car_rental_system.service;

import com.example.car_rental_system.entity.Admin;
import com.example.car_rental_system.entity.Lessor;
import com.example.car_rental_system.entity.Renter;
import com.example.car_rental_system.entity.User;
import com.example.car_rental_system.exception.UnauthorizedException;
import com.example.car_rental_system.model.req.LoginRequest;
import com.example.car_rental_system.model.req.RegisterRequest;
import com.example.car_rental_system.model.res.JwtResponse;
import com.example.car_rental_system.repository.AdminRepository;
import com.example.car_rental_system.repository.LessorRepository;
import com.example.car_rental_system.repository.RenterRepository;
import com.example.car_rental_system.repository.UserRepository;
import com.example.car_rental_system.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RenterRepository renterRepository;

    @Autowired
    private LessorRepository lessorRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public JwtResponse authenticateUser(LoginRequest loginRequest) throws BadRequestException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
            Optional<User> us = userRepository.findByEmail(userPrincipal.getUsername());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            Map<String, Object> jwt = tokenProvider.generateToken(authentication, us.get().getId());
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            log.info("roles : {}", roles);

            return new JwtResponse((String) jwt.get("token"), (Date) jwt.get("expire"));
        } catch (Exception e) {
            log.error("error : {}", e);
            throw new UnauthorizedException("Unauthorized");
        }
    }

    public User registerUser(RegisterRequest registerRequest) throws BadRequestException {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            System.out.println("Email is already in use!");
            throw new BadRequestException("Email is already in use!");
        }

        // Create new user according to role
        User user;

        switch (registerRequest.getRole()) {
            case ROLE_RENTER:
                Renter renter = Renter.builder()
                        .firstName(registerRequest.getFirstName())
                        .lastName(registerRequest.getLastName())
                        .email(registerRequest.getEmail())
                        .password(passwordEncoder.encode(registerRequest.getPassword()))
                        .phone(registerRequest.getPhone())
                        .role(User.Role.ROLE_RENTER)
                        .verified(false)
                        .idCardNumber(registerRequest.getIdCardNumber())
                        .driverLicenseNumber(registerRequest.getDriverLicenseNumber())
                        .build();
                user = renterRepository.save(renter);
                break;

            case ROLE_LESSOR:
                Lessor lessor = Lessor.builder()
                        .firstName(registerRequest.getFirstName())
                        .lastName(registerRequest.getLastName())
                        .email(registerRequest.getEmail())
                        .password(passwordEncoder.encode(registerRequest.getPassword()))
                        .phone(registerRequest.getPhone())
                        .role(User.Role.ROLE_LESSOR)
                        .verified(false)
                        .companyName(registerRequest.getCompanyName())
                        .taxId(registerRequest.getTaxId())
                        .bankAccount(registerRequest.getBankAccount())
                        .build();
                user = lessorRepository.save(lessor);
                break;

            case ROLE_ADMIN:
                Admin admin = Admin.builder()
                        .firstName(registerRequest.getFirstName())
                        .lastName(registerRequest.getLastName())
                        .email(registerRequest.getEmail())
                        .password(passwordEncoder.encode(registerRequest.getPassword()))
                        .phone(registerRequest.getPhone())
                        .role(User.Role.ROLE_ADMIN)
                        .verified(true) // Admin is verified by default
                        .adminCode("ADMIN" + System.currentTimeMillis())
                        .department("System")
                        .build();
                user = adminRepository.save(admin);
                break;

            default:
                throw new BadRequestException("Invalid role specified");
        }

        return user;
    }
}
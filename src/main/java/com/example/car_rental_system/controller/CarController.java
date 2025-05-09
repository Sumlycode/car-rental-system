package com.example.car_rental_system.controller;
import com.example.car_rental_system.model.req.CarRequest;
import com.example.car_rental_system.model.res.CarResponse;
import com.example.car_rental_system.security.JwtTokenProvider;
import com.example.car_rental_system.service.CarService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    @Autowired
    private CarService carService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/public/all")
    public ResponseEntity<List<CarResponse>> getAllAvailableCars() {
        List<CarResponse> cars = carService.getAvailableCars();
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/public/available")
    public ResponseEntity<List<CarResponse>> getAvailableCarsForDates(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) throws BadRequestException {
        List<CarResponse> cars = carService.getAvailableCarsForDates(startDate, endDate);
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<CarResponse> getCarById(@PathVariable Long id) throws Exception {
        CarResponse car = carService.getCarById(id);
        return ResponseEntity.ok(car);
    }

    @GetMapping("/lessor")
    @PreAuthorize("hasRole('LESSOR')")
    public ResponseEntity<List<CarResponse>> getLessorCars(@RequestParam Long lessorId) {
        List<CarResponse> cars = carService.getCarsByLessorId(lessorId);
        return ResponseEntity.ok(cars);
    }

    @PostMapping
    @PreAuthorize("hasRole('LESSOR')")
    public ResponseEntity<CarResponse> addCar(
            @Valid @RequestBody CarRequest carRequest
            , HttpServletRequest request) throws BadRequestException {
        Long lessorId = jwtTokenProvider.getUserIdFromJwtToken(request).longValue();
        CarResponse car = carService.addCar(carRequest, lessorId);
        return ResponseEntity.ok(car);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LESSOR')")
    public ResponseEntity<CarResponse> updateCar(
            @PathVariable Long id,
            @Valid @RequestBody CarRequest carRequest,
            @RequestParam Long lessorId) throws BadRequestException {
        CarResponse car = carService.updateCar(id, carRequest, lessorId);
        return ResponseEntity.ok(car);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LESSOR')")
    public ResponseEntity<?> deleteCar(
            @PathVariable Long id,
            @RequestParam Long lessorId) throws BadRequestException {
        carService.deleteCar(id, lessorId);
        return ResponseEntity.ok("Car deleted successfully");
    }

    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasRole('LESSOR')")
    public ResponseEntity<CarResponse> updateCarAvailability(
            @PathVariable Long id,
            @RequestParam boolean available,
            @RequestParam Long lessorId) throws BadRequestException {
        CarResponse car = carService.updateCarAvailability(id, available, lessorId);
        return ResponseEntity.ok(car);
    }
}
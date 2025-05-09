package com.example.car_rental_system.controller;

import com.example.car_rental_system.entity.Booking;
import com.example.car_rental_system.model.req.BookingRequest;
import com.example.car_rental_system.model.res.BookingResponse;
import com.example.car_rental_system.security.JwtTokenProvider;
import com.example.car_rental_system.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/renter")
    @PreAuthorize("hasRole('RENTER')")
    public ResponseEntity<List<BookingResponse>> getRenterBookings(@RequestParam Long renterId) {
        List<BookingResponse> bookings = bookingService.getBookingsByRenterId(renterId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/lessor")
    @PreAuthorize("hasRole('LESSOR')")
    public ResponseEntity<List<BookingResponse>> getLessorBookings(@RequestParam Long lessorId) {
        List<BookingResponse> bookings = bookingService.getBookingsByLessorId(lessorId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('RENTER', 'LESSOR', 'ADMIN')")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        BookingResponse booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }

    @PostMapping
    @PreAuthorize("hasRole('RENTER')")
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest bookingRequest,
            HttpServletRequest httpServletRequest) throws BadRequestException {
        Long renterId = jwtTokenProvider.getUserIdFromJwtToken(httpServletRequest).longValue();
        BookingResponse booking = bookingService.createBooking(bookingRequest, renterId);
        return ResponseEntity.ok(booking);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('RENTER', 'LESSOR')")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam Booking.BookingStatus status,
            @RequestParam(required = false) String rejectionReason,
            @RequestParam Long userId) throws BadRequestException {
        BookingResponse booking = bookingService.updateBookingStatus(id, status, rejectionReason, userId);
        return ResponseEntity.ok(booking);
    }
}

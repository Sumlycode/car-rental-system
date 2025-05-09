package com.example.car_rental_system.service;


import com.example.car_rental_system.entity.Booking;
import com.example.car_rental_system.entity.Car;
import com.example.car_rental_system.entity.Renter;
import com.example.car_rental_system.exception.ResourceNotFoundException;
import com.example.car_rental_system.model.req.BookingRequest;
import com.example.car_rental_system.model.res.BookingResponse;
import com.example.car_rental_system.repository.BookingRepository;
import com.example.car_rental_system.repository.CarRepository;
import com.example.car_rental_system.repository.RenterRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private RenterRepository renterRepository;

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::convertToBookingResponse)
                .collect(Collectors.toList());
    }

    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        return convertToBookingResponse(booking);
    }

    public List<BookingResponse> getBookingsByRenterId(Long renterId) {
        Renter renter = renterRepository.findById(renterId)
                .orElseThrow(() -> new ResourceNotFoundException("Renter not found with id: " + renterId));
        return bookingRepository.findByRenter(renter).stream()
                .map(this::convertToBookingResponse)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getBookingsByLessorId(Long lessorId) {
        return bookingRepository.findByLessorId(lessorId).stream()
                .map(this::convertToBookingResponse)
                .collect(Collectors.toList());
    }

    public BookingResponse createBooking(BookingRequest bookingRequest, Long renterId) throws BadRequestException {
        // Validate dates
        Date now = new Date();
        if (bookingRequest.getStartDate().before(now)) {
            throw new BadRequestException("Start date cannot be in the past");
        }
        if (bookingRequest.getEndDate().before(bookingRequest.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }

        // Check if car exists
        Car car = carRepository.findById(bookingRequest.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + bookingRequest.getCarId()));

        // Check if car is available
        if (!car.isAvailable()) {
            throw new BadRequestException("Car is not available for booking");
        }

        // Check for overlapping bookings
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                car.getId(), bookingRequest.getStartDate(), bookingRequest.getEndDate());

        if (!overlappingBookings.isEmpty()) {
            throw new BadRequestException("Car is already booked for the selected dates");
        }

        // Get renter
        Renter renter = renterRepository.findById(renterId)
                .orElseThrow(() -> new ResourceNotFoundException("Renter not found with id: " + renterId));

        // Calculate total price
        long diffInMillies = Math.abs(bookingRequest.getEndDate().getTime() - bookingRequest.getStartDate().getTime());
        long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        if (diffInDays == 0) diffInDays = 1; // Minimum 1 day

        BigDecimal totalPrice = car.getPricePerDay().multiply(BigDecimal.valueOf(diffInDays));

        // Create booking
        Booking booking = Booking.builder()
                .renter(renter)
                .car(car)
                .startDate(bookingRequest.getStartDate())
                .endDate(bookingRequest.getEndDate())
                .totalPrice(totalPrice)
                .status(Booking.BookingStatus.PENDING)
                .build();

        booking = bookingRepository.save(booking);
        return convertToBookingResponse(booking);
    }

    public BookingResponse updateBookingStatus(Long id, Booking.BookingStatus status, String rejectionReason, Long userId) throws BadRequestException {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        // If status is REJECTED and rejectionReason is null or empty, throw exception
        if (status == Booking.BookingStatus.REJECTED && (rejectionReason == null || rejectionReason.trim().isEmpty())) {
            throw new BadRequestException("Rejection reason is required when rejecting a booking");
        }

        // Check if the user is the lessor of the car
        if (!booking.getCar().getOwner().getId().equals(userId) &&
                !booking.getRenter().getId().equals(userId)) {
            throw new BadRequestException("You are not authorized to update this booking");
        }

        // Only allow renter to cancel booking
        if (status == Booking.BookingStatus.CANCELLED && !booking.getRenter().getId().equals(userId)) {
            throw new BadRequestException("Only renters can cancel bookings");
        }

        // Only allow lessor to approve or reject booking
        if ((status == Booking.BookingStatus.APPROVED || status == Booking.BookingStatus.REJECTED) &&
                !booking.getCar().getOwner().getId().equals(userId)) {
            throw new BadRequestException("Only lessors can approve or reject bookings");
        }

        booking.setStatus(status);
        if (status == Booking.BookingStatus.REJECTED) {
            booking.setRejectionReason(rejectionReason);
        }

        booking = bookingRepository.save(booking);
        return convertToBookingResponse(booking);
    }

    public void deleteBooking(Long id, Long renterId) throws BadRequestException {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if (!booking.getRenter().getId().equals(renterId)) {
            throw new BadRequestException("You are not authorized to delete this booking");
        }

        if (booking.getStatus() != Booking.BookingStatus.PENDING && booking.getStatus() != Booking.BookingStatus.REJECTED) {
            throw new BadRequestException("Cannot delete a booking that is not in PENDING or REJECTED status");
        }

        bookingRepository.delete(booking);
    }

    private BookingResponse convertToBookingResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getRenter().getId(),
                booking.getRenter().getFirstName() + " " + booking.getRenter().getLastName(),
                booking.getCar().getId(),
                booking.getCar().getBrand() + " " + booking.getCar().getModel() + " (" + booking.getCar().getLicensePlate() + ")",
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getTotalPrice(),
                booking.getStatus(),
                booking.getRejectionReason(),
                booking.getCreatedAt()
        );
    }
}
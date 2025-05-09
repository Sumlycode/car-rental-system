package com.example.car_rental_system.repository;

import com.example.car_rental_system.entity.Booking;
import com.example.car_rental_system.entity.Car;
import com.example.car_rental_system.entity.Renter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByRenter(Renter renter);
    List<Booking> findByCar(Car car);

    @Query("SELECT b FROM Booking b WHERE b.car.id = :carId AND " +
            "b.status IN ('PENDING', 'APPROVED') AND " +
            "((b.startDate <= :endDate AND b.endDate >= :startDate))")
    List<Booking> findOverlappingBookings(
            @Param("carId") Long carId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Query("SELECT b FROM Booking b WHERE b.car.owner.id = :lessorId")
    List<Booking> findByLessorId(@Param("lessorId") Long lessorId);
}
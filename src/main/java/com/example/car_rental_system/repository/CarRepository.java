package com.example.car_rental_system.repository;

import com.example.car_rental_system.entity.Car;
import com.example.car_rental_system.entity.Lessor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByOwner(Lessor owner);
    List<Car> findByAvailableTrue();

    @Query("SELECT c FROM Car c WHERE c.id NOT IN " +
            "(SELECT b.car.id FROM Booking b WHERE " +
            "b.status IN ('PENDING', 'APPROVED') AND " +
            "((b.startDate <= :endDate AND b.endDate >= :startDate)))")
    List<Car> findAvailableCars(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
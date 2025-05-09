package com.example.car_rental_system.service;


import com.example.car_rental_system.entity.Car;
import com.example.car_rental_system.entity.Lessor;
import com.example.car_rental_system.exception.ResourceNotFoundException;
import com.example.car_rental_system.model.req.CarRequest;
import com.example.car_rental_system.model.res.CarResponse;
import com.example.car_rental_system.repository.CarRepository;
import com.example.car_rental_system.repository.LessorRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private LessorRepository lessorRepository;

    public List<CarResponse> getAllCars() {
        return carRepository.findAll().stream()
                .map(this::convertToCarResponse)
                .collect(Collectors.toList());
    }

    public List<CarResponse> getAvailableCars() {
        return carRepository.findByAvailableTrue().stream()
                .map(this::convertToCarResponse)
                .collect(Collectors.toList());
    }

    public List<CarResponse> getAvailableCarsForDates(Date startDate, Date endDate) throws BadRequestException {
        if (startDate.before(new Date())) {
            throw new BadRequestException("Start date cannot be in the past");
        }
        if (endDate.before(startDate)) {
            throw new BadRequestException("End date must be after start date");
        }

        return carRepository.findAvailableCars(startDate, endDate).stream()
                .map(this::convertToCarResponse)
                .collect(Collectors.toList());
    }

    public CarResponse getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + id));
        return convertToCarResponse(car);
    }

    public List<CarResponse> getCarsByLessorId(Long lessorId) {
        Lessor lessor = lessorRepository.findById(lessorId)
                .orElseThrow(() -> new ResourceNotFoundException("Lessor not found with id: " + lessorId));
        return carRepository.findByOwner(lessor).stream()
                .map(this::convertToCarResponse)
                .collect(Collectors.toList());
    }

    public CarResponse addCar(CarRequest carRequest, Long lessorId) {
        Lessor lessor = lessorRepository.findById(lessorId)
                .orElseThrow(() -> new ResourceNotFoundException("Lessor not found with id: " + lessorId));

        Car car = Car.builder()
                .brand(carRequest.getBrand())
                .model(carRequest.getModel())
                .licensePlate(carRequest.getLicensePlate())
                .year(carRequest.getYear())
                .color(carRequest.getColor())
                .seats(carRequest.getSeats())
                .transmission(carRequest.getTransmission())
                .fuelType(carRequest.getFuelType())
                .pricePerDay(carRequest.getPricePerDay())
                .available(true)
                .description(carRequest.getDescription())
                .features(carRequest.getFeatures())
                .imageUrls(carRequest.getImageUrls())
                .owner(lessor)
                .build();

        car = carRepository.save(car);
        return convertToCarResponse(car);
    }

    public CarResponse updateCar(Long id, CarRequest carRequest, Long lessorId) throws BadRequestException {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + id));

        if (!car.getOwner().getId().equals(lessorId)) {
            throw new BadRequestException("You are not authorized to update this car");
        }

        car.setBrand(carRequest.getBrand());
        car.setModel(carRequest.getModel());
        car.setLicensePlate(carRequest.getLicensePlate());
        car.setYear(carRequest.getYear());
        car.setColor(carRequest.getColor());
        car.setSeats(carRequest.getSeats());
        car.setTransmission(carRequest.getTransmission());
        car.setFuelType(carRequest.getFuelType());
        car.setPricePerDay(carRequest.getPricePerDay());
        car.setDescription(carRequest.getDescription());
        car.setFeatures(carRequest.getFeatures());
        car.setImageUrls(carRequest.getImageUrls());

        car = carRepository.save(car);
        return convertToCarResponse(car);
    }

    public void deleteCar(Long id, Long lessorId) throws BadRequestException {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + id));

        if (!car.getOwner().getId().equals(lessorId)) {
            throw new BadRequestException("You are not authorized to delete this car");
        }

        carRepository.delete(car);
    }

    public CarResponse updateCarAvailability(Long id, boolean available, Long lessorId) throws BadRequestException {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + id));

        if (!car.getOwner().getId().equals(lessorId)) {
            throw new BadRequestException("You are not authorized to update this car");
        }

        car.setAvailable(available);
        car = carRepository.save(car);
        return convertToCarResponse(car);
    }

    private CarResponse convertToCarResponse(Car car) {
        return new CarResponse(
                car.getId(),
                car.getBrand(),
                car.getModel(),
                car.getLicensePlate(),
                car.getYear(),
                car.getColor(),
                car.getSeats(),
                car.getTransmission(),
                car.getFuelType(),
                car.getPricePerDay(),
                car.isAvailable(),
                car.getDescription(),
                car.getFeatures(),
                car.getImageUrls(),
                car.getOwner().getId(),
                car.getOwner().getFirstName() + " " + car.getOwner().getLastName(),
                car.getCreatedAt()
        );
    }
}

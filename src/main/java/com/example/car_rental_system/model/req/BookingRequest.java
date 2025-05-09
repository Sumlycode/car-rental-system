package com.example.car_rental_system.model.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    @NotNull
    private Long carId;

    @NotNull
    @Future(message = "Start date must be in the future")
    private Date startDate;

    @NotNull
    @Future(message = "End date must be in the future")
    private Date endDate;
}
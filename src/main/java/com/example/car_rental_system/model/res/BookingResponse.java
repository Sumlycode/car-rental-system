package com.example.car_rental_system.model.res;


import com.example.car_rental_system.entity.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private Long renterId;
    private String renterName;
    private Long carId;
    private String carDetails;
    private Date startDate;
    private Date endDate;
    private BigDecimal totalPrice;
    private Booking.BookingStatus status;
    private String rejectionReason;
    private Date createdAt;
}

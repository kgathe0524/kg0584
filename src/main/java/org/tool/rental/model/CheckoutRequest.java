package org.tool.rental.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

    private String toolCode;

    @Min(value = 1, message = "Rental Day count is not 1 or greater")
    private long rentalDayCount;

    @Min(value = 0, message = "Discount percent is not in the range 0-100")
    @Max(value = 100, message = "Discount percent is not in the range 0-100")
    private int discountPercent;

    private LocalDate checkOutDate;
}

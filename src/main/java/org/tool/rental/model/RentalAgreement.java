package org.tool.rental.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class RentalAgreement {

    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yy");
    public RentalAgreement(CheckoutRequest req){
        this.toolCode = req.getToolCode();
        this.rentalDays = req.getRentalDayCount();
        this.discountPercent = req.getDiscountPercent();
        this.checkOutDate = req.getCheckOutDate();
    }

    private String toolCode;
    private ToolType toolType;
    private String toolBrand;
    private long rentalDays;
    private LocalDate checkOutDate;
    @JsonFormat(pattern = "MM/dd/yy")
    private LocalDate dueDate;
    private BigDecimal dailyRentalCharge;
    private long chargeDays;
    private BigDecimal preDiscountCharge;
    private int discountPercent;
    private BigDecimal discountAmount;
    private BigDecimal finalCharge;

    @Override
    public String toString() {
        return "Tool code: '" + toolCode + "\n" +
                "Tool type: " + toolType + "\n" +
                "Tool brand: '" + toolBrand + "\n" +
                "Rental days: " + rentalDays + "\n" +
                "Checkout date: " + checkOutDate.format(FORMATTER) + "\n" +
                "Due date: " + dueDate.format(FORMATTER) + "\n" +
                "Daily rental charge: $" + dailyRentalCharge + "\n" +
                "Charge days: " + chargeDays + "\n" +
                "PreDiscount charge: $" + preDiscountCharge + "\n" +
                "Discount percent: " + discountPercent + "%\n" +
                "Discount amount: $" + discountAmount + "\n" +
                "Final charge: $" + finalCharge;
    }
}

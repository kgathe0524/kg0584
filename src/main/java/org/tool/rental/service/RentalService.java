package org.tool.rental.service;

import org.springframework.stereotype.Service;
import org.tool.rental.model.*;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

@Service
public class RentalService {

    // data mocked
    private final static Map<String,Tools> TOOL_CODE =  Map.of(
            "CHNS", new Tools(null, "CHNS", ToolType.Chainsaw, "Stihl"),
            "LADW", new Tools(null, "LADW", ToolType.Ladder, "Werner"),
            "JAKD", new Tools(null, "JAKD", ToolType.Jackhammer, "DeWalt"),
            "JAKR", new Tools(null, "JAKR", ToolType.Jackhammer, "Ridgid"));

    private final static Map<ToolType,Charge> CHARGES =  Map.of(
            ToolType.Ladder, new Charge(null, ToolType.Ladder, BigDecimal.valueOf(1.99), true, true, false),
            ToolType.Chainsaw, new Charge(null, ToolType.Chainsaw, BigDecimal.valueOf(1.49), true, false, true),
            ToolType.Jackhammer, new Charge(null, ToolType.Jackhammer, BigDecimal.valueOf(2.99), true, false, false));
    public RentalAgreement checkout(CheckoutRequest checkoutRequest) {
        RentalAgreement rentalAgreement = new RentalAgreement(checkoutRequest);
        rentalAgreement.setDueDate(checkoutRequest.getCheckOutDate().plusDays(checkoutRequest.getRentalDayCount()));
        rentalAgreement.setToolType(TOOL_CODE.get(checkoutRequest.getToolCode()).getToolType());
        rentalAgreement.setToolBrand(TOOL_CODE.get(checkoutRequest.getToolCode()).getBrand());
        BigDecimal dailyCharge = CHARGES.get(rentalAgreement.getToolType()).getDailyCharge();
        rentalAgreement.setDailyRentalCharge(dailyCharge);

        setRentalDayCount(rentalAgreement);

        BigDecimal preDiscountCharge = rentalAgreement.getDailyRentalCharge().multiply(BigDecimal.valueOf(rentalAgreement.getChargeDays()));
        rentalAgreement.setPreDiscountCharge(preDiscountCharge.setScale(2, RoundingMode.HALF_EVEN));

        BigDecimal discountAmount = rentalAgreement.getPreDiscountCharge()
                .multiply(BigDecimal.valueOf(rentalAgreement.getDiscountPercent()))
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_EVEN);
        rentalAgreement.setDiscountAmount(discountAmount);
        rentalAgreement.setFinalCharge(rentalAgreement.getPreDiscountCharge().subtract(rentalAgreement.getDiscountAmount()));
        return rentalAgreement;
    }

    private void setRentalDayCount(RentalAgreement rentalAgreement) {
        LocalDate rentalStartDate = rentalAgreement.getCheckOutDate();
        LocalDate rentalEndDate = rentalAgreement.getDueDate();
        long totalRentalDayCount = 0;
        if(CHARGES.get(rentalAgreement.getToolType()).isWeekdayCharge()){
            totalRentalDayCount += rentalStartDate.datesUntil(rentalEndDate)
                    .map(LocalDate::getDayOfWeek)
                    .filter(day -> !new HashSet<>(Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)).contains(day))
                    .count();
        }
        if(CHARGES.get(rentalAgreement.getToolType()).isWeekendCharge()){
            totalRentalDayCount += rentalStartDate.datesUntil(rentalEndDate)
                    .map(LocalDate::getDayOfWeek)
                    .filter(day -> new HashSet<>(Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)).contains(day))
                    .count();
        }
        if(!CHARGES.get(rentalAgreement.getToolType()).isHolidayCharge()){
            if(isLaborDayFalling(rentalStartDate, rentalEndDate)){
                totalRentalDayCount--;
            }
            if(isIndependenceDayFalling(rentalStartDate, rentalEndDate)){
                totalRentalDayCount--;
            }
        }
        rentalAgreement.setChargeDays(totalRentalDayCount);
    }

    private boolean isIndependenceDayFalling(LocalDate rentalStartDate, LocalDate rentalEndDate) {
        LocalDate independenceDay = LocalDate.of(rentalStartDate.getYear(), Month.JULY, 4);
        if(DayOfWeek.SATURDAY.equals(independenceDay.getDayOfWeek())){
            independenceDay = independenceDay.minusDays(1);
        }else if (DayOfWeek.SUNDAY.equals(independenceDay.getDayOfWeek())){
            independenceDay = independenceDay.plusDays(1);
        }
        return independenceDay.isAfter(rentalStartDate) && independenceDay.isBefore(rentalEndDate);
    }

    private boolean isLaborDayFalling(LocalDate rentalStartDate, LocalDate rentalEndDate) {
        LocalDate firstOfSeptember = LocalDate.of(rentalStartDate.getYear(), Month.SEPTEMBER, 1);
        LocalDate laborDay = firstOfSeptember.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
        return laborDay.isAfter(rentalStartDate) && laborDay.isBefore(rentalEndDate);
    }
}

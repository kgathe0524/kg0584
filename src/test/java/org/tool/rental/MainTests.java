package org.tool.rental;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.tool.rental.model.CheckoutRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
class MainTests {

    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yy");
    private final static ObjectMapper OM = new ObjectMapper();

    @BeforeAll
    public static void init() {
        OM.registerModule(new JavaTimeModule());
    }
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("test1")
    public void test1() throws Exception {
        CheckoutRequest expectedRecord = new CheckoutRequest(
                "JAKR", 5, 101, LocalDate.parse("09/03/15", FORMATTER));

        mockMvc.perform(post("/rental")
                        .contentType("application/json")
                        .content(OM.writeValueAsString(expectedRecord)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.discountPercent", Is.is("Discount percent is not in the range 0-100")));
    }
    @Test
    @DisplayName("test2")
    public void test2() throws Exception {
        CheckoutRequest expectedRecord = new CheckoutRequest(
                "LADW", 3, 10, LocalDate.parse("07/02/20", FORMATTER));

        mockMvc.perform(post("/rental")
                        .contentType("application/json")
                        .content(OM.writeValueAsString(expectedRecord)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dueDate", Is.is("07/05/20")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.chargeDays", Is.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.preDiscountCharge", Is.is(3.98)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.discountAmount", Is.is(0.40)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalCharge", Is.is(3.58)))
                .andExpect(status().is2xxSuccessful());
    }
    @Test
    @DisplayName("test3")
    public void test3() throws Exception {
        CheckoutRequest expectedRecord = new CheckoutRequest(
                "CHNS", 5, 25, LocalDate.parse("07/02/15", FORMATTER));

        mockMvc.perform(post("/rental")
                        .contentType("application/json")
                        .content(OM.writeValueAsString(expectedRecord)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dueDate", Is.is("07/07/15")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.chargeDays", Is.is(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.preDiscountCharge", Is.is(4.47)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.discountAmount", Is.is(1.12)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalCharge", Is.is(3.35)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("test4")
    public void test4() throws Exception {
        CheckoutRequest expectedRecord = new CheckoutRequest(
                "JAKD", 6, 0, LocalDate.parse("09/03/15", FORMATTER));

        mockMvc.perform(post("/rental")
                        .contentType("application/json")
                        .content(OM.writeValueAsString(expectedRecord)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dueDate", Is.is("09/09/15")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.chargeDays", Is.is(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.preDiscountCharge", Is.is(8.97)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.discountAmount", Is.is(0.0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalCharge", Is.is(8.97)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("test5")
    public void test5() throws Exception {
        CheckoutRequest expectedRecord = new CheckoutRequest(
                "JAKR", 9, 0, LocalDate.parse("07/02/15", FORMATTER));

        mockMvc.perform(post("/rental")
                        .contentType("application/json")
                        .content(OM.writeValueAsString(expectedRecord)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dueDate", Is.is("07/11/15")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.chargeDays", Is.is(6)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.preDiscountCharge", Is.is(17.94)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.discountAmount", Is.is(0.0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalCharge", Is.is(17.94)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("test6")
    public void test6() throws Exception {
        CheckoutRequest expectedRecord = new CheckoutRequest(
                "JAKR", 4, 50, LocalDate.parse("07/02/20", FORMATTER));

        mockMvc.perform(post("/rental")
                        .contentType("application/json")
                        .content(OM.writeValueAsString(expectedRecord)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dueDate", Is.is("07/06/20")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.chargeDays", Is.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.preDiscountCharge", Is.is(2.99)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.discountAmount", Is.is(1.5)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.finalCharge", Is.is(1.49)))
                .andExpect(status().is2xxSuccessful());
    }
}
package com.example.rhservice.infrastructure.web.controller;

import com.example.rhservice.domain.model.Employee;
import com.example.rhservice.domain.model.Shift;
import com.example.rhservice.domain.model.TimeBlock;
import com.example.rhservice.domain.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalTime;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PayrollControllerTest extends AbstractApiIntegrationTest {

    @Test
    void adminCanListPayrolls() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/payrolls").session(adminSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()")
                        .value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
    }

    @Test
    void adminCanCreatePayroll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/payrolls")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"month":"July","year":"2026","paid":false,"amount":2600,"employeeId":%d}
                                """.formatted(employeeId)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.month").value("July"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employeeId").value(employeeId.intValue()));
    }

    @Test
    void adminCanGetPayrollById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/payrolls/{id}", otherPayrollId).session(adminSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.employeeId").value(otherEmployeeId.intValue()));
    }

    @Test
    void adminCanUpdatePayroll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/payrolls/{id}", otherPayrollId)
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"month":"August","year":"2026","paid":true,"amount":2700,"employeeId":%d}
                                """.formatted(otherEmployeeId)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.month").value("August"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.paid").value(true));
    }

    @Test
    void adminCanDeletePayroll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/payrolls/{id}", otherPayrollId).session(adminSession))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void employeeCanListOwnPayrolls() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/payrolls").session(employeeSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()")
                        .value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)));
    }

    @Test
    void employeeCanReadOwnPayroll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/payrolls/{id}", employeePayrollId).session(employeeSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.employeeId").value(employeeId.intValue()));
    }

    @Test
    void employeeCannotReadAnotherPayroll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/payrolls/{id}", otherPayrollId).session(employeeSession))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void employeeCannotCreatePayroll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/payrolls")
                        .session(employeeSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"month":"July","year":"2026","paid":false,"amount":2600,"employeeId":%d}
                                """.formatted(employeeId)))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void employeeCannotGeneratePayroll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/payrolls/generate")
                        .session(employeeSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"employeeId":%d,"month":"July","year":"2026"}
                                """.formatted(employeeId)))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void employeeCannotUpdatePayroll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/payrolls/{id}", employeePayrollId)
                        .session(employeeSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"month":"July","year":"2026","paid":true,"amount":2500,"employeeId":%d}
                                """.formatted(employeeId)))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void employeeCannotDownloadAnotherPaystub() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/payrolls/{id}/paystub", otherPayrollId).session(employeeSession))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void adminCannotCreatePayrollForUnknownEmployee() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/payrolls")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"month":"July","year":"2026","paid":false,"amount":2600,"employeeId":999999}
                                """))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Not Found"));
    }

    @Test
    void adminCanGeneratePayrollWithOvertimePremium() throws Exception {
        Shift overtimeShift = new Shift();
        overtimeShift.setMonday(List.of(new TimeBlock(LocalTime.of(8, 0), LocalTime.of(17, 0))));
        overtimeShift.setTuesday(List.of(new TimeBlock(LocalTime.of(8, 0), LocalTime.of(17, 0))));
        overtimeShift.setWednesday(List.of(new TimeBlock(LocalTime.of(8, 0), LocalTime.of(17, 0))));
        overtimeShift.setThursday(List.of(new TimeBlock(LocalTime.of(8, 0), LocalTime.of(17, 0))));
        overtimeShift.setFriday(List.of(new TimeBlock(LocalTime.of(8, 0), LocalTime.of(17, 0))));
        overtimeShift.setSaturday(List.of(new TimeBlock(LocalTime.of(8, 0), LocalTime.of(12, 0))));
        overtimeShift.setSunday(List.of());
        overtimeShift = shiftJpaRepository.save(overtimeShift);

        Employee overtimeEmployee = new Employee();
        overtimeEmployee.setName("Overtime Employee");
        overtimeEmployee.setUsername("overtime");
        overtimeEmployee.setPassword(passwordEncoder.encode("overtimepassword"));
        overtimeEmployee.setRole(UserRole.EMPLOYEE);
        overtimeEmployee.setTelephone("555-500-500");
        overtimeEmployee.setAddress("Overtime Street 5");
        overtimeEmployee.setBankAccount("ES000000005");
        overtimeEmployee.setMonthlyHours(160.0);
        overtimeEmployee.setSalary(2400.0);
        overtimeEmployee.setShift(overtimeShift);
        overtimeEmployee = employeeJpaRepository.save(overtimeEmployee);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/payrolls/generate")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"employeeId":%d,"month":"July","year":"2026"}
                                """.formatted(overtimeEmployee.getId())))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(2994.0));
    }

    @Test
    void employeeCanDownloadOwnPaystub() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/payrolls/{id}/paystub", employeePayrollId)
                        .session(employeeSession))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_PDF));
    }
}
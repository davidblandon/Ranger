package com.example.productionservice.controller.partner;

import com.example.productionservice.dto.partner.PartnerResponse;
import com.example.productionservice.exception.NotFoundException;
import com.example.productionservice.service.partner.PartnerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PartnerController.class)
class PartnerControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean PartnerService partnerService;

    @Test
    void createReturns201WithBody() throws Exception {
        when(partnerService.createPartner(any()))
            .thenReturn(new PartnerResponse(1L, "Acme", "SUPPLIER", "FR123", "0600000000"));

        mockMvc.perform(post("/api/partners")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Acme\",\"rol\":\"SUPPLIER\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Acme"));
    }

    @Test
    void createReturns400WhenNameBlank() throws Exception {
        mockMvc.perform(post("/api/partners")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"rol\":\"SUPPLIER\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getByIdReturns404WhenMissing() throws Exception {
        when(partnerService.getPartnerById(eq(99L)))
            .thenThrow(new NotFoundException("Partner not found: 99"));

        mockMvc.perform(get("/api/partners/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }
}

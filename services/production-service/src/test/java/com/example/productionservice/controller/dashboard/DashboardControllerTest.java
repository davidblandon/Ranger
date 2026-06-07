package com.example.productionservice.controller.dashboard;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void summaryReflectsCreatedEntitiesAndCompletedTotals() throws Exception {
        long partnerId = createAndReadId("/api/partners",
            "{\"name\":\"Client A\",\"rol\":\"CLIENT\"}");
        long productId = createAndReadId("/api/products",
            "{\"name\":\"Table\",\"price\":200.0,\"cost\":120.0,\"stock\":2}");
        createAndReadId("/api/orders",
            "{\"partnerId\":" + partnerId + ",\"products\":{\"" + productId
                + "\":1},\"state\":\"COMPLETED\"}");

        MvcResult result = mockMvc.perform(get("/api/dashboard"))
            .andExpect(status().isOk())
            .andReturn();

        JsonNode dashboard = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(dashboard.get("totalPartners").asLong()).isGreaterThanOrEqualTo(1);
        assertThat(dashboard.get("totalProducts").asLong()).isGreaterThanOrEqualTo(1);
        assertThat(dashboard.get("totalOrders").asLong()).isGreaterThanOrEqualTo(1);
        assertThat(dashboard.get("ordersByState").get("COMPLETED").asLong()).isGreaterThanOrEqualTo(1);
        assertThat(dashboard.get("completedRevenue").asDouble()).isGreaterThanOrEqualTo(200.0);
        assertThat(dashboard.get("completedBenefice").asDouble()).isGreaterThanOrEqualTo(80.0);
    }

    private long createAndReadId(String url, String body) throws Exception {
        MvcResult result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }
}

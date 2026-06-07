package com.example.productionservice;

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
class ProductionFlowIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void completedOrderComputesTotalsAndConsumesStock() throws Exception {
        long partnerId = createAndReadId("/api/partners",
            "{\"name\":\"Client A\",\"rol\":\"CLIENT\"}");

        long productId = createAndReadId("/api/products",
            "{\"name\":\"Chair\",\"price\":100.0,\"cost\":60.0,\"stock\":5}");

        // A COMPLETED order should compute price/benefice and consume one unit of stock.
        MvcResult orderResult = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"partnerId\":" + partnerId + ",\"products\":{\"" + productId
                    + "\":1},\"state\":\"COMPLETED\"}"))
            .andExpect(status().isCreated())
            .andReturn();

        JsonNode order = objectMapper.readTree(orderResult.getResponse().getContentAsString());
        assertThat(order.get("price").asDouble()).isEqualTo(100.0);
        assertThat(order.get("benefice").asDouble()).isEqualTo(40.0);

        MvcResult productResult = mockMvc.perform(get("/api/products/" + productId))
            .andExpect(status().isOk())
            .andReturn();
        JsonNode product = objectMapper.readTree(productResult.getResponse().getContentAsString());
        assertThat(product.get("stock").asInt()).isEqualTo(4);
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

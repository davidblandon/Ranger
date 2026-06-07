package com.example.productionservice;

import com.example.productionservice.domain.order.OrderState;
import com.example.productionservice.dto.batch.BatchRequest;
import com.example.productionservice.dto.material.MaterialRequest;
import com.example.productionservice.dto.material.MaterialResponse;
import com.example.productionservice.dto.order.OrderRequest;
import com.example.productionservice.dto.partner.PartnerRequest;
import com.example.productionservice.dto.partner.PartnerResponse;
import com.example.productionservice.dto.product.ProductRequest;
import com.example.productionservice.dto.product.ProductResponse;
import com.example.productionservice.repository.batch.BatchRepository;
import com.example.productionservice.repository.material.MaterialRepository;
import com.example.productionservice.repository.order.OrderRepository;
import com.example.productionservice.repository.partner.PartnerRepository;
import com.example.productionservice.repository.product.ProductRepository;
import com.example.productionservice.service.batch.BatchService;
import com.example.productionservice.service.material.MaterialService;
import com.example.productionservice.service.order.OrderService;
import com.example.productionservice.service.partner.PartnerService;
import com.example.productionservice.service.product.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final OrderRepository orderRepository;
    private final BatchRepository batchRepository;
    private final ProductRepository productRepository;
    private final MaterialRepository materialRepository;
    private final PartnerRepository partnerRepository;
    private final PartnerService partnerService;
    private final MaterialService materialService;
    private final ProductService productService;
    private final BatchService batchService;
    private final OrderService orderService;

    public DataInitializer(
            OrderRepository orderRepository,
            BatchRepository batchRepository,
            ProductRepository productRepository,
            MaterialRepository materialRepository,
            PartnerRepository partnerRepository,
            PartnerService partnerService,
            MaterialService materialService,
            ProductService productService,
            BatchService batchService,
            OrderService orderService) {
        this.orderRepository = orderRepository;
        this.batchRepository = batchRepository;
        this.productRepository = productRepository;
        this.materialRepository = materialRepository;
        this.partnerRepository = partnerRepository;
        this.partnerService = partnerService;
        this.materialService = materialService;
        this.productService = productService;
        this.batchService = batchService;
        this.orderService = orderService;
    }

    @Override
    public void run(String... args) {
        // Clear all data in FK-safe order.
        // materials has a stale batch_id FK (ddl-auto=update never drops columns),
        // so products → materials → batches; orders and partners come last.
        orderRepository.deleteAll();
        productRepository.deleteAll();   // cascades product_materials join table
        materialRepository.deleteAll();  // must precede batches (stale batch_id FK)
        batchRepository.deleteAll();     // cascades batch_materials join table
        partnerRepository.deleteAll();

        // --- Partners ---
        PartnerResponse supplier = partnerService.createPartner(
            new PartnerRequest("TextilPro", "SUPPLIER", "FR7614508059420296226425Q71", "+33123456789"));
        PartnerResponse client = partnerService.createPartner(
            new PartnerRequest("FashionHub", "CLIENT", "FR7630006000011234567890189", "+33987654321"));

        // --- Materials (linked to the supplier) ---
        MaterialResponse cotton = materialService.createMaterial(
            new MaterialRequest("Cotton Fabric", "FABRIC", supplier.id(), 5.0, 0));
        MaterialResponse thread = materialService.createMaterial(
            new MaterialRequest("Polyester Thread", "THREAD", supplier.id(), 2.5, 0));
        MaterialResponse buttons = materialService.createMaterial(
            new MaterialRequest("Metal Buttons", "ACCESSORIES", supplier.id(), 0.8, 0));

        // --- Batches (each receipt increases the linked materials' stock) ---
        batchService.createBatch(new BatchRequest(
            LocalDate.of(2026, 1, 15),
            Map.of(cotton.id(), 50, thread.id(), 100)));
        batchService.createBatch(new BatchRequest(
            LocalDate.of(2026, 2, 20),
            Map.of(thread.id(), 50, buttons.id(), 200)));

        // --- Products (use the materials above) ---
        ProductResponse tshirt = productService.createProduct(new ProductRequest(
            "Cotton T-Shirt", "CLOTHING", "M", "White",
            List.of(cotton.id(), thread.id()), 25.0, 10.0, 50));
        ProductResponse jacket = productService.createProduct(new ProductRequest(
            "Polyester Jacket", "CLOTHING", "L", "Black",
            List.of(thread.id(), buttons.id()), 85.0, 40.0, 20));
        ProductResponse dress = productService.createProduct(new ProductRequest(
            "Summer Dress", "CLOTHING", "S", "Blue",
            List.of(cotton.id(), buttons.id()), 55.0, 22.0, 30));

        // --- Orders (linked to the client) ---
        orderService.createOrder(new OrderRequest(
            LocalDate.of(2026, 3, 10), client.id(),
            Map.of(jacket.id(), 3), OrderState.PENDING));
        orderService.createOrder(new OrderRequest(
            LocalDate.of(2026, 4, 5), client.id(),
            Map.of(tshirt.id(), 5, jacket.id(), 2), OrderState.IN_PRODUCTION));
        orderService.createOrder(new OrderRequest(
            LocalDate.of(2026, 5, 20), client.id(),
            Map.of(tshirt.id(), 2, dress.id(), 1), OrderState.COMPLETED));
    }
}

package com.example.productionservice.service.product.impl;

import com.example.productionservice.domain.material.Material;
import com.example.productionservice.domain.product.Product;
import com.example.productionservice.dto.product.ProductRequest;
import com.example.productionservice.dto.product.ProductResponse;
import com.example.productionservice.exception.NotFoundException;
import com.example.productionservice.mapper.product.ProductMapper;
import com.example.productionservice.repository.material.MaterialRepository;
import com.example.productionservice.repository.product.ProductRepository;
import com.example.productionservice.service.product.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final MaterialRepository materialRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository,
                              MaterialRepository materialRepository,
                              ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.materialRepository = materialRepository;
        this.productMapper = productMapper;
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        apply(product, request);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return productMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
            .map(productMapper::toResponse)
            .toList();
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = findOrThrow(id);
        apply(product, request);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.delete(findOrThrow(id));
    }

    private void apply(Product product, ProductRequest request) {
        product.setName(request.name());
        product.setType(request.type());
        product.setSize(request.size());
        product.setColor(request.color());
        product.setPrice(request.price());
        product.setCost(request.cost());
        product.setStock(request.stock());
        product.setMaterials(resolveMaterials(request.materialIds()));
    }

    private List<Material> resolveMaterials(List<Long> materialIds) {
        List<Material> materials = new ArrayList<>();
        if (materialIds == null) {
            return materials;
        }
        for (Long materialId : materialIds) {
            materials.add(materialRepository.findById(materialId)
                .orElseThrow(() -> new NotFoundException("Material not found: " + materialId)));
        }
        return materials;
    }

    private Product findOrThrow(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Product not found: " + id));
    }
}

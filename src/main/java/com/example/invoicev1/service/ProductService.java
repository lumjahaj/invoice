package com.example.invoicev1.service;

import com.example.invoicev1.dto.CreateProductDTO;
import com.example.invoicev1.entity.Product;
import com.example.invoicev1.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void saveProducts(List<CreateProductDTO> createProductDTOList) {
        List<Product> products = new ArrayList<>();
        for (CreateProductDTO productDTO : createProductDTOList) {
            Product product = new Product();
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            product.setVAT(productDTO.getVAT());
            product.setDiscount(productDTO.getDiscount());
            product.setQtyAvailable(productDTO.getQtyAvailable());
            products.add(product);
        }
        productRepository.saveAll(products);
    }
}

package com.example.invoicev1.repository;

import com.example.invoicev1.entity.InvoiceProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceProductRepository extends JpaRepository<InvoiceProduct, Long> {

}

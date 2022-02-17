package com.example.invoicev1.repository;

import com.example.invoicev1.entity.Invoice;
import com.example.invoicev1.entity.InvoiceProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface InvoiceProductRepository extends JpaRepository<InvoiceProduct, Long> {
    Set<InvoiceProduct> findInvoiceProductsByInvoice(Invoice invoice);
}

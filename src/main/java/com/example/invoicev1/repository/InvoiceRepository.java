package com.example.invoicev1.repository;

import com.example.invoicev1.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findInvoicesByOrder_Id(Long id);
}

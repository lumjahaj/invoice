package com.example.invoicev1.repository;

import com.example.invoicev1.entity.Invoice;
import com.example.invoicev1.entity.Order;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByOrder(Order order, Sort sort);
    List<Invoice> findInvoicesByOrder_Id(Long id);
}

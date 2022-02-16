package com.example.invoicev1.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InvoiceProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_product_id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    @Column(name = "qty", nullable = false)
    int qty;

    @Column(name = "subtotal", columnDefinition = "DECIMAL(19,5)", nullable = false)
    BigDecimal subtotal;

    @Column(name = "vat_total", columnDefinition = "DECIMAL(19,5)", nullable = false)
    BigDecimal VATtotal;

    @Column(name = "total", columnDefinition = "DECIMAL(19,5)", nullable = false)
    BigDecimal total;

}
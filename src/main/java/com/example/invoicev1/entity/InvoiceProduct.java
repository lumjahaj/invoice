package com.example.invoicev1.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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

}
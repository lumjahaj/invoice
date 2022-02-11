package com.example.invoicev1.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(name = "subtotal", columnDefinition = "DECIMAL(19,5)")
    private BigDecimal subtotal;

    @Column(name = "vat_total", columnDefinition = "DECIMAL(19,5)")
    private BigDecimal VATtotal;

    @Column(name = "total", columnDefinition = "DECIMAL(19,5)")
    private BigDecimal total;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<Invoice> orderInvoices;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    private List<Product> orderProducts;

}

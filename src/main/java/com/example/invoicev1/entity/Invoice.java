package com.example.invoicev1.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long id;

    @Column(name = "subtotal", columnDefinition = "DECIMAL(19,5)")
    private BigDecimal subtotal;

    @Column(name = "vat_total", columnDefinition = "DECIMAL(19,5)")
    private BigDecimal VATtotal;

    @Column(name = "total", columnDefinition = "DECIMAL(19,5)")
    private BigDecimal total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    private Order order;

    @OneToMany(mappedBy = "invoice")
    Set<InvoiceProduct> invoiceProducts;

    public Invoice(Long id, BigDecimal subtotal, BigDecimal vattotal, BigDecimal total) {
        this.subtotal = subtotal;
        this.VATtotal = vattotal;
        this.total = total;
    }
}

package com.example.invoicev1.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false, columnDefinition = "DECIMAL(19,5)")
    private BigDecimal price;

    @Column(name = "vat", nullable = false)
    private Integer VAT;

    @Column(name = "discount", nullable = false, columnDefinition = "DECIMAL(19,5)")
    private BigDecimal discount;

    @Column(name = "qty_available", nullable = false)
    private Integer QtyAvailable;

    @Column(name = "subtotal", columnDefinition = "DECIMAL(19,5)")
    private BigDecimal subtotal;

    @Column(name = "vat_total", columnDefinition = "DECIMAL(19,5)")
    private BigDecimal VATtotal;

    @Column(name = "total", columnDefinition = "DECIMAL(19,5)")
    private BigDecimal total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    private Order order;

    @OneToMany(mappedBy = "product")
    Set<InvoiceProduct> invoiceProducts = new HashSet<>();

    public Product(Long id, String description, double price, int VAT, double discount, int qty) {
        this.description = description;
        this.price = BigDecimal.valueOf(price);
        this.VAT = VAT;
        this.discount = BigDecimal.valueOf(discount);
        this.QtyAvailable = qty;
    }

    public Product(Product another, int qty) {
        this.description = another.description;
        this.price = another.price;
        this.VAT = another.VAT;
        this.discount = another.discount;
        this.QtyAvailable = qty;
    }

    public Product(Order order) {
        this.order = order;
    }

    public BigDecimal[] calculateProductTotal() {
        BigDecimal subtotal, total, VATtotal;
        if (!(this.getDiscount().equals(BigDecimal.valueOf(0)))) {
            subtotal = this.getPrice().subtract(this.getDiscount());
        } else {
            subtotal = this.getPrice();
        }
        VATtotal = subtotal.multiply((BigDecimal.valueOf(this.getVAT())).multiply(BigDecimal.valueOf(0.01)));
        total = subtotal.add(VATtotal);
        return new BigDecimal[]{subtotal, VATtotal, total};
    }

    public boolean checkProductQtyTotalOverValue(int value, int qty) {
        BigDecimal subtotal, total, VATtotal;
        if (!(this.getDiscount().equals(BigDecimal.valueOf(0)))) {
            subtotal = this.getPrice().subtract(this.getDiscount());
        } else {
            subtotal = this.getPrice();
        }
        VATtotal = subtotal.multiply((BigDecimal.valueOf(this.getVAT())).multiply(BigDecimal.valueOf(0.01)));
        total = subtotal.add(VATtotal);
        BigDecimal totalQty = total.multiply(BigDecimal.valueOf(qty));
        return !(totalQty.compareTo(BigDecimal.valueOf(value)) > 0);
    }
}

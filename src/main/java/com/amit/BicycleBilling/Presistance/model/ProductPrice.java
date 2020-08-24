package com.amit.BicycleBilling.Presistance.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.sql.Date;

@Entity
public class ProductPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int priceId;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private Date startOfPrice;

    @Column(nullable = false)
    private Date endOfPrice;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonBackReference
    private Product product;

    public int getPriceId() {
        return priceId;
    }

    public void setPriceId(int priceId) {
        this.priceId = priceId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getStartOfPrice() {
        return startOfPrice;
    }

    public void setStartOfPrice(Date start) {
        this.startOfPrice = start;
    }

    public Date getEndOfPrice() {
        return endOfPrice;
    }

    public void setEndOfPrice(Date end) {
        this.endOfPrice = end;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}

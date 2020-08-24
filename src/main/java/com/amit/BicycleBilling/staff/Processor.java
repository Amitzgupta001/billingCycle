package com.amit.BicycleBilling.staff;

import com.amit.BicycleBilling.Presistance.model.Category;
import com.amit.BicycleBilling.Presistance.model.Product;
import com.amit.BicycleBilling.Presistance.model.ProductPrice;
import com.amit.BicycleBilling.Presistance.repo.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Processor implements Runnable {
    List<StaffRole.CategoryProduct> categoryProducts;
    CategoryRepo categoryRepo;

    public Processor(List<StaffRole.CategoryProduct> subList, CategoryRepo categoryRepo) {
        this.categoryProducts = subList;
        this.categoryRepo = categoryRepo;
    }


    @Override
    public void run() {
        for (StaffRole.CategoryProduct categoryProduct :
                categoryProducts) {
            double sum = 0;

            for (int i = 0; i < categoryProduct.category.size(); i++) {
                Category category = categoryRepo.findByName(categoryProduct.category.get(i));
                int finalI = i;
                Product product = category.getProducts().stream().filter(product1 -> product1.getBrand().equals(categoryProduct.Product.get(finalI)))
                        .collect(Collectors.toList()).get(0);
                sum = sum + getPrice(product.getPrices(), new Date(System.currentTimeMillis())).getPrice();
            }

            System.out.println("total price of bicycle " + categoryProduct.getBicycle() + " : " + sum);
        }


    }

    private ProductPrice getPrice(List<ProductPrice> prices, Date endDate) {

        for (ProductPrice productPrice :
                prices) {

            if ((productPrice.getStartOfPrice().before(endDate)) && productPrice.getEndOfPrice().after(endDate)) {
                return productPrice;
            }
        }
        return null;
    }
}

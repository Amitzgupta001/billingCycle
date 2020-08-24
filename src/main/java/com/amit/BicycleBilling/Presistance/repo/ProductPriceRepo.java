package com.amit.BicycleBilling.Presistance.repo;

import com.amit.BicycleBilling.Presistance.model.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.Optional;

@Repository
public interface ProductPriceRepo extends JpaRepository<ProductPrice, Integer>, JpaSpecificationExecutor<ProductPrice> {

    // @Query("select * from Product_Price u inner join Product ar on ar.productId = u. where ar.productId = :idArea And  :sDate BETWEEN u.start_Of_Price AND u.end_Of_Price")
    Optional<ProductPrice> findByProductProductIdAndStartOfPriceLessThanAndEndOfPriceGreaterThan(int productId, Date sDate, Date eDate);
}

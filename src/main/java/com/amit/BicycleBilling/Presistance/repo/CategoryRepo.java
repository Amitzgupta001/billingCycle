package com.amit.BicycleBilling.Presistance.repo;

import com.amit.BicycleBilling.Presistance.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Integer>, JpaSpecificationExecutor<Category> {
    @Query("SELECT DISTINCT a.name FROM Category a")
    List<String> findDistinctName();

    @Query("SELECT a FROM Category a")
    List<Category> findDistinctNameAndMandatory();

    Category findByName(String name);


    Category findCategoryByName(String name);

}

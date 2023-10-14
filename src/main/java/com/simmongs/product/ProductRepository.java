package com.simmongs.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Products, Long> {

    @Query(value = """
            select 
                * 
            from 
                product 
            where 
                product_code = :product_code 
                and product_name = :product_name 
                and product_unit = :product_unit 
                and product_type = :product_type 
                and product_creation_date between :product_start_date and :product_end_date
            """, nativeQuery = true)
    List<Products> findSearchProduct(
            @Param("product_code") String product_code
            , @Param("product_name") String product_name
            , @Param("product_unit") String product_unit
            , @Param("product_type") String product_type
            , @Param("product_start_date") String product_start_date
            , @Param("product_end_date") String product_end_date);

}

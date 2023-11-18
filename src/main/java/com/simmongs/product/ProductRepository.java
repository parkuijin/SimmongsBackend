package com.simmongs.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Products, Long> {

    @Query(value = """
            select 
                * 
            from 
                PRODUCT_TB
            where 
                PRODUCT_CODE = :product_code 
                and PRODUCT_NAME = :product_name 
                and PRODUCT_UNIT = :product_unit 
                and PRODUCT_TYPE = :product_type 
                and PRODUCT_CREATION_DATE between :product_start_date and :product_end_date 
            """, nativeQuery = true)
    List<Products> findSearchProduct(
            @Param("product_code") String product_code
            , @Param("product_name") String product_name
            , @Param("product_unit") String product_unit
            , @Param("product_type") String product_type
            , @Param("product_start_date") String product_start_date
            , @Param("product_end_date") String product_end_date);

    @Query(value = """
            select 
                *
            from 
                PRODUCT_TB
            where
                ( PRODUCT_CODE LIKE %:keyword% OR PRODUCT_NAME LIKE %:keyword% )
                AND PRODUCT_TYPE = :productType
            """, nativeQuery = true)
    List<Products> findSearchComponentByKeyword(@Param("productType") String productType, @Param("keyword") String keyword);

    Optional<Products> findByProductCode(String productCode);

    @Query(value = """
            select 
                *
            from 
                PRODUCT_TB
            where
                PRODUCT_CODE = :productCode
            """, nativeQuery = true)
    Products getByProductCode(String productCode);

}

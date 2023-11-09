package com.simmongs.mrp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MRPRepository extends JpaRepository<MRPs, Long> {

    @Query(value = """
            select 
                *
            from 
                MRP_TB
            where
                WORK_ORDER_ID = :id and NEEDED_PRODUCT_CODE = :code
            """, nativeQuery = true)
    MRPs getByNeededProductCode(String id, String code);
}

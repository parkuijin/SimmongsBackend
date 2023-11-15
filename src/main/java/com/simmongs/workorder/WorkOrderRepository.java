package com.simmongs.workorder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrders, Long> {

    Optional<WorkOrders> findByWorkOrderId(String id);

    @Query(value = """
            select 
                *
            from 
                WORK_ORDER_TB
            where
                WORK_ORDER_ID = :id
            """, nativeQuery = true)
    WorkOrders getByWorkOrderId(String id);

    @Query(value = """
            select 
                *
            from 
                WORK_ORDER_TB
            where
                PRODUCT_CODE = :code
            """, nativeQuery = true)
    List<WorkOrders> searchByProductCode(String code);
}

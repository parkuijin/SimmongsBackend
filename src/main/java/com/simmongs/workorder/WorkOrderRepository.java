package com.simmongs.workorder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrders, Long> {

    Optional<WorkOrders> findByWorkOrderId(Long id);

    @Query(value = """
            select 
                *
            from 
                WORK_ORDER_TB
            where
                WORK_ORDER_ID = :id
            """, nativeQuery = true)
    WorkOrders getByWorkOrderId(Long id);
}

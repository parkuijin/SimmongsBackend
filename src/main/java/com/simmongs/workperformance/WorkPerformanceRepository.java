package com.simmongs.workperformance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkPerformanceRepository extends JpaRepository<WorkPerformance, Long> {

    @Query(value = """
            select
                *
            from
                WORK_PERFORMANCE_TB
            where
                WORK_ORDER_ID = :work_order_id
            """, nativeQuery = true)
    List<WorkPerformance> findByWorkOrderId(@Param(value = "work_order_id")Long work_order_id);
}

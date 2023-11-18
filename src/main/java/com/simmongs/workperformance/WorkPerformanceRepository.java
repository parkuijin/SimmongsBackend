package com.simmongs.workperformance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface WorkPerformanceRepository extends JpaRepository<WorkPerformance, Long> {

    @Query(value = """
            select
                *
            from
                WORK_PERFORMANCE_TB
            where
                WORK_ORDER_ID = :WORK_ORDER_ID
            """, nativeQuery = true)
    List<WorkPerformance> findByWorkOrderId(@Param(value = "WORK_ORDER_ID")String workOrderId);

    List<WorkPerformance> findByWorkNumber(String workNumber);

    @Query(value = """
            select distinct 
                COALESCE(p.PRODUCT_NAME, 'NA') as productNamem, wp.WORK_ORDER_ID as workOrderId, wp.WORK_NUMBER as workNumber, date_format(wp.WORK_PERFORMANCE_DATE, '%Y-%m-%d') as workPerformanceDate, wp.CURRENT_WORKLOAD as currentWorkload
            from 
                WORK_PERFORMANCE_TB as wp
                left join WORK_ORDER_TB as wo on wp.WORK_ORDER_ID = wo.WORK_ORDER_ID
                left join PRODUCT_TB as p on wo.PRODUCT_CODE = p.PRODUCT_CODE
            where
                wp.WORK_ORDER_ID = :workOrderId
            """, nativeQuery = true)
    List<Map<String, Object>> searchWorkPerformanceByWorkOrderId(String workOrderId);
}

package com.simmongs.workperformance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    WorkPerformance findByWorkPerformanceId(@Param(value = "WORK_PERFORMANCE_ID")Long workPerformanceId);
    List<WorkPerformance> findByWorkNumber(String workNumber);
}

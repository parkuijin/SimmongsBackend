package com.simmongs.workperformance;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "WORK_PERFORMANCE_TB")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WORK_PERFORMANCE_ID")
    private Long workPerformanceId;

    @Column(name = "WORK_ORDER_ID")
    private Long workOrderId;

    @Column(name = "CURRENT_WORKLOAD")
    private int currentWorkload;

    @Column(name = "USED_PRODUCT_CODE")
    private String usedProductCode;

    @Column(name = "USED_PRODUCT_AMOUNT")
    private int usedProductAmount;

    @Column(name = "WORK_PERFORMANCE_DATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime workPerformanceDate;

    public WorkPerformance(Long workOrderId, Integer currentWorkload, String usedProductCode, Integer usedProductAmount, LocalDateTime workPerformanceDate) {
        this.workOrderId = workOrderId;
        this.currentWorkload = currentWorkload;
        this.usedProductCode = usedProductCode;
        this.usedProductAmount = usedProductAmount;
        this.workPerformanceDate = workPerformanceDate;
    }

}

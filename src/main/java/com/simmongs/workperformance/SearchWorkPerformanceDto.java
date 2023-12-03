package com.simmongs.workperformance;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SearchWorkPerformanceDto {

    private String productName;
    private String workOrderId;
    private String workNumber;
    private LocalDate workPerformanceDate;
    private int currentWorkload;

    @QueryProjection
    public SearchWorkPerformanceDto(String productName, String workOrderId, String workNumber, LocalDateTime workPerformanceDate, int currentWorkload) {
        this.productName = productName;
        this.workOrderId = workOrderId;
        this.workNumber = workNumber;
        this.workPerformanceDate = workPerformanceDate.toLocalDate();
        this.currentWorkload = currentWorkload;
    }
}

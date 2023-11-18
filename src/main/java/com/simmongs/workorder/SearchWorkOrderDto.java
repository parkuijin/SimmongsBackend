package com.simmongs.workorder;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SearchWorkOrderDto {

    private String workOrderId;
    private String departmentName;
    private LocalDateTime workDeadline;
    private String productCode;
    private String productName;
    private String productUnit;
    private int workCurrentQuantity;
    private int workTargetQuantity;
    private String workStatus;

    @QueryProjection
    public SearchWorkOrderDto(String workOrderId, String departmentName, LocalDateTime workDeadline, String productCode, String productName, String productUnit, int workCurrentQuantity, int workTargetQuantity, String workStatus) {
        this.workOrderId = workOrderId;
        this.departmentName = departmentName;
        this.workDeadline = workDeadline;
        this.productCode = productCode;
        this.productName = productName;
        this.productUnit = productUnit;
        this.workCurrentQuantity = workCurrentQuantity;
        this.workTargetQuantity = workTargetQuantity;
        this.workStatus = workStatus;
    }
}

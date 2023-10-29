package com.simmongs.workorder;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity(name = "WORK_ORDER_TB")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkOrders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WORK_ORDER_ID")
    private Long workOrderId;

    @Column(name = "DEPARTMENT_NAME")
    private String departmentName;

    @Column(name = "WORK_SRART_DATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime workStartDate;

    @Column(name = "PRODUCT_CODE")
    private String productCode;

    @Column(name = "WORK_TARGET_QUANTITY")
    private int workTargetQuantity;

    @Column(name = "WORK_CURRENT_QUANTITY")
    @ColumnDefault("0")
    private int workCurrentQuantity;

    @Column(name = "WORK_DEADLINE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime workDeadline;

    @Column(name = "WORK_STATUS")
    private String workStatus;

    public WorkOrders(String departmentName, LocalDateTime workStartDate, String productCode, int workTargetQuantity, LocalDateTime workDeadline, String workStatus) {
        this.departmentName = departmentName;
        this.workStartDate = workStartDate;
        this.productCode = productCode;
        this.workTargetQuantity = workTargetQuantity;
        this.workDeadline = workDeadline;
        this.workStatus = workStatus;
    }

    public void workCurrentQuantityUpdate(int currentWorkLoad) {
        this.workCurrentQuantity += currentWorkLoad;
    }

}
